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

package org.apache.commons.math.linear;

import java.util.Arrays;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;


/**
 * Calculates the QR-decomposition of a matrix.
 * <p>The QR-decomposition of a matrix A consists of two matrices Q and R
 * that satisfy: A = QR, Q is orthogonal (Q<sup>T</sup>Q = I), and R is
 * upper triangular. If A is m&times;n, Q is m&times;m and R m&times;n.</p>
 * <p>This class compute the decomposition using Householder reflectors.</p>
 * <p>For efficiency purposes, the decomposition in packed form is transposed.
 * This allows inner loop to iterate inside rows, which is much more cache-efficient
 * in Java.</p>
 *
 * @see <a href="http://mathworld.wolfram.com/QRDecomposition.html">MathWorld</a>
 * @see <a href="http://en.wikipedia.org/wiki/QR_decomposition">Wikipedia</a>
 *
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 * @since 1.2
 */
public class QRDecompositionImpl implements QRDecomposition {

    /**
     * A packed TRANSPOSED representation of the QR decomposition.
     * <p>The elements BELOW the diagonal are the elements of the UPPER triangular
     * matrix R, and the rows ABOVE the diagonal are the Householder reflector vectors
     * from which an explicit form of Q can be recomputed if desired.</p>
     */
    private double[][] qrt;

    /** The diagonal elements of R. */
    private double[] rDiag;

    /** Cached value of Q. */
    private RealMatrix cachedQ;

    /** Cached value of QT. */
    private RealMatrix cachedQT;

    /** Cached value of R. */
    private RealMatrix cachedR;

    /** Cached value of H. */
    private RealMatrix cachedH;

    /**
     * Calculates the QR-decomposition of the given matrix.
     * @param matrix The matrix to decompose.
     */
    public QRDecompositionImpl(RealMatrix matrix) {

        final int m = matrix.getRowDimension();
        final int n = matrix.getColumnDimension();
        qrt = matrix.transpose().getData();
        rDiag = new double[FastMath.min(m, n)];
        cachedQ  = null;
        cachedQT = null;
        cachedR  = null;
        cachedH  = null;

        /*
         * The QR decomposition of a matrix A is calculated using Householder
         * reflectors by repeating the following operations to each minor
         * A(minor,minor) of A:
         */
        for (int minor = 0; minor < FastMath.min(m, n); minor++) {

            final double[] qrtMinor = qrt[minor];

            /*
             * Let x be the first column of the minor, and a^2 = |x|^2.
             * x will be in the positions qr[minor][minor] through qr[m][minor].
             * The first column of the transformed minor will be (a,0,0,..)'
             * The sign of a is chosen to be opposite to the sign of the first
             * component of x. Let's find a:
             */
            double xNormSqr = 0;
            for (int row = minor; row < m; row++) {
                final double c = qrtMinor[row];
                xNormSqr += c * c;
            }
            final double a = (qrtMinor[minor] > 0) ? -FastMath.sqrt(xNormSqr) : FastMath.sqrt(xNormSqr);
            rDiag[minor] = a;

            if (a != 0.0) {

                /*
                 * Calculate the normalized reflection vector v and transform
                 * the first column. We know the norm of v beforehand: v = x-ae
                 * so |v|^2 = <x-ae,x-ae> = <x,x>-2a<x,e>+a^2<e,e> =
                 * a^2+a^2-2a<x,e> = 2a*(a - <x,e>).
                 * Here <x, e> is now qr[minor][minor].
                 * v = x-ae is stored in the column at qr:
                 */
                qrtMinor[minor] -= a; // now |v|^2 = -2a*(qr[minor][minor])

                /*
                 * Transform the rest of the columns of the minor:
                 * They will be transformed by the matrix H = I-2vv'/|v|^2.
                 * If x is a column vector of the minor, then
                 * Hx = (I-2vv'/|v|^2)x = x-2vv'x/|v|^2 = x - 2<x,v>/|v|^2 v.
                 * Therefore the transformation is easily calculated by
                 * subtracting the column vector (2<x,v>/|v|^2)v from x.
                 *
                 * Let 2<x,v>/|v|^2 = alpha. From above we have
                 * |v|^2 = -2a*(qr[minor][minor]), so
                 * alpha = -<x,v>/(a*qr[minor][minor])
                 */
                for (int col = minor+1; col < n; col++) {
                    final double[] qrtCol = qrt[col];
                    double alpha = 0;
                    for (int row = minor; row < m; row++) {
                        alpha -= qrtCol[row] * qrtMinor[row];
                    }
                    alpha /= a * qrtMinor[minor];

                    // Subtract the column vector alpha*v from x.
                    for (int row = minor; row < m; row++) {
                        qrtCol[row] -= alpha * qrtMinor[row];
                    }
                }
            }
        }
    }

    /** {@inheritDoc} */
    public RealMatrix getR() {

        if (cachedR == null) {

            // R is supposed to be m x n
            final int n = qrt.length;
            final int m = qrt[0].length;
            cachedR = MatrixUtils.createRealMatrix(m, n);

            // copy the diagonal from rDiag and the upper triangle of qr
            for (int row = FastMath.min(m, n) - 1; row >= 0; row--) {
                cachedR.setEntry(row, row, rDiag[row]);
                for (int col = row + 1; col < n; col++) {
                    cachedR.setEntry(row, col, qrt[col][row]);
                }
            }

        }

        // return the cached matrix
        return cachedR;

    }

    /** {@inheritDoc} */
    public RealMatrix getQ() {
        if (cachedQ == null) {
            cachedQ = getQT().transpose();
        }
        return cachedQ;
    }

    /** {@inheritDoc} */
    public RealMatrix getQT() {

        if (cachedQT == null) {

            // QT is supposed to be m x m
            final int n = qrt.length;
            final int m = qrt[0].length;
            cachedQT = MatrixUtils.createRealMatrix(m, m);

            /*
             * Q = Q1 Q2 ... Q_m, so Q is formed by first constructing Q_m and then
             * applying the Householder transformations Q_(m-1),Q_(m-2),...,Q1 in
             * succession to the result
             */
            for (int minor = m - 1; minor >= FastMath.min(m, n); minor--) {
                cachedQT.setEntry(minor, minor, 1.0);
            }

            for (int minor = FastMath.min(m, n)-1; minor >= 0; minor--){
                final double[] qrtMinor = qrt[minor];
                cachedQT.setEntry(minor, minor, 1.0);
                if (qrtMinor[minor] != 0.0) {
                    for (int col = minor; col < m; col++) {
                        double alpha = 0;
                        for (int row = minor; row < m; row++) {
                            alpha -= cachedQT.getEntry(col, row) * qrtMinor[row];
                        }
                        alpha /= rDiag[minor] * qrtMinor[minor];

                        for (int row = minor; row < m; row++) {
                            cachedQT.addToEntry(col, row, -alpha * qrtMinor[row]);
                        }
                    }
                }
            }

        }

        // return the cached matrix
        return cachedQT;

    }

    /** {@inheritDoc} */
    public RealMatrix getH() {

        if (cachedH == null) {

            final int n = qrt.length;
            final int m = qrt[0].length;
            cachedH = MatrixUtils.createRealMatrix(m, n);
            for (int i = 0; i < m; ++i) {
                for (int j = 0; j < FastMath.min(i + 1, n); ++j) {
                    cachedH.setEntry(i, j, qrt[j][i] / -rDiag[j]);
                }
            }

        }

        // return the cached matrix
        return cachedH;

    }

    /** {@inheritDoc} */
    public DecompositionSolver getSolver() {
        return new Solver(qrt, rDiag);
    }

    /** Specialized solver. */
    private static class Solver implements DecompositionSolver {

        /**
         * A packed TRANSPOSED representation of the QR decomposition.
         * <p>The elements BELOW the diagonal are the elements of the UPPER triangular
         * matrix R, and the rows ABOVE the diagonal are the Householder reflector vectors
         * from which an explicit form of Q can be recomputed if desired.</p>
         */
        private final double[][] qrt;

        /** The diagonal elements of R. */
        private final double[] rDiag;

        /**
         * Build a solver from decomposed matrix.
         * @param qrt packed TRANSPOSED representation of the QR decomposition
         * @param rDiag diagonal elements of R
         */
        private Solver(final double[][] qrt, final double[] rDiag) {
            this.qrt   = qrt;
            this.rDiag = rDiag;
        }

        /** {@inheritDoc} */
        public boolean isNonSingular() {

            for (double diag : rDiag) {
                if (diag == 0) {
                    return false;
                }
            }
            return true;

        }

        /** {@inheritDoc} */
        public double[] solve(double[] b)
        throws IllegalArgumentException, InvalidMatrixException {

            final int n = qrt.length;
            final int m = qrt[0].length;
            if (b.length != m) {
                throw MathRuntimeException.createIllegalArgumentException(
                        LocalizedFormats.VECTOR_LENGTH_MISMATCH,
                        b.length, m);
            }
            if (!isNonSingular()) {
                throw new SingularMatrixException();
            }

            final double[] x = new double[n];
            final double[] y = b.clone();

            // apply Householder transforms to solve Q.y = b
            for (int minor = 0; minor < FastMath.min(m, n); minor++) {

                final double[] qrtMinor = qrt[minor];
                double dotProduct = 0;
                for (int row = minor; row < m; row++) {
                    dotProduct += y[row] * qrtMinor[row];
                }
                dotProduct /= rDiag[minor] * qrtMinor[minor];

                for (int row = minor; row < m; row++) {
                    y[row] += dotProduct * qrtMinor[row];
                }

            }

            // solve triangular system R.x = y
            for (int row = rDiag.length - 1; row >= 0; --row) {
                y[row] /= rDiag[row];
                final double yRow   = y[row];
                final double[] qrtRow = qrt[row];
                x[row] = yRow;
                for (int i = 0; i < row; i++) {
                    y[i] -= yRow * qrtRow[i];
                }
            }

            return x;

        }

        /** {@inheritDoc} */
        public RealVector solve(RealVector b)
        throws IllegalArgumentException, InvalidMatrixException {
            try {
                return solve((ArrayRealVector) b);
            } catch (ClassCastException cce) {
                return new ArrayRealVector(solve(b.getData()), false);
            }
        }

        /** Solve the linear equation A &times; X = B.
         * <p>The A matrix is implicit here. It is </p>
         * @param b right-hand side of the equation A &times; X = B
         * @return a vector X that minimizes the two norm of A &times; X - B
         * @throws IllegalArgumentException if matrices dimensions don't match
         * @throws InvalidMatrixException if decomposed matrix is singular
         */
        public ArrayRealVector solve(ArrayRealVector b)
        throws IllegalArgumentException, InvalidMatrixException {
            return new ArrayRealVector(solve(b.getDataRef()), false);
        }

        /** {@inheritDoc} */
        public RealMatrix solve(RealMatrix b)
        throws IllegalArgumentException, InvalidMatrixException {

            final int n = qrt.length;
            final int m = qrt[0].length;
            if (b.getRowDimension() != m) {
                throw MathRuntimeException.createIllegalArgumentException(
                        LocalizedFormats.DIMENSIONS_MISMATCH_2x2,
                        b.getRowDimension(), b.getColumnDimension(), m, "n");
            }
            if (!isNonSingular()) {
                throw new SingularMatrixException();
            }

            final int columns        = b.getColumnDimension();
            final int blockSize      = BlockRealMatrix.BLOCK_SIZE;
            final int cBlocks        = (columns + blockSize - 1) / blockSize;
            final double[][] xBlocks = BlockRealMatrix.createBlocksLayout(n, columns);
            final double[][] y       = new double[b.getRowDimension()][blockSize];
            final double[]   alpha   = new double[blockSize];

            for (int kBlock = 0; kBlock < cBlocks; ++kBlock) {
                final int kStart = kBlock * blockSize;
                final int kEnd   = FastMath.min(kStart + blockSize, columns);
                final int kWidth = kEnd - kStart;

                // get the right hand side vector
                b.copySubMatrix(0, m - 1, kStart, kEnd - 1, y);

                // apply Householder transforms to solve Q.y = b
                for (int minor = 0; minor < FastMath.min(m, n); minor++) {
                    final double[] qrtMinor = qrt[minor];
                    final double factor     = 1.0 / (rDiag[minor] * qrtMinor[minor]);

                    Arrays.fill(alpha, 0, kWidth, 0.0);
                    for (int row = minor; row < m; ++row) {
                        final double   d    = qrtMinor[row];
                        final double[] yRow = y[row];
                        for (int k = 0; k < kWidth; ++k) {
                            alpha[k] += d * yRow[k];
                        }
                    }
                    for (int k = 0; k < kWidth; ++k) {
                        alpha[k] *= factor;
                    }

                    for (int row = minor; row < m; ++row) {
                        final double   d    = qrtMinor[row];
                        final double[] yRow = y[row];
                        for (int k = 0; k < kWidth; ++k) {
                            yRow[k] += alpha[k] * d;
                        }
                    }

                }

                // solve triangular system R.x = y
                for (int j = rDiag.length - 1; j >= 0; --j) {
                    final int      jBlock = j / blockSize;
                    final int      jStart = jBlock * blockSize;
                    final double   factor = 1.0 / rDiag[j];
                    final double[] yJ     = y[j];
                    final double[] xBlock = xBlocks[jBlock * cBlocks + kBlock];
                    int index = (j - jStart) * kWidth;
                    for (int k = 0; k < kWidth; ++k) {
                        yJ[k]          *= factor;
                        xBlock[index++] = yJ[k];
                    }

                    final double[] qrtJ = qrt[j];
                    for (int i = 0; i < j; ++i) {
                        final double rIJ  = qrtJ[i];
                        final double[] yI = y[i];
                        for (int k = 0; k < kWidth; ++k) {
                            yI[k] -= yJ[k] * rIJ;
                        }
                    }

                }

            }

            return new BlockRealMatrix(n, columns, xBlocks, false);

        }

        /** {@inheritDoc} */
        public RealMatrix getInverse()
        throws InvalidMatrixException {
            return solve(MatrixUtils.createRealIdentityMatrix(rDiag.length));
        }

    }

}
