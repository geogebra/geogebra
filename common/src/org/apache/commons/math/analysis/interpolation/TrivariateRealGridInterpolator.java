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

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.TrivariateRealFunction;

/**
 * Interface representing a trivariate real interpolating function where the
 * sample points must be specified on a regular grid.
 *
 * @version $Revision$ $Date$
 * @since 2.2
 */
public interface TrivariateRealGridInterpolator {
    /**
     * Computes an interpolating function for the data set.
     *
     * @param xval All the x-coordinates of the interpolation points, sorted
     * in increasing order.
     * @param yval All the y-coordinates of the interpolation points, sorted
     * in increasing order.
     * @param zval All the z-coordinates of the interpolation points, sorted
     * in increasing order.
     * @param fval the values of the interpolation points on all the grid knots:
     * {@code fval[i][j][k] = f(xval[i], yval[j], zval[k])}.
     * @return a function that interpolates the data set.
     * @throws org.apache.commons.math.exception.NoDataException if any of the arrays has zero length.
     * @throws org.apache.commons.math.exception.DimensionMismatchException if the array lengths are inconsistent.
     * @throws MathException if arguments violate assumptions made by the
     *         interpolation algorithm.
     */
    TrivariateRealFunction interpolate(double[] xval, double[] yval, double[] zval, double[][][] fval)
        throws MathException;
}
