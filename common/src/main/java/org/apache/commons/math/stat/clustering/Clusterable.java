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

package org.apache.commons.math.stat.clustering;

import java.util.Collection;

/**
 * Interface for points that can be clustered together.
 * @param <T> the type of point that can be clustered
 * @version $Revision: 811685 $ $Date: 2009-09-05 19:36:48 +0200 (sam. 05 sept. 2009) $
 * @since 2.0
 */
public interface Clusterable<T> {

    /**
     * Returns the distance from the given point.
     *
     * @param p the point to compute the distance from
     * @return the distance from the given point
     */
    double distanceFrom(T p);

    /**
     * Returns the centroid of the given Collection of points.
     *
     * @param p the Collection of points to compute the centroid of
     * @return the centroid of the given Collection of Points
     */
    T centroidOf(Collection<T> p);

}
