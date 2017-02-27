/*
 * Value.java
 *
 * Created on January 4, 2001, 11:37 AM
 */

package org.freehep.util;

import java.util.Date;

/**
 * A class that can represent any Java object or primitive. Unlike the built-in
 * primitive proxies (Double, Integer etc) it is mutable. It is used to allow
 * values to be used without needing overloaded methods for each primitive type,
 * and without the overhead of object creation/deletion.
 *
 * When a value is returned by an Object method it should be assumed to be valid
 * only until the next method call to that Object. The use of Value should be
 * avoided in multi-threaded environments.
 *
 * @author tonyj
 * @version $Id: Value.java,v 1.4 2009-06-22 02:18:20 hohenwarter Exp $
 */
public class Value {

	private int intValue;
	private short shortValue;
	private long longValue;
	private float floatValue;
	private double doubleValue;
	private boolean boolValue;
	private byte byteValue;
	private char charValue;
	private Object obj;
	private Class<?> type;

	public final static Class<?> TYPE_INTEGER = Integer.TYPE;
	public final static Class<?> TYPE_SHORT = Short.TYPE;
	public final static Class<?> TYPE_LONG = Long.TYPE;
	public final static Class<?> TYPE_FLOAT = Float.TYPE;
	public final static Class<?> TYPE_DOUBLE = Double.TYPE;
	public final static Class<?> TYPE_BOOLEAN = Boolean.TYPE;
	public final static Class<?> TYPE_BYTE = Byte.TYPE;
	public final static Class<?> TYPE_CHAR = Character.TYPE;
	public final static Class<?> TYPE_STRING = String.class;
	public final static Class<?> TYPE_DATE = Date.class;

	public Value() {
	}

	public Value(Value v) {
		setValue(v);
	}

	public Value setValue(Value v) {
		this.type = v.getType();
		this.intValue = v.intValue;
		this.shortValue = v.shortValue;
		this.longValue = v.longValue;
		this.floatValue = v.floatValue;
		this.doubleValue = v.doubleValue;
		this.boolValue = v.boolValue;
		this.byteValue = v.byteValue;
		this.charValue = v.charValue;
		this.obj = v.obj;
		return this;
	}

	/**
	 * Get the Value's type
	 * 
	 * @return The Class of this Value.
	 *
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * Set the Value's internal value to an integer.
	 * 
	 * @param val
	 *            The integer value.
	 * @return The Value object with the given internal value.
	 *
	 */
	public Value set(int val) {
		intValue = val;
		type = TYPE_INTEGER;
		return this;
	}

	/**
	 * Set the Value's internal value to a short.
	 * 
	 * @param val
	 *            The short value.
	 * @return The Value object with the given internal value.
	 *
	 */
	public Value set(short val) {
		shortValue = val;
		type = TYPE_SHORT;
		return this;
	}

	/**
	 * Set the Value's internal value to a long.
	 * 
	 * @param val
	 *            The long value.
	 * @return The Value object with the given internal value.
	 *
	 */
	public Value set(long val) {
		longValue = val;
		type = TYPE_LONG;
		return this;
	}

	/**
	 * Set the Value's internal value to a float.
	 * 
	 * @param val
	 *            The float value.
	 * @return The Value object with the given internal value.
	 *
	 */
	public Value set(float val) {
		floatValue = val;
		type = TYPE_FLOAT;
		return this;
	}

	/**
	 * Set the Value's internal value to a double.
	 * 
	 * @param val
	 *            The double value.
	 * @return The Value object with the given internal value.
	 *
	 */
	public Value set(double val) {
		doubleValue = val;
		type = TYPE_DOUBLE;
		return this;
	}

	/**
	 * Set the Value's internal value to a boolean.
	 * 
	 * @param val
	 *            The boolean value.
	 * @return The Value object with the given internal value.
	 *
	 */
	public Value set(boolean val) {
		boolValue = val;
		type = TYPE_BOOLEAN;
		return this;
	}

	/**
	 * Set the Value's internal value to a byte.
	 * 
	 * @param val
	 *            The byte value.
	 * @return The Value object with the given internal value.
	 *
	 */
	public Value set(byte val) {
		byteValue = val;
		type = TYPE_BYTE;
		return this;
	}

	/**
	 * Set the Value's internal value to a char.
	 * 
	 * @param val
	 *            The char value.
	 * @return The Value object with the given internal value.
	 *
	 */
	public Value set(char val) {
		charValue = val;
		type = TYPE_CHAR;
		return this;
	}

	/**
	 * Set the Value's internal value to a String.
	 * 
	 * @param val
	 *            The String value.
	 * @return The Value object with the given internal value.
	 *
	 */
	public Value set(String val) {
		obj = val;
		type = TYPE_STRING;
		return this;
	}

	/**
	 * Set the Value's internal value to a Date.
	 * 
	 * @param val
	 *            The Date value.
	 * @return The Value object with the given internal value.
	 *
	 */
	public Value set(Date val) {
		obj = val;
		type = TYPE_DATE;
		return this;
	}

	/**
	 * Set the Value's internal value to an Object.
	 * 
	 * @param val
	 *            The Object value.
	 * @return The Value object with the given internal value.
	 *
	 */
	public Value set(Object val) {
		obj = val;
		type = obj == null ? Object.class : obj.getClass();
		return this;
	}

	/**
	 * Get the integer value.
	 * 
	 * @return The int value.
	 * @exception ClassCastException
	 *                is thrown if this Value has incompatible type.
	 *
	 */
	public int getInt() {
		if (type == TYPE_INTEGER) {
			return intValue;
		} else if (type == TYPE_SHORT) {
			return shortValue;
		} else if (type == TYPE_BYTE) {
			return byteValue;
		} else {
			throw new ClassCastException(
					"getInt cannot be called for type " + type.toString());
		}
	}

	/**
	 * Get the short value.
	 * 
	 * @return The short value.
	 * @exception ClassCastException
	 *                is thrown if this Value has incompatible type.
	 *
	 */
	public short getShort() {
		if (type == TYPE_SHORT) {
			return shortValue;
		} else if (type == TYPE_BYTE) {
			return byteValue;
		} else {
			throw new ClassCastException(
					"getShort cannot be called for type " + type.toString());
		}
	}

	/**
	 * Get the long value.
	 * 
	 * @return The long value.
	 * @exception ClassCastException
	 *                is thrown if this Value has incompatible type.
	 *
	 */
	public long getLong() {
		if (type == TYPE_LONG) {
			return longValue;
		} else if (type == TYPE_INTEGER) {
			return intValue;
		} else if (type == TYPE_SHORT) {
			return shortValue;
		} else if (type == TYPE_BYTE) {
			return byteValue;
		} else {
			throw new ClassCastException(
					"getLong cannot be called for type " + type.toString());
		}
	}

	/**
	 * Get the float value.
	 * 
	 * @return The float value.
	 * @exception ClassCastException
	 *                is thrown if this Value has incompatible type.
	 *
	 */
	public float getFloat() {
		if (type == TYPE_FLOAT) {
			return floatValue;
		} else if (type == TYPE_INTEGER) {
			return intValue;
		} else if (type == TYPE_SHORT) {
			return shortValue;
		} else if (type == TYPE_LONG) {
			return longValue;
		} else if (type == TYPE_BYTE) {
			return byteValue;
		} else {
			throw new ClassCastException(
					"getFloat cannot be called for type " + type.toString());
		}
	}

	/**
	 * Get the double value.
	 * 
	 * @return The double value.
	 * @exception ClassCastException
	 *                is thrown if this Value has incompatible type.
	 *
	 */
	public double getDouble() {
		if (type == TYPE_DOUBLE) {
			return doubleValue;
		} else if (type == TYPE_INTEGER) {
			return intValue;
		} else if (type == TYPE_SHORT) {
			return shortValue;
		} else if (type == TYPE_LONG) {
			return longValue;
		} else if (type == TYPE_FLOAT) {
			return floatValue;
		} else if (type == TYPE_BYTE) {
			return byteValue;
		} else if (type == TYPE_DATE) {
			return ((Date) obj).getTime();
		} else {
			throw new ClassCastException(
					"getDouble cannot be called for type " + type.toString());
		}
	}

	/**
	 * Get the boolean value.
	 * 
	 * @return The boolean value.
	 * @exception ClassCastException
	 *                is thrown if this Value has incompatible type.
	 *
	 */
	public boolean getBoolean() {
		if (type == TYPE_BOOLEAN) {
			return boolValue;
		}
		throw new ClassCastException(
				"getBoolean cannot be called for type " + type.toString());
	}

	/**
	 * Get the byte value.
	 * 
	 * @return The byte value.
	 * @exception ClassCastException
	 *                is thrown if this Value has incompatible type.
	 *
	 */
	public byte getByte() {
		if (type == TYPE_BYTE) {
			return byteValue;
		}
		throw new ClassCastException(
				"getByte cannot be called for type " + type.toString());
	}

	/**
	 * Get the char value.
	 * 
	 * @return The char value.
	 * @exception ClassCastException
	 *                is thrown if this Value has incompatible type.
	 *
	 */
	public char getChar() {
		if (type == TYPE_CHAR) {
			return charValue;
		}
		throw new ClassCastException(
				"getChar cannot be called for type " + type.toString());
	}

	/**
	 * Get the String value.
	 * 
	 * @return The String representation of the internal value.
	 *
	 */
	public String getString() {
		if (type == TYPE_STRING) {
			return (String) obj;
		} else if (type == TYPE_INTEGER) {
			return String.valueOf(intValue);
		} else if (type == TYPE_SHORT) {
			return String.valueOf(shortValue);
		} else if (type == TYPE_LONG) {
			return String.valueOf(longValue);
		} else if (type == TYPE_FLOAT) {
			return String.valueOf(floatValue);
		} else if (type == TYPE_DOUBLE) {
			return String.valueOf(doubleValue);
		} else if (type == TYPE_BOOLEAN) {
			return String.valueOf(boolValue);
		} else if (type == TYPE_BYTE) {
			return String.valueOf(byteValue);
		} else if (type == TYPE_CHAR) {
			return String.valueOf(charValue);
		} else if (type == TYPE_DATE) {
			return ((Date) obj).toString();
		} else {
			return obj != null ? obj.toString() : "null";
		}
	}

	/**
	 * Get the Date value.
	 * 
	 * @return The Date value.
	 * @exception ClassCastException
	 *                is thrown if this Value has incompatible type.
	 *
	 */
	public Date getDate() {
		if (type == TYPE_DATE) {
			return (Date) obj;
		}
		throw new ClassCastException(
				"getDate cannot be called for type " + type.toString());
	}

	/**
	 * Get the Object value.
	 * 
	 * @return The Object value.
	 *
	 */
	public Object getObject() {
		if (obj != null) {
			return obj;
		} else if (type == TYPE_INTEGER) {
			return Integer.valueOf(intValue);
		} else if (type == TYPE_SHORT) {
			return Short.valueOf(shortValue);
		} else if (type == TYPE_LONG) {
			return Long.valueOf(longValue);
		} else if (type == TYPE_FLOAT) {
			return Float.valueOf(floatValue);
		} else if (type == TYPE_DOUBLE) {
			return Double.valueOf(doubleValue);
		} else if (type == TYPE_BOOLEAN) {
			return Boolean.valueOf(boolValue);
		} else if (type == TYPE_BYTE) {
			return Byte.valueOf(byteValue);
		} else if (type == TYPE_CHAR) {
			return Character.valueOf(charValue);
		} else {
			return null;
		}
	}

	/**
	 * Get the String value.
	 * 
	 * @return The String representation of the internal value.
	 */
	@Override
	public String toString() {
		return getString();
	}

	/**
	 * Returns an external representation of this value
	 */
	public String toExternal() {
		// FIXME, does not work for arrays...
		return type.getName() + ":" + getString();
	}

	/**
	 * Set to value from the external representation
	 */
	// public Value fromExternal(String external) throws
	// IllegalArgumentException {
	// String[] part = external.split(":", 2);
	// if (part.length != 2)
	// throw new IllegalArgumentException(getClass()+": External '"+external+
	// "'does not contain ':' to separate type from value.");
	//
	// if (part[0].equals(TYPE_STRING.getName())) {
	// return set(part[1]);
	// } else if (part[0].equals(TYPE_SHORT.getName())) {
	// return set(Short.parseShort(part[1]));
	// } else if (part[0].equals(TYPE_LONG.getName())) {
	// return set(Long.parseLong(part[1]));
	// } else if (part[0].equals(TYPE_FLOAT.getName())) {
	// return set(Float.parseFloat(part[1]));
	// } else if (part[0].equals(TYPE_DOUBLE.getName())) {
	// return set(Double.parseDouble(part[1]));
	// } else if (part[0].equals(TYPE_BOOLEAN.getName())) {
	// return set(Boolean.getBoolean(part[1]));
	// } else if (part[0].equals(TYPE_BYTE.getName())) {
	// return set(Byte.parseByte(part[1]));
	// } else if (part[0].equals(TYPE_CHAR.getName())) {
	// return set(part[1].charAt(0));
	// } else if (part[0].equals(TYPE_INTEGER.getName())) {
	// return set(Integer.parseInt(part[1]));
	// } else if (part[0].equals(TYPE_DATE.getName())) {
	// try {
	// return set(new SimpleDateFormat().parse(part[1]));
	// } catch (ParseException e) {
	// throw new IllegalArgumentException(e.getMessage());
	// }
	// } else if (part[0].equals(Object.class.getName()) &&
	// part[1].equals("null")) {
	// return set((Object)null);
	// } else {
	// // FIXME will not work for arrays, which are encoded as
	// "[Lpackagename.classname;"
	// try {
	// Class<?> cls = Class.forName(part[0]);
	// Constructor<?> ctor = cls.getDeclaredConstructor(new Class[] {
	// String.class });
	// ctor.setAccessible(true);
	// return set(ctor.newInstance(new Object[] { part[1] }));
	// } catch (Exception e) {
	// throw new IllegalArgumentException(getClass()+": Cannot reconstruct value
	// from type: "+part[0]+", "
	// +"and value "+part[1]+", due to "+e.getMessage());
	// }
	// }
	// }
}
