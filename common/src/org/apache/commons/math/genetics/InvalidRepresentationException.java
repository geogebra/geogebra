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
package org.apache.commons.math.genetics;

/**
 * Exception indicating that the representation of a chromosome is not valid.
 *
 * @version $Revision: 811685 $ $Date: 2009-09-05 19:36:48 +0200 (sam. 05 sept. 2009) $
 * @since 2.0
 */
public class InvalidRepresentationException extends Exception {

    /** Serialization version id */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public InvalidRepresentationException() {
        super();
    }

    /**
     * Construct an InvalidRepresentationException
     * @param arg0 exception message
     */
    public InvalidRepresentationException(String arg0) {
        super(arg0);
    }

    /**
     * Construct an InvalidRepresentationException
     * @param arg0 cause
     */
    public InvalidRepresentationException(Throwable arg0) {
        super(arg0);
    }

    /**
     * Construct an InvalidRepresentationException
     *
     * @param arg0 exception message
     * @param arg1 cause
     */
    public InvalidRepresentationException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
