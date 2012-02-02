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
package org.apache.commons.math.exception;

import java.util.Locale;

import org.apache.commons.math.exception.util.Localizable;

/**
* Interface for commons-math throwables.
*
* @version $Revision: 1035475 $ $Date: 2010-11-15 23:39:25 +0100 (lun. 15 nov. 2010) $
* @since 2.2
*/
public interface MathThrowable {

    /** Gets the localizable pattern used to build the specific part of the message of this throwable.
     * @return localizable pattern used to build the specific part of the message of this throwable
     */
    Localizable getSpecificPattern();

    /** Gets the localizable pattern used to build the general part of the message of this throwable.
     * @return localizable pattern used to build the general part of the message of this throwable
     */
    Localizable getGeneralPattern();

    /** Gets the arguments used to build the message of this throwable.
     * @return the arguments used to build the message of this throwable
     */
    Object[] getArguments();

    /** Gets the message in a specified locale.
     * @param locale Locale in which the message should be translated
     * @return localized message
     */
    String getMessage(final Locale locale);

    /** Gets the message in a conventional US locale.
     * @return localized message
     */
    String getMessage();

    /** Gets the message in the system default locale.
     * @return localized message
     */
    String getLocalizedMessage();

}
