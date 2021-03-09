package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.framework.BaseOpMode;
import org.firstinspires.ftc.teamcode.framework.Motors;
import org.firstinspires.ftc.teamcode.framework.components.Drivetrain;

@TeleOp(name = "UltimateGoal JelleTele")
public class JelleTele extends BaseOpMode {
    protected static enum DriveMode {
        TANK,
        DRIVE,
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
            } else if (gamepad1.dpad_up) { 
                driveMode = DriveMode.MECANUM;
            } else if (gamepad1.dpad_right) {
                driveMode = DriveMode.DRIVE;
            }

            double mult = gamepad1.left_bumper ? 0.2 : gamepad1.right_bumper ? 0.5 : 1.0;

            logger.addData("drive mode", driveMode);
            logger.addData("precision mode", mult);

            switch (driveMode) {
            case TANK: {
                double l = gamepad1.left_stick_y,
                    r = gamepad1.right_stick_y;
                setMotorSpeeds(mult, new double[] {r, r, l, l});
                break;
            }
            case DRIVE: {
                double pivot = gamepad1.left_stick_x, y = gamepad1.left_stick_y;
                setMotorSpeeds(mult, new double[] {
                    y+pivot,
                    y+pivot,
                    y-pivot,
                    y-pivot
                });
                break;
            }
            case MECANUM: {
                // right = +, left = -
                double pivot = gamepad1.right_stick_x;
                double mX, mY;
                mX = gamepad1.left_stick_x;
                mY = gamepad1.left_stick_y;
                setMotorSpeeds(mult, new double[] {
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
    protected void setMotorSpeeds(double mult, double[] powers) {
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
        }
        for (int i = 0; i < 4; i++) {
            powers[i] *= Drivetrain.MAX_SPEED;
        }
        drivetrain.setSpeeds(powers);
    }
}
