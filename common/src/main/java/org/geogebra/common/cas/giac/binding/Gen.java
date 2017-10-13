package org.geogebra.common.cas.giac.binding;

/**
 * Giac expression
 */
public interface Gen {

	/**
	 * @param level
	 *            -- always 1
	 * @param context
	 *            giac context
	 * @return result of evaluation
	 */
    Gen eval(int level, Context context);

	/**
	 * @param context
	 *            giac context
	 * @return streing representation of this object
	 */
    String print(Context context);
}
