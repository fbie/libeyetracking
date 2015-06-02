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

 package dk.itu.pitlab.libeyetracking.data;

import processing.core.PVector;

/**
 * A collection of conversion functions.
 * 
 * @author Florian Biermann, fbie@itu.dk
 */
public class Conversion {

    /**
     * Convert a mutable TET Point2D into a custom, immutable Point2D.
     * 
     * @param p The original, mutable point.
     * @return The new, immutable point.
     */
    public static Point2D toImmutable(com.theeyetribe.client.data.Point2D p) {
        return new Point2D((float) p.x, (float) p.y);
    }

    /**
     * Convert a custom, immutable Point2D into a mutable PVector.
     * 
     * @param p The custom, immutable point.
     * @return The mutable PVector.
     */
    public static PVector toPVector(Point2D p) {
        return new PVector((float) p.x, (float) p.y);
    }

    /**
     * Convert a mutable TET Point2D into a mutable PVector.
     * 
     * @param p The original, mutable point.
     * @return The mutable PVector.
     */
    public static PVector toPVector(com.theeyetribe.client.data.Point2D p) {
        return new PVector((float) p.x, (float) p.y);
    }

    public static com.theeyetribe.client.data.Point2D toPoint2D(Point2D p) {
        return new com.theeyetribe.client.data.Point2D(p.x, p.y);
    }

    public static com.theeyetribe.client.data.Point2D toPoint2D(PVector p) {
        return new com.theeyetribe.client.data.Point2D(p.x, p.y);
    }
}
