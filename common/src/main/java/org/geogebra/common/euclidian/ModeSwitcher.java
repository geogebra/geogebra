package org.geogebra.common.euclidian;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;

public class ModeSwitcher {

	private final App app;
	private final Construction cons;

	/**
	 * @param app application
	 */
	public ModeSwitcher(App app) {
		this.app = app;
		cons = app.getKernel().getConstruction();
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

		case EuclidianConstants.MODE_H5P:
			getDialogManager().showH5PDialog();
			break;

		case EuclidianConstants.MODE_RULER:
			GeoImage ruler = cons.getRuler();
			if (ruler != null) {
				ruler.remove();
				cons.setRuler(null);
			} else {
				cons.setRuler(
						cons.getApplication().getActiveEuclidianView()
								.addMeasurementTool(EuclidianConstants.MODE_RULER, "Ruler.svg"));
			}
			break;

		case EuclidianConstants.MODE_PROTRACTOR:
			GeoImage protractor = cons.getProtractor();
			if (protractor != null) {
				protractor.remove();
				cons.setProtractor(null);
			} else {
				cons.setProtractor(
						cons.getApplication().getActiveEuclidianView()
								.addMeasurementTool(EuclidianConstants.MODE_PROTRACTOR,
										"Protractor.svg"));
			}
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
		ge.setAppName("suite");
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
