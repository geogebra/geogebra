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

package org.apache.commons.math.analysis;

import org.apache.commons.math.FunctionEvaluationException;

/**
 * An interface representing a bivariate real function.
 *
 * @since 2.1
 * @version $Revision: 1073498 $ $Date: 2011-02-22 21:57:26 +0100 (mar. 22 févr. 2011) $
 */
public interface BivariateRealFunction {
    /**
     * Compute the value for the function.
     *
     * @param x Abscissa for which the function value should be computed.
     * @param y Ordinate for which the function value should be computed.
     * @return the value.
     * @throws FunctionEvaluationException if the function evaluation fails.
     */
    double value(double x, double y)
        throws FunctionEvaluationException;

}
