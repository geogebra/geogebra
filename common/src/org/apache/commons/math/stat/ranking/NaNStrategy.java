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

package org.apache.commons.math.stat.ranking;

/**
 * Strategies for handling NaN values in rank transformations.
 * <ul>
 * <li>MINIMAL - NaNs are treated as minimal in the ordering, equivalent to
 * (that is, tied with) <code>Double.NEGATIVE_INFINITY</code>.</li>
 * <li>MAXIMAL - NaNs are treated as maximal in the ordering, equivalent to
 * <code>Double.POSITIVE_INFINITY</code></li>
 * <li>REMOVED - NaNs are removed before the rank transform is applied</li>
 * <li>FIXED - NaNs are left "in place," that is the rank transformation is
 * applied to the other elements in the input array, but the NaN elements
 * are returned unchanged.</li>
 * </ul>
 *
 * @since 2.0
 * @version $Revision: 811685 $ $Date: 2009-09-05 19:36:48 +0200 (sam. 05 sept. 2009) $
 */
public enum NaNStrategy {

    /** NaNs are considered minimal in the ordering */
    MINIMAL,

    /** NaNs are considered maximal in the ordering */
    MAXIMAL,

    /** NaNs are removed before computing ranks */
    REMOVED,

    /** NaNs are left in place */
    FIXED
}
