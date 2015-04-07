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

import java.util.Random;

/**
 * Extension of <code>java.util.Random</code> to implement
 * {@link RandomGenerator}.
 *
 * @since 1.1
 * @version $Revision: 811685 $ $Date: 2009-09-05 19:36:48 +0200 (sam. 05 sept. 2009) $
 */
public class JDKRandomGenerator extends Random implements RandomGenerator {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -7745277476784028798L;

    /** {@inheritDoc} */
    public void setSeed(int seed) {
        setSeed((long) seed);
    }

    /** {@inheritDoc} */
    public void setSeed(int[] seed) {
        // the following number is the largest prime that fits in 32 bits (it is 2^32 - 5)
        final long prime = 4294967291l;

        long combined = 0l;
        for (int s : seed) {
            combined = combined * prime + s;
        }
        setSeed(combined);
    }

}
