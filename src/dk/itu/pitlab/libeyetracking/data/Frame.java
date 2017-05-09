/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Florian Biermann (fbie@itu.dk) and others.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

 package dk.itu.pitlab.libeyetracking.data;

import com.theeyetribe.client.data.GazeData;

import dk.itu.pitlab.libeyetracking.core.Utils;

/**
 * A single, immutable frame describing the eye tracking state at some
 * point in time.
 *
 * It contains coordinates for the current gaze point on the screen in
 * pixels, the average position of the eyes, projected onto the screen
 * in pixels, the inter-pupillary distance in pixels and the head
 * roll, based on the position of both eyes, in degree.
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
     * Construct a Frame instance from a TET GazeData
     * instance. <code>data</code> contains much information which we
     * do not really require on a day-to-day basis. Moreover, it is
     * mutable, but for making things safe, we want an immutable type
     * instead. This static method also computes the convenience data,
     * like IDP, roll and so on.
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
