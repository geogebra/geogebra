package org.geogebra.common.euclidian;

import static org.geogebra.common.GeoGebraConstants.SUITE_APPCODE;

import org.geogebra.common.euclidian.measurement.MeasurementController;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;

public class ModeSwitcher {

	private final App app;
	private final Construction cons;
	private final MeasurementController measurementController;

	/**
	 * @param app application
	 * @param measurementController {@link MeasurementController}
	 */
	public ModeSwitcher(App app, MeasurementController measurementController) {
		this.app = app;
		cons = app.getKernel().getConstruction();
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
		if (embedManager != null
				&& newMode == EuclidianConstants.MODE_CALCULATOR) {
			setUpEmbedManager(embedManager);
		}

	}

	private DialogManager getDialogManager() {
		return app.getDialogManager();
	}

	private void setUpEmbedManager(EmbedManager embedManager) {
		final GeoEmbed ge = new GeoEmbed(cons);
		ge.setAppName(SUITE_APPCODE);
		EuclidianView view = app.getActiveEuclidianView();
		ge.initDefaultPosition(view);
		embedManager.initAppEmbed(ge);
		ge.setLabel(null);
		app.storeUndoInfo();
		app.invokeLater(() -> {
			view.getEuclidianController().selectAndShowSelectionUI(ge);
			ge.setBackground(false);
			view.update(ge); // force painting in the foreground
		});
	}
}
