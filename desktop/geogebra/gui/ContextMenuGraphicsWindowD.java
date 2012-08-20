/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * ZoomMenu.java
 *
 * Created on 24. J�nner 2002, 14:11
 */

package geogebra.gui;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.main.AppD;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author  markus
 * @version 
 */
public class ContextMenuGraphicsWindowD extends ContextMenuGeoElementD
implements ActionListener {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double px, py;
    //private JMenuItem miStandardView, miProperties; 
    
    private ImageIcon iconZoom;

    public ContextMenuGraphicsWindowD(AppD app){
    	super(app);	
    }
    
    /** Creates new ZoomMenu 
     * @param app 
     * @param px 
     * @param py */
    public ContextMenuGraphicsWindowD(AppD app, double px, double py) {  
        this(app);      
        
        iconZoom      = app.getImageIcon("zoom16.gif");
        
        // zoom point
        this.px = px;
        this.py = py;
        
        EuclidianViewInterfaceCommon ev= app.getActiveEuclidianView();
        if(ev.getEuclidianViewNo()==2){
        	 setTitle("<html>" + app.getPlain("DrawingPad2") + "</html>");
        }
        else{
        	setTitle("<html>" + app.getPlain("DrawingPad") + "</html>");
        }
        
        addAxesAndGridCheckBoxes();
        
        wrappedPopup.addSeparator();
        
        // zoom for both axes
        JMenu zoomMenu = new JMenu(app.getMenu("Zoom"));
        zoomMenu.setIcon(iconZoom);
        zoomMenu.setBackground(wrappedPopup.getBackground());           
        addZoomItems(zoomMenu);
        wrappedPopup.add(zoomMenu);
                
        // zoom for y-axis
        JMenu yaxisMenu = new JMenu(app.getPlain("xAxis") + " : " 
        							+ app.getPlain("yAxis"));
        yaxisMenu.setIcon(app.getEmptyIcon());
        yaxisMenu.setBackground(wrappedPopup.getBackground());   
        addAxesRatioItems(yaxisMenu);
        wrappedPopup.add(yaxisMenu);                        
       
        JMenuItem miShowAllObjectsView = new JMenuItem(app.getPlain("ShowAllObjects"));
        miShowAllObjectsView.setIcon(app.getEmptyIcon());
        miShowAllObjectsView.setActionCommand("showAllObjects");
        miShowAllObjectsView.addActionListener(this);
        miShowAllObjectsView.setBackground(bgColor);
        wrappedPopup.add(miShowAllObjectsView);                

        JMenuItem miStandardView = new JMenuItem(app.getPlain("StandardView"));
        setMenuShortCutAccelerator(miStandardView, 'M');
        miStandardView.setIcon(app.getEmptyIcon());
        miStandardView.setActionCommand("standardView");
        miStandardView.addActionListener(this);
        miStandardView.setBackground(bgColor);
        wrappedPopup.add(miStandardView);
        
        wrappedPopup.addSeparator();          
        if(!ev.isZoomable()){
        	zoomMenu.setEnabled(false);
        	yaxisMenu.setEnabled(false);
        	miShowAllObjectsView.setEnabled(false);
        	miStandardView.setEnabled(false);
        }
        
        if(ev.isLockedAxesRatio()){
        	yaxisMenu.setEnabled(false);
        }
   
        addMiProperties();
         
    }
    
    protected void addMiProperties(){
        JMenuItem miProperties = new JMenuItem(app.getPlain("DrawingPad") + " ...");
        miProperties.setIcon(((AppD) app).getImageIcon("view-properties16.png"));
        miProperties.setActionCommand("properties");
        miProperties.addActionListener(this);
        miProperties.setBackground(bgColor);
        wrappedPopup.add(miProperties); 
    }   
    
    protected void addAxesAndGridCheckBoxes(){

        // checkboxes for axes and grid        
        JCheckBoxMenuItem cbShowAxes = new JCheckBoxMenuItem(((GuiManagerD)app.getGuiManager()).getShowAxesAction());
        //cbShowAxes.setSelected(ev.getShowXaxis() && ev.getShowYaxis());
        ((AppD) app).setShowAxesSelected(cbShowAxes);
        cbShowAxes.setBackground(wrappedPopup.getBackground());
        wrappedPopup.add(cbShowAxes);
        
        JCheckBoxMenuItem cbShowGrid = new JCheckBoxMenuItem(((GuiManagerD) app.getGuiManager()).getShowGridAction());
        //cbShowGrid.setSelected(ev.getShowGrid());
        ((AppD) app).setShowGridSelected(cbShowGrid);
        cbShowGrid.setBackground(wrappedPopup.getBackground());
        wrappedPopup.add(cbShowGrid);
    }
        
    public void actionPerformed(ActionEvent e) {                                            
    	String cmd = e.getActionCommand();
    	
    	if (cmd.equals("standardView")) {
            app.setStandardView();        
        }
    	else if (cmd.equals("showAllObjects")) {
    		app.setViewShowAllObjects();
    	}
    	else if (cmd.equals("properties")) {
    		showOptionsDialog();
    	}
    }
    
    protected void showOptionsDialog(){
    	app.getGuiManager().setShowView(true, App.VIEW_PROPERTIES);
    	((GuiManagerD)app.getGuiManager()).setFocusedPanel(app.getActiveEuclidianView().getViewID(), true);
    	//app.getDialogManager().showOptionsDialog(OptionsDialog.TAB_EUCLIDIAN);
		//app.getGuiManager().showDrawingPadPropertiesDialog();
    }
    
    private void addZoomItems(JMenu menu) {	  
      int perc;            
      
      ActionListener al = new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            try {   
	                zoom(Double.parseDouble(e.getActionCommand()));
	            } catch (Exception ex) {
	            }       
	        }  
	    };     
      
      //ImageIcon icon;
      JMenuItem mi;
      boolean separatorAdded = false;
      StringBuilder sb = new StringBuilder();       
      for (int i=0; i < zoomFactors.length; i++) {
          perc = (int) (zoomFactors[i] * 100.0);
          
          // build text like "125%" or "75%"
          sb.setLength(0);
          if (perc > 100) {           
               
          } else {
              if (! separatorAdded) {
                  menu.addSeparator();
                  separatorAdded = true;
              }         
          }                           
          sb.append(perc);
          sb.append('%');             
          
          mi = new JMenuItem(sb.toString());
          mi.setActionCommand("" + zoomFactors[i]);
          mi.addActionListener(al);
          mi.setBackground(wrappedPopup.getBackground());
          menu.add(mi);
      }            	
    }   
    
    private void addAxesRatioItems(JMenu menu) {	                           
    	ActionListener al = new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            try {   
	                zoomYaxis(Double.parseDouble(e.getActionCommand()));
	            } catch (Exception ex) {
	            }       
	        }  
        };  
  	    
        // get current axes ratio
        double scaleRatio = ((EuclidianView)app.getActiveEuclidianView()).getScaleRatio();        
        
        JMenuItem mi;		
        //int perc;   	         
        //ImageIcon icon;        
        boolean separatorAdded = false;
        StringBuilder sb = new StringBuilder();       
        for (int i=0; i < axesRatios.length; i++) {                        
            // build text like "1 : 2" 
            sb.setLength(0);
            if (axesRatios[i] > 1.0) {                                 
                sb.append((int) axesRatios[i]);
                sb.append(" : 1");
                if (! separatorAdded) {
                    menu.addSeparator();
                    separatorAdded = true;
                }
                
            } else { // factor 
            	if (axesRatios[i] == 1) 
                	menu.addSeparator(); 
                sb.append("1 : "); 
                sb.append((int) (1.0 / axesRatios[i]));                               
            }                                    
            
            mi = new JCheckBoxMenuItem(sb.toString());           
            mi.setSelected(Kernel.isEqual(axesRatios[i], scaleRatio));
            mi.setActionCommand("" + axesRatios[i]);
            mi.addActionListener(al);           
            mi.setBackground(wrappedPopup.getBackground());
            menu.add(mi);
        }            	
      } 
    
    private void zoom(double zoomFactor) {
        app.zoom(px, py, zoomFactor);       
    }
    
    // ratio: yaxis / xaxis
    private void zoomYaxis(double axesRatio) {
    	app.zoomAxesRatio(axesRatio);    	
    }      
}
