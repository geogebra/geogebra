// vendored
/*
 * Copyright 2013 Nicolas Morel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.geogebra.web.html5.util;

import java.util.Random;

/**
 * A simplified implementation of {@link java.util.UUID} as a translatable class
 * for GWT. Only methods implemented here can be used in client-side code. Also
 * note that while the equality and hashcode should operate as expected, the
 * hash codes would not translate between client and server; also note that
 * natural ordering of these objects on the client side would likely be
 * different.
 *
 * @author Ross M. Lodge
 */
public final class UUIDW {

	private static final Random rnd = new Random();

	/**
	 * Generates an RFC-4122, version 4, random UUID as a formatted string. Code
	 * altered from http://www.broofa.com/Tools/Math.uuid.js under the MIT
	 * license.
	 *
	 * @return UUID string
	 */
	public static String generateUUIDString() {
		char[] uuid = new char[36];

		// rfc4122 requires these characters
		uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
		uuid[14] = '4';

		// Fill in random data.  At i==19 set the high bits of clock sequence
		// as per rfc4122, sec. 4.1.5
		for (int i = 0; i < 36; i++) {
			if (uuid[i] == 0) {
				int r = rnd.nextInt(16);
				uuid[i] = Integer.toString((i == 19) ? (r & 0x3) | 0x8 : r, 16).charAt(0);
			}
		}

		return new String(uuid);
	}

}