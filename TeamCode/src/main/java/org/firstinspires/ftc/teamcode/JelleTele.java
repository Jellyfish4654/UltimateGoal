package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.framework.BaseOpMode;
import org.firstinspires.ftc.teamcode.framework.Motors;

@TeleOp(name = "UltimateGoal JelleTele")
public class JelleTele extends BaseOpMode {
    protected static enum DriveMode {
        TANK,
        MECANUM,
    }

    protected DriveMode driveMode = DriveMode.MECANUM;

    @Override
    public void runOpMode() throws InterruptedException {
        initHardware();
        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.dpad_left) {
                driveMode = DriveMode.TANK;
            } else if (gamepad1.dpad_right) { 
                driveMode = DriveMode.MECANUM;
            }

            double mult = gamepad1.left_bumper ? 0.2 : gamepad1.right_bumper ? 0.5 : 1.0;

            logger.addData("drive mode", driveMode);
            logger.addData("precision mode", mult);

            switch (driveMode) {
            case TANK: {
                double l = gamepad1.left_stick_y,
                    r = gamepad1.right_stick_y;
                setMotorPowers(mult, new double[] {r, r, l, l});
                break;
            }
            case MECANUM: {
                // right = +, left = -
                double pivot = gamepad1.right_stick_x;
                double mX, mY;
                mX = gamepad1.left_stick_x;
                mY = gamepad1.left_stick_y;
                setMotorPowers(mult, new double[] {
                    mY + mX + pivot,
                    mY - mX + pivot,
                    mY - mX - pivot,
                    mY + mX - pivot});
                break;
            }
            }
            logger.update();
        }
    }

    /**
     * Corrects the given motor powers so that they are all <= 1.0
     * and sets the motor powers.
     */
    protected void setMotorPowers(double mult, double[] powers) {
        for (int i = 0; i < 4; i++) {
            powers[i] = powers[i] * mult;
        }

        double max = Math.max(Math.max(Math.abs(powers[0]), Math.abs(powers[1])), Math.max(Math.abs(powers[2]), Math.abs(powers[3])));
        double scale = Math.abs(1 / max);
        // don't increase power, only decrease
        if (scale > 1) {
            scale = 1;
        }

        for (int i = 0; i < 4; i++) {
            powers[i] *= scale;
            drivetrain.motors[i].setPower(powers[i]);
        }
    }
}
