package org.firstinspires.ftc.teamcode.framework;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.teamcode.logging.Logger;

/**
 * Base class for all OpModes. Provides methods for initializing hardware.
 * */
public abstract class BaseOpMode extends LinearOpMode {
    protected DcMotor[] motors;
    protected BNO055IMU imu;
    protected Logger logger;

    protected void initHardware() {
        motors = new DcMotor[] {
            hardwareMap.dcMotor.get("motor fr"),
            hardwareMap.dcMotor.get("motor br"),
            hardwareMap.dcMotor.get("motor fl"),
            hardwareMap.dcMotor.get("motor bl")
        };

        for (DcMotor motor : motors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        logger = new Logger(telemetry);

        imu = hardwareMap.get(BNO055IMU.class, "imu");
    }
}