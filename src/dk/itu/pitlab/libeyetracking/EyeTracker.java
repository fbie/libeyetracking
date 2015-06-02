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

package dk.itu.pitlab.libeyetracking;

import processing.core.PApplet;
import processing.core.PVector;

import com.theeyetribe.client.IGazeListener;
import com.theeyetribe.client.data.GazeData;

import dk.itu.pitlab.libeyetracking.core.TrackingManager;
import dk.itu.pitlab.libeyetracking.core.Utils;
import dk.itu.pitlab.libeyetracking.data.Frame;

/**
 * A listener that converts GazeData instances into immutable instances of Frame.
 *
 * @author Florian Biermann, fbie@itu.dk
 */
class FrameListener implements IGazeListener {

    private Frame last;

    public FrameListener() {
        last = Frame.getZero();
    }

    @Override
    public synchronized void onGazeUpdate(GazeData data) {
        last = Frame.fromGazeData(data);
    }

    /**
     * @return The last smoothed gaze frame.
     */
    public synchronized Frame gazeFrame() {
        return last;
    }
}

/**
 * A listener that checks for validity of GazeData instances and stores the last validity for later access.
 *
 * @author Florian Biermann, fbie@itu.dk
 */
class RawListener implements IGazeListener {

    private boolean isTracking;

    public RawListener() {
        isTracking = false;
    }

    @Override
    public synchronized void onGazeUpdate(GazeData data) {
        isTracking = Utils.valid(data);
    }

    /**
     * @return True if the eye tracker is tracking something, false otherwise.
     */
    public synchronized boolean isTracking() {
        return isTracking;
    }
}

/**
 * Encapsulates the state of the tracker by providing the last frame and the last state (i.e. tracking or not).
 *
 * @author Florian Biermann, fbie@itu.dk
 */
class State {

    private final RawListener raw;
    private final FrameListener state;

    public State() {
        raw = new RawListener();
        state = new FrameListener();
    }

    /**
     * Add listeners and initialize manager.
     *
     * @param man The manager to initialize and to listen to.
     */
    public void init(TrackingManager man) {
        man.addListener(state);
        man.setGazeQualityListener(raw);
        man.init();
    }

    /**
     * @return True if the eye tracker is tracking something, false otherwise.
     */
    public boolean isTracking() {
        return raw.isTracking();
    }

    /**
     * @return The last smoothed gaze frame.
     */
    public Frame gazeFrame() {
        return state.gazeFrame();
    }
}

/**
 * A library for Processing that makes building eye tracking applications easy.
 *
 * @author Florian Biermann, fbie@itu.dk
 */
public class EyeTracker {

    private final TrackingManager man;
    private final State state;

    public EyeTracker(PApplet parent) {
        this(parent, 3);
    }

    public EyeTracker(PApplet parent, int cacheFrames) {
        parent.registerMethod("dispose", this);
        man = new TrackingManager(cacheFrames);
        state = new State();
        state.init(man);
    }

    public void dispose() {
        man.close();
    }

    /**
     * @return The last smoothed gaze frame.
     */
    public Frame gazeFrame() {
        return state.gazeFrame();
    }

    /**
     * @return True if the eye tracker is tracking something, false otherwise.
     */
    public boolean isTracking() {
        return state.isTracking();
    }

    /**
     * @return The last smoothed gaze coordinates.
     */
    public PVector gazeCoords() {
        return gazeFrame().getGazePoint().toPVector();
    }

    /**
     * @return The last smoothed eye coordinates.
     */
    public PVector eyeCenter() {
        return gazeFrame().getEyeCenter().toPVector();
    }

    /**
     * @return The last smoothed inter-pupillary distance in pixels.
     */
    public float ipd() {
        return gazeFrame().getIpd();
    }

    /**
     * @return The last smoothed head-roll based on eye positions in degree.
     */
    public float roll() {
        return gazeFrame().getRoll();
    }
}
