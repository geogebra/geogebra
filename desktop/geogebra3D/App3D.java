/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/**
 * GeoGebra Application
 *
 * @author Markus Hohenwarter
 */
package geogebra3D;

import geogebra.CommandLineArguments;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CommandsConstants;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.ViewCreator;
import geogebra.euclidian.EuclidianControllerD;
import geogebra.gui.GuiManagerD;
import geogebra.gui.layout.LayoutD;
import geogebra.main.AppD;
import geogebra.main.AppletImplementation;
import geogebra.main.GlobalKeyDispatcherD;
import geogebra3D.euclidian3D.EuclidianController3D;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.euclidian3D.opengl.RendererJogl;
import geogebra3D.euclidianFor3D.EuclidianControllerFor3D;
import geogebra3D.euclidianFor3D.EuclidianViewFor3D;
import geogebra3D.euclidianForPlane.EuclidianControllerForPlane;
import geogebra3D.euclidianForPlane.EuclidianViewForPlane;
import geogebra3D.gui.GuiManager3D;
import geogebra3D.gui.layout.panels.EuclidianDockPanelForPlane;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.Kernel3D;
import geogebra3D.util.ImageManager3D;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;

public class App3D extends AppD {

	private EuclidianView3D euclidianView3D;
	private EuclidianController3D euclidianController3D;
	protected Kernel3D kernel3D;

	private EuclidianViewForPlane euclidianViewForPlane;

	public App3D(CommandLineArguments args, JFrame frame,
			boolean undoActive) {
		this(args, frame, null, undoActive);
	}

	public App3D(CommandLineArguments args,
			AppletImplementation applet, boolean undoActive) {
		this(args, null, applet, undoActive);
	}

	private App3D(CommandLineArguments args, JFrame frame,
			AppletImplementation applet, boolean undoActive) {

		super(args, frame, applet, null, undoActive);

		// euclidianView3D.initAxisAndPlane();

		// TODO remove 3D test : just comment following line
		// new Test3D(kernel3D,euclidianView,euclidianView3D,this);

	}

	@Override
	public void initKernel() {
		kernel3D = new Kernel3D(this);
		kernel = kernel3D;
	}

	@Override
	protected boolean tableVisible(int table) {
		return !(table ==CommandsConstants.TABLE_ENGLISH);
	}
	@Override
	protected void initImageManager(Component component) {
		imageManager = new ImageManager3D(component);
	}

	/**
	 * init the EuclidianView (and EuclidianView3D for 3D)
	 */
	@Override
	public void initEuclidianViews() {

		// init the 2D euclidian view
		super.initEuclidianViews();

		// init the 3D euclidian view
		euclidianController3D = new EuclidianController3D(kernel3D);
		euclidianView3D = new EuclidianView3D(euclidianController3D,null);

	}

	@Override
	protected EuclidianControllerD newEuclidianController(Kernel kernel) {
		return new EuclidianControllerFor3D(kernel);
	}

	@Override
	protected EuclidianView newEuclidianView(boolean[] showAxes,
			boolean showGrid) {
		return new EuclidianViewFor3D((EuclidianControllerD)euclidianController, showAxes, showGrid,
				1);
	}

	@Override
	public void setMode(int mode) {
		super.setMode(mode);

		// if (euclidianView3D != null)
		euclidianView3D.setMode(mode);
	}

	@Override
	public String getCompleteUserInterfaceXML(boolean asPreference) {
		StringBuilder sb = new StringBuilder();

		// save super settings
		sb.append(super.getCompleteUserInterfaceXML(asPreference));

		// save euclidianView3D settings
		euclidianView3D.getXML(sb,asPreference);

		return sb.toString();
	}

	/**
	 * return the 3D euclidian view
	 * 
	 * @return the 3D euclidian view
	 */
	@Override
	public EuclidianView3D getEuclidianView3D() {
		return euclidianView3D;
	}

	@Override
	public boolean hasEuclidianView3D() {
		return euclidianView3D != null;
	}

	@Override
	public void getEuclidianViewXML(StringBuilder sb, boolean asPreference) {
		super.getEuclidianViewXML(sb, asPreference);
		getEuclidianView3D().getXML(sb,asPreference);
	}

	@Override
	public BufferedImage getExportImage(double maxX, double maxY)
			throws OutOfMemoryError {
		// TODO use maxX, maxY values
		return getEuclidianView3D().getRenderer().getExportImage();
	}

	@Override
	public boolean saveGeoGebraFile(File file) {
		// TODO generate it before
		getEuclidianView3D().getRenderer().needExportImage();

		return super.saveGeoGebraFile(file);
	}

	// ///////////////////////////////
	// EUCLIDIAN VIEW FOR PLANE
	// ///////////////////////////////

	/**
	 * @param plane
	 * @return create a new euclidian view for the plane
	 */
	public EuclidianViewForPlane createEuclidianViewForPlane(ViewCreator plane) {
		// create new view for plane and controller
		EuclidianControllerD ec = new EuclidianControllerForPlane(kernel3D);
		euclidianViewForPlane = new EuclidianViewForPlane(ec, plane);
		euclidianViewForPlane.updateFonts();
		euclidianViewForPlane.addExistingGeos();

		// create dock panel
		EuclidianDockPanelForPlane panel = new EuclidianDockPanelForPlane(this,
				euclidianViewForPlane);
		((LayoutD) getGuiManager().getLayout()).registerPanel(panel);

		// panel.setToolbarString(dpInfo[i].getToolbarString());
		panel.setFrameBounds(new Rectangle(600, 400));
		// panel.setEmbeddedDef(dpInfo[i].getEmbeddedDef());
		// panel.setEmbeddedSize(dpInfo[i].getEmbeddedSize());
		// panel.setShowStyleBar(dpInfo[i].showStyleBar());
		// panel.setOpenInFrame(dpInfo[i].isOpenInFrame());
		panel.setVisible(true);
		panel.toggleStyleBar();

		((LayoutD) getGuiManager().getLayout()).getDockManager().show(panel);

		return euclidianViewForPlane;
	}

	// ///////////////////////////////
	// GUI
	// ///////////////////////////////

	@Override
	public void refreshViews() {
		getEuclidianView3D().reset();
		super.refreshViews();
	}

	public void toggleAxis3D() {
		// toggle axis
		getEuclidianView3D().toggleAxis();
	}

	public void togglePlane() {
		// toggle xOy plane
		getEuclidianView3D().togglePlane();
	}

	public void toggleGrid3D() {
		// toggle xOy grid
		getEuclidianView3D().toggleGrid();
	}

	public void setShowAxesSelected3D(JCheckBoxMenuItem cb) {
		cb.setSelected(getEuclidianView3D().axesAreAllVisible());
	}

	/**
	 * set the show plane combo box selected if the plane is visible
	 * 
	 * @param cb
	 */
	public void setShowPlaneSelected(JCheckBoxMenuItem cb) {
		GeoPlane3D p = getEuclidianView3D().getxOyPlane();
		cb.setSelected(p.isPlateVisible());
	}

	/**
	 * set the show grid combo box selected if the plane is visible
	 * 
	 * @param cb
	 */
	public void setShowGridSelected3D(JCheckBoxMenuItem cb) {
		GeoPlane3D p = getEuclidianView3D().getxOyPlane();
		cb.setSelected(p.isGridVisible());
	}

	@Override
	protected GuiManagerD newGuiManager() {
		return new GuiManager3D(this);
	}

	@Override
	public void resetFonts() {

		super.resetFonts();

		if (euclidianViewForPlane != null) {
			euclidianViewForPlane.updateFonts();
		}
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
	public String getCommandSyntax(String key) {
		String command = getCommand(key);
		String key3D = key + syntax3D;
		String syntax = getCommand(key3D);
		if (!syntax.equals(key3D)) {
			syntax = syntax.replace("[", command + '[');
			return syntax;
		}

		return super.getCommandSyntax(key);
	}

	@Override
	public void addToEuclidianView(GeoElement geo) {
		super.addToEuclidianView(geo);
		geo.addView(AppD.VIEW_EUCLIDIAN3D);
		getEuclidianView3D().add(geo);
	}

	@Override
	public void removeFromEuclidianView(GeoElement geo) {
		super.removeFromEuclidianView(geo);
		geo.removeView(AppD.VIEW_EUCLIDIAN3D);
		getEuclidianView3D().remove(geo);
	}

	@Override
	public void updateStyleBars() {
		super.updateStyleBars();
		getEuclidianView3D().getStyleBar().updateStyleBar();
	}

	// ///////////////////////////////
	// FOR TESTING : TODO remove all

	@Override
	protected GlobalKeyDispatcherD newGlobalKeyDispatcher() {
		return new GlobalKeyDispatcher3D(this);
	}

	/*
	 * private static final int WIREFRAME_OFF =0; private static final int
	 * WIREFRAME_ON =1; private int wireframe = WIREFRAME_OFF ;
	 * 
	 * public void toggleWireframe(){ switch (wireframe){ case WIREFRAME_OFF:
	 * wireframe = WIREFRAME_ON; Application.debug("wireframe on"); break; case
	 * WIREFRAME_ON: wireframe = WIREFRAME_OFF;
	 * Application.debug("wireframe off"); break;
	 * 
	 * }
	 * 
	 * }
	 * 
	 * public boolean drawWireFrame(){ return (wireframe == WIREFRAME_ON); }
	 */

	@Override
	public boolean is3D() {
		return true;
	}
	
	
	
	
	private int oldCursorMode;

	@Override
	protected void handleShiftEvent(boolean isShiftDown){
		if (isShiftDown){
			oldCursorMode=getEuclidianView3D().getCursor();
			getEuclidianView3D().setMoveCursor();
		}else{
			getEuclidianView3D().setCursor(oldCursorMode);

		}
	}
	
	@Override
	public String getVersionString() {
		return super.getVersionString() + "-" + RendererJogl.JOGL_VERSION;
	}
	
}
