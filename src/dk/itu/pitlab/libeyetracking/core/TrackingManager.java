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

 package dk.itu.pitlab.libeyetracking.core;

import java.util.ArrayList;
import java.util.List;

import com.theeyetribe.client.GazeManager;
import com.theeyetribe.client.GazeManager.ApiVersion;
import com.theeyetribe.client.GazeManager.ClientMode;
import com.theeyetribe.client.IGazeListener;
import com.theeyetribe.client.data.GazeData;

/**
 * This class wraps the TET client transparently to the user. Internally, it maintains the gaze manager singleton and
 * launders all gaze tracking data. Moreover, it dispatches the updates to registered listeners.
 *
 * @author Florian Biermann, fbie@itu.dk
 */
public class TrackingManager {

    final GazeManager gaze;
    final DataLaundry cache;
    final List<IGazeListener> listeners;

    IGazeListener qualityListener;

    public TrackingManager(int cacheFrames) {
        gaze = GazeManager.getInstance();
        cache = new DataLaundry(cacheFrames);
        listeners = new ArrayList<IGazeListener>();
    }

    /**
     * Initializes the TET client as demonstrated in the Java tutorial.
     *
     * @throws LegoGazeException If initialization was unsuccessful.
     */
    public synchronized void init() throws RuntimeException {
        if (!gaze.isActivated()) {
            if (!gaze.activate(ApiVersion.VERSION_1_0, ClientMode.PUSH))
                throw new RuntimeException("Could not activate TET gaze API.");
        }
        // Entirely enclose data update. Gaze updates
        // must be performed by LegoGazeManager and
        // the data thereby laundered.
        gaze.addGazeListener(new IGazeListener() {
            @Override
            public void onGazeUpdate(GazeData data) {
                synchronized (TrackingManager.this) {
                    // Propagate sample to quality listener if any is set.
                    if (TrackingManager.this.getGazeQualityListener() != null)
                        dispatchSafely(data, TrackingManager.this.getGazeQualityListener());
                    TrackingManager.this.onGazeUpdate(data);
                }
            }
        });
    }

    public synchronized String getVersionString() {
        if (gaze.isActivated())
            return gaze.getVersion().toString().replace("VERSION_", "").replace("_", ".");
        return "0.0";
    }

    public double getVersion() {
        return Double.parseDouble(getVersionString());
    }

    /**
     * Deactivates the TET client.
     */
    public synchronized void close() {
        if (gaze.isCalibrating())
            gaze.calibrationAbort();
        gaze.deactivate();
    }

    /**
     * Adds a new listener to the gaze manager.
     *
     * @param listener Listener to be added.
     */
    public synchronized void addListener(IGazeListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    /**
     * Dispatches laundered gaze events to the listeners.
     *
     * @param data New data point to be laundered and dispatched.
     */
    private void onGazeUpdate(GazeData data) {
        try {
            final GazeData smooth = cache.launder(data);
            if (smooth != null)
                for (final IGazeListener listener : listeners)
                    dispatchSafely(smooth, listener);
        } catch (Exception e) {
            System.err.println("Exception during onGazeUpdate(): " + e.getMessage());
        }
    }

    private void dispatchSafely(GazeData data, IGazeListener listener) {
        try {
            listener.onGazeUpdate(data);
        } catch (Exception e) {
            System.err.println("Exception during dispatchSafely(): " + e.getMessage());
        }
    }

    public synchronized void setGazeQualityListener(IGazeListener listener) {
        qualityListener = listener;
    }

    public synchronized IGazeListener getGazeQualityListener() {
        return qualityListener;
    }
}
