package org.geogebra.web.full.gui.exam;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
		app.fileNew();
	}

	@Override
	public void examClearClipboard() {
		app.getCopyPaste().clearClipboard();
		app.getCopyPaste().copyTextToSystemClipboard("");
	}

	@Override
	public void examSetActiveMaterial(@Nullable Material material) {
		app.setActiveMaterial(material);
	}

	@CheckForNull
	@Override
	public Material examGetActiveMaterial() {
		return app.getActiveMaterial();
	}

	@CheckForNull
	@Override
	public SuiteSubApp examGetCurrentSubApp() {
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
		app.switchToSubapp(subApp.appCode);
	}
}
