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
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.GuiManager;
import geogebra.kernel.GeoElement;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.main.AppletImplementation;
import geogebra.main.Application;
import geogebra.main.GlobalKeyDispatcher;
import geogebra3D.euclidian3D.EuclidianController3D;
import geogebra3D.euclidian3D.EuclidianView3D;
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


public class Application3D extends Application{
	
    private EuclidianView3D euclidianView3D;
    private EuclidianController3D euclidianController3D;      
    protected Kernel3D kernel3D;
    
    private EuclidianViewForPlane euclidianViewForPlane;


    public Application3D(CommandLineArguments args, JFrame frame, boolean undoActive) {
        this(args, frame, null, undoActive);
    }

    public Application3D(CommandLineArguments args, AppletImplementation applet, boolean undoActive) {
    	this(args, null, applet, undoActive);
    }
    
    
    private Application3D(CommandLineArguments args, JFrame frame, AppletImplementation applet, boolean undoActive) { 
    	
    	super(args, frame, applet, null, undoActive);
    	
    	//euclidianView3D.initAxisAndPlane();
    	
        
	    //TODO remove 3D test : just comment following line        
        //new Test3D(kernel3D,euclidianView,euclidianView3D,this);
        
        
    }
    
    
    
    
	public void initKernel(){
		kernel3D = new Kernel3D(this);
		kernel = kernel3D;
	}
	
	protected void initImageManager(Component component){
		imageManager = new ImageManager3D(component);
	}
	
	/**
	 * init the EuclidianView (and EuclidianView3D for 3D)
	 */
	public void initEuclidianViews(){
		
		//init the 2D euclidian view
		super.initEuclidianViews();
		
		//init the 3D euclidian view
		euclidianController3D = new EuclidianController3D(kernel3D);
        euclidianView3D = new EuclidianView3D(euclidianController3D); 
        
	}
	
	public void setMode(int mode) {
		super.setMode(mode);
		
		//if (euclidianView3D != null)
			euclidianView3D.setMode(mode);
		
	}

	public String getCompleteUserInterfaceXML(boolean asPreference) {
		StringBuilder sb = new StringBuilder();

		// save super settings
		sb.append(super.getCompleteUserInterfaceXML(asPreference));

		// save euclidianView3D settings
		sb.append(euclidianView3D.getXML());


		return sb.toString();
	}
	
	/** return the 3D euclidian view
	 * @return the 3D euclidian view
	 */
	public EuclidianView3D getEuclidianView3D(){
		return euclidianView3D;
	}
	
	public void getEuclidianViewXML(StringBuilder sb,boolean asPreference){
		super.getEuclidianViewXML(sb, asPreference);
		sb.append(getEuclidianView3D().getXML());
	}
	
	
	public BufferedImage getExportImage(double maxX, double maxY) throws OutOfMemoryError {
		//TODO use maxX, maxY values
		return getEuclidianView3D().getRenderer().getExportImage();
	}
	
	public boolean saveGeoGebraFile(File file) {		
		//TODO generate it before
		getEuclidianView3D().getRenderer().needExportImage();
		
		return super.saveGeoGebraFile(file);
	}
	
	/////////////////////////////////
	// EUCLIDIAN VIEW FOR PLANE
	/////////////////////////////////
	
	
	
	/**
	 * @param plane
	 * @return create a new euclidian view for the plane
	 */
	public EuclidianView createEuclidianViewForPlane(GeoCoordSys2D plane){
		// create new view for plane and controller
		EuclidianController ec = new EuclidianControllerForPlane(kernel3D);
		euclidianViewForPlane = new EuclidianViewForPlane(ec, plane);
		euclidianViewForPlane.updateFonts();
		euclidianViewForPlane.addExistingGeos();
		
		// create dock panel
		EuclidianDockPanelForPlane panel = new EuclidianDockPanelForPlane(this,euclidianViewForPlane);
		getGuiManager().getLayout().registerPanel(panel);
		
		//panel.setToolbarString(dpInfo[i].getToolbarString());
		panel.setFrameBounds(new Rectangle(600, 400));
		//panel.setEmbeddedDef(dpInfo[i].getEmbeddedDef());
		//panel.setEmbeddedSize(dpInfo[i].getEmbeddedSize());
		//panel.setShowStyleBar(dpInfo[i].showStyleBar());
		//panel.setOpenInFrame(dpInfo[i].isOpenInFrame());
		panel.setVisible(true);
		panel.toggleStyleBar();
		
		
		getGuiManager().getLayout().getDockManager().show(panel);
		
		return euclidianViewForPlane;
	
	}
	
	/////////////////////////////////
	// GUI
	/////////////////////////////////
	

	public void refreshViews() {
		getEuclidianView3D().reset();
		super.refreshViews();
	}
	
	public void toggleAxis3D(){
		// toggle axis
		getEuclidianView3D().toggleAxis();
	}
	
	public void togglePlane(){
		// toggle xOy plane
		getEuclidianView3D().togglePlane();
	}
	
	public void toggleGrid3D(){
		// toggle xOy grid
		getEuclidianView3D().toggleGrid();
	}
	
	
	public void setShowAxesSelected(JCheckBoxMenuItem cb){
		cb.setSelected(getEuclidianView3D().axesAreAllVisible());
	}
	
	/** set the show plane combo box selected if the plane is visible
	 * @param cb
	 */
	public void setShowPlaneSelected(JCheckBoxMenuItem cb){
		GeoPlane3D p = getEuclidianView3D().getxOyPlane();
		cb.setSelected(p.isPlateVisible());
	}
	
	/** set the show grid combo box selected if the plane is visible
	 * @param cb
	 */
	public void setShowGridSelected(JCheckBoxMenuItem cb){
		GeoPlane3D p = getEuclidianView3D().getxOyPlane();
		cb.setSelected(p.isGridVisible());
	}
	
	
	protected GuiManager newGuiManager(){
		return new GuiManager3D(this);
	}
	
	public void updateFonts() {

		super.updateFonts();
		
		if (euclidianViewForPlane!=null)
			euclidianViewForPlane.updateFonts();
	}
    
	///////////////////////////////////////
	// COMMANDS
	///////////////////////////////////////
	
	public String getCommandSyntax(String key) {
		String key3D = key + syntax3D;
		String command3D = getCommand(key3D);
		if (!command3D.equals(key3D)) return command3D;
		
		return super.getCommandSyntax(key);
	}
	
	
	
	
	
	public void addToEuclidianView(GeoElement geo){
		super.addToEuclidianView(geo);
		geo.addView(Application.VIEW_EUCLIDIAN3D);
		getEuclidianView3D().add(geo);
	}

	public void removeFromEuclidianView(GeoElement geo){
		super.removeFromEuclidianView(geo);
		geo.removeView(Application.VIEW_EUCLIDIAN3D);
		getEuclidianView3D().remove(geo);
	}	
	
	
	
	
	public void updateStyleBars(){		
		super.updateStyleBars();
		getEuclidianView3D().getStyleBar().updateStyleBar();
	}
	
	
	/////////////////////////////////
	// FOR TESTING : TODO remove all

	protected GlobalKeyDispatcher newGlobalKeyDispatcher(){
		return new GlobalKeyDispatcher3D(this);
	}
	
	/*
	private static final int WIREFRAME_OFF =0;
	private static final int WIREFRAME_ON =1;
	private int wireframe = WIREFRAME_OFF ;
	
	public void toggleWireframe(){
		switch (wireframe){
		case WIREFRAME_OFF:
			wireframe = WIREFRAME_ON;
			Application.debug("wireframe on");
			break;
		case WIREFRAME_ON:
			wireframe = WIREFRAME_OFF;
			Application.debug("wireframe off");
			break;
			
		}
		
	}
	
	public boolean drawWireFrame(){
		return (wireframe == WIREFRAME_ON);
	}
	
	*/
	
	
	public boolean is3D(){
		return true;
	}
}
