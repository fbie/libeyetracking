package dk.itu.pitlab.libeyetracking.core;

import com.theeyetribe.client.data.GazeData;

/**
 * A laundromat for gaze data. Often, there are tracking errors occurring. This class caches the last n frames and
 * smoothes them, but never caches bad tracking data.
 *
 * @author Florian Biermann, fbie@itu.dk
 */
public class DataLaundry {

    private final GazeData cache[];
    private GazeData last;
    int head;

    public DataLaundry(int frames) {
        cache = new GazeData[frames];
        last = new GazeData();
        head = 0;
    }

    /**
     * Smoothes and launders data. If the tracking state is bad, the return value will be the last valid tracking data.
     * Otherwise, the good data will be returned.
     *
     * @param data Tracking data to launder and cache.
     * @return The laundered tracking data.
     */
    public synchronized GazeData launder(final GazeData data) {
        if (push(data) == null)
            return null;
        final GazeData smooth = Utils.emptyDataFrom(data);
        int n = 0;
        for (final GazeData sample : cache) {
            if (sample != null) {
                ++n;
                Utils.addTo(smooth, sample);
            }
        }
        this.last = Utils.divide(smooth, n);
        return getLastSmooth();
    }

    private synchronized GazeData push(GazeData data) {
        if (Utils.valid(data)) {
            head = (head + 1) % cache.length;
            return cache[head] = new GazeData(data);
        }
        return null;
    }

    /**
     * @return The last good but un-smoothed data point.
     */
    public synchronized GazeData getLast() {
        return cache[head] == null ? new GazeData() : new GazeData(cache[head]);
    }

    /**
     * @return The last smoothed data point.
     */
    public synchronized GazeData getLastSmooth() {
        return new GazeData(last);
    }

}