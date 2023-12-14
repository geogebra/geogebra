package org.geogebra.common.exam;

/**
 * The idea of this delegate is to "externalize" any functionality that does not fit into
 * the {@link ExamController} itself (e.g., because it's platform-specific behaviour).
 */
public interface ExamControllerDelegate {

	/**
	 * Clear (i.e., reset the contents of) all subapps.
	 *
	 * @implNote The reason this is handled via the delegate is that the different client
	 * platforms handle ownership of the {@link org.geogebra.common.main.App} instances differently.
	 */
	void clearAllApps();

	/**
	 * Clear the clipboard.
	 */
	void clearClipboard();
}
