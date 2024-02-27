package org.geogebra.common.exam;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.move.ggtapi.models.Material;

/**
 * The idea of this delegate is to "externalize" any functionality that does not fit into
 * the {@link ExamController} itself (either because it's platform-specific behaviour,
 * or is functionality outside the responsibility of the ExamController).
 */
public interface ExamControllerDelegate {

	void requestClearApps();
	void requestClearClipboard();
	void requestSetActiveMaterial(Material material);
	SuiteSubApp getCurrentSubApp();
	void requestSwitchSubApp(SuiteSubApp subApp);
}
