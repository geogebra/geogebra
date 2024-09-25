package org.geogebra.web.html5.main;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianSimplePanelW;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.gui.GeoGebraFrameSimple;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.zoompanel.ZoomPanel;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.Widget;

/**
 * Simple app, may only have one EV
 *
 */
public class AppWsimple extends AppW {
	private GeoGebraFrameSimple frame;

	/******************************************************
	 * Constructs AppW for applets
	 * 
	 * @param ae
	 *            article element
	 * @param gf
	 *            frame
	 * 
	 * @param undoActive
	 *            if true you can undo by CTRL+Z and redo by CTRL+Y
	 */
	public AppWsimple(GeoGebraElement ae, AppletParameters parameters,
			GeoGebraFrameSimple gf, final boolean undoActive) {
		super(ae, parameters, 2, null);
		this.frame = gf;
		setAppletHeight(frame.getComputedHeight());
		setAppletWidth(frame.getComputedWidth());

		this.useFullGui = false;

		Log.info("GeoGebra " + GeoGebraConstants.VERSION_STRING + " "
				+ GeoGebraConstants.BUILD_DATE);
		initCommonObjects();
		initing = true;

		// TODO: EuclidianSimplePanelW
		this.euclidianViewPanel = new EuclidianSimplePanelW(this);

		initCoreObjects();
		setUndoActive(undoActive);
		afterCoreObjectsInited();
		getFontSettingsUpdater().resetFonts();
		Browser.removeDefaultContextMenu(getGeoGebraElement().getElement());
	}

	private void afterCoreObjectsInited() {
		// Code to run before buildApplicationPanel
		GeoGebraFrameW.handleLoadFile(appletParameters, this);
		initing = false;
		if (ZoomPanel.neededFor(this)) {
			ZoomPanel zp = new ZoomPanel(getEuclidianView1(), this, true, true);
			setZoomPanel(zp);
			frame.add(zp);
		}
	}

	@Override
	public void buildApplicationPanel() {
		if (frame != null) {
			frame.clear();
			frame.add((Widget) getEuclidianViewpanel());
			if (getZoomPanel() != null) {
				frame.add(getZoomPanel());
			}
			getEuclidianViewpanel().setPixelSize(
					getInnerAppletWidth(),
					getInnerAppletHeight());
			updateVoiceover();
		}
	}

	@Override
	public void afterLoadFileAppOrNot(boolean asSlide) {
		super.afterLoadFileAppOrNot(asSlide);

		buildApplicationPanel();
		getScriptManager().ggbOnInit(); // put this here from Application
										// constructor because we have to delay
										// scripts until the EuclidianView is
										// shown
		if (needsSpreadsheetTableModel()) {
			getSpreadsheetTableModel(); // spreadsheet trace useful even without UI
		}
		initUndoInfoSilent();

		EuclidianViewW view = getEuclidianView1();
		view.synCanvasSize();
		view.createImage();
		getAppletFrame().resetAutoSize();

		frame.hideSplash();

		setDefaultCursor();
		checkScaleContainer();
		frame.useDataParamBorder();
		updateEuclidianView(view);
	}

	private void updateEuclidianView(EuclidianViewW view) {
		view.updateBounds(true, true);
		view.doRepaint2();
	}

	@Override
	public Element getFrameElement() {
		return frame.getElement();
	}

	@Override
	public GeoGebraFrameW getAppletFrame() {
		return frame;
	}

	@Override
	public boolean isSelectionRectangleAllowed() {
		return getToolbar() != null;
	}

	@Override
	public void setLanguage(final String browserLang) {
		// no localization support needed in webSimple
	}

	@Override
	public void copyGraphicsViewToClipboard() {
		Log.debug("unimplemented");
	}

	@Override
	final public String getReverseCommand(String command) {
		// translations not available in webSimple
		return command;
	}

	@Override
	public AlgebraView getAlgebraView() {
		return null;
	}

	@Override
	public void initFactories() {
		super.initFactories();
		frame.initCasFactory();
	}

	@Override
	public ErrorHandler getDefaultErrorHandler() {
		return new ErrorHandler() {
			@Override
			public void showError(String msg) {
				getToolTipManager().showBottomMessage(msg, frame.getApp());
			}

			@Override
			public void showCommandError(String command, String message) {
				getToolTipManager().showBottomMessage(message, frame.getApp());
			}

			@Override
			public String getCurrentCommand() {
				return null;
			}

			@Override
			public boolean onUndefinedVariables(String string, AsyncOperation<String[]> callback) {
				return false;
			}

			@Override
			public void resetError() {
				// nothing to di here
			}
		};
	}
}
