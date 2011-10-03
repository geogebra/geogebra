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
package org.apache.commons.math.analysis.interpolation;

import org.apache.commons.math.DimensionMismatchException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.BivariateRealFunction;
import org.apache.commons.math.exception.NoDataException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.util.MathUtils;

/**
 * Function that implements the
 * <a href="http://en.wikipedia.org/wiki/Bicubic_interpolation">
 * bicubic spline interpolation</a>.
 *
 * @version $Revision$ $Date$
 * @since 2.1
 */
public class BicubicSplineInterpolatingFunction
    implements BivariateRealFunction {
    /**
     * Matrix to compute the spline coefficients from the function values
     * and function derivatives values
     */
    private static final double[][] AINV = {
        { 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 },
        { 0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0 },
        { -3,3,0,0,-2,-1,0,0,0,0,0,0,0,0,0,0 },
        { 2,-2,0,0,1,1,0,0,0,0,0,0,0,0,0,0 },
        { 0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0 },
        { 0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0 },
        { 0,0,0,0,0,0,0,0,-3,3,0,0,-2,-1,0,0 },
        { 0,0,0,0,0,0,0,0,2,-2,0,0,1,1,0,0 },
        { -3,0,3,0,0,0,0,0,-2,0,-1,0,0,0,0,0 },
        { 0,0,0,0,-3,0,3,0,0,0,0,0,-2,0,-1,0 },
        { 9,-9,-9,9,6,3,-6,-3,6,-6,3,-3,4,2,2,1 },
        { -6,6,6,-6,-3,-3,3,3,-4,4,-2,2,-2,-2,-1,-1 },
        { 2,0,-2,0,0,0,0,0,1,0,1,0,0,0,0,0 },
        { 0,0,0,0,2,0,-2,0,0,0,0,0,1,0,1,0 },
        { -6,6,6,-6,-4,-2,4,2,-3,3,-3,3,-2,-1,-2,-1 },
        { 4,-4,-4,4,2,2,-2,-2,2,-2,2,-2,1,1,1,1 }
    };

    /** Samples x-coordinates */
    private final double[] xval;
    /** Samples y-coordinates */
    private final double[] yval;
    /** Set of cubic splines patching the whole data grid */
    private final BicubicSplineFunction[][] splines;
    /**
     * Partial derivatives
     * The value of the first index determines the kind of derivatives:
     * 0 = first partial derivatives wrt x
     * 1 = first partial derivatives wrt y
     * 2 = second partial derivatives wrt x
     * 3 = second partial derivatives wrt y
     * 4 = cross partial derivatives
     */
    private BivariateRealFunction[][][] partialDerivatives = null;

    /**
     * @param x Sample values of the x-coordinate, in increasing order.
     * @param y Sample values of the y-coordinate, in increasing order.
     * @param f Values of the function on every grid point.
     * @param dFdX Values of the partial derivative of function with respect
     * to x on every grid point.
     * @param dFdY Values of the partial derivative of function with respect
     * to y on every grid point.
     * @param d2FdXdY Values of the cross partial derivative of function on
     * every grid point.
     * @throws DimensionMismatchException if the various arrays do not contain
     * the expected number of elements.
     * @throws org.apache.commons.math.exception.NonMonotonousSequenceException
     * if {@code x} or {@code y} are not strictly increasing.
     * @throws NoDataException if any of the arrays has zero length.
     */
    public BicubicSplineInterpolatingFunction(double[] x,
                                              double[] y,
                                              double[][] f,
                                              double[][] dFdX,
                                              double[][] dFdY,
                                              double[][] d2FdXdY)
        throws DimensionMismatchException {
        final int xLen = x.length;
        final int yLen = y.length;

        if (xLen == 0 || yLen == 0 || f.length == 0 || f[0].length == 0) {
            throw new NoDataException();
        }
        if (xLen != f.length) {
            throw new DimensionMismatchException(xLen, f.length);
        }
        if (xLen != dFdX.length) {
            throw new DimensionMismatchException(xLen, dFdX.length);
        }
        if (xLen != dFdY.length) {
            throw new DimensionMismatchException(xLen, dFdY.length);
        }
        if (xLen != d2FdXdY.length) {
            throw new DimensionMismatchException(xLen, d2FdXdY.length);
        }

        MathUtils.checkOrder(x);
        MathUtils.checkOrder(y);

        xval = x.clone();
        yval = y.clone();

        final int lastI = xLen - 1;
        final int lastJ = yLen - 1;
        splines = new BicubicSplineFunction[lastI][lastJ];

        for (int i = 0; i < lastI; i++) {
            if (f[i].length != yLen) {
                throw new DimensionMismatchException(f[i].length, yLen);
            }
            if (dFdX[i].length != yLen) {
                throw new DimensionMismatchException(dFdX[i].length, yLen);
            }
            if (dFdY[i].length != yLen) {
                throw new DimensionMismatchException(dFdY[i].length, yLen);
            }
            if (d2FdXdY[i].length != yLen) {
                throw new DimensionMismatchException(d2FdXdY[i].length, yLen);
            }
            final int ip1 = i + 1;
            for (int j = 0; j < lastJ; j++) {
                final int jp1 = j + 1;
                final double[] beta = new double[] {
                    f[i][j], f[ip1][j], f[i][jp1], f[ip1][jp1],
                    dFdX[i][j], dFdX[ip1][j], dFdX[i][jp1], dFdX[ip1][jp1],
                    dFdY[i][j], dFdY[ip1][j], dFdY[i][jp1], dFdY[ip1][jp1],
                    d2FdXdY[i][j], d2FdXdY[ip1][j], d2FdXdY[i][jp1], d2FdXdY[ip1][jp1]
                };

                splines[i][j] = new BicubicSplineFunction(computeSplineCoefficients(beta));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public double value(double x, double y) {
        final int i = searchIndex(x, xval);
        if (i == -1) {
            throw new OutOfRangeException(x, xval[0], xval[xval.length - 1]);
        }
        final int j = searchIndex(y, yval);
        if (j == -1) {
            throw new OutOfRangeException(y, yval[0], yval[yval.length - 1]);
        }

        final double xN = (x - xval[i]) / (xval[i + 1] - xval[i]);
        final double yN = (y - yval[j]) / (yval[j + 1] - yval[j]);

        return splines[i][j].value(xN, yN);
    }

    /**
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @return the value at point (x, y) of the first partial derivative with
     * respect to x.
     * @since 2.2
     */
    public double partialDerivativeX(double x, double y) {
        return partialDerivative(0, x, y);
    }
    /**
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @return the value at point (x, y) of the first partial derivative with
     * respect to y.
     * @since 2.2
     */
    public double partialDerivativeY(double x, double y) {
        return partialDerivative(1, x, y);
    }
    /**
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @return the value at point (x, y) of the second partial derivative with
     * respect to x.
     * @since 2.2
     */
    public double partialDerivativeXX(double x, double y) {
        return partialDerivative(2, x, y);
    }
    /**
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @return the value at point (x, y) of the second partial derivative with
     * respect to y.
     * @since 2.2
     */
    public double partialDerivativeYY(double x, double y) {
        return partialDerivative(3, x, y);
    }
    /**
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @return the value at point (x, y) of the second partial cross-derivative.
     * @since 2.2
     */
    public double partialDerivativeXY(double x, double y) {
        return partialDerivative(4, x, y);
    }

    /**
     * @param which First index in {@link #partialDerivatives}.
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @return the value at point (x, y) of the selected partial derivative.
     * @throws FunctionEvaluationException
     */
    private double partialDerivative(int which, double x, double y) {
        if (partialDerivatives == null) {
            computePartialDerivatives();
        }

        final int i = searchIndex(x, xval);
        if (i == -1) {
            throw new OutOfRangeException(x, xval[0], xval[xval.length - 1]);
        }
        final int j = searchIndex(y, yval);
        if (j == -1) {
            throw new OutOfRangeException(y, yval[0], yval[yval.length - 1]);
        }

        final double xN = (x - xval[i]) / (xval[i + 1] - xval[i]);
        final double yN = (y - yval[j]) / (yval[j + 1] - yval[j]);

        try {
            return partialDerivatives[which][i][j].value(xN, yN);
        } catch (FunctionEvaluationException fee) {
            // this should never happen
            throw new RuntimeException(fee);
        }

    }

    /**
     * Compute all partial derivatives.
     */
    private void computePartialDerivatives() {
        final int lastI = xval.length - 1;
        final int lastJ = yval.length - 1;
        partialDerivatives = new BivariateRealFunction[5][lastI][lastJ];

        for (int i = 0; i < lastI; i++) {
            for (int j = 0; j < lastJ; j++) {
                final BicubicSplineFunction f = splines[i][j];
                partialDerivatives[0][i][j] = f.partialDerivativeX();
                partialDerivatives[1][i][j] = f.partialDerivativeY();
                partialDerivatives[2][i][j] = f.partialDerivativeXX();
                partialDerivatives[3][i][j] = f.partialDerivativeYY();
                partialDerivatives[4][i][j] = f.partialDerivativeXY();
            }
        }
    }

    /**
     * @param c Coordinate.
     * @param val Coordinate samples.
     * @return the index in {@code val} corresponding to the interval
     * containing {@code c}, or {@code -1} if {@code c} is out of the
     * range defined by the end values of {@code val}.
     */
    private int searchIndex(double c, double[] val) {
        if (c < val[0]) {
            return -1;
        }

        final int max = val.length;
        for (int i = 1; i < max; i++) {
            if (c <= val[i]) {
                return i - 1;
            }
        }

        return -1;
    }

    /**
     * Compute the spline coefficients from the list of function values and
     * function partial derivatives values at the four corners of a grid
     * element. They must be specified in the following order:
     * <ul>
     *  <li>f(0,0)</li>
     *  <li>f(1,0)</li>
     *  <li>f(0,1)</li>
     *  <li>f(1,1)</li>
     *  <li>f<sub>x</sub>(0,0)</li>
     *  <li>f<sub>x</sub>(1,0)</li>
     *  <li>f<sub>x</sub>(0,1)</li>
     *  <li>f<sub>x</sub>(1,1)</li>
     *  <li>f<sub>y</sub>(0,0)</li>
     *  <li>f<sub>y</sub>(1,0)</li>
     *  <li>f<sub>y</sub>(0,1)</li>
     *  <li>f<sub>y</sub>(1,1)</li>
     *  <li>f<sub>xy</sub>(0,0)</li>
     *  <li>f<sub>xy</sub>(1,0)</li>
     *  <li>f<sub>xy</sub>(0,1)</li>
     *  <li>f<sub>xy</sub>(1,1)</li>
     * </ul>
     * where the subscripts indicate the partial derivative with respect to
     * the corresponding variable(s).
     *
     * @param beta List of function values and function partial derivatives
     * values.
     * @return the spline coefficients.
     */
    private double[] computeSplineCoefficients(double[] beta) {
        final double[] a = new double[16];

        for (int i = 0; i < 16; i++) {
            double result = 0;
            final double[] row = AINV[i];
            for (int j = 0; j < 16; j++) {
                result += row[j] * beta[j];
            }
            a[i] = result;
        }

        return a;
    }
}

/**
 * 2D-spline function.
 *
 * @version $Revision$ $Date$
 */
class BicubicSplineFunction
    implements BivariateRealFunction {

    /** Number of points. */
    private static final short N = 4;

    /** Coefficients */
    private final double[][] a;

    /** First partial derivative along x. */
    private BivariateRealFunction partialDerivativeX;

    /** First partial derivative along y. */
    private BivariateRealFunction partialDerivativeY;

    /** Second partial derivative along x. */
    private BivariateRealFunction partialDerivativeXX;

    /** Second partial derivative along y. */
    private BivariateRealFunction partialDerivativeYY;

    /** Second crossed partial derivative. */
    private BivariateRealFunction partialDerivativeXY;

    /**
     * Simple constructor.
     * @param a Spline coefficients
     */
    public BicubicSplineFunction(double[] a) {
        this.a = new double[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.a[i][j] = a[i + N * j];
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public double value(double x, double y) {
        if (x < 0 || x > 1) {
            throw new OutOfRangeException(x, 0, 1);
        }
        if (y < 0 || y > 1) {
            throw new OutOfRangeException(y, 0, 1);
        }

        final double x2 = x * x;
        final double x3 = x2 * x;
        final double[] pX = {1, x, x2, x3};

        final double y2 = y * y;
        final double y3 = y2 * y;
        final double[] pY = {1, y, y2, y3};

        return apply(pX, pY, a);
    }

    /**
     * Compute the value of the bicubic polynomial.
     *
     * @param pX Powers of the x-coordinate.
     * @param pY Powers of the y-coordinate.
     * @param coeff Spline coefficients.
     * @return the interpolated value.
     */
    private double apply(double[] pX, double[] pY, double[][] coeff) {
        double result = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                result += coeff[i][j] * pX[i] * pY[j];
            }
        }

        return result;
    }

    /**
     * @return the partial derivative wrt {@code x}.
     */
    public BivariateRealFunction partialDerivativeX() {
        if (partialDerivativeX == null) {
            computePartialDerivatives();
        }

        return partialDerivativeX;
    }
    /**
     * @return the partial derivative wrt {@code y}.
     */
    public BivariateRealFunction partialDerivativeY() {
        if (partialDerivativeY == null) {
            computePartialDerivatives();
        }

        return partialDerivativeY;
    }
    /**
     * @return the second partial derivative wrt {@code x}.
     */
    public BivariateRealFunction partialDerivativeXX() {
        if (partialDerivativeXX == null) {
            computePartialDerivatives();
        }

        return partialDerivativeXX;
    }
    /**
     * @return the second partial derivative wrt {@code y}.
     */
    public BivariateRealFunction partialDerivativeYY() {
        if (partialDerivativeYY == null) {
            computePartialDerivatives();
        }

        return partialDerivativeYY;
    }
    /**
     * @return the second partial cross-derivative.
     */
    public BivariateRealFunction partialDerivativeXY() {
        if (partialDerivativeXY == null) {
            computePartialDerivatives();
        }

        return partialDerivativeXY;
    }

    /**
     * Compute all partial derivatives functions.
     */
    private void computePartialDerivatives() {
        final double[][] aX = new double[N][N];
        final double[][] aY = new double[N][N];
        final double[][] aXX = new double[N][N];
        final double[][] aYY = new double[N][N];
        final double[][] aXY = new double[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                final double c = a[i][j];
                aX[i][j] = i * c;
                aY[i][j] = j * c;
                aXX[i][j] = (i - 1) * aX[i][j];
                aYY[i][j] = (j - 1) * aY[i][j];
                aXY[i][j] = j * aX[i][j];
            }
        }

        partialDerivativeX = new BivariateRealFunction() {
                public double value(double x, double y)  {
                    final double x2 = x * x;
                    final double[] pX = {0, 1, x, x2};

                    final double y2 = y * y;
                    final double y3 = y2 * y;
                    final double[] pY = {1, y, y2, y3};

                    return apply(pX, pY, aX);
                }
            };
        partialDerivativeY = new BivariateRealFunction() {
                public double value(double x, double y)  {
                    final double x2 = x * x;
                    final double x3 = x2 * x;
                    final double[] pX = {1, x, x2, x3};

                    final double y2 = y * y;
                    final double[] pY = {0, 1, y, y2};

                    return apply(pX, pY, aY);
                }
            };
        partialDerivativeXX = new BivariateRealFunction() {
                public double value(double x, double y)  {
                    final double[] pX = {0, 0, 1, x};

                    final double y2 = y * y;
                    final double y3 = y2 * y;
                    final double[] pY = {1, y, y2, y3};

                    return apply(pX, pY, aXX);
                }
            };
        partialDerivativeYY = new BivariateRealFunction() {
                public double value(double x, double y)  {
                    final double x2 = x * x;
                    final double x3 = x2 * x;
                    final double[] pX = {1, x, x2, x3};

                    final double[] pY = {0, 0, 1, y};

                    return apply(pX, pY, aYY);
                }
            };
        partialDerivativeXY = new BivariateRealFunction() {
                public double value(double x, double y)  {
                    final double x2 = x * x;
                    final double[] pX = {0, 1, x, x2};

                    final double y2 = y * y;
                    final double[] pY = {0, 1, y, y2};

                    return apply(pX, pY, aXY);
                }
            };
    }
}
