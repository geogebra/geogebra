/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
