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

package org.apache.commons.math.optimization.linear;

import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.optimization.OptimizationException;

/**
 * This class represents exceptions thrown by optimizers when a solution
 * escapes to infinity.
 * @version $Revision: 983921 $ $Date: 2010-08-10 12:46:06 +0200 (mar. 10 août 2010) $
 * @since 2.0
 */
public class UnboundedSolutionException extends OptimizationException {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 940539497277290619L;

    /**
     * Simple constructor using a default message.
     */
    public UnboundedSolutionException() {
        super(LocalizedFormats.UNBOUNDED_SOLUTION);
    }

}
