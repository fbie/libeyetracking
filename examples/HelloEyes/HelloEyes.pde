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
