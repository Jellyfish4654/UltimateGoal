package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.framework.BaseOpMode;
import org.firstinspires.ftc.teamcode.framework.Motors;
import org.firstinspires.ftc.teamcode.framework.components.Drivetrain;

@TeleOp(name = "JelleTest")
public class JelleTest extends BaseOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        initHardware();
        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.b) {
                drivetrain._setPowers(new double[]{1,1,1,1});
            } else if (gamepad1.a) {
                double s = 0.5*Drivetrain.MAX_SPEED;
                drivetrain.setSpeeds(new double[]{s,s,s,s});
            } else {
                drivetrain.setSpeeds(new double[] {
                    gamepad1.dpad_up ? 0.2*Drivetrain.MAX_SPEED : 0, // FR
                    gamepad1.dpad_right ? 0.2*Drivetrain.MAX_SPEED : 0, // BR
                    gamepad1.dpad_left ? 0.2*Drivetrain.MAX_SPEED : 0, // FL
                    gamepad1.dpad_down ? 0.2*Drivetrain.MAX_SPEED : 0 // BL
                });
            }

            drivetrain.update();
        }
    }
}