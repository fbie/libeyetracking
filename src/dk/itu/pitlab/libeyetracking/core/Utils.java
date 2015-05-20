package dk.itu.pitlab.libeyetracking.core;

import com.theeyetribe.client.GazeManager;
import com.theeyetribe.client.GazeUtils;
import com.theeyetribe.client.data.GazeData;
import com.theeyetribe.client.data.GazeData.Eye;
import com.theeyetribe.client.data.Point2D;

public class Utils {

    private static void assertNonNull(GazeData data) {
        assert data != null;
        assert data.isFixated != null;
        assertNonNull(data.leftEye);
        assert data.rawCoordinates != null;
        assertNonNull(data.rightEye);
        assert data.smoothedCoordinates != null;
        assert data.state != null;
        assert data.timeStamp != null;
        assert data.timeStampString != null;
    }

    private static void assertNonNull(Eye eye) {
        assert eye != null;
        assert eye.pupilCenterCoordinates != null;
        assert eye.pupilSize != null;
        assert eye.rawCoordinates != null;
        assert eye.smoothedCoordinates != null;
    }

    /**
     * Checks if the passed gaze data is valid.
     *
     * @param data Sample to check for validity.
     * @return True if the tracker tracks eye or gaze, false otherwise.
     */
    public static boolean valid(GazeData data) {
        assertNonNull(data);
        return valid(data.state);
    }

    public static boolean valid(int state) {
        return (state & (GazeData.STATE_TRACKING_EYES | GazeData.STATE_TRACKING_GAZE)) != 0;
    }

    /**
     * Compute the center of eye coordinates in pixels.
     *
     * @param data The sample to compute center for.
     * @return The center as a com.theeyetribe.client.data.Point2D
     */
    public static Point2D getEyesCenterPixel(GazeData data) {
        return GazeUtils.getEyesCenterPixels(data, GazeManager.getInstance().getScreenResolutionWidth(), GazeManager
                .getInstance().getScreenResolutionHeight());
    }

    /**
     * Compute the head roll for some data sample.
     * 
     * @param data Sample to compute head roll for.
     * @return The head roll in [-90, 90].
     * @author Diako Mardanbegi, dima@itu.dk
     */
    public static double getHeadRoll(GazeData data) {
        Point2D delta = data.leftEye.pupilCenterCoordinates.subtract(data.rightEye.pupilCenterCoordinates);
        double roll = ((Math.toDegrees(Math.atan2(delta.y, delta.x)) + 360) % 360) - 180;
        if (Math.abs(roll) > 90)
            return 0;
        return roll;
    }

    /**
     * Compute the inter-pupillary distance (IPD) between the currently tracked eyes.
     * 
     * @param data Sample to compute IPD for.
     * @return An IPD in pixels.
     */
    public static double getIpd(GazeData data) {
        Point2D delta = data.leftEye.pupilCenterCoordinates.subtract(data.rightEye.pupilCenterCoordinates);
        return Math.sqrt(Math.pow(delta.x, 2) + Math.pow(delta.y, 2));
    }

    /**
     * Build an empty sample point, copying fixation and state from a source.
     *
     * @param from The source to copy fixation and state from
     * @return An otherwise empty GazeData sample.
     */
    public static GazeData emptyDataFrom(GazeData from) {
        final GazeData data = new GazeData();
        assertNonNull(data);
        data.isFixated = from.isFixated;
        data.state = from.state;
        return data;
    }

    /**
     * Adds two GazeData's values in-place!
     *
     * @param target Values will be written here.
     * @param other Values to add to target.
     * @return A reference to target.
     */
    public static GazeData addTo(GazeData target, GazeData other) {
        assertNonNull(target);
        assertNonNull(other);
        target.rawCoordinates = target.rawCoordinates.add(other.rawCoordinates);
        target.smoothedCoordinates = target.smoothedCoordinates.add(other.smoothedCoordinates);
        target.leftEye = addTo(target.leftEye, other.leftEye);
        target.rightEye = addTo(target.rightEye, other.rightEye);
        return target;
    }

    /**
     * Adds two Eye's values in-place!
     *
     * @param target Values will be written here.
     * @param other Values to add to target.
     * @return A reference to target.
     */
    public static Eye addTo(Eye target, Eye other) {
        target.pupilCenterCoordinates = target.pupilCenterCoordinates.add(other.pupilCenterCoordinates);
        target.pupilSize += other.pupilSize;
        target.rawCoordinates = target.rawCoordinates.add(other.rawCoordinates);
        target.smoothedCoordinates = target.smoothedCoordinates.add(other.smoothedCoordinates);
        return target;
    }

    /**
     * Divides a GazeData sample by k in-place!
     *
     * @param data Sample to divide.
     * @param k Value to divide by.
     * @return The divided sample.
     */
    public static GazeData divide(GazeData data, double k) {
        assertNonNull(data);
        data.rawCoordinates = data.rawCoordinates.divide(k);
        data.smoothedCoordinates = data.smoothedCoordinates.divide(k);
        divide(data.leftEye, k);
        divide(data.rightEye, k);
        return data;
    }

    /**
     * Divides an Eye sample by k in-place!
     *
     * @param data Sample to divide.
     * @param k Value to divide by.
     * @return The divided sample.
     */
    public static Eye divide(Eye eye, double k) {
        eye.pupilCenterCoordinates = eye.pupilCenterCoordinates.divide(k);
        eye.pupilSize /= k;
        eye.rawCoordinates = eye.rawCoordinates.divide(k);
        eye.smoothedCoordinates = eye.smoothedCoordinates.divide(k);
        return eye;
    }

}
