package dk.itu.pitlab.libeyetracking.data;

import processing.core.PVector;

/**
 * A collection of conversion functions.
 * @author Florian Biermann, fbie@itu.dk
 */
public class Conversion {

    /**
     * Convert a mutable TET Point2D into a custom, immutable Point2D.
     * @param p The original, mutable point.
     * @return The new, immutable point.
     */
    public static Point2D toImmutable(com.theeyetribe.client.data.Point2D p) {
        return new Point2D((float)p.x, (float)p.y);
    }

    /**
     * Convert a custom, immutable Point2D into a mutable PVector.
     * @param p The custom, immutable point.
     * @return The mutable PVector.
     */
    public static PVector toPVector(Point2D p) {
        return new PVector((float)p.x, (float)p.y);
    }

    /**
     * Convert a mutable TET Point2D into a mutable PVector.
     * @param p The original, mutable point.
     * @return The mutable PVector.
     */
    public static PVector toPVector(com.theeyetribe.client.data.Point2D p) {
        return new PVector((float)p.x, (float)p.y);
    }
}
