package org.geogebra.common.factories;

import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.Reflection;
import org.geogebra.common.util.URLEncoder;

/**
 * Various util-like factories for common usage
 * 
 * @author Zoltan Kovacs
 */
public abstract class UtilFactory {
	/**
	 * UtilFactory prototype for various common utils
	 */
	private static volatile UtilFactory prototype;

	private static final Object lock = new Object();

	public static UtilFactory getPrototype() {
		return prototype;
	}

	/**
	 * @param p
	 *            prototype
	 */
	public static void setPrototypeIfNull(UtilFactory p) {

		synchronized (lock) {
			if (prototype == null) {
				prototype = p;
			}
		}
	}

	/**
	 * @return HttpRequest object Creates a HttpRequest object
	 */
	public abstract HttpRequest newHttpRequest();

	/**
	 * @return URLEncoder object Creates a URLEncoder object
	 */
	public abstract URLEncoder newURLEncoder();

	/**
	 * @return Prover Creates a Prover object
	 */
	public abstract Prover newProver();

	/**
	 * 
	 * @return current time in milliseconds
	 */
	public abstract double getMillisecondTime();

	/**
	 * @param clazz parameter to reflect
	 * @return new Reflection object
	 */
	public abstract Reflection newReflection(Class clazz);

	/**
	 * @param listener to notify when timer fires
	 * @param delay delay to fire
	 * @return new GTimer object
	 */
	public abstract GTimer newTimer(GTimerListener listener, int delay);
}
