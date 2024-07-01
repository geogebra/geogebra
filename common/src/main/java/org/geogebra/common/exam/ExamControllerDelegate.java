package org.geogebra.common.exam;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.move.ggtapi.models.Material;

/**
 * The idea of this delegate is to "externalize" any functionality that does not fit into
 * the {@link ExamController} itself (either because it's platform-specific behaviour,
 * or is functionality outside the responsibility of the ExamController).
 *
 * @apiNote The `exam` prefix is intended to clearly identify the functions (in the implementing
 * class) as being tied to exam mode.
 */
public interface ExamControllerDelegate {

	/**
	 * Clear (reset) all subapps
	 */

	void examClearApps();

	/**
	 * Clear the clipboard.
	 */
	void examClearClipboard();

	/**
	 * Set the material as the active material in the current subapp.
	 *
	 * @param material A material.
	 */
	void examSetActiveMaterial(@Nullable Material material);

	/**
	 * @return The current active material, or null.
	 */
	@CheckForNull Material examGetActiveMaterial();

	/**
	 * @return The current sub-app, or null in case the app switcher is currently shown.
	 */
	@CheckForNull SuiteSubApp examGetCurrentSubApp();

	/**
	 * Activate the given sub-app.
	 *
	 * @param subApp The sub-app to switch to.
	 */
	void examSwitchSubApp(@Nonnull SuiteSubApp subApp);
}
