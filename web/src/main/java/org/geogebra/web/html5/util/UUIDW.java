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

import java.io.Serializable;

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
public class UUIDW implements Serializable, Comparable<UUIDW> {

	private String uuidValue;

	/**
	 * Default constructor to make this work for GWT serialization.
	 */
	private UUIDW() {
		// Intentionally blank
	}

	/**
	 * Constructs a new UUID from a string representation.
	 *
	 * @param name
	 *
	 * @return
	 */
	public static UUIDW fromString(String name) {
		UUIDW newUUID = new UUIDW();
		newUUID.uuidValue = name;
		return newUUID;
	}

	/**
	 * Generates a random UUID that <em>should</em> be RFC-4122 Version 4
	 * compliant, although we can't really guarantee much about it's randomness.
	 *
	 * @return
	 */
	public static UUIDW randomUUID() {
		return fromString(generateUUIDString());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(UUIDW o) {
		return uuidValue.compareTo(o.toString());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return uuidValue.hashCode();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return uuidValue.equals(obj.toString());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return uuidValue;
	}

	/**
	 * Generates an RFC-4122, version 4, random UUID as a formatted string. Code
	 * altered from http://www.broofa.com/Tools/Math.uuid.js under the MIT
	 * license.
	 *
	 * @return
	 */
	private static native String generateUUIDString() /*-{
		var chars = '0123456789ABCDEF'.split(''), uuid = [];
		radix = chars.length;

		var r;

		// rfc4122 requires these characters
		uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
		uuid[14] = '4';

		// Fill in random data.  At i==19 set the high bits of clock sequence as per rfc4122, sec. 4.1.5
		for (var i = 0; i < 36; i++) {
			if (!uuid[i]) {
				r = 0 | Math.random() * 16;
				uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
			}
		}

		return uuid.join('');
	}-*/;

}