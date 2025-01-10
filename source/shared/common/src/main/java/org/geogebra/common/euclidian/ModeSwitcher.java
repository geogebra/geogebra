package org.geogebra.common.euclidian;

import org.geogebra.common.euclidian.measurement.MeasurementController;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;

public class ModeSwitcher {

	private final App app;
	private final MeasurementController measurementController;

	/**
	 * @param app application
	 * @param measurementController {@link MeasurementController}
	 */
	public ModeSwitcher(App app, MeasurementController measurementController) {
		this.app = app;
		this.measurementController = measurementController;
	}

	/**
	 * Switch mode from toolbar
	 * @param newMode mode being set
	 */
	public void switchMode(int newMode) {
		EmbedManager embedManager = app.getEmbedManager();
		switch (newMode) {
		case EuclidianConstants.MODE_CAMERA:
			app.getGuiManager().loadWebcam();
			return;

		case EuclidianConstants.MODE_AUDIO:
			getDialogManager().showAudioInputDialog();
			break;

		case EuclidianConstants.MODE_VIDEO:
			getDialogManager().showVideoInputDialog();
			break;

		case EuclidianConstants.MODE_PDF:
			getDialogManager().showPDFInputDialog();
			break;

		case EuclidianConstants.MODE_GRASPABLE_MATH:
			if (embedManager != null) {
				embedManager.openGraspableMTool();
			}
			break;

		case EuclidianConstants.MODE_EXTENSION:
			getDialogManager().showEmbedDialog();
			break;

		case EuclidianConstants.MODE_RULER:
		case EuclidianConstants.MODE_PROTRACTOR:
		case EuclidianConstants.MODE_TRIANGLE_PROTRACTOR:
			measurementController.toggleActiveTool(newMode);
			break;

		default:
			break;
		}
	}

	private DialogManager getDialogManager() {
		return app.getDialogManager();
	}
}
