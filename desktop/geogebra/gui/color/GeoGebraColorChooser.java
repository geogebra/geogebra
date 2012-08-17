package geogebra.gui.color;

import geogebra.main.AppD;

import java.awt.Color;

import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.DefaultColorSelectionModel;

/**
 * Extends JColorChooser to do the following:
 * 1) Replace the default color chooser panels with an instance of GeoGebraColorChooserPanel
 * 2) Handle null color selection 
 * 3) Localize dialog button strings using GeoGebra properties
 * 
 * @author G Sturr
 *
 */
public class GeoGebraColorChooser extends JColorChooser{
	
	private static final long serialVersionUID = 1L;
	
	private AppD app;
	
	public GeoGebraColorChooser(AppD app){
		this.app = app;
		setSelectionModel(new MyColorSelectionModel());
		
		// remove default chooser panels and replace with our custom panel
		AbstractColorChooserPanel panels[] = { new GeoGebraColorChooserPanel(app) };
		setChooserPanels(panels);
		
		// hide the default preview panel
		setPreviewPanel(new JLabel());
		
		setLabels();
	}
	
	private boolean isNullSelection = false;
	
	/**
	 * Returns true is the current color selection should be treated as a null selection.
	 * (JColorChooser cannot handle a null selected color)
	 * @return
	 */
	public boolean isNullSelection() {
		return isNullSelection;
	}

	
	protected class MyColorSelectionModel extends DefaultColorSelectionModel{
		private static final long serialVersionUID = 1L;
		@Override
		public void setSelectedColor(Color color){
			boolean isNullColor = color == null;
			
			// set the null selection flag 
			if(!isNullColor == isNullSelection){
				isNullSelection = isNullColor;
				// super.setSelectedColor() does not always fire state changed events,
				// e.g. when the new color is the same as the old one
				fireStateChanged();	 		
			}
			super.setSelectedColor(color); 
		}
	}
	
	
	public void setLabels(){
		
		UIManager.put("ColorChooser.okText",app.getPlain("OK"));
		UIManager.put("ColorChooser.cancelText", app.getPlain("Cancel"));
		UIManager.put("ColorChooser.resetText", app.getMenu("Reset"));
	}
	
	public void updateFonts(){
		AbstractColorChooserPanel[] panels = getChooserPanels();
		for(int i=0; i<panels.length; i++){
			AbstractColorChooserPanel panel = panels[i];
			if (panel instanceof GeoGebraColorChooserPanel)
				((GeoGebraColorChooserPanel) panel).updateFonts();
		}
	}

}