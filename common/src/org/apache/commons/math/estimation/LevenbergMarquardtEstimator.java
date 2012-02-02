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
package org.apache.commons.math.estimation;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;


/**
 * This class solves a least squares problem.
 *
 * <p>This implementation <em>should</em> work even for over-determined systems
 * (i.e. systems having more variables than equations). Over-determined systems
 * are solved by ignoring the variables which have the smallest impact according
 * to their jacobian column norm. Only the rank of the matrix and some loop bounds
 * are changed to implement this.</p>
 *
 * <p>The resolution engine is a simple translation of the MINPACK <a
 * href="http://www.netlib.org/minpack/lmder.f">lmder</a> routine with minor
 * changes. The changes include the over-determined resolution and the Q.R.
 * decomposition which has been rewritten following the algorithm described in the
 * P. Lascaux and R. Theodor book <i>Analyse num&eacute;rique matricielle
 * appliqu&eacute;e &agrave; l'art de l'ing&eacute;nieur</i>, Masson 1986.</p>
 * <p>The authors of the original fortran version are:
 * <ul>
 * <li>Argonne National Laboratory. MINPACK project. March 1980</li>
 * <li>Burton S. Garbow</li>
 * <li>Kenneth E. Hillstrom</li>
 * <li>Jorge J. More</li>
 * </ul>
 * The redistribution policy for MINPACK is available <a
 * href="http://www.netlib.org/minpack/disclaimer">here</a>, for convenience, it
 * is reproduced below.</p>
 *
 * <table border="0" width="80%" cellpadding="10" align="center" bgcolor="#E0E0E0">
 * <tr><td>
 *    Minpack Copyright Notice (1999) University of Chicago.
 *    All rights reserved
 * </td></tr>
 * <tr><td>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * <ol>
 *  <li>Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.</li>
 * <li>Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.</li>
 * <li>The end-user documentation included with the redistribution, if any,
 *     must include the following acknowledgment:
 *     <code>This product includes software developed by the University of
 *           Chicago, as Operator of Argonne National Laboratory.</code>
 *     Alternately, this acknowledgment may appear in the software itself,
 *     if and wherever such third-party acknowledgments normally appear.</li>
 * <li><strong>WARRANTY DISCLAIMER. THE SOFTWARE IS SUPPLIED "AS IS"
 *     WITHOUT WARRANTY OF ANY KIND. THE COPYRIGHT HOLDER, THE
 *     UNITED STATES, THE UNITED STATES DEPARTMENT OF ENERGY, AND
 *     THEIR EMPLOYEES: (1) DISCLAIM ANY WARRANTIES, EXPRESS OR
 *     IMPLIED, INCLUDING BUT NOT LIMITED TO ANY IMPLIED WARRANTIES
 *     OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE
 *     OR NON-INFRINGEMENT, (2) DO NOT ASSUME ANY LEGAL LIABILITY
 *     OR RESPONSIBILITY FOR THE ACCURACY, COMPLETENESS, OR
 *     USEFULNESS OF THE SOFTWARE, (3) DO NOT REPRESENT THAT USE OF
 *     THE SOFTWARE WOULD NOT INFRINGE PRIVATELY OWNED RIGHTS, (4)
 *     DO NOT WARRANT THAT THE SOFTWARE WILL FUNCTION
 *     UNINTERRUPTED, THAT IT IS ERROR-FREE OR THAT ANY ERRORS WILL
 *     BE CORRECTED.</strong></li>
 * <li><strong>LIMITATION OF LIABILITY. IN NO EVENT WILL THE COPYRIGHT
 *     HOLDER, THE UNITED STATES, THE UNITED STATES DEPARTMENT OF
 *     ENERGY, OR THEIR EMPLOYEES: BE LIABLE FOR ANY INDIRECT,
 *     INCIDENTAL, CONSEQUENTIAL, SPECIAL OR PUNITIVE DAMAGES OF
 *     ANY KIND OR NATURE, INCLUDING BUT NOT LIMITED TO LOSS OF
 *     PROFITS OR LOSS OF DATA, FOR ANY REASON WHATSOEVER, WHETHER
 *     SUCH LIABILITY IS ASSERTED ON THE BASIS OF CONTRACT, TORT
 *     (INCLUDING NEGLIGENCE OR STRICT LIABILITY), OR OTHERWISE,
 *     EVEN IF ANY OF SAID PARTIES HAS BEEN WARNED OF THE
 *     POSSIBILITY OF SUCH LOSS OR DAMAGES.</strong></li>
 * <ol></td></tr>
 * </table>

 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 * @since 1.2
 * @deprecated as of 2.0, everything in package org.apache.commons.math.estimation has
 * been deprecated and replaced by package org.apache.commons.math.optimization.general
 *
 */
@Deprecated
public class LevenbergMarquardtEstimator extends AbstractEstimator implements Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = -5705952631533171019L;

    /** Number of solved variables. */
    private int solvedCols;

    /** Diagonal elements of the R matrix in the Q.R. decomposition. */
    private double[] diagR;

    /** Norms of the columns of the jacobian matrix. */
    private double[] jacNorm;

    /** Coefficients of the Householder transforms vectors. */
    private double[] beta;

    /** Columns permutation array. */
    private int[] permutation;

    /** Rank of the jacobian matrix. */
    private int rank;

    /** Levenberg-Marquardt parameter. */
    private double lmPar;

    /** Parameters evolution direction associated with lmPar. */
    private double[] lmDir;

    /** Positive input variable used in determining the initial step bound. */
    private double initialStepBoundFactor;

    /** Desired relative error in the sum of squares. */
    private double costRelativeTolerance;

    /**  Desired relative error in the approximate solution parameters. */
    private double parRelativeTolerance;

    /** Desired max cosine on the orthogonality between the function vector
     * and the columns of the jacobian. */
    private double orthoTolerance;

  /**
   * Build an estimator for least squares problems.
   * <p>The default values for the algorithm settings are:
   *   <ul>
   *    <li>{@link #setInitialStepBoundFactor initial step bound factor}: 100.0</li>
   *    <li>{@link #setMaxCostEval maximal cost evaluations}: 1000</li>
   *    <li>{@link #setCostRelativeTolerance cost relative tolerance}: 1.0e-10</li>
   *    <li>{@link #setParRelativeTolerance parameters relative tolerance}: 1.0e-10</li>
   *    <li>{@link #setOrthoTolerance orthogonality tolerance}: 1.0e-10</li>
   *   </ul>
   * </p>
   */
  public LevenbergMarquardtEstimator() {

    // set up the superclass with a default  max cost evaluations setting
    setMaxCostEval(1000);

    // default values for the tuning parameters
    setInitialStepBoundFactor(100.0);
    setCostRelativeTolerance(1.0e-10);
    setParRelativeTolerance(1.0e-10);
    setOrthoTolerance(1.0e-10);

  }

  /**
   * Set the positive input variable used in determining the initial step bound.
   * This bound is set to the product of initialStepBoundFactor and the euclidean norm of diag*x if nonzero,
   * or else to initialStepBoundFactor itself. In most cases factor should lie
   * in the interval (0.1, 100.0). 100.0 is a generally recommended value
   *
   * @param initialStepBoundFactor initial step bound factor
   * @see #estimate
   */
  public void setInitialStepBoundFactor(double initialStepBoundFactor) {
    this.initialStepBoundFactor = initialStepBoundFactor;
  }

  /**
   * Set the desired relative error in the sum of squares.
   *
   * @param costRelativeTolerance desired relative error in the sum of squares
   * @see #estimate
   */
  public void setCostRelativeTolerance(double costRelativeTolerance) {
    this.costRelativeTolerance = costRelativeTolerance;
  }

  /**
   * Set the desired relative error in the approximate solution parameters.
   *
   * @param parRelativeTolerance desired relative error
   * in the approximate solution parameters
   * @see #estimate
   */
  public void setParRelativeTolerance(double parRelativeTolerance) {
    this.parRelativeTolerance = parRelativeTolerance;
  }

  /**
   * Set the desired max cosine on the orthogonality.
   *
   * @param orthoTolerance desired max cosine on the orthogonality
   * between the function vector and the columns of the jacobian
   * @see #estimate
   */
  public void setOrthoTolerance(double orthoTolerance) {
    this.orthoTolerance = orthoTolerance;
  }

  /**
   * Solve an estimation problem using the Levenberg-Marquardt algorithm.
   * <p>The algorithm used is a modified Levenberg-Marquardt one, based
   * on the MINPACK <a href="http://www.netlib.org/minpack/lmder.f">lmder</a>
   * routine. The algorithm settings must have been set up before this method
   * is called with the {@link #setInitialStepBoundFactor},
   * {@link #setMaxCostEval}, {@link #setCostRelativeTolerance},
   * {@link #setParRelativeTolerance} and {@link #setOrthoTolerance} methods.
   * If these methods have not been called, the default values set up by the
   * {@link #LevenbergMarquardtEstimator() constructor} will be used.</p>
   * <p>The authors of the original fortran function are:</p>
   * <ul>
   *   <li>Argonne National Laboratory. MINPACK project. March 1980</li>
   *   <li>Burton  S. Garbow</li>
   *   <li>Kenneth E. Hillstrom</li>
   *   <li>Jorge   J. More</li>
   *   </ul>
   * <p>Luc Maisonobe did the Java translation.</p>
   *
   * @param problem estimation problem to solve
   * @exception EstimationException if convergence cannot be
   * reached with the specified algorithm settings or if there are more variables
   * than equations
   * @see #setInitialStepBoundFactor
   * @see #setCostRelativeTolerance
   * @see #setParRelativeTolerance
   * @see #setOrthoTolerance
   */
  @Override
  public void estimate(EstimationProblem problem)
    throws EstimationException {

    initializeEstimate(problem);

    // arrays shared with the other private methods
    solvedCols  = FastMath.min(rows, cols);
    diagR       = new double[cols];
    jacNorm     = new double[cols];
    beta        = new double[cols];
    permutation = new int[cols];
    lmDir       = new double[cols];

    // local variables
    double   delta   = 0;
    double   xNorm = 0;
    double[] diag    = new double[cols];
    double[] oldX    = new double[cols];
    double[] oldRes  = new double[rows];
    double[] work1   = new double[cols];
    double[] work2   = new double[cols];
    double[] work3   = new double[cols];

    // evaluate the function at the starting point and calculate its norm
    updateResidualsAndCost();

    // outer loop
    lmPar = 0;
    boolean firstIteration = true;
    while (true) {

      // compute the Q.R. decomposition of the jacobian matrix
      updateJacobian();
      qrDecomposition();

      // compute Qt.res
      qTy(residuals);

      // now we don't need Q anymore,
      // so let jacobian contain the R matrix with its diagonal elements
      for (int k = 0; k < solvedCols; ++k) {
        int pk = permutation[k];
        jacobian[k * cols + pk] = diagR[pk];
      }

      if (firstIteration) {

        // scale the variables according to the norms of the columns
        // of the initial jacobian
        xNorm = 0;
        for (int k = 0; k < cols; ++k) {
          double dk = jacNorm[k];
          if (dk == 0) {
            dk = 1.0;
          }
          double xk = dk * parameters[k].getEstimate();
          xNorm  += xk * xk;
          diag[k] = dk;
        }
        xNorm = FastMath.sqrt(xNorm);

        // initialize the step bound delta
        delta = (xNorm == 0) ? initialStepBoundFactor : (initialStepBoundFactor * xNorm);

      }

      // check orthogonality between function vector and jacobian columns
      double maxCosine = 0;
      if (cost != 0) {
        for (int j = 0; j < solvedCols; ++j) {
          int    pj = permutation[j];
          double s  = jacNorm[pj];
          if (s != 0) {
            double sum = 0;
            int index = pj;
            for (int i = 0; i <= j; ++i) {
              sum += jacobian[index] * residuals[i];
              index += cols;
            }
            maxCosine = FastMath.max(maxCosine, FastMath.abs(sum) / (s * cost));
          }
        }
      }
      if (maxCosine <= orthoTolerance) {
        return;
      }

      // rescale if necessary
      for (int j = 0; j < cols; ++j) {
        diag[j] = FastMath.max(diag[j], jacNorm[j]);
      }

      // inner loop
      for (double ratio = 0; ratio < 1.0e-4;) {

        // save the state
        for (int j = 0; j < solvedCols; ++j) {
          int pj = permutation[j];
          oldX[pj] = parameters[pj].getEstimate();
        }
        double previousCost = cost;
        double[] tmpVec = residuals;
        residuals = oldRes;
        oldRes    = tmpVec;

        // determine the Levenberg-Marquardt parameter
        determineLMParameter(oldRes, delta, diag, work1, work2, work3);

        // compute the new point and the norm of the evolution direction
        double lmNorm = 0;
        for (int j = 0; j < solvedCols; ++j) {
          int pj = permutation[j];
          lmDir[pj] = -lmDir[pj];
          parameters[pj].setEstimate(oldX[pj] + lmDir[pj]);
          double s = diag[pj] * lmDir[pj];
          lmNorm  += s * s;
        }
        lmNorm = FastMath.sqrt(lmNorm);

        // on the first iteration, adjust the initial step bound.
        if (firstIteration) {
          delta = FastMath.min(delta, lmNorm);
        }

        // evaluate the function at x + p and calculate its norm
        updateResidualsAndCost();

        // compute the scaled actual reduction
        double actRed = -1.0;
        if (0.1 * cost < previousCost) {
          double r = cost / previousCost;
          actRed = 1.0 - r * r;
        }

        // compute the scaled predicted reduction
        // and the scaled directional derivative
        for (int j = 0; j < solvedCols; ++j) {
          int pj = permutation[j];
          double dirJ = lmDir[pj];
          work1[j] = 0;
          int index = pj;
          for (int i = 0; i <= j; ++i) {
            work1[i] += jacobian[index] * dirJ;
            index += cols;
          }
        }
        double coeff1 = 0;
        for (int j = 0; j < solvedCols; ++j) {
         coeff1 += work1[j] * work1[j];
        }
        double pc2 = previousCost * previousCost;
        coeff1 = coeff1 / pc2;
        double coeff2 = lmPar * lmNorm * lmNorm / pc2;
        double preRed = coeff1 + 2 * coeff2;
        double dirDer = -(coeff1 + coeff2);

        // ratio of the actual to the predicted reduction
        ratio = (preRed == 0) ? 0 : (actRed / preRed);

        // update the step bound
        if (ratio <= 0.25) {
          double tmp =
            (actRed < 0) ? (0.5 * dirDer / (dirDer + 0.5 * actRed)) : 0.5;
          if ((0.1 * cost >= previousCost) || (tmp < 0.1)) {
            tmp = 0.1;
          }
          delta = tmp * FastMath.min(delta, 10.0 * lmNorm);
          lmPar /= tmp;
        } else if ((lmPar == 0) || (ratio >= 0.75)) {
          delta = 2 * lmNorm;
          lmPar *= 0.5;
        }

        // test for successful iteration.
        if (ratio >= 1.0e-4) {
          // successful iteration, update the norm
          firstIteration = false;
          xNorm = 0;
          for (int k = 0; k < cols; ++k) {
            double xK = diag[k] * parameters[k].getEstimate();
            xNorm    += xK * xK;
          }
          xNorm = FastMath.sqrt(xNorm);
        } else {
          // failed iteration, reset the previous values
          cost = previousCost;
          for (int j = 0; j < solvedCols; ++j) {
            int pj = permutation[j];
            parameters[pj].setEstimate(oldX[pj]);
          }
          tmpVec    = residuals;
          residuals = oldRes;
          oldRes    = tmpVec;
        }

        // tests for convergence.
        if (((FastMath.abs(actRed) <= costRelativeTolerance) &&
             (preRed <= costRelativeTolerance) &&
             (ratio <= 2.0)) ||
             (delta <= parRelativeTolerance * xNorm)) {
          return;
        }

        // tests for termination and stringent tolerances
        // (2.2204e-16 is the machine epsilon for IEEE754)
        if ((FastMath.abs(actRed) <= 2.2204e-16) && (preRed <= 2.2204e-16) && (ratio <= 2.0)) {
          throw new EstimationException("cost relative tolerance is too small ({0})," +
                                        " no further reduction in the" +
                                        " sum of squares is possible",
                                        costRelativeTolerance);
        } else if (delta <= 2.2204e-16 * xNorm) {
          throw new EstimationException("parameters relative tolerance is too small" +
                                        " ({0}), no further improvement in" +
                                        " the approximate solution is possible",
                                        parRelativeTolerance);
        } else if (maxCosine <= 2.2204e-16)  {
          throw new EstimationException("orthogonality tolerance is too small ({0})," +
                                        " solution is orthogonal to the jacobian",
                                        orthoTolerance);
        }

      }

    }

  }

  /**
   * Determine the Levenberg-Marquardt parameter.
   * <p>This implementation is a translation in Java of the MINPACK
   * <a href="http://www.netlib.org/minpack/lmpar.f">lmpar</a>
   * routine.</p>
   * <p>This method sets the lmPar and lmDir attributes.</p>
   * <p>The authors of the original fortran function are:</p>
   * <ul>
   *   <li>Argonne National Laboratory. MINPACK project. March 1980</li>
   *   <li>Burton  S. Garbow</li>
   *   <li>Kenneth E. Hillstrom</li>
   *   <li>Jorge   J. More</li>
   * </ul>
   * <p>Luc Maisonobe did the Java translation.</p>
   *
   * @param qy array containing qTy
   * @param delta upper bound on the euclidean norm of diagR * lmDir
   * @param diag diagonal matrix
   * @param work1 work array
   * @param work2 work array
   * @param work3 work array
   */
  private void determineLMParameter(double[] qy, double delta, double[] diag,
                                    double[] work1, double[] work2, double[] work3) {

    // compute and store in x the gauss-newton direction, if the
    // jacobian is rank-deficient, obtain a least squares solution
    for (int j = 0; j < rank; ++j) {
      lmDir[permutation[j]] = qy[j];
    }
    for (int j = rank; j < cols; ++j) {
      lmDir[permutation[j]] = 0;
    }
    for (int k = rank - 1; k >= 0; --k) {
      int pk = permutation[k];
      double ypk = lmDir[pk] / diagR[pk];
      int index = pk;
      for (int i = 0; i < k; ++i) {
        lmDir[permutation[i]] -= ypk * jacobian[index];
        index += cols;
      }
      lmDir[pk] = ypk;
    }

    // evaluate the function at the origin, and test
    // for acceptance of the Gauss-Newton direction
    double dxNorm = 0;
    for (int j = 0; j < solvedCols; ++j) {
      int pj = permutation[j];
      double s = diag[pj] * lmDir[pj];
      work1[pj] = s;
      dxNorm += s * s;
    }
    dxNorm = FastMath.sqrt(dxNorm);
    double fp = dxNorm - delta;
    if (fp <= 0.1 * delta) {
      lmPar = 0;
      return;
    }

    // if the jacobian is not rank deficient, the Newton step provides
    // a lower bound, parl, for the zero of the function,
    // otherwise set this bound to zero
    double sum2;
    double parl = 0;
    if (rank == solvedCols) {
      for (int j = 0; j < solvedCols; ++j) {
        int pj = permutation[j];
        work1[pj] *= diag[pj] / dxNorm;
      }
      sum2 = 0;
      for (int j = 0; j < solvedCols; ++j) {
        int pj = permutation[j];
        double sum = 0;
        int index = pj;
        for (int i = 0; i < j; ++i) {
          sum += jacobian[index] * work1[permutation[i]];
          index += cols;
        }
        double s = (work1[pj] - sum) / diagR[pj];
        work1[pj] = s;
        sum2 += s * s;
      }
      parl = fp / (delta * sum2);
    }

    // calculate an upper bound, paru, for the zero of the function
    sum2 = 0;
    for (int j = 0; j < solvedCols; ++j) {
      int pj = permutation[j];
      double sum = 0;
      int index = pj;
      for (int i = 0; i <= j; ++i) {
        sum += jacobian[index] * qy[i];
        index += cols;
      }
      sum /= diag[pj];
      sum2 += sum * sum;
    }
    double gNorm = FastMath.sqrt(sum2);
    double paru = gNorm / delta;
    if (paru == 0) {
      // 2.2251e-308 is the smallest positive real for IEE754
      paru = 2.2251e-308 / FastMath.min(delta, 0.1);
    }

    // if the input par lies outside of the interval (parl,paru),
    // set par to the closer endpoint
    lmPar = FastMath.min(paru, FastMath.max(lmPar, parl));
    if (lmPar == 0) {
      lmPar = gNorm / dxNorm;
    }

    for (int countdown = 10; countdown >= 0; --countdown) {

      // evaluate the function at the current value of lmPar
      if (lmPar == 0) {
        lmPar = FastMath.max(2.2251e-308, 0.001 * paru);
      }
      double sPar = FastMath.sqrt(lmPar);
      for (int j = 0; j < solvedCols; ++j) {
        int pj = permutation[j];
        work1[pj] = sPar * diag[pj];
      }
      determineLMDirection(qy, work1, work2, work3);

      dxNorm = 0;
      for (int j = 0; j < solvedCols; ++j) {
        int pj = permutation[j];
        double s = diag[pj] * lmDir[pj];
        work3[pj] = s;
        dxNorm += s * s;
      }
      dxNorm = FastMath.sqrt(dxNorm);
      double previousFP = fp;
      fp = dxNorm - delta;

      // if the function is small enough, accept the current value
      // of lmPar, also test for the exceptional cases where parl is zero
      if ((FastMath.abs(fp) <= 0.1 * delta) ||
          ((parl == 0) && (fp <= previousFP) && (previousFP < 0))) {
        return;
      }

      // compute the Newton correction
      for (int j = 0; j < solvedCols; ++j) {
       int pj = permutation[j];
        work1[pj] = work3[pj] * diag[pj] / dxNorm;
      }
      for (int j = 0; j < solvedCols; ++j) {
        int pj = permutation[j];
        work1[pj] /= work2[j];
        double tmp = work1[pj];
        for (int i = j + 1; i < solvedCols; ++i) {
          work1[permutation[i]] -= jacobian[i * cols + pj] * tmp;
        }
      }
      sum2 = 0;
      for (int j = 0; j < solvedCols; ++j) {
        double s = work1[permutation[j]];
        sum2 += s * s;
      }
      double correction = fp / (delta * sum2);

      // depending on the sign of the function, update parl or paru.
      if (fp > 0) {
        parl = FastMath.max(parl, lmPar);
      } else if (fp < 0) {
        paru = FastMath.min(paru, lmPar);
      }

      // compute an improved estimate for lmPar
      lmPar = FastMath.max(parl, lmPar + correction);

    }
  }

  /**
   * Solve a*x = b and d*x = 0 in the least squares sense.
   * <p>This implementation is a translation in Java of the MINPACK
   * <a href="http://www.netlib.org/minpack/qrsolv.f">qrsolv</a>
   * routine.</p>
   * <p>This method sets the lmDir and lmDiag attributes.</p>
   * <p>The authors of the original fortran function are:</p>
   * <ul>
   *   <li>Argonne National Laboratory. MINPACK project. March 1980</li>
   *   <li>Burton  S. Garbow</li>
   *   <li>Kenneth E. Hillstrom</li>
   *   <li>Jorge   J. More</li>
   * </ul>
   * <p>Luc Maisonobe did the Java translation.</p>
   *
   * @param qy array containing qTy
   * @param diag diagonal matrix
   * @param lmDiag diagonal elements associated with lmDir
   * @param work work array
   */
  private void determineLMDirection(double[] qy, double[] diag,
                                    double[] lmDiag, double[] work) {

    // copy R and Qty to preserve input and initialize s
    //  in particular, save the diagonal elements of R in lmDir
    for (int j = 0; j < solvedCols; ++j) {
      int pj = permutation[j];
      for (int i = j + 1; i < solvedCols; ++i) {
        jacobian[i * cols + pj] = jacobian[j * cols + permutation[i]];
      }
      lmDir[j] = diagR[pj];
      work[j]  = qy[j];
    }

    // eliminate the diagonal matrix d using a Givens rotation
    for (int j = 0; j < solvedCols; ++j) {

      // prepare the row of d to be eliminated, locating the
      // diagonal element using p from the Q.R. factorization
      int pj = permutation[j];
      double dpj = diag[pj];
      if (dpj != 0) {
        Arrays.fill(lmDiag, j + 1, lmDiag.length, 0);
      }
      lmDiag[j] = dpj;

      //  the transformations to eliminate the row of d
      // modify only a single element of Qty
      // beyond the first n, which is initially zero.
      double qtbpj = 0;
      for (int k = j; k < solvedCols; ++k) {
        int pk = permutation[k];

        // determine a Givens rotation which eliminates the
        // appropriate element in the current row of d
        if (lmDiag[k] != 0) {

          final double sin;
          final double cos;
          double rkk = jacobian[k * cols + pk];
          if (FastMath.abs(rkk) < FastMath.abs(lmDiag[k])) {
            final double cotan = rkk / lmDiag[k];
            sin   = 1.0 / FastMath.sqrt(1.0 + cotan * cotan);
            cos   = sin * cotan;
          } else {
            final double tan = lmDiag[k] / rkk;
            cos = 1.0 / FastMath.sqrt(1.0 + tan * tan);
            sin = cos * tan;
          }

          // compute the modified diagonal element of R and
          // the modified element of (Qty,0)
          jacobian[k * cols + pk] = cos * rkk + sin * lmDiag[k];
          final double temp = cos * work[k] + sin * qtbpj;
          qtbpj = -sin * work[k] + cos * qtbpj;
          work[k] = temp;

          // accumulate the tranformation in the row of s
          for (int i = k + 1; i < solvedCols; ++i) {
            double rik = jacobian[i * cols + pk];
            final double temp2 = cos * rik + sin * lmDiag[i];
            lmDiag[i] = -sin * rik + cos * lmDiag[i];
            jacobian[i * cols + pk] = temp2;
          }

        }
      }

      // store the diagonal element of s and restore
      // the corresponding diagonal element of R
      int index = j * cols + permutation[j];
      lmDiag[j]       = jacobian[index];
      jacobian[index] = lmDir[j];

    }

    // solve the triangular system for z, if the system is
    // singular, then obtain a least squares solution
    int nSing = solvedCols;
    for (int j = 0; j < solvedCols; ++j) {
      if ((lmDiag[j] == 0) && (nSing == solvedCols)) {
        nSing = j;
      }
      if (nSing < solvedCols) {
        work[j] = 0;
      }
    }
    if (nSing > 0) {
      for (int j = nSing - 1; j >= 0; --j) {
        int pj = permutation[j];
        double sum = 0;
        for (int i = j + 1; i < nSing; ++i) {
          sum += jacobian[i * cols + pj] * work[i];
        }
        work[j] = (work[j] - sum) / lmDiag[j];
      }
    }

    // permute the components of z back to components of lmDir
    for (int j = 0; j < lmDir.length; ++j) {
      lmDir[permutation[j]] = work[j];
    }

  }

  /**
   * Decompose a matrix A as A.P = Q.R using Householder transforms.
   * <p>As suggested in the P. Lascaux and R. Theodor book
   * <i>Analyse num&eacute;rique matricielle appliqu&eacute;e &agrave;
   * l'art de l'ing&eacute;nieur</i> (Masson, 1986), instead of representing
   * the Householder transforms with u<sub>k</sub> unit vectors such that:
   * <pre>
   * H<sub>k</sub> = I - 2u<sub>k</sub>.u<sub>k</sub><sup>t</sup>
   * </pre>
   * we use <sub>k</sub> non-unit vectors such that:
   * <pre>
   * H<sub>k</sub> = I - beta<sub>k</sub>v<sub>k</sub>.v<sub>k</sub><sup>t</sup>
   * </pre>
   * where v<sub>k</sub> = a<sub>k</sub> - alpha<sub>k</sub> e<sub>k</sub>.
   * The beta<sub>k</sub> coefficients are provided upon exit as recomputing
   * them from the v<sub>k</sub> vectors would be costly.</p>
   * <p>This decomposition handles rank deficient cases since the tranformations
   * are performed in non-increasing columns norms order thanks to columns
   * pivoting. The diagonal elements of the R matrix are therefore also in
   * non-increasing absolute values order.</p>
   * @exception EstimationException if the decomposition cannot be performed
   */
  private void qrDecomposition() throws EstimationException {

    // initializations
    for (int k = 0; k < cols; ++k) {
      permutation[k] = k;
      double norm2 = 0;
      for (int index = k; index < jacobian.length; index += cols) {
        double akk = jacobian[index];
        norm2 += akk * akk;
      }
      jacNorm[k] = FastMath.sqrt(norm2);
    }

    // transform the matrix column after column
    for (int k = 0; k < cols; ++k) {

      // select the column with the greatest norm on active components
      int nextColumn = -1;
      double ak2 = Double.NEGATIVE_INFINITY;
      for (int i = k; i < cols; ++i) {
        double norm2 = 0;
        int iDiag = k * cols + permutation[i];
        for (int index = iDiag; index < jacobian.length; index += cols) {
          double aki = jacobian[index];
          norm2 += aki * aki;
        }
        if (Double.isInfinite(norm2) || Double.isNaN(norm2)) {
            throw new EstimationException(
                    LocalizedFormats.UNABLE_TO_PERFORM_QR_DECOMPOSITION_ON_JACOBIAN,
                    rows, cols);
        }
        if (norm2 > ak2) {
          nextColumn = i;
          ak2        = norm2;
        }
      }
      if (ak2 == 0) {
        rank = k;
        return;
      }
      int pk                  = permutation[nextColumn];
      permutation[nextColumn] = permutation[k];
      permutation[k]          = pk;

      // choose alpha such that Hk.u = alpha ek
      int    kDiag = k * cols + pk;
      double akk   = jacobian[kDiag];
      double alpha = (akk > 0) ? -FastMath.sqrt(ak2) : FastMath.sqrt(ak2);
      double betak = 1.0 / (ak2 - akk * alpha);
      beta[pk]     = betak;

      // transform the current column
      diagR[pk]        = alpha;
      jacobian[kDiag] -= alpha;

      // transform the remaining columns
      for (int dk = cols - 1 - k; dk > 0; --dk) {
        int dkp = permutation[k + dk] - pk;
        double gamma = 0;
        for (int index = kDiag; index < jacobian.length; index += cols) {
          gamma += jacobian[index] * jacobian[index + dkp];
        }
        gamma *= betak;
        for (int index = kDiag; index < jacobian.length; index += cols) {
          jacobian[index + dkp] -= gamma * jacobian[index];
        }
      }

    }

    rank = solvedCols;

  }

  /**
   * Compute the product Qt.y for some Q.R. decomposition.
   *
   * @param y vector to multiply (will be overwritten with the result)
   */
  private void qTy(double[] y) {
    for (int k = 0; k < cols; ++k) {
      int pk = permutation[k];
      int kDiag = k * cols + pk;
      double gamma = 0;
      int index = kDiag;
      for (int i = k; i < rows; ++i) {
        gamma += jacobian[index] * y[i];
        index += cols;
      }
      gamma *= beta[pk];
      index = kDiag;
      for (int i = k; i < rows; ++i) {
        y[i] -= gamma * jacobian[index];
        index += cols;
      }
    }
  }

}
