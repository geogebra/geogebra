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

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

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
public class ContextMenuGraphicsWindow extends ContextMenuGeoElement
implements ActionListener {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double px, py;
    //private JMenuItem miStandardView, miProperties; 
    
    private static double [] zoomFactors = 
		{4.0, 2.0, 1.5, 1.25, 1.0/1.25, 1.0/1.5, 0.5, 0.25};
    
    private static double [] axesRatios = 
        {1.0/1000.0, 1.0/500.0, 1.0/200.0, 1.0/100.0, 1.0/50.0, 1.0/20.0, 1.0/10.0, 1.0/5.0, 1.0/2.0,
    		1, 2, 5, 10, 20, 50, 100, 200, 500, 1000};
     
    private ImageIcon iconZoom;

    public ContextMenuGraphicsWindow(Application app){
    	super(app);	
    }
    
    /** Creates new ZoomMenu 
     * @param app 
     * @param px 
     * @param py */
    public ContextMenuGraphicsWindow(Application app, double px, double py) {  
        this(app);      
        
        iconZoom      = app.getImageIcon("zoom16.gif");
        
        // zoom point
        this.px = px;
        this.py = py;
        
        EuclidianView ev=((EuclidianView)app.getActiveEuclidianView());
        if(ev.getEuclidianViewNo()==2){
        	 setTitle("<html>" + app.getPlain("DrawingPad2") + "</html>");
        }
        else{
        	setTitle("<html>" + app.getPlain("DrawingPad") + "</html>");
        }
        
        
        addAxesAndGridCheckBoxes();
        
        addSeparator();
        
        // zoom for both axes
        JMenu zoomMenu = new JMenu(app.getMenu("Zoom"));
        zoomMenu.setIcon(iconZoom);
        zoomMenu.setBackground(getBackground());           
        addZoomItems(zoomMenu);
        add(zoomMenu);
                
        // zoom for y-axis
        JMenu yaxisMenu = new JMenu(app.getPlain("xAxis") + " : " 
        							+ app.getPlain("yAxis"));
        yaxisMenu.setIcon(app.getEmptyIcon());
        yaxisMenu.setBackground(getBackground());   
        addAxesRatioItems(yaxisMenu);
        add(yaxisMenu);                        
       
        JMenuItem miShowAllObjectsView = new JMenuItem(app.getPlain("ShowAllObjects"));
        miShowAllObjectsView.setIcon(app.getEmptyIcon());
        miShowAllObjectsView.setActionCommand("showAllObjects");
        miShowAllObjectsView.addActionListener(this);
        miShowAllObjectsView.setBackground(bgColor);
        add(miShowAllObjectsView);                

        JMenuItem miStandardView = new JMenuItem(app.getPlain("StandardView"));
        setMenuShortCutAccelerator(miStandardView, 'M');
        miStandardView.setIcon(app.getEmptyIcon());
        miStandardView.setActionCommand("standardView");
        miStandardView.addActionListener(this);
        miStandardView.setBackground(bgColor);
        add(miStandardView);
        
        addSeparator();          
        if(!ev.isZoomable()){
        	zoomMenu.setEnabled(false);
        	yaxisMenu.setEnabled(false);
        	miShowAllObjectsView.setEnabled(false);
        	miStandardView.setEnabled(false);
        }
        
        if(ev.isUnitAxesRatio()){
        	yaxisMenu.setEnabled(false);
        }
   
        addMiProperties();
         
    }
    
    protected void addMiProperties(){
        JMenuItem miProperties = new JMenuItem(app.getPlain("DrawingPad") + " ...");
        miProperties.setIcon(app.getImageIcon("document-properties.png"));
        miProperties.setActionCommand("properties");
        miProperties.addActionListener(this);
        miProperties.setBackground(bgColor);
        add(miProperties); 
    }
    
    
    protected void addAxesAndGridCheckBoxes(){

        // checkboxes for axes and grid        
        JCheckBoxMenuItem cbShowAxes = new JCheckBoxMenuItem(app.getGuiManager().getShowAxesAction());
        //cbShowAxes.setSelected(ev.getShowXaxis() && ev.getShowYaxis());
        app.setShowAxesSelected(cbShowAxes);
        cbShowAxes.setBackground(getBackground());
        add(cbShowAxes);
        
        JCheckBoxMenuItem cbShowGrid = new JCheckBoxMenuItem(app.getGuiManager().getShowGridAction());
        //cbShowGrid.setSelected(ev.getShowGrid());
        app.setShowGridSelected(cbShowGrid);
        cbShowGrid.setBackground(getBackground());
        add(cbShowGrid);
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
    	app.getGuiManager().showOptionsDialog(OptionsDialog.TAB_EUCLIDIAN);
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
          sb.append("%");             
          
          mi = new JMenuItem(sb.toString());
          mi.setActionCommand("" + zoomFactors[i]);
          mi.addActionListener(al);
          mi.setBackground(getBackground());
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
            mi.setBackground(getBackground());
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
