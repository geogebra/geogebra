/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math.geometry;

import java.io.Serializable;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.MathUtils;
import org.apache.commons.math.util.FastMath;

/**
 * This class implements vectors in a three-dimensional space.
 * <p>Instance of this class are guaranteed to be immutable.</p>
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 * @since 1.2
 */

public class Vector3D
  implements Serializable {

  /** Null vector (coordinates: 0, 0, 0). */
  public static final Vector3D ZERO   = new Vector3D(0, 0, 0);

  /** First canonical vector (coordinates: 1, 0, 0). */
  public static final Vector3D PLUS_I = new Vector3D(1, 0, 0);

  /** Opposite of the first canonical vector (coordinates: -1, 0, 0). */
  public static final Vector3D MINUS_I = new Vector3D(-1, 0, 0);

  /** Second canonical vector (coordinates: 0, 1, 0). */
  public static final Vector3D PLUS_J = new Vector3D(0, 1, 0);

  /** Opposite of the second canonical vector (coordinates: 0, -1, 0). */
  public static final Vector3D MINUS_J = new Vector3D(0, -1, 0);

  /** Third canonical vector (coordinates: 0, 0, 1). */
  public static final Vector3D PLUS_K = new Vector3D(0, 0, 1);

  /** Opposite of the third canonical vector (coordinates: 0, 0, -1).  */
  public static final Vector3D MINUS_K = new Vector3D(0, 0, -1);

  // CHECKSTYLE: stop ConstantName
  /** A vector with all coordinates set to NaN. */
  public static final Vector3D NaN = new Vector3D(Double.NaN, Double.NaN, Double.NaN);
  // CHECKSTYLE: resume ConstantName

  /** A vector with all coordinates set to positive infinity. */
  public static final Vector3D POSITIVE_INFINITY =
      new Vector3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

  /** A vector with all coordinates set to negative infinity. */
  public static final Vector3D NEGATIVE_INFINITY =
      new Vector3D(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

  /** Default format. */
  private static final Vector3DFormat DEFAULT_FORMAT =
      Vector3DFormat.getInstance();

  /** Serializable version identifier. */
  private static final long serialVersionUID = 5133268763396045979L;

  /** Abscissa. */
  private final double x;

  /** Ordinate. */
  private final double y;

  /** Height. */
  private final double z;

  /** Simple constructor.
   * Build a vector from its coordinates
   * @param x abscissa
   * @param y ordinate
   * @param z height
   * @see #getX()
   * @see #getY()
   * @see #getZ()
   */
  public Vector3D(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /** Simple constructor.
   * Build a vector from its azimuthal coordinates
   * @param alpha azimuth (&alpha;) around Z
   *              (0 is +X, &pi;/2 is +Y, &pi; is -X and 3&pi;/2 is -Y)
   * @param delta elevation (&delta;) above (XY) plane, from -&pi;/2 to +&pi;/2
   * @see #getAlpha()
   * @see #getDelta()
   */
  public Vector3D(double alpha, double delta) {
    double cosDelta = FastMath.cos(delta);
    this.x = FastMath.cos(alpha) * cosDelta;
    this.y = FastMath.sin(alpha) * cosDelta;
    this.z = FastMath.sin(delta);
  }

  /** Multiplicative constructor
   * Build a vector from another one and a scale factor.
   * The vector built will be a * u
   * @param a scale factor
   * @param u base (unscaled) vector
   */
  public Vector3D(double a, Vector3D u) {
    this.x = a * u.x;
    this.y = a * u.y;
    this.z = a * u.z;
  }

  /** Linear constructor
   * Build a vector from two other ones and corresponding scale factors.
   * The vector built will be a1 * u1 + a2 * u2
   * @param a1 first scale factor
   * @param u1 first base (unscaled) vector
   * @param a2 second scale factor
   * @param u2 second base (unscaled) vector
   */
  public Vector3D(double a1, Vector3D u1, double a2, Vector3D u2) {
    this.x = a1 * u1.x + a2 * u2.x;
    this.y = a1 * u1.y + a2 * u2.y;
    this.z = a1 * u1.z + a2 * u2.z;
  }

  /** Linear constructor
   * Build a vector from three other ones and corresponding scale factors.
   * The vector built will be a1 * u1 + a2 * u2 + a3 * u3
   * @param a1 first scale factor
   * @param u1 first base (unscaled) vector
   * @param a2 second scale factor
   * @param u2 second base (unscaled) vector
   * @param a3 third scale factor
   * @param u3 third base (unscaled) vector
   */
  public Vector3D(double a1, Vector3D u1, double a2, Vector3D u2,
                  double a3, Vector3D u3) {
    this.x = a1 * u1.x + a2 * u2.x + a3 * u3.x;
    this.y = a1 * u1.y + a2 * u2.y + a3 * u3.y;
    this.z = a1 * u1.z + a2 * u2.z + a3 * u3.z;
  }

  /** Linear constructor
   * Build a vector from four other ones and corresponding scale factors.
   * The vector built will be a1 * u1 + a2 * u2 + a3 * u3 + a4 * u4
   * @param a1 first scale factor
   * @param u1 first base (unscaled) vector
   * @param a2 second scale factor
   * @param u2 second base (unscaled) vector
   * @param a3 third scale factor
   * @param u3 third base (unscaled) vector
   * @param a4 fourth scale factor
   * @param u4 fourth base (unscaled) vector
   */
  public Vector3D(double a1, Vector3D u1, double a2, Vector3D u2,
                  double a3, Vector3D u3, double a4, Vector3D u4) {
    this.x = a1 * u1.x + a2 * u2.x + a3 * u3.x + a4 * u4.x;
    this.y = a1 * u1.y + a2 * u2.y + a3 * u3.y + a4 * u4.y;
    this.z = a1 * u1.z + a2 * u2.z + a3 * u3.z + a4 * u4.z;
  }

  /** Get the abscissa of the vector.
   * @return abscissa of the vector
   * @see #Vector3D(double, double, double)
   */
  public double getX() {
    return x;
  }

  /** Get the ordinate of the vector.
   * @return ordinate of the vector
   * @see #Vector3D(double, double, double)
   */
  public double getY() {
    return y;
  }

  /** Get the height of the vector.
   * @return height of the vector
   * @see #Vector3D(double, double, double)
   */
  public double getZ() {
    return z;
  }

  /** Get the L<sub>1</sub> norm for the vector.
   * @return L<sub>1</sub> norm for the vector
   */
  public double getNorm1() {
    return FastMath.abs(x) + FastMath.abs(y) + FastMath.abs(z);
  }

  /** Get the L<sub>2</sub> norm for the vector.
   * @return euclidian norm for the vector
   */
  public double getNorm() {
    return FastMath.sqrt (x * x + y * y + z * z);
  }

  /** Get the square of the norm for the vector.
   * @return square of the euclidian norm for the vector
   */
  public double getNormSq() {
    return x * x + y * y + z * z;
  }

  /** Get the L<sub>&infin;</sub> norm for the vector.
   * @return L<sub>&infin;</sub> norm for the vector
   */
  public double getNormInf() {
    return FastMath.max(FastMath.max(FastMath.abs(x), FastMath.abs(y)), FastMath.abs(z));
  }

  /** Get the azimuth of the vector.
   * @return azimuth (&alpha;) of the vector, between -&pi; and +&pi;
   * @see #Vector3D(double, double)
   */
  public double getAlpha() {
    return FastMath.atan2(y, x);
  }

  /** Get the elevation of the vector.
   * @return elevation (&delta;) of the vector, between -&pi;/2 and +&pi;/2
   * @see #Vector3D(double, double)
   */
  public double getDelta() {
    return FastMath.asin(z / getNorm());
  }

  /** Add a vector to the instance.
   * @param v vector to add
   * @return a new vector
   */
  public Vector3D add(Vector3D v) {
    return new Vector3D(x + v.x, y + v.y, z + v.z);
  }

  /** Add a scaled vector to the instance.
   * @param factor scale factor to apply to v before adding it
   * @param v vector to add
   * @return a new vector
   */
  public Vector3D add(double factor, Vector3D v) {
    return new Vector3D(x + factor * v.x, y + factor * v.y, z + factor * v.z);
  }

  /** Subtract a vector from the instance.
   * @param v vector to subtract
   * @return a new vector
   */
  public Vector3D subtract(Vector3D v) {
    return new Vector3D(x - v.x, y - v.y, z - v.z);
  }

  /** Subtract a scaled vector from the instance.
   * @param factor scale factor to apply to v before subtracting it
   * @param v vector to subtract
   * @return a new vector
   */
  public Vector3D subtract(double factor, Vector3D v) {
    return new Vector3D(x - factor * v.x, y - factor * v.y, z - factor * v.z);
  }

  /** Get a normalized vector aligned with the instance.
   * @return a new normalized vector
   * @exception ArithmeticException if the norm is zero
   */
  public Vector3D normalize() {
    double s = getNorm();
    if (s == 0) {
      throw MathRuntimeException.createArithmeticException(LocalizedFormats.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR);
    }
    return scalarMultiply(1 / s);
  }

  /** Get a vector orthogonal to the instance.
   * <p>There are an infinite number of normalized vectors orthogonal
   * to the instance. This method picks up one of them almost
   * arbitrarily. It is useful when one needs to compute a reference
   * frame with one of the axes in a predefined direction. The
   * following example shows how to build a frame having the k axis
   * aligned with the known vector u :
   * <pre><code>
   *   Vector3D k = u.normalize();
   *   Vector3D i = k.orthogonal();
   *   Vector3D j = Vector3D.crossProduct(k, i);
   * </code></pre></p>
   * @return a new normalized vector orthogonal to the instance
   * @exception ArithmeticException if the norm of the instance is null
   */
  public Vector3D orthogonal() {

    double threshold = 0.6 * getNorm();
    if (threshold == 0) {
      throw MathRuntimeException.createArithmeticException(LocalizedFormats.ZERO_NORM);
    }

    if ((x >= -threshold) && (x <= threshold)) {
      double inverse  = 1 / FastMath.sqrt(y * y + z * z);
      return new Vector3D(0, inverse * z, -inverse * y);
    } else if ((y >= -threshold) && (y <= threshold)) {
      double inverse  = 1 / FastMath.sqrt(x * x + z * z);
      return new Vector3D(-inverse * z, 0, inverse * x);
    }
    double inverse  = 1 / FastMath.sqrt(x * x + y * y);
    return new Vector3D(inverse * y, -inverse * x, 0);

  }

  /** Compute the angular separation between two vectors.
   * <p>This method computes the angular separation between two
   * vectors using the dot product for well separated vectors and the
   * cross product for almost aligned vectors. This allows to have a
   * good accuracy in all cases, even for vectors very close to each
   * other.</p>
   * @param v1 first vector
   * @param v2 second vector
   * @return angular separation between v1 and v2
   * @exception ArithmeticException if either vector has a null norm
   */
  public static double angle(Vector3D v1, Vector3D v2) {

    double normProduct = v1.getNorm() * v2.getNorm();
    if (normProduct == 0) {
      throw MathRuntimeException.createArithmeticException(LocalizedFormats.ZERO_NORM);
    }

    double dot = dotProduct(v1, v2);
    double threshold = normProduct * 0.9999;
    if ((dot < -threshold) || (dot > threshold)) {
      // the vectors are almost aligned, compute using the sine
      Vector3D v3 = crossProduct(v1, v2);
      if (dot >= 0) {
        return FastMath.asin(v3.getNorm() / normProduct);
      }
      return FastMath.PI - FastMath.asin(v3.getNorm() / normProduct);
    }

    // the vectors are sufficiently separated to use the cosine
    return FastMath.acos(dot / normProduct);

  }

  /** Get the opposite of the instance.
   * @return a new vector which is opposite to the instance
   */
  public Vector3D negate() {
    return new Vector3D(-x, -y, -z);
  }

  /** Multiply the instance by a scalar
   * @param a scalar
   * @return a new vector
   */
  public Vector3D scalarMultiply(double a) {
    return new Vector3D(a * x, a * y, a * z);
  }

  /**
   * Returns true if any coordinate of this vector is NaN; false otherwise
   * @return  true if any coordinate of this vector is NaN; false otherwise
   */
  public boolean isNaN() {
      return Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z);
  }

  /**
   * Returns true if any coordinate of this vector is infinite and none are NaN;
   * false otherwise
   * @return  true if any coordinate of this vector is infinite and none are NaN;
   * false otherwise
   */
  public boolean isInfinite() {
      return !isNaN() && (Double.isInfinite(x) || Double.isInfinite(y) || Double.isInfinite(z));
  }

  /**
   * Test for the equality of two 3D vectors.
   * <p>
   * If all coordinates of two 3D vectors are exactly the same, and none are
   * <code>Double.NaN</code>, the two 3D vectors are considered to be equal.
   * </p>
   * <p>
   * <code>NaN</code> coordinates are considered to affect globally the vector
   * and be equals to each other - i.e, if either (or all) coordinates of the
   * 3D vector are equal to <code>Double.NaN</code>, the 3D vector is equal to
   * {@link #NaN}.
   * </p>
   *
   * @param other Object to test for equality to this
   * @return true if two 3D vector objects are equal, false if
   *         object is null, not an instance of Vector3D, or
   *         not equal to this Vector3D instance
   *
   */
  @Override
  public boolean equals(Object other) {

    if (this == other) {
      return true;
    }

    if (other instanceof Vector3D) {
      final Vector3D rhs = (Vector3D)other;
      if (rhs.isNaN()) {
          return this.isNaN();
      }

      return (x == rhs.x) && (y == rhs.y) && (z == rhs.z);
    }
    return false;
  }

  /**
   * Get a hashCode for the 3D vector.
   * <p>
   * All NaN values have the same hash code.</p>
   *
   * @return a hash code value for this object
   */
  @Override
  public int hashCode() {
      if (isNaN()) {
          return 8;
      }
      return 31 * (23 * MathUtils.hash(x) +  19 * MathUtils.hash(y) +  MathUtils.hash(z));
  }

  /** Compute the dot-product of two vectors.
   * @param v1 first vector
   * @param v2 second vector
   * @return the dot product v1.v2
   */
  public static double dotProduct(Vector3D v1, Vector3D v2) {
    return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
  }

  /** Compute the cross-product of two vectors.
   * @param v1 first vector
   * @param v2 second vector
   * @return the cross product v1 ^ v2 as a new Vector
   */
  public static Vector3D crossProduct(Vector3D v1, Vector3D v2) {
    return new Vector3D(v1.y * v2.z - v1.z * v2.y,
                        v1.z * v2.x - v1.x * v2.z,
                        v1.x * v2.y - v1.y * v2.x);
  }

  /** Compute the distance between two vectors according to the L<sub>1</sub> norm.
   * <p>Calling this method is equivalent to calling:
   * <code>v1.subtract(v2).getNorm1()</code> except that no intermediate
   * vector is built</p>
   * @param v1 first vector
   * @param v2 second vector
   * @return the distance between v1 and v2 according to the L<sub>1</sub> norm
   */
  public static double distance1(Vector3D v1, Vector3D v2) {
    final double dx = FastMath.abs(v2.x - v1.x);
    final double dy = FastMath.abs(v2.y - v1.y);
    final double dz = FastMath.abs(v2.z - v1.z);
    return dx + dy + dz;
  }

  /** Compute the distance between two vectors according to the L<sub>2</sub> norm.
   * <p>Calling this method is equivalent to calling:
   * <code>v1.subtract(v2).getNorm()</code> except that no intermediate
   * vector is built</p>
   * @param v1 first vector
   * @param v2 second vector
   * @return the distance between v1 and v2 according to the L<sub>2</sub> norm
   */
  public static double distance(Vector3D v1, Vector3D v2) {
    final double dx = v2.x - v1.x;
    final double dy = v2.y - v1.y;
    final double dz = v2.z - v1.z;
    return FastMath.sqrt(dx * dx + dy * dy + dz * dz);
  }

  /** Compute the distance between two vectors according to the L<sub>&infin;</sub> norm.
   * <p>Calling this method is equivalent to calling:
   * <code>v1.subtract(v2).getNormInf()</code> except that no intermediate
   * vector is built</p>
   * @param v1 first vector
   * @param v2 second vector
   * @return the distance between v1 and v2 according to the L<sub>&infin;</sub> norm
   */
  public static double distanceInf(Vector3D v1, Vector3D v2) {
    final double dx = FastMath.abs(v2.x - v1.x);
    final double dy = FastMath.abs(v2.y - v1.y);
    final double dz = FastMath.abs(v2.z - v1.z);
    return FastMath.max(FastMath.max(dx, dy), dz);
  }

  /** Compute the square of the distance between two vectors.
   * <p>Calling this method is equivalent to calling:
   * <code>v1.subtract(v2).getNormSq()</code> except that no intermediate
   * vector is built</p>
   * @param v1 first vector
   * @param v2 second vector
   * @return the square of the distance between v1 and v2
   */
  public static double distanceSq(Vector3D v1, Vector3D v2) {
    final double dx = v2.x - v1.x;
    final double dy = v2.y - v1.y;
    final double dz = v2.z - v1.z;
    return dx * dx + dy * dy + dz * dz;
  }

  /** Get a string representation of this vector.
   * @return a string representation of this vector
   */
  @Override
  public String toString() {
      return DEFAULT_FORMAT.format(this);
  }

}
