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
/* This file was modified by GeoGebra Inc. */
package org.apache.commons.math.distribution;

import org.apache.commons.math.MathException;

/**
 * <p>Interface that signals that a distribution can compute the probability density function
 * for a particular point.
 * @param <P> the type of the point at which density is to be computed, this
 * may be for example <code>Double.</code></p>
 *
 * <p>This interface is deprecated.  As of version 2.0, the {@link ContinuousDistribution}
 * interface will be extended to include a <code>density(double)<code> method.</p>
 *
 * @deprecated to be removed in math 3.0
 * @version $Revision: 924362 $ $Date: 2010-03-17 12:45:31 -0400 (Wed, 17 Mar 2010) $
 */
public interface HasDensity<P> {

    /**
     * Compute the probability density function.
     * @param x point for which the probability density is requested
     * @return probability density at point x
     * @throws MathException if probability density cannot be computed at specifed point
     */
    double density(P x) throws MathException;

}
