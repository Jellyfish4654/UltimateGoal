package org.firstinspires.ftc.teamcode.framework.components.drivetrain;

import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.framework.components.drivetrain.Drivetrain;
import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.teamcode.logging.Logger;
import org.firstinspires.ftc.teamcode.framework.Motors;

/**
 * Mecanum2 represents a mecanum drivetrain using the built-in
 * encoders.
 */
public class Mecanum2 implements Drivetrain {
    private DcMotor[] motors;
    private BNO055IMU imu;

    public Mecanum2(DcMotor[] motors, BNO055IMU imu) {
        this.motors = motors;
        this.imu = imu;
    }

    /** 
     * Returns the current magnitude of a move operation (i.e. how much the robot has moved so far) in ticks.
     */
    protected double getMoveMagnitude() {
        Logger.instance.addData("move \\ motor positions", "r %d %d l %d %d", motors[Motors.FR].getCurrentPosition(), motors[Motors.BL].getCurrentPosition(),
            motors[Motors.FL].getCurrentPosition(), motors[Motors.BR].getCurrentPosition());

        double rPos = (motors[Motors.FR].getCurrentPosition() + motors[Motors.BL].getCurrentPosition()) / 2; // should be about the same
        double lPos = (motors[Motors.FL].getCurrentPosition() + motors[Motors.BR].getCurrentPosition()) / 2; // should be about the same
        return Math.sqrt(rPos*rPos + lPos*lPos);
    }

    @Override
    public boolean move(double magnitude, double angle, Drivetrain.Params params) {
        double currentMagnitude = getMoveMagnitude();
        double distanceLeft = magnitude - currentMagnitude;

        Logger.instance.addData("move \\ distance left", distanceLeft);
        if (distanceLeft <= params.allowableDistanceError) {
            this.stop();
            return false;
        }

        double power;
        if (distanceLeft < 0) {
            Logger.instance.addData("move \\ state", "ramp down correct");
            power = -params.minPower;
        } else if (distanceLeft < params.rampDownEnd) {
            Logger.instance.addData("move \\ state", "ramp down end");
            power = params.minPower;
        } else if (distanceLeft > params.rampDownEnd && distanceLeft < params.rampDown) {
            Logger.instance.addData("move \\ state", "ramp down");
            power = params.minPower + (params.maxPower - params.minPower) * (distanceLeft - params.rampDownEnd) / (params.rampDown - params.rampDownEnd);
        } else {
            Logger.instance.addData("move \\ state", "full power");
            power = params.maxPower;
        }

        // calculate power
        double pX = power * Math.cos(angle);
        double pY = power * Math.sin(angle);

        setMotorPowers(new double[] {
            pY - pX, pY + pX, pY + pX, pY - pX
        });
        Logger.instance.update();

        return true;
    }

    double pivotInitialAngle;
    @Override public boolean pivot(double angle, Drivetrain.Params params) {
        double currentAngle = -imu.getAngularOrientation().firstAngle - pivotInitialAngle;
        Logger.instance.addData("pivot \\ angle", currentAngle * 180 / Math.PI);
        int direction = (int)(Math.abs(angle) / angle);
        double absCurrentAngle = Math.abs(currentAngle);
        double absAngle = Math.abs(angle);
        double angleDiff = absAngle - absCurrentAngle;

        if (Math.abs(angleDiff) < params.allowableDistanceError) {
            return false;
        }

        double power;
        if (angleDiff < 0) {
            Logger.instance.addData("pivot \\ state", "ramp down correct");
            power = -params.minPower;
        } else if (angleDiff < params.rampDownEnd) {
            Logger.instance.addData("pivot \\ state", "ramp down end");
            power = params.minPower;
        } else if (angleDiff > params.rampDownEnd && angleDiff < params.rampDown) {
            Logger.instance.addData("pivot \\ state", "ramp down");
            power = params.minPower + (params.maxPower - params.minPower) * angleDiff / (params.rampDown - params.rampDownEnd);
        } else {
            Logger.instance.addData("pivot \\ state", "full power");
            power = params.maxPower;
        }

        if (direction == 1) {
            // Counterclockwise
            setMotorPowers(new double[] {power, power, -power, -power});
        } else {
            // Clockwise
            setMotorPowers(new double[] {-power, -power, power, power});
        }

        Logger.instance.update();

        return true;
    }

    @Override public void stop() {
        for (DcMotor motor : motors) {
            motor.setPower(0);
        }
        reset();
    }

    /**
     * Corrects the given motor powers so that they are all <= 1.0
     * and sets the motor powers.
     */
    protected void setMotorPowers(double[] powers) {
        double max = Math.max(Math.max(powers[0], powers[1]), Math.max(powers[2], powers[3]));
        double scale = 1 / max;
        // don't increase power, only decrease
        if (scale > 1) {
            scale = 1;
        }

        for (int i = 0; i < 4; i++) {
            powers[i] *= scale;
            motors[i].setPower(powers[i]);
        }
    }

    @Override public void reset() {
        pivotInitialAngle = -imu.getAngularOrientation().firstAngle;
        Drivetrain.resetEncoders(motors);
    }
}