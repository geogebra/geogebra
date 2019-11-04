package org.geogebra.web.html5.main;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianSimplePanelW;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.zoompanel.ZoomPanel;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.ArticleElementInterface;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Simple app, may only have one EV
 *
 */
public class AppWsimple extends AppW {
	private GeoGebraFrameW frame;

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
	public AppWsimple(ArticleElementInterface ae, GeoGebraFrameW gf,
	        final boolean undoActive) {
		super(ae, 2, null);
		this.frame = gf;
		setAppletHeight(frame.getComputedHeight());
		setAppletWidth(frame.getComputedWidth());

		this.useFullGui = false;

		Log.info("GeoGebra " + GeoGebraConstants.VERSION_STRING + " "
		        + GeoGebraConstants.BUILD_DATE + " "
		        + Window.Navigator.getUserAgent());
		initCommonObjects();
		initing = true;

		// TODO: EuclidianSimplePanelW
		this.euclidianViewPanel = new EuclidianSimplePanelW(this);

		this.canvas = this.euclidianViewPanel.getCanvas();
		canvas.setWidth("1px");
		canvas.setHeight("1px");
		canvas.setCoordinateSpaceHeight(1);
		canvas.setCoordinateSpaceWidth(1);
		initCoreObjects();
		setUndoActive(undoActive);
		afterCoreObjectsInited();
		getSettingsUpdater().getFontSettingsUpdater().resetFonts();
		Browser.removeDefaultContextMenu(this.getArticleElement().getElement());
		if (Browser.runningLocal() && ArticleElement.isEnableUsageStats()) {
			new GeoGebraTubeAPIWSimple(has(Feature.TUBE_BETA), ae)
			        .checkAvailable(null);
		}
	}

	private void afterCoreObjectsInited() {
		// Code to run before buildApplicationPanel

		// initGuiManager();// TODO: comment it out

		GeoGebraFrameW.handleLoadFile(articleElement, this);
		initing = false;
		if (ZoomPanel.neededFor(this)) {
			ZoomPanel zp = new ZoomPanel(getEuclidianView1(), this, true, true);
			setZoomPanel(zp);
			euclidianViewPanel.getAbsolutePanel().add(zp);
		}
	}

	@Override
	public void buildApplicationPanel() {
		if (frame != null) {
			frame.clear();
			frame.add((Widget) getEuclidianViewpanel());
			getEuclidianViewpanel()
			        .setPixelSize(
			                getSettings().getEuclidian(1).getPreferredSize()
			                        .getWidth(),
			                getSettings().getEuclidian(1).getPreferredSize()
			                        .getHeight());
		}
	}

	@Override
	public void afterLoadFileAppOrNot(boolean asSlide) {
		for (GeoElement geo : kernel.getConstruction()
				.getGeoSetConstructionOrder()) {
			if (geo.hasScripts()) {
				getAsyncManager().loadAllCommands();
				break;
			}
		}

		buildApplicationPanel();

		getScriptManager().ggbOnInit(); // put this here from Application
										// constructor because we have to delay
										// scripts until the EuclidianView is
										// shown

		initUndoInfoSilent();

		EuclidianViewW view = getEuclidianView1();
		view.synCanvasSize();
		view.createImage();
		getAppletFrame().resetAutoSize();

		frame.hideSplash();

		setDefaultCursor();
		checkScaleContainer();
		frame.useDataParamBorder();
		setAltText();
		updateEuclidianView(view);
	}

	private void updateEuclidianView(EuclidianViewW view) {
		view.updateBounds(true, true);
		view.doRepaint2();
	}

	@Override
	public void focusLost(View v, Element el) {
		super.focusLost(v, el);
		frame.useDataParamBorder();
		this.getGlobalKeyDispatcher().setFocused(false);
	}

	@Override
	public void focusGained(View v, Element el) {
		super.focusGained(v, el);
		frame.useFocusedBorder();
		Log.debug("AppWsimple_focusGained");

		// if focusLost sets this to false, it is probably
		// right to set this to true again here! Otherwise
		// it would only be set to true in case of key ENTER,
		// but of course, we also want to be able to focus by mouse
		// Graphics views and Algebra views register GlobalKeyDispatcher,
		// so in those cases, this is good, otherwise (?)
		switch (v.getViewID()) {
		case App.VIEW_ALGEBRA:
		case App.VIEW_EUCLIDIAN:
		case App.VIEW_EUCLIDIAN2:
			this.getGlobalKeyDispatcher().setFocusedIfNotTab();
			break;
		default:
			if (App.isView3D(v.getViewID())
					|| ((v.getViewID() >= App.VIEW_EUCLIDIAN_FOR_PLANE_START) && (v
							.getViewID() <= App.VIEW_EUCLIDIAN_FOR_PLANE_END))) {
				this.getGlobalKeyDispatcher().setFocusedIfNotTab();
			}
		}
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
	public Panel getPanel() {
		return frame;
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

}
