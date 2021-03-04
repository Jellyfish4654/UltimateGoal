package org.firstinspires.ftc.teamcode.framework.components.drivetrain;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Base interface for auto Drivetrain. All
 * positions/magnitudes are in encoder ticks.
 */
public interface Drivetrain {
    public static class Params {
        /** Position where robot stops ramping up */
        public double rampUp;
        /** Position from the end where robot starts ramping down */
        public double rampDown;
        /** Position from the end where robot stops ramping down (at min power) */
        public double rampDownEnd;

        /** Maximum motor power, from 0.0 to 1.0 */
        public double maxPower;
        /** Minimum motor power, from 0.0 to 1.0 */
        public double minPower;

        /** Amount of time to spend correcting, in milliseconds. */
        public double correctionTime;

        public double allowableDistanceError;

        public Params() {
            this.maxPower = 0.9;
            this.minPower = 0.3;
            this.correctionTime = 100;
        }

        public static Params moveParams(double ticksPerIn) {
            Params p = new Params();
            p.rampUp = 1 * ticksPerIn;
            p.rampDown = 10 * ticksPerIn;
            p.rampDownEnd = 3 * ticksPerIn;
            p.allowableDistanceError = 0.2 * ticksPerIn;
            return p;
        }

        public static Params pivotParams() {
            Params p = new Params();
            p.rampUp = 5 * Math.PI / 180;
            p.rampDown = 10 * Math.PI / 180;
            p.rampDownEnd = 5 * Math.PI / 180;
            p.allowableDistanceError = 2 * Math.PI / 180;
            return p;
        }
    }
    
    /**
     * Moves the robot to the desired location. Continue
     * calling this function until it returns false.
     * 
     * @param magnitude magnitude to travel in encoder ticks
     * @param angle direction in radians
     * @return true if move is incomplete
     */
    boolean move(double magnitude, double angle, Params params);

    /**
     * Pivots the robot to the desired angle. Continue
     * calling this function until it returns false.
     *
     * @param angle angle to pivot in radians. positive angle = counterclockwise; negative angle = clockwise
     * @return true if pivot is incomplete
     */
    boolean pivot(double angle, Params params);

    /**
     * Stops all motors.
     */
    void stop();

    /**
     * Resets encoder positions. This should be called before the first call to pivot or move.
     */
    void reset();

    /**
     * Resets all given motors.
     */
    static void resetEncoders(DcMotor[] encoders) {
        for (DcMotor encoder: encoders) {
            encoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            encoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }
}