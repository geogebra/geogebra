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

/**
 * This class represents measurements in estimation problems.
 *
 * <p>This abstract class implements all the methods needed to handle
 * measurements in a general way. It defines neither the {@link
 * #getTheoreticalValue getTheoreticalValue} nor the {@link
 * #getPartial getPartial} methods, which should be defined by
 * sub-classes according to the specific problem.</p>
 *
 * <p>The {@link #getTheoreticalValue getTheoreticalValue} and {@link
 * #getPartial getPartial} methods must always use the current
 * estimate of the parameters set by the solver in the problem. These
 * parameters can be retrieved through the {@link
 * EstimationProblem#getAllParameters
 * EstimationProblem.getAllParameters} method if the measurements are
 * independent of the problem, or directly if they are implemented as
 * inner classes of the problem.</p>
 *
 * <p>The instances for which the <code>ignored</code> flag is set
 * through the {@link #setIgnored setIgnored} method are ignored by the
 * solvers. This can be used to reject wrong measurements at some
 * steps of the estimation.</p>
 *
 * @see EstimationProblem
 *
 * @version $Revision: 811827 $ $Date: 2009-09-06 17:32:50 +0200 (dim. 06 sept. 2009) $
 * @since 1.2
 * @deprecated as of 2.0, everything in package org.apache.commons.math.estimation has
 * been deprecated and replaced by package org.apache.commons.math.optimization.general
 */

@Deprecated
public abstract class WeightedMeasurement implements Serializable {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 4360046376796901941L;

    /** Measurement weight. */
    private final double  weight;

    /** Value of the measurements. */
    private final double  measuredValue;

    /** Ignore measurement indicator. */
    private boolean ignored;

  /**
   * Simple constructor.
   * Build a measurement with the given parameters, and set its ignore
   * flag to false.
   * @param weight weight of the measurement in the least squares problem
   * (two common choices are either to use 1.0 for all measurements, or to
   * use a value proportional to the inverse of the variance of the measurement
   * type)
   *
   * @param measuredValue measured value
   */
  public WeightedMeasurement(double weight, double measuredValue) {
    this.weight        = weight;
    this.measuredValue = measuredValue;
    ignored            = false;
  }

  /** Simple constructor.
   *
   * Build a measurement with the given parameters
   *
   * @param weight weight of the measurement in the least squares problem
   * @param measuredValue measured value
   * @param ignored true if the measurement should be ignored
   */
  public WeightedMeasurement(double weight, double measuredValue,
                             boolean ignored) {
    this.weight        = weight;
    this.measuredValue = measuredValue;
    this.ignored       = ignored;
  }

  /**
   * Get the weight of the measurement in the least squares problem
   *
   * @return weight
   */
  public double getWeight() {
    return weight;
  }

  /**
   * Get the measured value
   *
   * @return measured value
   */
  public double getMeasuredValue() {
    return measuredValue;
  }

  /**
   * Get the residual for this measurement
   * The residual is the measured value minus the theoretical value.
   *
   * @return residual
   */
  public double getResidual() {
    return measuredValue - getTheoreticalValue();
  }

  /**
   * Get the theoretical value expected for this measurement
   * <p>The theoretical value is the value expected for this measurement
   * if the model and its parameter were all perfectly known.</p>
   * <p>The value must be computed using the current estimate of the parameters
   * set by the solver in the problem.</p>
   *
   * @return theoretical value
   */
  public abstract double getTheoreticalValue();

  /**
   * Get the partial derivative of the {@link #getTheoreticalValue
   * theoretical value} according to the parameter.
   * <p>The value must be computed using the current estimate of the parameters
   * set by the solver in the problem.</p>
   *
   * @param parameter parameter against which the partial derivative
   * should be computed
   * @return partial derivative of the {@link #getTheoreticalValue
   * theoretical value}
   */
  public abstract double getPartial(EstimatedParameter parameter);

  /**
   * Set the ignore flag to the specified value
   * Setting the ignore flag to true allow to reject wrong
   * measurements, which sometimes can be detected only rather late.
   *
   * @param ignored value for the ignore flag
   */
  public void setIgnored(boolean ignored) {
    this.ignored = ignored;
  }

  /**
   * Check if this measurement should be ignored
   *
   * @return true if the measurement should be ignored
   */
  public boolean isIgnored() {
    return ignored;
  }

}
