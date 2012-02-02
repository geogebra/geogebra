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
import org.apache.commons.math.analysis.UnivariateRealFunction;

/**
 * Interface representing a univariate real interpolating function.
 *
 * @version $Revision: 821626 $ $Date: 2009-10-04 23:57:30 +0200 (dim. 04 oct. 2009) $
 */
public interface UnivariateRealInterpolator {

    /**
     * Computes an interpolating function for the data set.
     * @param xval the arguments for the interpolation points
     * @param yval the values for the interpolation points
     * @return a function which interpolates the data set
     * @throws MathException if arguments violate assumptions made by the
     *         interpolation algorithm
     */
    UnivariateRealFunction interpolate(double xval[], double yval[])
        throws MathException;
}
