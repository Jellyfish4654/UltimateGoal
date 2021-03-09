package org.firstinspires.ftc.teamcode.framework.components.autodrivetrain;

import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.framework.components.autodrivetrain.AutoDrivetrain;
import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.teamcode.logging.Logger;
import org.firstinspires.ftc.teamcode.framework.Motors;
import org.firstinspires.ftc.teamcode.framework.components.Drivetrain;

/**
 * Mecanum2 represents a mecanum drivetrain using the built-in
 * encoders.
 */
public class Mecanum2 implements AutoDrivetrain {
    private Drivetrain drivetrain;
    private BNO055IMU imu;

    public Mecanum2(Drivetrain drivetrain, BNO055IMU imu) {
        this.drivetrain = drivetrain;
        this.imu = imu;
    }

    /** 
     * Returns the current magnitude of a move operation (i.e. how much the robot has moved so far) in ticks.
     */
    protected double getMoveMagnitude() {
        double[] encoders = drivetrain.encoderPositions();
        Logger.instance.addData("move \\ motor positions", "r %.1f %.1f l %.1f %.1f",
            encoders[Motors.FR], encoders[Motors.BR], encoders[Motors.FL], encoders[Motors.BL]);

        double rPos = (encoders[Motors.FR] + encoders[Motors.BL]) / 2; // should be about the same
        double lPos = (encoders[Motors.BR] + encoders[Motors.FL]) / 2; // should be about the same
        return Math.sqrt(rPos*rPos + lPos*lPos);
    }

    @Override
    public boolean move(double magnitude, double angle, AutoDrivetrain.Params params) {
        double currentMagnitude = getMoveMagnitude();
        double distanceLeft = magnitude - currentMagnitude;

        Logger.instance.addData("move \\ distance left", distanceLeft);
        if (Math.abs(distanceLeft) <= params.allowableDistanceError) {
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

        Logger.instance.addData("move \\ power", "%f", power);
        Logger.instance.addData("move \\ angle", "%f", angle);
        Logger.instance.addData("move \\ coords", "%f, %f", pX, pY);

        setMotorSpeeds(new double[] {
            pY + pX, pY - pX, pY - pX, pY + pX
        });
        Logger.instance.update();
        drivetrain.update();

        return true;
    }

    double pivotInitialAngle;
    @Override public boolean pivot(double angle, AutoDrivetrain.Params params) {
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
            setMotorSpeeds(new double[] {power, power, -power, -power});
        } else {
            // Clockwise
            setMotorSpeeds(new double[] {-power, -power, power, power});
        }

        Logger.instance.update();
        drivetrain.update();

        return true;
    }

    @Override public void stop() {
        drivetrain.setSpeeds(new double[]{0,0,0,0});
        reset();
    }

    /**
     * Corrects the given motor powers so that they are all <= 1.0
     * and sets the motor powers.
     */
    protected void setMotorSpeeds(double[] powers) {
        double max = Math.max(Math.max(Math.abs(powers[0]), Math.abs(powers[1])), Math.max(Math.abs(powers[2]), Math.abs(powers[3])));
        double scale = Math.abs(1 / max);
        // don't increase power, only decrease
        if (scale > 1) {
            scale = 1;
        }

        for (int i = 0; i < 4; i++) {
            powers[i] *= scale;
        }
        for (int i = 0; i < 4; i++) {
            powers[i] *= Drivetrain.MAX_SPEED;
        }
        drivetrain.setSpeeds(powers);
    }

    @Override public void reset() {
        pivotInitialAngle = -imu.getAngularOrientation().firstAngle;
        drivetrain.reset();
    }
}