package org.geogebra.common.util;

/**
 * Reflection class.
 */
public interface Reflection {

	/**
	 * Calls the method with simple name methodName on object with parameters.
	 * @param object the object
	 * @param methodName the simple method name (e.g. call or update)
	 * @param parameters the parameters
	 * @throws Exception if the method does not exist, if it is private or protected or
	 *                   if the underlying method throws an exception
	 */
	void call(Object object, String methodName, Object[] parameters) throws Exception;
}
