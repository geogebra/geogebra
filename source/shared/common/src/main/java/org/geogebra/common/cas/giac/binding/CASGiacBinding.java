package org.geogebra.common.cas.giac.binding;

/**
 * Cross-platform interfac for Giac binding
 */
public interface CASGiacBinding {

	/**
	 * @return evaluation context
	 */
    Context createContext();

	/**
	 * @param string
	 *            expression in giac syntax
	 * @param context
	 *            evaluation context
	 * @return parsed expression
	 */
    Gen createGen(String string, Context context);
}
