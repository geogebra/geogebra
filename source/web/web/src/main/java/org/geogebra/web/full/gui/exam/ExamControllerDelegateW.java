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

package org.geogebra.web.full.gui.exam;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.ExamControllerDelegate;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;

public class ExamControllerDelegateW implements ExamControllerDelegate {

	private final AppW app;

	public ExamControllerDelegateW(AppW app) {
		this.app = app;
	}

	@Override
	public void examClearApps() {
		if (app instanceof AppWFull) {
			((AppWFull) app).clearSubAppCons();
		}
		if (app.getGuiManager() != null) {
			app.fileNew();
		}
	}

	@Override
	public void examClearClipboard() {
		app.getCopyPaste().clearClipboard();
		app.getCopyPaste().copyTextToSystemClipboard("");
	}

	@Override
	public void examSetActiveMaterial(@CheckForNull Material material) {
		app.setActiveMaterial(material);
	}

	@Override
	public @CheckForNull Material examGetActiveMaterial() {
		return app.getActiveMaterial();
	}

	@Override
	public @CheckForNull SuiteSubApp examGetCurrentSubApp() {
		String subAppCode = app.getConfig().getSubAppCode();
		if (!app.isSuite() || subAppCode == null) {
			return null;
		}
		switch (subAppCode) {
		case GeoGebraConstants.CAS_APPCODE:
			return SuiteSubApp.CAS;
		case GeoGebraConstants.GEOMETRY_APPCODE:
			return SuiteSubApp.GEOMETRY;
		case GeoGebraConstants.GRAPHING_APPCODE:
			return SuiteSubApp.GRAPHING;
		case GeoGebraConstants.G3D_APPCODE:
			return SuiteSubApp.G3D;
		case GeoGebraConstants.PROBABILITY_APPCODE:
			return SuiteSubApp.PROBABILITY;
		case GeoGebraConstants.SCIENTIFIC_APPCODE:
			return SuiteSubApp.SCIENTIFIC;
		default:
			return null;
		}
	}

	@Override
	public void examSwitchSubApp(@Nonnull SuiteSubApp subApp) {
		if (!app.isSuite()) {
			return;
		}
		app.switchToSubapp(subApp);
	}
}
