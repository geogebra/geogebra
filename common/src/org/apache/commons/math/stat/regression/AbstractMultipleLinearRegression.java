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
package org.apache.commons.math.stat.regression;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.stat.descriptive.moment.Variance;
import org.apache.commons.math.util.FastMath;

/**
 * Abstract base class for implementations of MultipleLinearRegression.
 * @version $Revision: 1073459 $ $Date: 2011-02-22 20:18:12 +0100 (mar. 22 févr. 2011) $
 * @since 2.0
 */
public abstract class AbstractMultipleLinearRegression implements
        MultipleLinearRegression {

    /** X sample data. */
    protected RealMatrix X;

    /** Y sample data. */
    protected RealVector Y;

    /** Whether or not the regression model includes an intercept.  True means no intercept. */
    private boolean noIntercept = false;

    /**
     * @return true if the model has no intercept term; false otherwise
     * @since 2.2
     */
    public boolean isNoIntercept() {
        return noIntercept;
    }

    /**
     * @param noIntercept true means the model is to be estimated without an intercept term
     * @since 2.2
     */
    public void setNoIntercept(boolean noIntercept) {
        this.noIntercept = noIntercept;
    }

    /**
     * <p>Loads model x and y sample data from a flat input array, overriding any previous sample.
     * </p>
     * <p>Assumes that rows are concatenated with y values first in each row.  For example, an input
     * <code>data</code> array containing the sequence of values (1, 2, 3, 4, 5, 6, 7, 8, 9) with
     * <code>nobs = 3</code> and <code>nvars = 2</code> creates a regression dataset with two
     * independent variables, as below:
     * <pre>
     *   y   x[0]  x[1]
     *   --------------
     *   1     2     3
     *   4     5     6
     *   7     8     9
     * </pre>
     * </p>
     * <p>Note that there is no need to add an initial unitary column (column of 1's) when
     * specifying a model including an intercept term.  If {@link #isNoIntercept()} is <code>true</code>,
     * the X matrix will be created without an initial column of "1"s; otherwise this column will
     * be added.
     * </p>
     * <p>Throws IllegalArgumentException if any of the following preconditions fail:
     * <ul><li><code>data</code> cannot be null</li>
     * <li><code>data.length = nobs * (nvars + 1)</li>
     * <li><code>nobs > nvars</code></li></ul>
     * </p>
     *
     * @param data input data array
     * @param nobs number of observations (rows)
     * @param nvars number of independent variables (columns, not counting y)
     * @throws IllegalArgumentException if the preconditions are not met
     */
    public void newSampleData(double[] data, int nobs, int nvars) {
        if (data == null) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.NULL_NOT_ALLOWED);
        }
        if (data.length != nobs * (nvars + 1)) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.INVALID_REGRESSION_ARRAY, data.length, nobs, nvars);
        }
        if (nobs <= nvars) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS);
        }
        double[] y = new double[nobs];
        final int cols = noIntercept ? nvars: nvars + 1;
        double[][] x = new double[nobs][cols];
        int pointer = 0;
        for (int i = 0; i < nobs; i++) {
            y[i] = data[pointer++];
            if (!noIntercept) {
                x[i][0] = 1.0d;
            }
            for (int j = noIntercept ? 0 : 1; j < cols; j++) {
                x[i][j] = data[pointer++];
            }
        }
        this.X = new Array2DRowRealMatrix(x);
        this.Y = new ArrayRealVector(y);
    }

    /**
     * Loads new y sample data, overriding any previous data.
     *
     * @param y the array representing the y sample
     * @throws IllegalArgumentException if y is null or empty
     */
    protected void newYSampleData(double[] y) {
        if (y == null) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.NULL_NOT_ALLOWED);
        }
        if (y.length == 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.NO_DATA);
        }
        this.Y = new ArrayRealVector(y);
    }

    /**
     * <p>Loads new x sample data, overriding any previous data.
     * </p>
     * The input <code>x</code> array should have one row for each sample
     * observation, with columns corresponding to independent variables.
     * For example, if <pre>
     * <code> x = new double[][] {{1, 2}, {3, 4}, {5, 6}} </code></pre>
     * then <code>setXSampleData(x) </code> results in a model with two independent
     * variables and 3 observations:
     * <pre>
     *   x[0]  x[1]
     *   ----------
     *     1    2
     *     3    4
     *     5    6
     * </pre>
     * </p>
     * <p>Note that there is no need to add an initial unitary column (column of 1's) when
     * specifying a model including an intercept term.
     * </p>
     * @param x the rectangular array representing the x sample
     * @throws IllegalArgumentException if x is null, empty or not rectangular
     */
    protected void newXSampleData(double[][] x) {
        if (x == null) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.NULL_NOT_ALLOWED);
        }
        if (x.length == 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.NO_DATA);
        }
        if (noIntercept) {
            this.X = new Array2DRowRealMatrix(x, true);
        } else { // Augment design matrix with initial unitary column
            final int nVars = x[0].length;
            final double[][] xAug = new double[x.length][nVars + 1];
            for (int i = 0; i < x.length; i++) {
                if (x[i].length != nVars) {
                    throw MathRuntimeException.createIllegalArgumentException(
                            LocalizedFormats.DIFFERENT_ROWS_LENGTHS,
                            x[i].length, nVars);
                }
                xAug[i][0] = 1.0d;
                System.arraycopy(x[i], 0, xAug[i], 1, nVars);
            }
            this.X = new Array2DRowRealMatrix(xAug, false);
        }
    }

    /**
     * Validates sample data.  Checks that
     * <ul><li>Neither x nor y is null or empty;</li>
     * <li>The length (i.e. number of rows) of x equals the length of y</li>
     * <li>x has at least one more row than it has columns (i.e. there is
     * sufficient data to estimate regression coefficients for each of the
     * columns in x plus an intercept.</li>
     * </ul>
     *
     * @param x the [n,k] array representing the x data
     * @param y the [n,1] array representing the y data
     * @throws IllegalArgumentException if any of the checks fail
     *
     */
    protected void validateSampleData(double[][] x, double[] y) {
        if ((x == null) || (y == null) || (x.length != y.length)) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE,
                  (x == null) ? 0 : x.length,
                  (y == null) ? 0 : y.length);
        }
        if (x.length == 0) {  // Must be no y data either
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.NO_DATA);
        }
        if (x[0].length + 1 > x.length) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS,
                  x.length, x[0].length);
        }
    }

    /**
     * Validates that the x data and covariance matrix have the same
     * number of rows and that the covariance matrix is square.
     *
     * @param x the [n,k] array representing the x sample
     * @param covariance the [n,n] array representing the covariance matrix
     * @throws IllegalArgumentException if the number of rows in x is not equal
     * to the number of rows in covariance or covariance is not square.
     */
    protected void validateCovarianceData(double[][] x, double[][] covariance) {
        if (x.length != covariance.length) {
            throw MathRuntimeException.createIllegalArgumentException(
                 LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, x.length, covariance.length);
        }
        if (covariance.length > 0 && covariance.length != covariance[0].length) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.NON_SQUARE_MATRIX,
                  covariance.length, covariance[0].length);
        }
    }

    /**
     * {@inheritDoc}
     */
    public double[] estimateRegressionParameters() {
        RealVector b = calculateBeta();
        return b.getData();
    }

    /**
     * {@inheritDoc}
     */
    public double[] estimateResiduals() {
        RealVector b = calculateBeta();
        RealVector e = Y.subtract(X.operate(b));
        return e.getData();
    }

    /**
     * {@inheritDoc}
     */
    public double[][] estimateRegressionParametersVariance() {
        return calculateBetaVariance().getData();
    }

    /**
     * {@inheritDoc}
     */
    public double[] estimateRegressionParametersStandardErrors() {
        double[][] betaVariance = estimateRegressionParametersVariance();
        double sigma = calculateErrorVariance();
        int length = betaVariance[0].length;
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = FastMath.sqrt(sigma * betaVariance[i][i]);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public double estimateRegressandVariance() {
        return calculateYVariance();
    }

    /**
     * Estimates the variance of the error.
     *
     * @return estimate of the error variance
     * @since 2.2
     */
    public double estimateErrorVariance() {
        return calculateErrorVariance();

    }

    /**
     * Estimates the standard error of the regression.
     *
     * @return regression standard error
     * @since 2.2
     */
    public double estimateRegressionStandardError() {
        return Math.sqrt(estimateErrorVariance());
    }

    /**
     * Calculates the beta of multiple linear regression in matrix notation.
     *
     * @return beta
     */
    protected abstract RealVector calculateBeta();

    /**
     * Calculates the beta variance of multiple linear regression in matrix
     * notation.
     *
     * @return beta variance
     */
    protected abstract RealMatrix calculateBetaVariance();


    /**
     * Calculates the variance of the y values.
     *
     * @return Y variance
     */
    protected double calculateYVariance() {
        return new Variance().evaluate(Y.getData());
    }

    /**
     * <p>Calculates the variance of the error term.</p>
     * Uses the formula <pre>
     * var(u) = u &middot; u / (n - k)
     * </pre>
     * where n and k are the row and column dimensions of the design
     * matrix X.
     *
     * @return error variance estimate
     * @since 2.2
     */
    protected double calculateErrorVariance() {
        RealVector residuals = calculateResiduals();
        return residuals.dotProduct(residuals) /
               (X.getRowDimension() - X.getColumnDimension());
    }

    /**
     * Calculates the residuals of multiple linear regression in matrix
     * notation.
     *
     * <pre>
     * u = y - X * b
     * </pre>
     *
     * @return The residuals [n,1] matrix
     */
    protected RealVector calculateResiduals() {
        RealVector b = calculateBeta();
        return Y.subtract(X.operate(b));
    }

}
