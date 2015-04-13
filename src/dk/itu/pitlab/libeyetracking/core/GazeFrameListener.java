package dk.itu.pitlab.libeyetracking.core;

import dk.itu.pitlab.libeyetracking.data.Frame;

public interface GazeFrameListener {

    public void onFrameUpdate(Frame frame);

}
