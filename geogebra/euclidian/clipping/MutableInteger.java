/*
 * @(#)MutableInteger.java
 *
 * $Date: 2010-01-03 07:17:54 -0600 (Sun, 03 Jan 2010) $
 *
 * Copyright (c) 2009 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * http://javagraphics.blogspot.com/
 * 
 * And the latest version should be available here:
 * https://javagraphics.dev.java.net/
 */

package geogebra.euclidian.clipping;

/** Similar to <code>java.lang.Integer</code>, except this object
 * is mutable.
 * <P>This can be very handy when you want to use integers
 * as keys in a Hashtable: constantly creating new integer wrappers is very
 * wasteful, but if you create 1 mutable integer wrapper and change its
 * value every time you need it (in a safe synchronized environment) this
 * can really save a lot of memory allocation.  In same instances.
 *
 */
public class MutableInteger extends Number {
	private static final long serialVersionUID = 1L;
	
	public int value = 0;
	
	public MutableInteger(int value) {
		this.value = value;
	}

	public double doubleValue() {
		return value;
	}

	public float floatValue() {
		return value;
	}

	public int intValue() {
		return value;
	}

	public long longValue() {
		return value;
	}
	
	public int hashCode() {
		return value;
	}
	
	public String toString() {
		return Integer.toString(value);
	}

	public boolean equals(Object obj) {
		if(obj==this) return true;
		if(obj instanceof MutableInteger) {
			return ((MutableInteger)obj).value==value;
		}
		if(obj instanceof Number) {
			return ((Number)obj).intValue()==value;
		}
		return false;
	}
}
