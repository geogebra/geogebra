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

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;

/**
 * Calculates the compact Singular Value Decomposition of a matrix.
 * <p>
 * The Singular Value Decomposition of matrix A is a set of three matrices: U,
 * &Sigma; and V such that A = U &times; &Sigma; &times; V<sup>T</sup>. Let A be
 * a m &times; n matrix, then U is a m &times; p orthogonal matrix, &Sigma; is a
 * p &times; p diagonal matrix with positive or null elements, V is a p &times;
 * n orthogonal matrix (hence V<sup>T</sup> is also orthogonal) where
 * p=min(m,n).
 * </p>
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 * @since 2.0
 */
public class SingularValueDecompositionImpl implements
        SingularValueDecomposition {

    /** Number of rows of the initial matrix. */
    private int m;

    /** Number of columns of the initial matrix. */
    private int n;

    /** Eigen decomposition of the tridiagonal matrix. */
    private EigenDecomposition eigenDecomposition;

    /** Singular values. */
    private double[] singularValues;

    /** Cached value of U. */
    private RealMatrix cachedU;

    /** Cached value of U<sup>T</sup>. */
    private RealMatrix cachedUt;

    /** Cached value of S. */
    private RealMatrix cachedS;

    /** Cached value of V. */
    private RealMatrix cachedV;

    /** Cached value of V<sup>T</sup>. */
    private RealMatrix cachedVt;

    /**
     * Calculates the compact Singular Value Decomposition of the given matrix.
     * @param matrix
     *            The matrix to decompose.
     * @exception InvalidMatrixException
     *                (wrapping a
     *                {@link org.apache.commons.math.ConvergenceException} if
     *                algorithm fails to converge
     */
    public SingularValueDecompositionImpl(final RealMatrix matrix)
            throws InvalidMatrixException {

        m = matrix.getRowDimension();
        n = matrix.getColumnDimension();

        cachedU = null;
        cachedS = null;
        cachedV = null;
        cachedVt = null;

        double[][] localcopy = matrix.getData();
        double[][] matATA = new double[n][n];
        //
        // create A^T*A
        //
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                matATA[i][j] = 0.0;
                for (int k = 0; k < m; k++) {
                    matATA[i][j] += localcopy[k][i] * localcopy[k][j];
                }
                matATA[j][i]=matATA[i][j];
            }
        }

        double[][] matAAT = new double[m][m];
        //
        // create A*A^T
        //
        for (int i = 0; i < m; i++) {
            for (int j = i; j < m; j++) {
                matAAT[i][j] = 0.0;
                for (int k = 0; k < n; k++) {
                    matAAT[i][j] += localcopy[i][k] * localcopy[j][k];
                }
                 matAAT[j][i]=matAAT[i][j];
            }
        }
        int p;
        if (m>=n) {
            p=n;
            // compute eigen decomposition of A^T*A
            eigenDecomposition = new EigenDecompositionImpl(
                    new Array2DRowRealMatrix(matATA),1.0);
            singularValues = eigenDecomposition.getRealEigenvalues();
            cachedV = eigenDecomposition.getV();
            // compute eigen decomposition of A*A^T
            eigenDecomposition = new EigenDecompositionImpl(
                    new Array2DRowRealMatrix(matAAT),1.0);
            cachedU = eigenDecomposition.getV().getSubMatrix(0, m - 1, 0, p - 1);
        } else {
            p=m;
            // compute eigen decomposition of A*A^T
            eigenDecomposition = new EigenDecompositionImpl(
                    new Array2DRowRealMatrix(matAAT),1.0);
            singularValues = eigenDecomposition.getRealEigenvalues();
            cachedU = eigenDecomposition.getV();

            // compute eigen decomposition of A^T*A
            eigenDecomposition = new EigenDecompositionImpl(
                    new Array2DRowRealMatrix(matATA),1.0);
            cachedV = eigenDecomposition.getV().getSubMatrix(0,n-1,0,p-1);
        }
        for (int i = 0; i < p; i++) {
            singularValues[i] = FastMath.sqrt(FastMath.abs(singularValues[i]));
        }
        // Up to this point, U and V are computed independently of each other.
        // There still a sign indetermination of each column of, say, U.
        // The sign is set such that A.V_i=sigma_i.U_i (i<=p)
        // The right sign corresponds to a positive dot product of A.V_i and U_i
        for (int i = 0; i < p; i++) {
          RealVector tmp = cachedU.getColumnVector(i);
          double product=matrix.operate(cachedV.getColumnVector(i)).dotProduct(tmp);
          if (product<0) {
            cachedU.setColumnVector(i, tmp.mapMultiply(-1.0));
          }
        }
    }

    /** {@inheritDoc} */
    public RealMatrix getU() throws InvalidMatrixException {
        // return the cached matrix
        return cachedU;

    }

    /** {@inheritDoc} */
    public RealMatrix getUT() throws InvalidMatrixException {

        if (cachedUt == null) {
            cachedUt = getU().transpose();
        }

        // return the cached matrix
        return cachedUt;

    }

    /** {@inheritDoc} */
    public RealMatrix getS() throws InvalidMatrixException {

        if (cachedS == null) {

            // cache the matrix for subsequent calls
            cachedS = MatrixUtils.createRealDiagonalMatrix(singularValues);

        }
        return cachedS;
    }

    /** {@inheritDoc} */
    public double[] getSingularValues() throws InvalidMatrixException {
        return singularValues.clone();
    }

    /** {@inheritDoc} */
    public RealMatrix getV() throws InvalidMatrixException {
        // return the cached matrix
        return cachedV;

    }

    /** {@inheritDoc} */
    public RealMatrix getVT() throws InvalidMatrixException {

        if (cachedVt == null) {
            cachedVt = getV().transpose();
        }

        // return the cached matrix
        return cachedVt;

    }

    /** {@inheritDoc} */
    public RealMatrix getCovariance(final double minSingularValue) {

        // get the number of singular values to consider
        final int p = singularValues.length;
        int dimension = 0;
        while ((dimension < p) && (singularValues[dimension] >= minSingularValue)) {
            ++dimension;
        }

        if (dimension == 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.TOO_LARGE_CUTOFF_SINGULAR_VALUE,
                    minSingularValue, singularValues[0]);
        }

        final double[][] data = new double[dimension][p];
        getVT().walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
            /** {@inheritDoc} */
            @Override
            public void visit(final int row, final int column,
                    final double value) {
                data[row][column] = value / singularValues[row];
            }
        }, 0, dimension - 1, 0, p - 1);

        RealMatrix jv = new Array2DRowRealMatrix(data, false);
        return jv.transpose().multiply(jv);

    }

    /** {@inheritDoc} */
    public double getNorm() throws InvalidMatrixException {
        return singularValues[0];
    }

    /** {@inheritDoc} */
    public double getConditionNumber() throws InvalidMatrixException {
        return singularValues[0] / singularValues[singularValues.length - 1];
    }

    /** {@inheritDoc} */
    public int getRank() throws IllegalStateException {

        final double threshold = FastMath.max(m, n) * FastMath.ulp(singularValues[0]);

        for (int i = singularValues.length - 1; i >= 0; --i) {
            if (singularValues[i] > threshold) {
                return i + 1;
            }
        }
        return 0;

    }

    /** {@inheritDoc} */
    public DecompositionSolver getSolver() {
        return new Solver(singularValues, getUT(), getV(), getRank() == Math
                .max(m, n));
    }

    /** Specialized solver. */
    private static class Solver implements DecompositionSolver {

        /** Pseudo-inverse of the initial matrix. */
        private final RealMatrix pseudoInverse;

        /** Singularity indicator. */
        private boolean nonSingular;

        /**
         * Build a solver from decomposed matrix.
         * @param singularValues
         *            singularValues
         * @param uT
         *            U<sup>T</sup> matrix of the decomposition
         * @param v
         *            V matrix of the decomposition
         * @param nonSingular
         *            singularity indicator
         */
        private Solver(final double[] singularValues, final RealMatrix uT,
                final RealMatrix v, final boolean nonSingular) {
            double[][] suT = uT.getData();
            for (int i = 0; i < singularValues.length; ++i) {
                final double a;
                if (singularValues[i]>0) {
                 a=1.0 / singularValues[i];
                } else {
                 a=0.0;
                }
                final double[] suTi = suT[i];
                for (int j = 0; j < suTi.length; ++j) {
                    suTi[j] *= a;
                }
            }
            pseudoInverse = v.multiply(new Array2DRowRealMatrix(suT, false));
            this.nonSingular = nonSingular;
        }

        /**
         * Solve the linear equation A &times; X = B in least square sense.
         * <p>
         * The m&times;n matrix A may not be square, the solution X is such that
         * ||A &times; X - B|| is minimal.
         * </p>
         * @param b
         *            right-hand side of the equation A &times; X = B
         * @return a vector X that minimizes the two norm of A &times; X - B
         * @exception IllegalArgumentException
         *                if matrices dimensions don't match
         */
        public double[] solve(final double[] b) throws IllegalArgumentException {
            return pseudoInverse.operate(b);
        }

        /**
         * Solve the linear equation A &times; X = B in least square sense.
         * <p>
         * The m&times;n matrix A may not be square, the solution X is such that
         * ||A &times; X - B|| is minimal.
         * </p>
         * @param b
         *            right-hand side of the equation A &times; X = B
         * @return a vector X that minimizes the two norm of A &times; X - B
         * @exception IllegalArgumentException
         *                if matrices dimensions don't match
         */
        public RealVector solve(final RealVector b)
                throws IllegalArgumentException {
            return pseudoInverse.operate(b);
        }

        /**
         * Solve the linear equation A &times; X = B in least square sense.
         * <p>
         * The m&times;n matrix A may not be square, the solution X is such that
         * ||A &times; X - B|| is minimal.
         * </p>
         * @param b
         *            right-hand side of the equation A &times; X = B
         * @return a matrix X that minimizes the two norm of A &times; X - B
         * @exception IllegalArgumentException
         *                if matrices dimensions don't match
         */
        public RealMatrix solve(final RealMatrix b)
                throws IllegalArgumentException {
            return pseudoInverse.multiply(b);
        }

        /**
         * Check if the decomposed matrix is non-singular.
         * @return true if the decomposed matrix is non-singular
         */
        public boolean isNonSingular() {
            return nonSingular;
        }

        /**
         * Get the pseudo-inverse of the decomposed matrix.
         * @return inverse matrix
         */
        public RealMatrix getInverse() {
            return pseudoInverse;
        }

    }

}
