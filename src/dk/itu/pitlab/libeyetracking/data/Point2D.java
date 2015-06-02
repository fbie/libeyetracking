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
 * An immutable point in 2D.
 *
 * This class overrides the <code>equals()</code> method. Two instances of Point2D are logically equals if their two
 * corresponding components do not differ more than 10e-4.
 *
 * @author Florian Biermann, fbie@itu.dk
 */
public class Point2D {

    public final double x, y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2D(PVector v) {
        this(v.x, v.y);
    }

    /**
     * @return A mutable PVector from this immutable Point2D instance.
     */
    public PVector toPVector() {
        return Conversion.toPVector(this);
    }

    public String toString() {
        return String.format("(%f, %f)", x, y);
    }

    /**
     * Add two immutable points, producing a new point.
     * 
     * @param other The point to add to <code>this</code>.
     * @return A new instance of Point2D.
     */
    public Point2D add(Point2D other) {
        return new Point2D(this.x + other.x, this.y + other.y);
    }

    /**
     * Subtract <code>other</code> from this vector, producing a new point.
     * 
     * @param other The point to subtract from <code>this</this>.
     * @return A new instance of Point2D.
     */
    public Point2D sub(Point2D other) {
        return new Point2D(this.x + other.x, this.y + other.y);
    }

    /**
     * Multiply the vector by k.
     * 
     * @param k The factor to multiply the vector with.
     * @return A new instance of Point2D.
     */
    public Point2D mul(double k) {
        return new Point2D(this.x * k, this.y * k);
    }

    /**
     * Divide the vector by k.
     * 
     * @param k The factor to divide the vector with.
     * @return A new instance of Point2D.
     */
    public Point2D div(double k) {
        return this.mul(1d / k);
    }

    /**
     * Compute the dot product between this vector and <code>other</code>.
     * 
     * @param other The second vector to compute the dot product for.
     * @return The dot product of both points.
     */
    public double dot(Point2D other) {
        return Math.sqrt(this.x * other.x + this.y * other.y);
    }

    /**
     * Compute the length of this point. This method uses the {@link Point2D.dot} method internally, calling
     * <code>this.dot(this)</code>.
     * 
     * @return The length of the vector.
     */
    public double length() {
        return dot(this);
    }

    private static Point2D zero = new Point2D(0d, 0d);

    /**
     * The point at origin (0,0). There exists only a single static instance of origin.
     * 
     * @return The point at origin.
     */
    public static Point2D getZero() {
        return zero;
    }

    private static double epsilon = 10e-4;

    @Override
    public boolean equals(Object o) {
        if (o instanceof Point2D) {
            Point2D other = (Point2D) o;
            return Math.abs(this.x - other.x) < epsilon && Math.abs(this.y - other.y) < epsilon;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 127;
        long hx = Double.doubleToLongBits(x);
        long hy = Double.doubleToLongBits(y);
        hash = 91 * hash + (int) (hx ^ (hx >>> 32));
        hash = 91 * hash + (int) (hy ^ (hy >>> 32));
        return hash;
    }
}
