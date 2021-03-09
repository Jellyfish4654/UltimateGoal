package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.framework.BaseOpMode;
import org.firstinspires.ftc.teamcode.framework.Motors;

@TeleOp(name = "JelleTest")
public class JelleTest extends BaseOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        initHardware();
        waitForStart();

        while (opModeIsActive()) {
            motors[Motors.BR].setPower(gamepad1.a ? 0.2 : 0);
            motors[Motors.BL].setPower(gamepad1.x ? 0.2 : 0);
            motors[Motors.FL].setPower(gamepad1.y ? 0.2 : 0);
            motors[Motors.FR].setPower(gamepad1.b ? 0.2 : 0);
        }
    }
}