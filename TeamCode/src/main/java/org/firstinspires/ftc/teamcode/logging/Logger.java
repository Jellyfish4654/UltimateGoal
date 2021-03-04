package org.firstinspires.ftc.teamcode.logging;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import android.os.Environment;
import java.util.Random;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

/** Logs both to Telemetry and to the filesystem. */
public class Logger {
    Telemetry telemetry;
    FileLogger fileLogger;
    public static Logger instance;

    public Logger(Telemetry telemetry) {
        this.telemetry = telemetry;
        this.fileLogger = new FileLogger();
        instance = this;
    }

    public void addData(String label, Object o) {
        telemetry.addData(label, o);
        fileLogger.addData(label, o);
    }

    public void addData(String label, String format, Object... args) {
        this.addData(label, String.format(format, args));
    }

    public void addAssert(boolean condition, String msg) {
        if (!condition)
            this.addData("assert", msg);
    }

    public void update() {
        telemetry.update();

        Calendar now = Calendar.getInstance();
        String timestamp = String.format("%2d:%2d:%2d", now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE),
                now.get(Calendar.SECOND));
        fileLogger.addLine("=== " + timestamp + " UPDATE ===\n");
    }
}

class FileLogger {
    private OutputStream file;

    public FileLogger() {
        try {
            Calendar now = Calendar.getInstance();
            String dateString = String.format("%4d-%2d-%2d", now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
            File dir = new File(Environment.getExternalStorageDirectory(), ".ultimategoal-logs/" + dateString);
            dir.mkdirs();

            String random = Long.toString(new Random().nextLong(), 36);
            file = new FileOutputStream(new File(dir, random + ".txt"));
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean addLine(String line) {
        try {
            file.write(line.getBytes());
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public boolean addData(String label, Object object) {
        Calendar now = Calendar.getInstance();
        String line = String.format("%2d:%2d:%2d ", now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND));
        if (label.isEmpty()) {
            line += object.toString() + "\n";
        } else {
            line += label + ": " + object.toString() + "\n";
        }

        return addLine(line);
    }
}