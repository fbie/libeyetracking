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
