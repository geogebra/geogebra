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


/**
 * Interface representing <a href="http://mathworld.wolfram.com/Field.html">field</a> elements.
 * @param <T> the type of the field elements
 * @see Field
 * @version $Revision: 811685 $ $Date: 2009-09-05 19:36:48 +0200 (sam. 05 sept. 2009) $
 * @since 2.0
 */
public interface FieldElement<T> {

    /** Compute this + a.
     * @param a element to add
     * @return a new element representing this + a
     */
    T add(T a);

    /** Compute this - a.
     * @param a element to subtract
     * @return a new element representing this - a
     */
    T subtract(T a);

    /** Compute this &times; a.
     * @param a element to multiply
     * @return a new element representing this &times; a
     */
    T multiply(T a);

    /** Compute this &divide; a.
     * @param a element to add
     * @return a new element representing this &divide; a
     * @exception ArithmeticException if a is the zero of the
     * additive operation (i.e. additive identity)
     */
    T divide(T a) throws ArithmeticException;

    /** Get the {@link Field} to which the instance belongs.
     * @return {@link Field} to which the instance belongs
     */
    Field<T> getField();

}
