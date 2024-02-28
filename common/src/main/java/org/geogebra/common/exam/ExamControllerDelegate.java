package org.geogebra.common.exam;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.move.ggtapi.models.Material;

/**
 * The idea of this delegate is to "externalize" any functionality that does not fit into
 * the {@link ExamController} itself (either because it's platform-specific behaviour,
 * or is functionality outside the responsibility of the ExamController).
 */
public interface ExamControllerDelegate {

	/**
	 * Clear (reset) all subapps other than the currently active one.
	 */
	void examClearOtherApps();

	/**
	 * Clear the clipboard.
	 */
	void examClearClipboard();

	/**
	 * Perform the equivalent of File / New.
	 */
	void examCreateNewFile();

	/**
	 * Set the material as the active material in the current subapp.
	 * @param material
	 */
	void examSetActiveMaterial(Material material);
	SuiteSubApp examGetCurrentSubApp();
	void examSwitchSubApp(SuiteSubApp subApp);
}
