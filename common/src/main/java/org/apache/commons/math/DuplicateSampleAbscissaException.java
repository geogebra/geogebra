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
package org.apache.commons.math;

import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * Exception thrown when a sample contains several entries at the same abscissa.
 *
 * @since 1.2
 * @version $Revision: 983921 $ $Date: 2010-08-10 12:46:06 +0200 (mar. 10 août 2010) $
 */
public class DuplicateSampleAbscissaException extends MathException  {

    /** Serializable version identifier */
    private static final long serialVersionUID = -2271007547170169872L;

    /**
     * Construct an exception indicating the duplicate abscissa.
     * @param abscissa duplicate abscissa
     * @param i1 index of one entry having the duplicate abscissa
     * @param i2 index of another entry having the duplicate abscissa
     */
    public DuplicateSampleAbscissaException(double abscissa, int i1, int i2) {
        super(LocalizedFormats.DUPLICATED_ABSCISSA,
              abscissa, i1, i2);
    }

    /**
     * Get the duplicate abscissa.
     * @return duplicate abscissa
     */
    public double getDuplicateAbscissa() {
        return ((Double) getArguments()[0]).doubleValue();
    }

}
