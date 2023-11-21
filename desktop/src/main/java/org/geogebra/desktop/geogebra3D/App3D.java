/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.geogebra3D;

import java.awt.Component;
import java.awt.Container;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianCursor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLFactory;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.main.App3DCompanion;
import org.geogebra.common.gui.layout.DockManager;
import org.geogebra.common.jre.openGL.GLFactoryJre;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.AnimationExportSlider;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppCompanion;
import org.geogebra.common.main.settings.updater.SettingsUpdaterBuilder;
import org.geogebra.desktop.CommandLineArguments;
import org.geogebra.desktop.geogebra3D.euclidian3D.EuclidianController3DD;
import org.geogebra.desktop.geogebra3D.euclidian3D.EuclidianView3DD;
import org.geogebra.desktop.geogebra3D.euclidianFor3D.EuclidianControllerFor3DD;
import org.geogebra.desktop.geogebra3D.euclidianFor3D.EuclidianViewFor3DD;
import org.geogebra.desktop.geogebra3D.gui.GuiManager3D;
import org.geogebra.desktop.geogebra3D.gui.layout.panels.EuclidianDockPanel3DD;
import org.geogebra.desktop.geogebra3D.util.ImageManager3D;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.app.GeoGebraFrame3D;
import org.geogebra.desktop.gui.layout.DockManagerD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.main.settings.updater.FontSettingsUpdaterD;
import org.geogebra.desktop.util.FrameCollector;

/**
 * GeoGebra Application
 *
 * @author Markus Hohenwarter
 */
public class App3D extends AppD {

	private EuclidianView3D euclidianView3D;
	private EuclidianController3D euclidianController3D;

	private EuclidianCursor oldCursorMode;

	private boolean useShaders;

	private boolean isStereo3D;

	/**
	 * @param args arguments
	 * @param frame frame
	 */
	public App3D(CommandLineArguments args, JFrame frame) {
		super(args, frame, null, true, new LocalizationD(3));
	}

	/**
	 * @param args arguments
	 * @param comp frame
	 */
	public App3D(CommandLineArguments args, Container comp) {
		super(args, null, comp, true, new LocalizationD(3));
	}

	@Override
	protected void initImageManager(Component component) {
		imageManager = new ImageManager3D(component);
	}

	private void initEuclidianController3D() {
		euclidianController3D = new EuclidianController3DD(kernel);
	}

	@Override
	protected void exitFrame() {
		super.exitFrame();
		if (euclidianController3D != null) {
			euclidianController3D.exitInput3D();
		}
	}

	@Override
	public EuclidianController newEuclidianController(Kernel kernel) {
		return new EuclidianControllerFor3DD(kernel);
	}

	@Override
	protected EuclidianView newEuclidianView(boolean[] showAxes1,
			boolean showGrid1) {
		return new EuclidianViewFor3DD(getEuclidianController(), showAxes1,
				showGrid1, 1, getSettings().getEuclidian(1));
	}

	@Override
	public void setMode(int mode) {
		super.setMode(mode);

		if (isEuclidianView3Dinited()) {
			euclidianView3D.setMode(mode);
		}
	}

	@Override
	public String getCompleteUserInterfaceXML(boolean asPreference) {
		StringBuilder sb = new StringBuilder();

		// save super settings
		sb.append(super.getCompleteUserInterfaceXML(asPreference));

		// save euclidianView3D settings
		if (isEuclidianView3Dinited()) {
			euclidianView3D.getXML(sb, asPreference);
		}

		// save euclidian views for plane settings
		((App3DCompanion) companion).addCompleteUserInterfaceXMLForPlane(sb,
				asPreference);

		return sb.toString();
	}

	/**
	 * return the 3D euclidian view
	 * 
	 * @return the 3D euclidian view
	 */
	@Override
	public EuclidianView3D getEuclidianView3D() {
		if (this.euclidianView3D == null) {
			setWaitCursor();
			initEuclidianController3D();
			euclidianView3D = new EuclidianView3DD(euclidianController3D,
					getSettings().getEuclidian(3));

			setDefaultCursor();
		}
		return euclidianView3D;
	}

	@Override
	public boolean isEuclidianView3Dinited() {
		return this.euclidianView3D != null;
	}

	@Override
	public void needThumbnailFor3D() {
		if (euclidianView3D != null) {
			getEuclidianView3D().getRenderer().needExportImage();
		}
	}

	/**
	 * check is view is 3D WITHOUT creating 3D View
	 * 
	 * @param view
	 *            view
	 * @return true if it's 3D
	 */
	@Override
	public boolean isEuclidianView3D(EuclidianViewInterfaceCommon view) {
		// euclidianView3D might be null
		return view != null && view == euclidianView3D;
	}

	// ///////////////////////////////
	// GUI
	// ///////////////////////////////

	@Override
	public void refreshViews() {
		if (isEuclidianView3Dinited()) {
			getEuclidianView3D().reset();
			DockManagerD dockManager = (DockManagerD) getGuiManager()
					.getLayout().getDockManager();
			((EuclidianDockPanel3DD) dockManager.getPanel(VIEW_EUCLIDIAN3D))
					.refresh(dockManager);

		}
		super.refreshViews();
	}

	@Override
	public void resume3DRenderer() {
		if (isEuclidianView3Dinited()) {
			DockManager dockManager = getGuiManager().getLayout()
					.getDockManager();
			((EuclidianDockPanel3DD) dockManager.getPanel(VIEW_EUCLIDIAN3D))
					.resumeRenderer();

		}
	}

	public void toggleAxis3D() {
		getEuclidianView3D().toggleAxis();
	}

	public void togglePlane() {
		getEuclidianView3D().getSettings().togglePlane();
	}

	public void toggleGrid3D() {
		getEuclidianView3D().toggleGrid();
	}

	public void setShowAxesSelected3D(JCheckBoxMenuItem cb) {
		cb.setSelected(getEuclidianView3D().axesAreAllVisible());
	}

	/**
	 * set the show plane combo box selected if the plane is visible
	 * 
	 * @param cb checkbox
	 */
	public void setShowPlaneSelected(JCheckBoxMenuItem cb) {
		GeoPlane3D p = (GeoPlane3D) getKernel().getXOYPlane();
		cb.setSelected(p.isPlateVisible());
	}

	/**
	 * set the show grid combo box selected if the plane is visible
	 * 
	 * @param cb checkbox
	 */
	public void setShowGridSelected3D(JCheckBoxMenuItem cb) {
		GeoPlane3D p = (GeoPlane3D) getKernel().getXOYPlane();
		cb.setSelected(p.isGridVisible());
	}

	@Override
	protected GuiManagerD newGuiManager() {
		return new GuiManager3D(this);
	}

	// /////////////////////////////////////
	// COMMANDS
	// /////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see geogebra.main.Application#getCommandSyntax(java.lang.String) check
	 * if there's a Command.Syntax3D key. If not, return Command.Syntax key
	 */

	@Override
	public void updateStyleBars() {
		super.updateStyleBars();
		if (showView(App.VIEW_EUCLIDIAN3D) && getEuclidianView3D().hasStyleBar()) {
			getEuclidianView3D().getStyleBar().updateStyleBar();
		}
	}

	// ///////////////////////////////
	// FOR TESTING : TODO remove all

	@Override
	public boolean is3D() {
		return true;
	}

	@Override
	protected void handleShiftEvent(boolean isShiftDown) {
		if (!this.isEuclidianView3Dinited()) {
			return;
		}
		if (isShiftDown) {
			EuclidianCursor cursor = getEuclidianView3D()
					.updateCursorIfNotTranslateViewCursor();
			if (cursor != null) {
				oldCursorMode = cursor;
			}
			// oldCursorMode = getEuclidianView3D().getCursor();
			// getEuclidianView3D().setCursor(EuclidianCursor.MOVE);
			// Log.debug(oldCursorMode);
		} else {
			if (oldCursorMode != null) {
				getEuclidianView3D().setCursor(oldCursorMode);
			}
		}
	}

	@Override
	public void exportAnimatedGIF(EuclidianView ev, FrameCollector gifEncoder,
			AnimationExportSlider num, int n, double val, double min,
			double max, double step) {

		if (!(ev instanceof EuclidianView3D)) {
			// regular 2D export
			super.exportAnimatedGIF(ev, gifEncoder, num, n, val, min, max,
					step);
			return;
		}

		getEuclidianView3D().getRenderer().startAnimatedGIFExport(gifEncoder,
				num, n, val, min, max, step);
	}

	@Override
	public void copyGraphicsViewToClipboard() {

		if (!(getActiveEuclidianView() instanceof EuclidianView3D)) {
			// regular 2D export
			super.copyGraphicsViewToClipboard();
			return;
		}

		getEuclidianView3D().getRenderer().exportToClipboard();

	}

	@Override
	public void fileNew() {
		super.fileNew();

		((App3DCompanion) companion).removeAllEuclidianViewForPlane();
	}

	@Override
	public boolean loadFile(File file, boolean isMacroFile) {

		if (!checkFileExistsAndShowFileNotFound(file)) {
			return false;
		}

		((App3DCompanion) companion).removeAllEuclidianViewForPlane();

		return loadExistingFile(file, isMacroFile);
	}

	@Override
	public void createNewWindow() {
		GeoGebraFrame3D.createNewWindow3D(cmdArgs.getGlobalArguments());
	}

	@Override
	public GBufferedImage getActiveEuclidianViewExportImage(double maxX,
			double maxY) {

		EuclidianView ev = getActiveEuclidianView();

		// force 3D view if showing
		if (this.euclidianView3D != null) {
			EuclidianView3D ev3D = getEuclidianView3D();
			if (ev3D.isShowing()) {
				ev = ev3D;
			}
		}

		return getEuclidianViewExportImage(ev, maxX, maxY);
	}

	/**
	 * only for 3D really. Overridden in App3D
	 */
	@Override
	public void uploadToGeoGebraTubeOnCallback() {

		if (!isEuclidianView3Dinited()) {
			uploadToGeoGebraTube();
			return;
		}

		EuclidianView3D ev3D = getEuclidianView3D();

		if (ev3D.isShowing()) {
			ev3D.getRenderer().uploadToGeoGebraTube();
		} else {
			uploadToGeoGebraTube();
		}

	}

	@Override
	protected void initFactories() {
		super.initFactories();

		if (GLFactory.getPrototype() == null) {
			GLFactory.setPrototypeIfNull(new GLFactoryJre());
		}
	}

	@Override
	protected AppCompanion newAppCompanion() {
		return new App3DCompanionD(this);
	}

	@Override
	protected void handleOptionArgsEarly(CommandLineArguments args) {
		super.handleOptionArgsEarly(args);

		isStereo3D = false;
		useShaders = false;

		if (args == null) {
			return;
		}

		if (args.containsArg("testShaders")) {
			useShaders = true;
		}

		if (args.containsArg("stereo")) {
			isStereo3D = true;
		}

	}

	public boolean isStereo3D() {
		return isStereo3D;
	}

	@Override
	public boolean useShaders() {
		return useShaders;
	}

	@Override
	protected AppD newAppForTemplateOrInsertFile() {
		return new App3D(new CommandLineArguments(null), new JPanel());
	}

	@Override
	public boolean handleSpaceKey() {

		if (isEuclidianView3Dinited()) {
			if (euclidianView3D.getCompanion().useHandGrabbing()) {
				euclidianView3D.handleSpaceKey();
			}
		}

		return super.handleSpaceKey();
	}

	@Override
	protected SettingsUpdaterBuilder newSettingsUpdaterBuilder() {
		return new SettingsUpdaterBuilder(this)
				.withFontSettingsUpdater(new FontSettingsUpdaterD(this));
	}
}
