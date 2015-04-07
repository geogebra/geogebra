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

package org.apache.commons.math.random;

import org.apache.commons.math.DimensionMismatchException;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.NotPositiveDefiniteMatrixException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.util.FastMath;

/**
 * A {@link RandomVectorGenerator} that generates vectors with with
 * correlated components.
 * <p>Random vectors with correlated components are built by combining
 * the uncorrelated components of another random vector in such a way that
 * the resulting correlations are the ones specified by a positive
 * definite covariance matrix.</p>
 * <p>The main use for correlated random vector generation is for Monte-Carlo
 * simulation of physical problems with several variables, for example to
 * generate error vectors to be added to a nominal vector. A particularly
 * interesting case is when the generated vector should be drawn from a <a
 * href="http://en.wikipedia.org/wiki/Multivariate_normal_distribution">
 * Multivariate Normal Distribution</a>. The approach using a Cholesky
 * decomposition is quite usual in this case. However, it can be extended
 * to other cases as long as the underlying random generator provides
 * {@link NormalizedRandomGenerator normalized values} like {@link
 * GaussianRandomGenerator} or {@link UniformRandomGenerator}.</p>
 * <p>Sometimes, the covariance matrix for a given simulation is not
 * strictly positive definite. This means that the correlations are
 * not all independent from each other. In this case, however, the non
 * strictly positive elements found during the Cholesky decomposition
 * of the covariance matrix should not be negative either, they
 * should be null. Another non-conventional extension handling this case
 * is used here. Rather than computing <code>C = U<sup>T</sup>.U</code>
 * where <code>C</code> is the covariance matrix and <code>U</code>
 * is an upper-triangular matrix, we compute <code>C = B.B<sup>T</sup></code>
 * where <code>B</code> is a rectangular matrix having
 * more rows than columns. The number of columns of <code>B</code> is
 * the rank of the covariance matrix, and it is the dimension of the
 * uncorrelated random vector that is needed to compute the component
 * of the correlated vector. This class handles this situation
 * automatically.</p>
 *
 * @version $Revision: 1043908 $ $Date: 2010-12-09 12:53:14 +0100 (jeu. 09 déc. 2010) $
 * @since 1.2
 */

public class CorrelatedRandomVectorGenerator
    implements RandomVectorGenerator {

    /** Mean vector. */
    private final double[] mean;

    /** Underlying generator. */
    private final NormalizedRandomGenerator generator;

    /** Storage for the normalized vector. */
    private final double[] normalized;

    /** Permutated Cholesky root of the covariance matrix. */
    private RealMatrix root;

    /** Rank of the covariance matrix. */
    private int rank;

    /** Simple constructor.
     * <p>Build a correlated random vector generator from its mean
     * vector and covariance matrix.</p>
     * @param mean expected mean values for all components
     * @param covariance covariance matrix
     * @param small diagonal elements threshold under which  column are
     * considered to be dependent on previous ones and are discarded
     * @param generator underlying generator for uncorrelated normalized
     * components
     * @exception IllegalArgumentException if there is a dimension
     * mismatch between the mean vector and the covariance matrix
     * @exception NotPositiveDefiniteMatrixException if the
     * covariance matrix is not strictly positive definite
     * @exception DimensionMismatchException if the mean and covariance
     * arrays dimensions don't match
     */
    public CorrelatedRandomVectorGenerator(double[] mean,
                                           RealMatrix covariance, double small,
                                           NormalizedRandomGenerator generator)
    throws NotPositiveDefiniteMatrixException, DimensionMismatchException {

        int order = covariance.getRowDimension();
        if (mean.length != order) {
            throw new DimensionMismatchException(mean.length, order);
        }
        this.mean = mean.clone();

        decompose(covariance, small);

        this.generator = generator;
        normalized = new double[rank];

    }

    /** Simple constructor.
     * <p>Build a null mean random correlated vector generator from its
     * covariance matrix.</p>
     * @param covariance covariance matrix
     * @param small diagonal elements threshold under which  column are
     * considered to be dependent on previous ones and are discarded
     * @param generator underlying generator for uncorrelated normalized
     * components
     * @exception NotPositiveDefiniteMatrixException if the
     * covariance matrix is not strictly positive definite
     */
    public CorrelatedRandomVectorGenerator(RealMatrix covariance, double small,
                                           NormalizedRandomGenerator generator)
    throws NotPositiveDefiniteMatrixException {

        int order = covariance.getRowDimension();
        mean = new double[order];
        for (int i = 0; i < order; ++i) {
            mean[i] = 0;
        }

        decompose(covariance, small);

        this.generator = generator;
        normalized = new double[rank];

    }

    /** Get the underlying normalized components generator.
     * @return underlying uncorrelated components generator
     */
    public NormalizedRandomGenerator getGenerator() {
        return generator;
    }

    /** Get the root of the covariance matrix.
     * The root is the rectangular matrix <code>B</code> such that
     * the covariance matrix is equal to <code>B.B<sup>T</sup></code>
     * @return root of the square matrix
     * @see #getRank()
     */
    public RealMatrix getRootMatrix() {
        return root;
    }

    /** Get the rank of the covariance matrix.
     * The rank is the number of independent rows in the covariance
     * matrix, it is also the number of columns of the rectangular
     * matrix of the decomposition.
     * @return rank of the square matrix.
     * @see #getRootMatrix()
     */
    public int getRank() {
        return rank;
    }

    /** Decompose the original square matrix.
     * <p>The decomposition is based on a Choleski decomposition
     * where additional transforms are performed:
     * <ul>
     *   <li>the rows of the decomposed matrix are permuted</li>
     *   <li>columns with the too small diagonal element are discarded</li>
     *   <li>the matrix is permuted</li>
     * </ul>
     * This means that rather than computing M = U<sup>T</sup>.U where U
     * is an upper triangular matrix, this method computed M=B.B<sup>T</sup>
     * where B is a rectangular matrix.
     * @param covariance covariance matrix
     * @param small diagonal elements threshold under which  column are
     * considered to be dependent on previous ones and are discarded
     * @exception NotPositiveDefiniteMatrixException if the
     * covariance matrix is not strictly positive definite
     */
    private void decompose(RealMatrix covariance, double small)
    throws NotPositiveDefiniteMatrixException {

        int order = covariance.getRowDimension();
        double[][] c = covariance.getData();
        double[][] b = new double[order][order];

        int[] swap  = new int[order];
        int[] index = new int[order];
        for (int i = 0; i < order; ++i) {
            index[i] = i;
        }

        rank = 0;
        for (boolean loop = true; loop;) {

            // find maximal diagonal element
            swap[rank] = rank;
            for (int i = rank + 1; i < order; ++i) {
                int ii  = index[i];
                int isi = index[swap[i]];
                if (c[ii][ii] > c[isi][isi]) {
                    swap[rank] = i;
                }
            }


            // swap elements
            if (swap[rank] != rank) {
                int tmp = index[rank];
                index[rank] = index[swap[rank]];
                index[swap[rank]] = tmp;
            }

            // check diagonal element
            int ir = index[rank];
            if (c[ir][ir] < small) {

                if (rank == 0) {
                    throw new NotPositiveDefiniteMatrixException();
                }

                // check remaining diagonal elements
                for (int i = rank; i < order; ++i) {
                    if (c[index[i]][index[i]] < -small) {
                        // there is at least one sufficiently negative diagonal element,
                        // the covariance matrix is wrong
                        throw new NotPositiveDefiniteMatrixException();
                    }
                }

                // all remaining diagonal elements are close to zero,
                // we consider we have found the rank of the covariance matrix
                ++rank;
                loop = false;

            } else {

                // transform the matrix
                double sqrt = FastMath.sqrt(c[ir][ir]);
                b[rank][rank] = sqrt;
                double inverse = 1 / sqrt;
                for (int i = rank + 1; i < order; ++i) {
                    int ii = index[i];
                    double e = inverse * c[ii][ir];
                    b[i][rank] = e;
                    c[ii][ii] -= e * e;
                    for (int j = rank + 1; j < i; ++j) {
                        int ij = index[j];
                        double f = c[ii][ij] - e * b[j][rank];
                        c[ii][ij] = f;
                        c[ij][ii] = f;
                    }
                }

                // prepare next iteration
                loop = ++rank < order;

            }

        }

        // build the root matrix
        root = MatrixUtils.createRealMatrix(order, rank);
        for (int i = 0; i < order; ++i) {
            for (int j = 0; j < rank; ++j) {
                root.setEntry(index[i], j, b[i][j]);
            }
        }

    }

    /** Generate a correlated random vector.
     * @return a random vector as an array of double. The returned array
     * is created at each call, the caller can do what it wants with it.
     */
    public double[] nextVector() {

        // generate uncorrelated vector
        for (int i = 0; i < rank; ++i) {
            normalized[i] = generator.nextNormalizedDouble();
        }

        // compute correlated vector
        double[] correlated = new double[mean.length];
        for (int i = 0; i < correlated.length; ++i) {
            correlated[i] = mean[i];
            for (int j = 0; j < rank; ++j) {
                correlated[i] += root.getEntry(i, j) * normalized[j];
            }
        }

        return correlated;

    }

}
