package geogebra3D.gui;

import geogebra.gui.ContextMenuGraphicsWindowD;
import geogebra.gui.dialog.options.OptionsDialog;
import geogebra.main.AppD;
import geogebra3D.App3D;

import javax.swing.JCheckBoxMenuItem;

/** Extending ContextMenuGraphicsWindow class for 3D
 * @author matthieu
 *
 */
public class ContextMenuGraphicsWindow3D extends ContextMenuGraphicsWindowD {
	private static final long serialVersionUID = 1L;
	/** default constructor
	 * @param app
	 * @param px
	 * @param py
	 */
	public ContextMenuGraphicsWindow3D(AppD app, double px, double py) {
		 super(app); 
		 
		 setTitle("<html>" + app.getPlain("GraphicsView3D") + "</html>");
		 
		 addAxesAndGridCheckBoxes();
		 
		 getWrappedPopup().addSeparator();
		 
		 addMiProperties();
		
	}
	
	@Override
	protected void addAxesAndGridCheckBoxes(){

        // checkboxes for axes and grid
        JCheckBoxMenuItem cbShowAxes = new JCheckBoxMenuItem(((GuiManager3D) app.getGuiManager()).getShowAxes3DAction());
        //cbShowAxes.setSelected(ev.getShowXaxis() && ev.getShowYaxis());
        ((App3D) app).setShowAxesSelected3D(cbShowAxes);
        cbShowAxes.setBackground(getWrappedPopup().getBackground());
        getWrappedPopup().add(cbShowAxes);
        
        JCheckBoxMenuItem cbShowGrid = new JCheckBoxMenuItem(((GuiManager3D) app.getGuiManager()).getShowGrid3DAction());
        //cbShowGrid.setSelected(ev.getShowGrid());
        ((App3D) app).setShowGridSelected3D(cbShowGrid);
        cbShowGrid.setBackground(getWrappedPopup().getBackground());
        getWrappedPopup().add(cbShowGrid);

		JCheckBoxMenuItem cbShowPlane = new JCheckBoxMenuItem(((GuiManager3D) app.getGuiManager()).getShowPlaneAction());
		((App3D) app).setShowPlaneSelected(cbShowPlane);
		cbShowPlane.setBackground(getWrappedPopup().getBackground());
		getWrappedPopup().add(cbShowPlane);
	}
	
	@Override
	protected void showOptionsDialog(){
    	app.getDialogManager().showOptionsDialog(OptionsDialog.TAB_EUCLIDIAN3D);
    }

}
