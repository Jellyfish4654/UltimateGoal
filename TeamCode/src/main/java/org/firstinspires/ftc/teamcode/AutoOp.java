package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.framework.BaseOpMode;
import org.firstinspires.ftc.teamcode.framework.Motors;
import org.firstinspires.ftc.teamcode.framework.components.drivetrain.Drivetrain;
import org.firstinspires.ftc.teamcode.framework.components.drivetrain.Mecanum2;

@Autonomous(name = "UltimateGoal Auto")
public class AutoOp extends BaseOpMode {
    // 28 ticks / internal revolution * 12 internal revolution / 1 external revolution * 2 external revolution / 1 wheel revolutions
    private final static double TICKS_PER_REV = 28 * 12 * 2;

    // ticks / revolution * 1 revolution / (circumfrence) inches
    private final static double TICKS_PER_IN = TICKS_PER_REV / (4 * Math.PI);

    Drivetrain dt;
    Drivetrain.Params defaultMoveParams, defaultPivotParams;

    @Override
    public void runOpMode() throws InterruptedException {
        initHardware();

        dt = new Mecanum2(motors, imu);
        defaultMoveParams = Drivetrain.Params.moveParams(TICKS_PER_IN);
        defaultPivotParams = Drivetrain.Params.pivotParams();

        waitForStart();

        dt.reset();
        while (dt.move(5 * TICKS_PER_IN, 30, defaultMoveParams));
        dt.stop();

        try {
            Thread.sleep(5000);
        } catch(InterruptedException err) {
            logger.addData("error", "sleep thread interrupted");
            logger.update();
        }

        dt.reset();
        while (dt.pivot(Math.PI / 2, defaultPivotParams));
        dt.stop();
    }
}