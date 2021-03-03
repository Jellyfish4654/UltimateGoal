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

    protected DriveMode driveMode;

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

            logger.addData("drive mode", driveMode);

            double mult = 1;
            switch (driveMode) {
            case TANK: {
                double l = gamepad1.left_stick_y,
                    r = gamepad1.right_stick_y;
                setMotorPowers(mult, new double[] {r, r, l, l});
                break;
            }
            case MECANUM: {
                double pivot = gamepad1.right_stick_x;
                double mX, mY;
                mX = gamepad1.left_stick_x;
                mY = gamepad1.left_stick_y;
                setMotorPowers(mult, new double[] {
                    mY - mX - pivot,
                    mY + mX - pivot,
                    mY + mX + pivot,
                    mY - mX + pivot});
                break;
            }
            }
        }
    }

    protected void setMotorPowers(double mult, double[] powers) {
        for (int i = 0; i < 4; i++) {
            motors[i].setPower(powers[i] * mult);
        }
    }
}
