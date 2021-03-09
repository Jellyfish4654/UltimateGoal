package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.framework.BaseOpMode;
import org.firstinspires.ftc.teamcode.framework.Motors;
import org.firstinspires.ftc.teamcode.framework.components.autodrivetrain.AutoDrivetrain;
import org.firstinspires.ftc.teamcode.framework.components.autodrivetrain.Mecanum2;

@Autonomous(name = "UltimateGoal Auto")
public class AutoOp extends BaseOpMode {
    // 28 ticks / internal revolution * 24 wheel revolution / 1 external revolution * 4/3 (magic number)
    private final static double TICKS_PER_REV = 28 * 24 * 4/3;

    // ticks / revolution * 1 revolution / (circumfrence) inches
    private final static double TICKS_PER_IN = TICKS_PER_REV / (4 * Math.PI);

    AutoDrivetrain dt;
    AutoDrivetrain.Params defaultMoveParams, defaultPivotParams;

    @Override
    public void runOpMode() throws InterruptedException {
        initHardware();

        dt = new Mecanum2(drivetrain.motors, imu);
        defaultMoveParams = AutoDrivetrain.Params.moveParams(TICKS_PER_REV);
        defaultPivotParams = AutoDrivetrain.Params.pivotParams();
        defaultMoveParams.maxPower = 0.4;
        defaultMoveParams.minPower = 0.3;

        waitForStart();

        dt.reset();
        while (opModeIsActive() && dt.move((5 + 1/12) * TICKS_PER_REV, Math.PI / 2, defaultMoveParams));
        dt.stop();
/*
        try {
            Thread.sleep(5000);
        } catch(InterruptedException err) {
            logger.addData("error", "sleep thread interrupted");
            logger.update();
        }

        dt.reset();
        while (dt.pivot(Math.PI / 2, defaultPivotParams));
        dt.stop();*/
    }
}