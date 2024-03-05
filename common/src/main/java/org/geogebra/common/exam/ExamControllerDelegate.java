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
 * @implNote The `exam` prefix is intended to clearly identify the functions (in the implementing
 * class) as being tied to exam mode.
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
	 * Perform the equivalent of File / New. The intent here is to clear the current apps' content.
	 */
	void examCreateNewFile();

	/**
	 * Set the material as the active material in the current subapp.
	 * @param material
	 */
	void examSetActiveMaterial(@Nullable Material material);

	@CheckForNull Material examGetActiveMaterial();

	@CheckForNull SuiteSubApp examGetCurrentSubApp();

	void examSwitchSubApp(@Nonnull SuiteSubApp subApp);
}
