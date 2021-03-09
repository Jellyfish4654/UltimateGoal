package org.firstinspires.ftc.teamcode.framework;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.teamcode.logging.Logger;
import org.firstinspires.ftc.teamcode.framework.Motors;
import org.firstinspires.ftc.teamcode.framework.components.Drivetrain;

/**
 * Base class for all OpModes. Provides methods for initializing hardware.
 * */
public abstract class BaseOpMode extends LinearOpMode {
    protected Drivetrain drivetrain;
    protected BNO055IMU imu;

    protected Logger logger;

    protected void initHardware() {
        drivetrain = new Drivetrain(new DcMotor[] {
            hardwareMap.dcMotor.get("motor fr"),
            hardwareMap.dcMotor.get("motor br"),
            hardwareMap.dcMotor.get("motor fl"),
            hardwareMap.dcMotor.get("motor bl")
        });
        drivetrain.motors[Motors.FR].setDirection(DcMotorSimple.Direction.FORWARD);
        drivetrain.motors[Motors.BR].setDirection(DcMotorSimple.Direction.FORWARD);
        drivetrain.motors[Motors.FL].setDirection(DcMotorSimple.Direction.REVERSE);
        drivetrain.motors[Motors.BL].setDirection(DcMotorSimple.Direction.REVERSE);
        drivetrain.reset();

        logger = new Logger(telemetry);
        imu = hardwareMap.get(BNO055IMU.class, "imu");
    }
}