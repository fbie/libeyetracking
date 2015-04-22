/**
 * ##library.name##
 * ##library.sentence##
 * ##library.url##
 *
 * Copyright ##copyright## ##author##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 *
 * @author      ##author##
 * @modified    ##date##
 * @version     ##library.prettyVersion## (##library.version##)
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
 * A listener that converts GazeData instances
 * into immutable instances of Frame.
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
 * A listener that checks for validity of
 * GazeData instances and stores the last
 * validity for later access.
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
 * Encapsulates the state of the tracker by
 * providing the last frame and the last
 * state (i.e. tracking or not).
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
 * A library for Processing that makes building
 * eye tracking applications easy.
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
    public double ipd() {
        return gazeFrame().getIpd();
    }

    /**
     * @return The last smoothed head-roll based on eye positions in degree.
     */
    public double roll() {
        return gazeFrame().getRoll();
    }
}
