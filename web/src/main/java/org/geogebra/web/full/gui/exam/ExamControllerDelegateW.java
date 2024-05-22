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
	public void examClearCurrentApp() {
		app.fileNew();
	}

	@Override
	public void examClearOtherApps() {
		if (app instanceof AppWFull) {
			((AppWFull) app).clearSubAppCons();
		}
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
		switch (subApp) {
		case CAS:
			app.switchToSubapp(GeoGebraConstants.CAS_APPCODE);
			return;
		case GEOMETRY:
			app.switchToSubapp(GeoGebraConstants.GEOMETRY_APPCODE);
			return;
		case GRAPHING:
			app.switchToSubapp(GeoGebraConstants.GRAPHING_APPCODE);
			return;
		case G3D:
			app.switchToSubapp(GeoGebraConstants.G3D_APPCODE);
			return;
		case PROBABILITY:
			app.switchToSubapp(GeoGebraConstants.PROBABILITY_APPCODE);
			return;
		case SCIENTIFIC:
			app.switchToSubapp(GeoGebraConstants.SCIENTIFIC_APPCODE);
		}
	}
}
