# Eye Tracking for Processing

This is a small wrapper for [The Eye Tribe Java Client](https://github.com/EyeTribe/tet-java-client) for [Processing](http://processing.org). Hopefully, this will make eye tracking more accessible for rapid prototyping. You'll need a The Eye Tribe tracker and the accompanying software package (i.e. The Eye Tribe server).

More information on these can be found at [theeyetribe.com](http://theeyetribe.com).

### Getting the library

You can download a build of the library
[here](https://github.com/fbie/libeyetracking/blob/master/packages/libeyetracking.zip?raw=true). You
can also build it yourself:

```
$ cd libeyetracking/resources
$ ant
```

### A simple example

After you have calibrated the tracker using The Eye Tribe GUI, you can
simply run this small sketch in order to get a feeling for how eye
tracking works (located in
[```examples/HelloEyes```](https://github.com/fbie/libeyetracking/blob/master/examples/HelloEyes/HelloEyes.pde)):

``` {.Processing}
// -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil-*-
import dk.itu.pitlab.libeyetracking.*;

EyeTracker tracker;

public void setup() {
  size(displayWidth, displayHeight);
  background(0);
  ellipseMode(CENTER);
  smooth();
  tracker = new EyeTracker(this);
}

public void draw() {
  clear();
  noFill();
  strokeWeight(10);

  // Circle is green when tracking, red otherwise.
  if (tracker.isTracking())
    stroke(0, 255, 0);
  else
    stroke(255, 0, 0);

  // Counter arm rotation by rotating gaze coordinates
  // into the opposite direction.
  PVector c = tracker.gazeCoords();
  c.rotate(-radians(tracker.roll()));

  // Scale circle by inter-pupillary distance.
  float ipd = tracker.ipd();
  float ipd2 = ipd * ipd * ipd * 10000;
  ellipse(c.x, c.y, ipd2, ipd2);
}
```


### Misc.

This repository is based on the [Processing library template](https://github.com/processing/processing-library-template).
