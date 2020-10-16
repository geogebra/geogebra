package org.geogebra.web.html5.main;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.cas.giac.CASFactoryW;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianSimplePanelW;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.zoompanel.ZoomPanel;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.GeoGebraElement;

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
	public AppWsimple(GeoGebraElement ae, AppletParameters parameters,
			GeoGebraFrameW gf, final boolean undoActive) {
		super(ae, parameters, 2, null);
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

		initCoreObjects();
		setUndoActive(undoActive);
		afterCoreObjectsInited();
		getSettingsUpdater().getFontSettingsUpdater().resetFonts();
		Browser.removeDefaultContextMenu(getGeoGebraElement());
	}

	private void afterCoreObjectsInited() {
		// Code to run before buildApplicationPanel
		GeoGebraFrameW.handleLoadFile(appletParameters, this);
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
			updateVoiceover();
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

	@Override
	public void initFactories() {
		super.initFactories();
		if (!CASFactory.isInitialized()) {
			CASFactory.setPrototype(new CASFactoryW());
		}
	}

}
