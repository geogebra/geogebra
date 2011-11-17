/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */



package geogebra.gui.view.properties;

import geogebra.gui.PropertiesDialog.JTreeGeoElements;
import geogebra.gui.PropertiesPanel;
import geogebra.kernel.Kernel;
import geogebra.kernel.View;
import geogebra.kernel.geos.GeoElement;
import geogebra.main.Application;
import geogebra.main.GeoElementSelectionListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

/**
 * View for properties
 * 
 * @author mathieu
 * @version
 */
public class PropertiesView extends JPanel implements View, GeoElementSelectionListener {
	
	private JTreeGeoElements geoTree;
	private PropertiesPanel propPanel;
	private Kernel kernel;
	private Application app;
	private boolean attached;
	
	public PropertiesView(Application app, JTreeGeoElements geoTree, PropertiesPanel propPanel){

		super(new BorderLayout());
		
		this.app = app;
		app.setPropertiesView(this);
		this.kernel=app.getKernel();
		this.geoTree=geoTree;
		this.propPanel=propPanel;
		
		
		initGUI();
	}
	
	public void initGUI(){
		
		geoTree.setFont(app.getPlainFont());		
	}
	
	private void setGeoTreeVisible(){
		
		removeAll();
		
		//		LIST PANEL		
		JScrollPane listScroller = new JScrollPane(geoTree);			
		listScroller.setMinimumSize(new Dimension(120, 200));
		listScroller.setBackground(geoTree.getBackground());
		listScroller.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setLeftComponent(listScroller);
		splitPane.setRightComponent(propPanel);
		
		add(splitPane);
	}
	
	private void setGeoTreeNotVisible(){
		
		removeAll();

		add(propPanel);
	}	
	

	////////////////////////////////////////////////////////
	// VIEW INTERFACE
	////////////////////////////////////////////////////////

	public void attachView() {
		clearView();
		kernel.notifyAddAll(this);
		kernel.attach(this);
		attached = true;
	}

	public void detachView() {
		kernel.detach(this);
		clearView();
		attached = false;
	}
	
	
	

	public void add(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}

	public void remove(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}

	public void rename(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}

	public void update(GeoElement geo) {
		
		//updateSelection();
		//propPanel.updateSelection(new GeoElement[] {geo});
		
	}

	public void updateVisualStyle(GeoElement geo) {
		update(geo);
		
	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}

	public void repaintView() {
		//propPanel.updateSelection(app.getSelectedGeos().toArray());
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public void clearView() {
		// TODO Auto-generated method stub
		
	}

	public void setMode(int mode) {
		// TODO Auto-generated method stub
		
	}

	public int getViewID() {
		return Application.VIEW_PROPERTIES;
	}


	////////////////////////////////////////////////////////
	// SELECTION 
	////////////////////////////////////////////////////////

	public void updateSelection() {
		propPanel.updateSelection(app.getSelectedGeos().toArray());		
	}
	
	private ArrayList<GeoElement> tempArrayList = new ArrayList<GeoElement>();
	
	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		if (geo == null) return;
		tempArrayList.clear();
		tempArrayList.add(geo);
		geoTree.setSelected(tempArrayList, addToSelection);
		//requestFocus();
	}
	
	
	////////////////////////////////////////////////////////
	// FOR DOCK/UNDOCK PANEL 
	////////////////////////////////////////////////////////

	
	public void windowPanel() {
		setGeoTreeVisible();
		
		kernel.attach(geoTree);
		kernel.notifyAddAll(geoTree);	
		app.setSelectionListenerMode(this);
	}
	
	public void unwindowPanel() {
		setGeoTreeNotVisible();
		
		kernel.detach(geoTree);					
		geoTree.clear();				
		app.setSelectionListenerMode(null);
	}
	
} 
