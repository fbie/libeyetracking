# Eye Tracking for Processing

This is a small wrapper for [The Eye Tribe Java Client](https://github.com/EyeTribe/tet-java-client) for [Processing](http://processing.org). Hopefully, this will make eye tracking more accessible for rapid prototyping. You'll need a The Eye Tribe tracker and the accompanying software package (i.e. The Eye Tribe server).

More information on these can be found at [theeyetribe.com](http://theeyetribe.com).

### Getting the library

You can download a build of the library [here](https://github.com/fbie/libeyetracking/blob/master/packages/libeyetracking.zip?raw=true). You can also build it yourself:

```
$ cd libeyetracking/resources
$ ant
```

### A simple example

After you have calibrated the tracker using The Eye Tribe GUI, you can simply run this small sketch in order to get a feeling for how eye tracking works (located in ```examples/HelloEyes```):

``` {.Processing}
import dk.itu.pitlab.libeyetracking.*;

EyeTracker tracker;

public void setup() {
  size(displayWidth, displayHeight);
  background(0);
  tracker = new EyeTracker(this);
  smooth();
}

public void draw() {
  clear();
  noFill();
  strokeWeight(10);
  if (tracker.isTracking())
    stroke(0, 255, 0);
  else
    stroke(255, 0, 0);
  PVector c = tracker.gazeCoords();
  ellipse(c.x, c.y, 50, 50);
}
```


### Misc.

This repository is based on the [Processing library template](https://github.com/processing/processing-library-template).
