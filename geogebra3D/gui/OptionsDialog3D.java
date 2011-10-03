package geogebra3D.gui;

import geogebra.gui.OptionsDialog;
import geogebra.main.Application;
import geogebra3D.Application3D;

import java.awt.Color;


public class OptionsDialog3D extends OptionsDialog {
	
	private OptionsEuclidian3D euclidianPanel3D;

	public OptionsDialog3D(Application app) {
		super(app);
	}
	
	protected void createTabs(){
		super.createTabs();
		euclidianPanel3D = new OptionsEuclidian3D((Application3D) app);
		
	}
	
	protected void addTabs(){

		super.addTabs();
		
		tabbedPane.addTab("", app.getToolBarImage("mode_delete_32.gif",
				Color.RED), euclidianPanel3D);
	}
	

	public void setLabels() {
		super.setLabels();
				
		tabbedPane.setTitleAt(TAB_EUCLIDIAN3D, app.getPlain("GraphicsView3D"));
		
		euclidianPanel3D.setLabels();
	}
}
