package org.geogebra.common.exam;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.gui.toolcategorization.ToolsProvider;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.main.settings.Settings;
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
	 *
	 * @apiNote This method is only relevant for the mobile use case (where there is only
	 * one Suite instance). Web use case: if there are multiple Suite app instances with different
	 * active apps, we can't use this mechanism to switch away from a forbidden subapp at exam
	 * start; this would need to be handled in Web client code.
	 */
	@CheckForNull SuiteSubApp examGetCurrentSubApp();

	/**
	 * Activate the given sub-app.
	 *
	 * @param subApp The sub-app to switch to.
	 *
	 * @apiNote For the mobile use case, this method is expected to call {@link ExamController#setActiveContext(Object, CommandDispatcher, AlgebraProcessor, Localization, Settings, AutocompleteProvider, ToolsProvider)}
	 * after switching to the new subapp.
	 * For the Web use case, this method must not call {@link ExamController#setActiveContext(Object, CommandDispatcher, AlgebraProcessor, Localization, Settings, AutocompleteProvider, ToolsProvider)}.
	 */
	void examSwitchSubApp(@Nonnull SuiteSubApp subApp);
}
