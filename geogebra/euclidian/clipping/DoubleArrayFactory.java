/*
 * @(#)doubleArrayFactory.java
 *
 * $Date: 2009-06-13 17:18:17 -0500 (Sat, 13 Jun 2009) $
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

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;

/** This is a mechanism to recycle arrays.
 * <P>Creating arrays in a heavy, fast-paced loop
 * ends up being very expensive.  This stores
 * arrays and returns them on demand.
 * <P>This factory stores these arrays with
 * strong references, so this class should only
 * really be used when the possible array sizes
 * are limited and the usage is predictable.
 *
 */
public class DoubleArrayFactory {
	private static DoubleArrayFactory globalFactory;
	
	public static DoubleArrayFactory getStaticFactory() {
		if(globalFactory==null)
			globalFactory = new DoubleArrayFactory();
		return globalFactory;
	}
	
	private Map map = createMap();
	
	private static Map createMap() {
		try {
			//break this up into two strings for the JarWriter
			//If the whole name is listed, then the JarWriter
			//will bundle the trove jar automatically...
			Class troveMap = Class.forName("gnu."+"trove.THashMap");
			Constructor[] constructors = troveMap.getConstructors();
			for(int a = 0; a<constructors.length; a++) {
				if(constructors[a].getParameterTypes().length==0)
					return (Map)constructors[a].newInstance(new Object[] {});
			}
		} catch(Throwable e) {
			//in addition to the expected exceptions, consider
			//UnsupportedClassVersionErrors, and other weirdnesses.
		}
		
		return new Hashtable();
	}
	
	private MutableInteger key = new MutableInteger(0);
	
	/** Returns a double array of the indicated size.
	 * <P>If arrays of that size have previously been
	 * stored in this factory, then an existing array
	 * will be returned.
	 * @param size the array size you need.
	 * @return a double array of the size indicated.
	 */
	public double[] getArray(int size) {
		Stack stack;
		synchronized(key) {
			key.value = size;
			stack = (Stack)map.get(key);
			if(stack==null) {
				stack = new Stack();
				map.put(new MutableInteger(size),stack);
			}
		}
		if(stack.size()==0) {
			return new double[size];
		}
		return (double[])stack.pop();
	}
	
	/** Stores an array for future use.
	 * <P>As soon as you call this method you should nullify
	 * all other references to the argument.  If you continue
	 * to use it, and someone else retrieves this array
	 * by calling <code>getArray()</code>, then you may have
	 * two entities using the same array to manipulate data...
	 * and that can be really hard to debug!
	 * 
	 * @param array the array you no longer need that might be
	 * needed later.
	 */
	public void putArray(double[] array) {
		Stack stack;
		synchronized(key) {
			key.value = array.length;
			stack = (Stack)map.get(key);
			if(stack==null) {
				stack = new Stack();
				map.put(new MutableInteger(array.length),stack);
			}
		}
		stack.push(array);
	}
}
