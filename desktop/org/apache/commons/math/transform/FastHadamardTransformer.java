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
package org.apache.commons.math.transform;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * Implements the <a href="http://www.archive.chipcenter.com/dsp/DSP000517F1.html">Fast Hadamard Transform</a> (FHT).
 * Transformation of an input vector x to the output vector y.
 * <p>In addition to transformation of real vectors, the Hadamard transform can
 * transform integer vectors into integer vectors. However, this integer transform
 * cannot be inverted directly. Due to a scaling factor it may lead to rational results.
 * As an example, the inverse transform of integer vector (0, 1, 0, 1) is rational
 * vector (1/2, -1/2, 0, 0).</p>
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 * @since 2.0
 */
public class FastHadamardTransformer implements RealTransformer {

    /** {@inheritDoc} */
    public double[] transform(double f[])
        throws IllegalArgumentException {
        return fht(f);
    }

    /** {@inheritDoc} */
    public double[] transform(UnivariateRealFunction f,
                              double min, double max, int n)
        throws FunctionEvaluationException, IllegalArgumentException {
        return fht(FastFourierTransformer.sample(f, min, max, n));
    }

    /** {@inheritDoc} */
    public double[] inversetransform(double f[])
    throws IllegalArgumentException {
        return FastFourierTransformer.scaleArray(fht(f), 1.0 / f.length);
   }

    /** {@inheritDoc} */
    public double[] inversetransform(UnivariateRealFunction f,
                                     double min, double max, int n)
        throws FunctionEvaluationException, IllegalArgumentException {
        final double[] unscaled =
            fht(FastFourierTransformer.sample(f, min, max, n));
        return FastFourierTransformer.scaleArray(unscaled, 1.0 / n);
    }

    /**
     * Transform the given real data set.
     * <p>The integer transform cannot be inverted directly, due to a scaling
     * factor it may lead to double results.</p>
     * @param f the integer data array to be transformed (signal)
     * @return the integer transformed array (spectrum)
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public int[] transform(int f[])
        throws IllegalArgumentException {
        return fht(f);
    }

    /**
     * The FHT (Fast Hadamard Transformation) which uses only subtraction and addition.
     * <br>
     * Requires <b>Nlog2N = n2</b><sup>n</sup> additions.
     * <br>
     * <br>
     * <b><u>Short Table of manual calculation for N=8:</u></b>
     * <ol>
     * <li><b>x</b> is the input vector we want to transform</li>
     * <li><b>y</b> is the output vector which is our desired result</li>
     * <li>a and b are just helper rows</li>
     * </ol>
     * <pre>
     * <code>
     * +----+----------+---------+----------+
     * | <b>x</b>  |    <b>a</b>     |    <b>b</b>    |    <b>y</b>     |
     * +----+----------+---------+----------+
     * | x<sub>0</sub> | a<sub>0</sub>=x<sub>0</sub>+x<sub>1</sub> | b<sub>0</sub>=a<sub>0</sub>+a<sub>1</sub> | y<sub>0</sub>=b<sub>0</sub>+b<sub>1</sub> |
     * +----+----------+---------+----------+
     * | x<sub>1</sub> | a<sub>1</sub>=x<sub>2</sub>+x<sub>3</sub> | b<sub>0</sub>=a<sub>2</sub>+a<sub>3</sub> | y<sub>0</sub>=b<sub>2</sub>+b<sub>3</sub> |
     * +----+----------+---------+----------+
     * | x<sub>2</sub> | a<sub>2</sub>=x<sub>4</sub>+x<sub>5</sub> | b<sub>0</sub>=a<sub>4</sub>+a<sub>5</sub> | y<sub>0</sub>=b<sub>4</sub>+b<sub>5</sub> |
     * +----+----------+---------+----------+
     * | x<sub>3</sub> | a<sub>3</sub>=x<sub>6</sub>+x<sub>7</sub> | b<sub>0</sub>=a<sub>6</sub>+a<sub>7</sub> | y<sub>0</sub>=b<sub>6</sub>+b<sub>7</sub> |
     * +----+----------+---------+----------+
     * | x<sub>4</sub> | a<sub>0</sub>=x<sub>0</sub>-x<sub>1</sub> | b<sub>0</sub>=a<sub>0</sub>-a<sub>1</sub> | y<sub>0</sub>=b<sub>0</sub>-b<sub>1</sub> |
     * +----+----------+---------+----------+
     * | x<sub>5</sub> | a<sub>1</sub>=x<sub>2</sub>-x<sub>3</sub> | b<sub>0</sub>=a<sub>2</sub>-a<sub>3</sub> | y<sub>0</sub>=b<sub>2</sub>-b<sub>3</sub> |
     * +----+----------+---------+----------+
     * | x<sub>6</sub> | a<sub>2</sub>=x<sub>4</sub>-x<sub>5</sub> | b<sub>0</sub>=a<sub>4</sub>-a<sub>5</sub> | y<sub>0</sub>=b<sub>4</sub>-b<sub>5</sub> |
     * +----+----------+---------+----------+
     * | x<sub>7</sub> | a<sub>3</sub>=x<sub>6</sub>-x<sub>7</sub> | b<sub>0</sub>=a<sub>6</sub>-a<sub>7</sub> | y<sub>0</sub>=b<sub>6</sub>-b<sub>7</sub> |
     * +----+----------+---------+----------+
     * </code>
     * </pre>
     *
     * <b><u>How it works</u></b>
     * <ol>
     * <li>Construct a matrix with N rows and n+1 columns<br>   <b>hadm[n+1][N]</b>
     * <br><i>(If I use [x][y] it always means [row-offset][column-offset] of a Matrix with n rows and m columns. Its entries go from M[0][0] to M[n][m])</i></li>
     * <li>Place the input vector <b>x[N]</b> in the first column of the matrix <b>hadm</b></li>
     * <li>The entries of the submatrix D<sub>top</sub> are calculated as follows.
     * <br>D<sub>top</sub> goes from entry [0][1] to [N/2-1][n+1].
     * <br>The columns of D<sub>top</sub> are the pairwise mutually exclusive sums of the previous column
     * </li>
     * <li>The entries of the submatrix D<sub>bottom</sub> are calculated as follows.
     * <br>D<sub>bottom</sub> goes from entry [N/2][1] to [N][n+1].
     * <br>The columns of D<sub>bottom</sub> are the pairwise differences of the previous column
     * </li>
     * <li>How D<sub>top</sub> and D<sub>bottom</sub> you can understand best with the example for N=8 above.
     * <li>The output vector y is now in the last column of <b>hadm</b></li>
     * <li><i>Algorithm from: http://www.archive.chipcenter.com/dsp/DSP000517F1.html</i></li>
     * </ol>
     * <br>
     * <b><u>Visually</u></b>
     * <pre>
     *        +--------+---+---+---+-----+---+
     *        |   0    | 1 | 2 | 3 | ... |n+1|
     * +------+--------+---+---+---+-----+---+
     * |0     | x<sub>0</sub>     |       /\            |
     * |1     | x<sub>1</sub>     |       ||            |
     * |2     | x<sub>2</sub>     |   <= D<sub>top</sub>  =>       |
     * |...   | ...    |       ||            |
     * |N/2-1 | x<sub>N/2-1</sub>  |       \/            |
     * +------+--------+---+---+---+-----+---+
     * |N/2   | x<sub>N/2</sub>   |       /\            |
     * |N/2+1 | x<sub>N/2+1</sub>  |       ||            |
     * |N/2+2 | x<sub>N/2+2</sub>  |  <= D<sub>bottom</sub>  =>      | which is in the last column of the matrix
     * |...   | ...    |       ||            |
     * |N     | x<sub>N/2</sub>   |        \/           |
     * +------+--------+---+---+---+-----+---+
     * </pre>
     *
     * @param x input vector
     * @return y output vector
     * @exception IllegalArgumentException if input array is not a power of 2
     */
    protected double[] fht(double x[]) throws IllegalArgumentException {

        // n is the row count of the input vector x
        final int n     = x.length;
        final int halfN = n / 2;

        // n has to be of the form n = 2^p !!
        if (!FastFourierTransformer.isPowerOf2(n)) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.NOT_POWER_OF_TWO,
                    n);
        }

        // Instead of creating a matrix with p+1 columns and n rows
        // we will use two single dimension arrays which we will use in an alternating way.
        double[] yPrevious = new double[n];
        double[] yCurrent  = x.clone();

        // iterate from left to right (column)
        for (int j = 1; j < n; j <<= 1) {

            // switch columns
            final double[] yTmp = yCurrent;
            yCurrent  = yPrevious;
            yPrevious = yTmp;

            // iterate from top to bottom (row)
            for (int i = 0; i < halfN; ++i) {
                // D<sub>top</sub>
                // The top part works with addition
                final int twoI = 2 * i;
                yCurrent[i] = yPrevious[twoI] + yPrevious[twoI + 1];
            }
            for (int i = halfN; i < n; ++i) {
                // D<sub>bottom</sub>
                // The bottom part works with subtraction
                final int twoI = 2 * i;
                yCurrent[i] = yPrevious[twoI - n] - yPrevious[twoI - n + 1];
            }
        }

        // return the last computed output vector y
        return yCurrent;

    }
    /**
     * The FHT (Fast Hadamard Transformation) which uses only subtraction and addition.
     * @param x input vector
     * @return y output vector
     * @exception IllegalArgumentException if input array is not a power of 2
     */
    protected int[] fht(int x[]) throws IllegalArgumentException {

        // n is the row count of the input vector x
        final int n     = x.length;
        final int halfN = n / 2;

        // n has to be of the form n = 2^p !!
        if (!FastFourierTransformer.isPowerOf2(n)) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.NOT_POWER_OF_TWO,
                    n);
        }

        // Instead of creating a matrix with p+1 columns and n rows
        // we will use two single dimension arrays which we will use in an alternating way.
        int[] yPrevious = new int[n];
        int[] yCurrent  = x.clone();

        // iterate from left to right (column)
        for (int j = 1; j < n; j <<= 1) {

            // switch columns
            final int[] yTmp = yCurrent;
            yCurrent  = yPrevious;
            yPrevious = yTmp;

            // iterate from top to bottom (row)
            for (int i = 0; i < halfN; ++i) {
                // D<sub>top</sub>
                // The top part works with addition
                final int twoI = 2 * i;
                yCurrent[i] = yPrevious[twoI] + yPrevious[twoI + 1];
            }
            for (int i = halfN; i < n; ++i) {
                // D<sub>bottom</sub>
                // The bottom part works with subtraction
                final int twoI = 2 * i;
                yCurrent[i] = yPrevious[twoI - n] - yPrevious[twoI - n + 1];
            }
        }

        // return the last computed output vector y
        return yCurrent;

    }

}
