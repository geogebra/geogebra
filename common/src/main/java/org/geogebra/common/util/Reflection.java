package org.geogebra.common.util;

import java.lang.reflect.InvocationTargetException;

/**
 * Reflection class.
 */
public interface Reflection {

	/**
	 * Calls the method with simple name methodName on object with parameters.
	 * @param object the object
	 * @param methodName the simple method name (e.g. call or update)
	 * @param parameters the parameters
	 * @throws NoSuchMethodException if the method does not exist
	 * @throws IllegalAccessException if the method is private or protected
	 * @throws InvocationTargetException if the underlying method throws an exception
	 */
	void call(Object object, String methodName, Object[] parameters)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;
}
