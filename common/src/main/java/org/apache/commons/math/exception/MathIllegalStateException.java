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


/**
 * Base class for all exceptions that signal a mismatch between the
 * current state and the user's expectations.
 *
 * @since 2.2
 * @version $Revision: 1061496 $ $Date: 2011-01-20 21:32:16 +0100 (jeu. 20 janv. 2011) $
 */
public class MathIllegalStateException extends IllegalStateException implements MathThrowable {

	public MathIllegalStateException(String specific, String noData,
			Object[] objects) {
		// TODO Auto-generated constructor stub
	}

	public MathIllegalStateException(String internalError, String reportUrl) {
		// TODO Auto-generated constructor stub
	}
	
}
