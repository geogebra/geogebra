/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.jre.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.util.Reflection;

/**
 * Implements general reflection utilities.
 */
public class ReflectionJre implements Reflection {

	private final Method[] methods;
	private final Map<String, Method> cache;

	/**
	 * Create a reflection for reflecting clazz methods.
	 *
	 * @param clazz methods to reflect
	 */
	public ReflectionJre(Class clazz) {
		this.methods = clazz.getMethods();
		this.cache = new HashMap<>();
	}

	@Override
	public void call(Object object, String methodName, Object[] parameters) throws Exception {
		Method method = getMethod(methodName);
		method.invoke(object, parameters);
	}

	private Method getMethod(String methodName) throws Exception {
		Method method = cache.get(methodName);
		if (method == null) {
			method = findMethod(methodName);
			cache.put(methodName, method);
		}
		return method;
	}

	private Method findMethod(String methodName) throws Exception {
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(methodName)) {
				return methods[i];
			}
		}
		throw new NoSuchMethodException(methodName);
	}
}
