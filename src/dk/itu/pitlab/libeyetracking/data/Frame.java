package dk.itu.pitlab.libeyetracking.data;

import com.theeyetribe.client.data.GazeData;

import dk.itu.pitlab.libeyetracking.core.Utils;

/**
 * A single, immutable frame describing the eye tracking state at some point in time.
 *
 * It contains coordinates for the current gaze point on the screen in pixels, the average position of the eyes,
 * projected onto the screen in pixels, the inter-pupillary distance in pixels and the head roll, based on the position
 * of both eyes, in degree.
 *
 * @author Florian Biermann, fbie@itu.dk
 */
public class Frame {

    private final Point2D gazePoint, eyeCenter;
    private final float ipd, roll;

    public Frame(Point2D gazePoint, Point2D eyeCenter, float idp, float roll) {
        this.gazePoint = gazePoint;
        this.eyeCenter = eyeCenter;
        this.ipd = idp;
        this.roll = roll;
    }

    /**
     * @return The current gaze coordinates in pixels.
     */
    public Point2D getGazePoint() {
        return gazePoint;
    }

    /**
     * @return The current average eye position projected onto the screen, in pixels.
     */
    public Point2D getEyeCenter() {
        return eyeCenter;
    }

    /**
     * @return The inter-pupillary distance in pixels.
     */
    public float getIpd() {
        return ipd;
    }

    /**
     * @return The current head roll, based on the position of both eyes, in degree.
     */
    public float getRoll() {
        return roll;
    }

    private static Frame zero = new Frame(Point2D.getZero(), Point2D.getZero(), 0f, 0f);

    /**
     * Return the empty frame. There is only one static instance of the empty frame.
     *
     * @return The empty frame.
     */
    public static Frame getZero() {
        return zero;
    }

    /**
     * Construct a Frame instance from a TET GazeData instance. <code>data</code> contains much information which we do
     * not really require on a day-to-day basis. Moreover, it is mutable, but for making things safe, we want an
     * immutable type instead. This static method also computes the convenience data, like IDP, roll and so on.
     *
     * @param data The original, mutable frame.
     * @return An immutable frame.
     */
    public static Frame fromGazeData(GazeData data) {
        if (data == null)
            return zero;
        return new Frame(
                Conversion.toImmutable(data.smoothedCoordinates),
                Conversion.toImmutable(Utils.getEyesCenterPixel(data)),
                (float) Utils.getIpd(data),
                (float) Utils.getHeadRoll(data));
    }
}
