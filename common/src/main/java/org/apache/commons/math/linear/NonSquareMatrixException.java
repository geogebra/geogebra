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

package org.apache.commons.math.linear;

import org.apache.commons.math.exception.util.LocalizedFormats;


/**
 * Thrown when an operation defined only for square matrices is applied to non-square ones.
 * @version $Revision: 983921 $ $Date: 2010-08-10 12:46:06 +0200 (mar. 10 août 2010) $
 * @since 2.0
 */
public class NonSquareMatrixException extends InvalidMatrixException {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 8996207526636673730L;

    /**
     * Construct an exception with the given message.
     * @param rows number of rows of the faulty matrix
     * @param columns number of columns of the faulty matrix
     */
    public NonSquareMatrixException(final int rows, final int columns) {
        super(LocalizedFormats.NON_SQUARE_MATRIX, rows, columns);
    }

}
