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
package org.apache.commons.math.distribution;

import org.apache.commons.math.MathException;

/**
 * <p>Base interface for continuous distributions.</p>
 *
 * <p>Note: this interface will be extended in version 3.0 to include
 * <br/><code>public double density(double x)</code><br/>
 * that is, from version 3.0 forward, continuous distributions <strong>must</strong>
 * include implementations of probability density functions. As of version
 * 2.1, all continuous distribution implementations included in commons-math
 * provide implementations of this method.</p>
 *
 * @version $Revision: 924362 $ $Date: 2010-03-17 17:45:31 +0100 (mer. 17 mars 2010) $
 */
public interface ContinuousDistribution extends Distribution {

    /**
     * For this distribution, X, this method returns x such that P(X &lt; x) = p.
     * @param p the cumulative probability.
     * @return x.
     * @throws MathException if the inverse cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     */
    double inverseCumulativeProbability(double p) throws MathException;
}
