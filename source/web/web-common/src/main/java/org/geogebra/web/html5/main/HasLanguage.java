package org.geogebra.web.html5.main;

/**
 * Interface for AppW's doSetLanguage function.
 */
public interface HasLanguage {

	/**
	 * @param lang
	 *            locale string
	 * @param asyncCall
	 *            whether to call this asynchronously
	 */
	void doSetLanguage(String lang, boolean asyncCall);

}
