package geogebra3D.gui;

import javax.swing.JCheckBoxMenuItem;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.ContextMenuGraphicsWindow;
import geogebra.gui.OptionsDialog;
import geogebra.main.Application;
import geogebra3D.Application3D;

/** Extending ContextMenuGraphicsWindow class for 3D
 * @author matthieu
 *
 */
public class ContextMenuGraphicsWindow3D extends ContextMenuGraphicsWindow {

	/** default constructor
	 * @param app
	 * @param px
	 * @param py
	 */
	public ContextMenuGraphicsWindow3D(Application app, double px, double py) {
		 super(app); 
		 
		 setTitle("<html>" + app.getPlain("GraphicsView3D") + "</html>");
		 
		 addAxesAndGridCheckBoxes();
		 
		 addSeparator();
		 
		 addMiProperties();
		
	}
	
	

	protected void addAxesAndGridCheckBoxes(){

        // checkboxes for axes and grid
        JCheckBoxMenuItem cbShowAxes = new JCheckBoxMenuItem(((GuiManager3D) app.getGuiManager()).getShowAxes3DAction());
        //cbShowAxes.setSelected(ev.getShowXaxis() && ev.getShowYaxis());
        app.setShowAxesSelected(cbShowAxes);
        cbShowAxes.setBackground(getBackground());
        add(cbShowAxes);
        
        JCheckBoxMenuItem cbShowGrid = new JCheckBoxMenuItem(((GuiManager3D) app.getGuiManager()).getShowGrid3DAction());
        //cbShowGrid.setSelected(ev.getShowGrid());
        app.setShowGridSelected(cbShowGrid);
        cbShowGrid.setBackground(getBackground());
        add(cbShowGrid);

		JCheckBoxMenuItem cbShowPlane = new JCheckBoxMenuItem(((GuiManager3D) app.getGuiManager()).getShowPlaneAction());
		((Application3D) app).setShowPlaneSelected(cbShowPlane);
		cbShowPlane.setBackground(getBackground());
		add(cbShowPlane);
	}
	
	protected void showOptionsDialog(){
    	app.getGuiManager().showOptionsDialog(OptionsDialog.TAB_EUCLIDIAN3D);
		//app.getGuiManager().showDrawingPadPropertiesDialog();
    }

}
