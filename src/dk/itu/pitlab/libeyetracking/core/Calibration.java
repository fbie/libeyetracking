package dk.itu.pitlab.libeyetracking.core;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import processing.core.PVector;

import com.theeyetribe.client.GazeManager;
import com.theeyetribe.client.ICalibrationProcessHandler;
import com.theeyetribe.client.data.CalibrationResult;
import com.theeyetribe.client.data.CalibrationResult.CalibrationPoint;
import com.theeyetribe.client.data.Point2D;

import dk.itu.pitlab.libeyetracking.data.Conversion;

/**
 * A base implementation for calibrating the tracker using any kind of visuals.
 *
 * @author Florian Biermann, fbie@itu.dk
 */
public abstract class Calibration implements ICalibrationProcessHandler {

    final Random random;
    final GazeManager gaze;

    final List<Point2D> points;
    boolean firstPointTaken;

    Duration sampleTime;
    boolean calibrating;

    Runnable start;
    Runnable stop;

    protected Calibration() {
        random = new Random();
        gaze = GazeManager.getInstance();

        points = new ArrayList<Point2D>();
        firstPointTaken = false;

        sampleTime = Duration.ofMillis(500);
        calibrating = false;

        setOnStart(new Runnable() {
            @Override
            public void run() {
            }
        });
        setOnStop(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    public final void setSampleDuration(Duration duration) {
        sampleTime = duration;
    }

    @Override
    public synchronized void onCalibrationProcessing() {
        // TODO: Do we want to give the user some feedback on this?
    }

    @Override
    public final synchronized void onCalibrationProgress(double progress) {
        nextPoint();
    }

    /**
     * Calls the implementation of {@link Calibration#showPoint(Point2D, Runnable)} internally.
     *
     * @return True if there is another next point, false otherwise.
     */
    private final boolean nextPoint() {
        if (points.isEmpty())
            return false;

        final Point2D next;
        // The most obscure one-liner for
        // "if (!firstPointTaken) firstPointTaken = true;"
        // Usually I would not do this, but today I feel dangerous!
        if (!firstPointTaken == (firstPointTaken = true))
            next = points.remove(0);
        else
            next = points.remove(random.nextInt(points.size()));

        showPoint(next, new Runnable() {
            @Override
            public void run() {
                gaze.calibrationPointStart((int) next.x, (int) next.y);
                try {
                    Thread.sleep(sampleTime.toMillis());
                } catch (InterruptedException e) {
                    System.err.println("Warning: Sleep was interrupted during calibration.");
                }
                gaze.calibrationPointEnd();
            }
        });

        return true;
    }

    @Override
    public final synchronized void onCalibrationResult(CalibrationResult result) {
        if (result.result) {
            System.err.println("Calibration quality is " + resultToString(result) + ".");
            stop();
        } else {
            for (CalibrationPoint p : result.calibpoints) {
                if ((p.state & (CalibrationPoint.STATE_RESAMPLE | CalibrationPoint.STATE_NO_DATA)) != 0)
                    points.add(p.coordinates);
            }
            if (!nextPoint()) // We do not trust the server logic too much right now.
                stop();
        }
    }

    @Override
    public final synchronized void onCalibrationStarted() {
        nextPoint();
    }

    /**
     * Returns true if the system is currently calibrating.
     *
     * @return True if calibrating, false otherwise.
     */
    public synchronized boolean isCalibrating() {
        return calibrating;
    }

    /**
     * Set one or more points for the calibration to use, if the system is not currently calibrating. The point at index
     * 0 will always be taken first!
     *
     * @param points One or more points to use during calibration.
     * @return True if not currently calibrating, false otherwise.
     */
    public synchronized final boolean setPoints(Point2D... points) {
        if (!isCalibrating()) {
            this.points.clear();
            this.points.addAll(Arrays.asList(points));
            return true;
        }
        return false;
    }

    /**
     * Set points and start calibrating if not currently calibrating.
     *
     * @param points Points to use during calibration.
     * @return True if points were set and calibration has started, false otherwise.
     */
    public final boolean start(Point2D... points) {
        return setPoints(points) && startImpl();
    }

    /**
     * Set points and start calibrating if not currently calibrating.
     *
     * @param points Points to use during calibration.
     * @return True if points were set and calibration has started, false otherwise.
     */
    public final boolean start(dk.itu.pitlab.libeyetracking.data.Point2D... points) {
        Point2D[] np = new Point2D[points.length];
        for (int i = 0; i < points.length; ++i)
            np[i] = Conversion.toPoint2D(points[i]);
        return start(np);
    }

    /**
     * Set points and start calibrating if not currently calibrating.
     *
     * @param points Points to use during calibration.
     * @return True if points were set and calibration has started, false otherwise.
     */
    public final boolean start(PVector... points) {
        Point2D[] np = new Point2D[points.length];
        for (int i = 0; i < points.length; ++i)
            np[i] = Conversion.toPoint2D(points[i]);
        return start(np);
    }

    /**
     * Start calibration if not currently calibrating.
     *
     * @return True if calibration started, false otherwise.
     */
    private synchronized boolean startImpl() {
        if (isCalibrating())
            return false;
        calibrating = true;
        firstPointTaken = false;
        gaze.calibrationStart(this.points.size(), this);
        start.run();
        return true;
    }

    public synchronized final void setOnStart(Runnable start) {
        this.start = start;
    }

    /**
     * Stop calibration.
     */
    public synchronized void stop() {
        if (!points.isEmpty())
            gaze.calibrationAbort();
        calibrating = false;
        stop.run();
    }

    public synchronized final void setOnStop(Runnable stop) {
        this.stop = stop;
    }

    /**
     * Translate a result into a human readable string.
     *
     * @param result Result to translate
     * @return Calibration result in human terms.
     */
    public static String resultToString(CalibrationResult result) {
        if (result == null)
            return "error";
        else if (result.averageErrorDegree < 0.5)
            return "perfect";
        else if (result.averageErrorDegree < 0.7)
            return "good";
        else if (result.averageErrorDegree < 1)
            return "moderate";
        else if (result.averageErrorDegree < 1.5)
            return "poor";
        return "redo";
    }

    /**
     * Translate a result into a discrete value.
     *
     * @param result Result to translate
     * @return 0 means no calibration, 5 is best and 1 is worst.
     */
    public static int resultToInt(CalibrationResult result) {
        if (result == null)
            return 0;
        else if (result.averageErrorDegree < 0.5)
            return 5;
        else if (result.averageErrorDegree < 0.7)
            return 4;
        else if (result.averageErrorDegree < 1)
            return 3;
        else if (result.averageErrorDegree < 1.5)
            return 2;
        else
            return 1;
    }

    /**
     * Implementations of {@link Calibration#showPoint(Point2D, Runnable)} should show some visuals at the given point
     * to attract user's attention.
     *
     * @param point The point to show.
     * @param callback Callback to call when point is visible to the user.
     */
    public abstract void showPoint(Point2D point, Runnable callback);

    public final static boolean isCalibrated() {
        return GazeManager.getInstance().isCalibrated();
    }
}
