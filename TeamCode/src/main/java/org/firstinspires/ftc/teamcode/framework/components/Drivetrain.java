package org.firstinspires.ftc.teamcode.framework.components;

import java.util.List;
import java.util.ArrayList;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Represents a drivetrain.
 */
public class Drivetrain {
    // estimate, tbd
    public final static double MAX_SPEED = 1.5;

    private class Readings {
        public long time;
        public double[] readings;

        public Readings(double[] readings, long time) {
            this.readings = readings;
            this.time = time;
        }
    }

    protected DcMotor[] motors;
    public double[] encoderPositions() {
        double[] readingsArr = new double[4];
        for (int i = 0; i < 4; i++) {
            readingsArr[i] = motors[i].getCurrentPosition();
        }
        return readingsArr;
    }

    /**
     * Array of length 2 whose first element
     * is the proportional component and whose second element
     * is the integral component.
     * 
     * Both numbers should be small (<0.2)
     */
    public double[] pid;

    protected List<Readings> readings;
    protected double[] targetSpeeds;

    public Drivetrain(DcMotor[] motors) {
        this.readings = new ArrayList<Readings>();
        this.motors = motors;
        this.pid = new double[]{0.1, 0.1};
    }

    /**
     * Sets the speed of the function.
     * @param speeds The target speed in encoder ticks per second.
     */
    public void setSpeeds(double[] speeds) {
        for (int i = 0; i < 4; i++) {
            if (speeds[i] == 0) {
                motors[i].setPower(0);
            }
        }
        this.targetSpeeds = speeds;
    }

    /**
     * Temporary testing function.
     */
    public void _setPowers(double[] powers) {
        targetSpeeds = null;
        for (int i = 0; i < 4; i++) {
            motors[i].setPower(powers[i]);
        }
    }

    private void addReading() {
        Readings readingsItem = new Readings(encoderPositions(), System.currentTimeMillis());
        readings.add(readingsItem);
        while (readings.size() > 10) {
            readings.remove(0);
        }
    }

    /**
     * Call this function in your main loop.
     * It automatically adjusts motor speeds.
     */
    public void update() {
        if (this.targetSpeeds == null) return;

        addReading();

        for (int i = 0; i < 4; i++) {
            double[] errors = new double[readings.size() - 1];

            for (int j = 0; j < readings.size() - 2; j++) {
                Readings lastReading = readings.get(j);
                Readings currentReading = readings.get(j+1);
                long timeDiff = currentReading.time - lastReading.time;

                double targetPosition = lastReading.readings[i] + targetSpeeds[i] * timeDiff/1000;
                errors[j] = targetPosition - currentReading.readings[i];
            }

            double integral = 0;
            for (double error : errors) { 
                integral += error;
            }

            double correction = 
                pid[0] * errors[errors.length-1] + // P
                pid[1] * integral / errors.length;
            
            double power = motors[i].getPower() + correction;
            motors[i].setPower(power);
        }
    }

    /**
     * Resets all encoder readings.
     */
    public void reset() {
        for (DcMotor encoder: motors) {
            encoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            encoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        readings.clear();
    }
}