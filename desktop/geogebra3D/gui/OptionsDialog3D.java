package geogebra3D.gui;

import geogebra.gui.dialog.options.OptionsDialog;
import geogebra.main.AppD;
import geogebra3D.Application3D;

import java.awt.Color;


public class OptionsDialog3D extends OptionsDialog {
	private static final long serialVersionUID = 1L;
	private OptionsEuclidian3D euclidianPanel3D;

	public OptionsDialog3D(AppD app) {
		super(app);
	}
	
	@Override
	protected void createTabs(){
		super.createTabs();
		euclidianPanel3D = new OptionsEuclidian3D((Application3D) app);
		
	}
	
	@Override
	protected void addTabs(){

		super.addTabs();
		
		tabbedPane.addTab("", app.getToolBarImage("mode_delete_32.gif",
				Color.RED), euclidianPanel3D);
	}
	
	@Override
	public void setLabels() {
		super.setLabels();
				
		tabbedPane.setTitleAt(TAB_EUCLIDIAN3D, app.getPlain("GraphicsView3D"));
		
		euclidianPanel3D.setLabels();
	}
	
	public static class Factory extends OptionsDialog.Factory {
		public OptionsDialog create(AppD app) {
			return new OptionsDialog3D(app);
		}
	}
}
