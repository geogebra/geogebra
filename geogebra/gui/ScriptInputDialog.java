/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.gui;

import geogebra.gui.editor.GeoGebraEditorPane;
import geogebra.kernel.GeoButton;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 * Input dialog for GeoText objects with additional option
 * to set a "LaTeX formula" flag
 * 
 * @author hohenwarter
 */
public class ScriptInputDialog extends InputDialog {
	
	private static final long serialVersionUID = 1L;

	private GeoElement geo;
	private boolean global = false;
	private boolean javaScript = false;
	private boolean updateScript = false;
	private JComboBox languageSelector;
	
	/**
	 * Input Dialog for a GeoButton object
	 * @param app 
	 * @param title 
	 * @param button 
	 * @param cols 
	 * @param rows 
	 * @param updateScript 
	 * @param forceJavaScript 
	 */
	public ScriptInputDialog(Application app,  String title, GeoButton button,
								int cols, int rows, boolean updateScript, boolean forceJavaScript) {	
		super(app.getFrame(), false);
		this.app = app;
		
		this.updateScript = updateScript;
		inputHandler = new TextInputHandler();
				
				
		createGUI(title, "", false, cols, rows, true, true, false, true, false, false, false);		
		
		// init dialog using text
		setGeo(button);
		
		JPanel centerPanel = new JPanel(new BorderLayout());		
		
		languageSelector = new JComboBox();
		languageSelector.addItem(app.getPlain("Script"));
		languageSelector.addItem(app.getPlain("JavaScript"));
		languageSelector.addActionListener(this);
		
		if(forceJavaScript){
			languageSelector.setSelectedIndex(1);
			languageSelector.setEnabled(false);
		}
		setJSMode(forceJavaScript);
		btPanel.add(languageSelector,0);
			
		centerPanel.add(inputPanel, BorderLayout.CENTER);		

		getContentPane().add(centerPanel, BorderLayout.CENTER);
		
		
		
		centerOnScreen();		
	}
	
	
	public void setGeo(GeoElement geo) {
		
		if (global) {
			setGlobal();
			return;
		}
		this.geo = geo;
		
		if (geo != null){
			inputPanel.setText(updateScript ? geo.getUpdateScript() : geo.getClickScript());
			setJSMode(updateScript ? geo.isUpdateJavaScript():geo.isClickJavaScript());
		}
	}
	
	/**
	 * edit global javascript
	 */
	public void setGlobal() {
		geo = null;
		global = true;

        inputPanel.setText(app.getKernel().getLibraryJavaScript());
	}
	
	
	public JPanel getInputPanel() {
		return inputPanel;
	}
	
	public JButton getApplyButton() {
		return btApply;
	}
	
	
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		try {
			if (source == btOK || source == inputPanel.getTextComponent()) {
				inputText = inputPanel.getText();
				
				boolean finished = inputHandler.processInput(inputText);	
				if (isShowing()) {	
					// text dialog window is used and open
					setVisible(!finished);
				} else {		
					// text input field embedded in properties window
					setGeo(getGeo());
				}
			} 
			else if (source == btCancel) {
				if (isShowing())
					setVisible(false);		
				else {
					setGeo(getGeo());
				}
			}
			else if(source == languageSelector){
				setJSMode(languageSelector.getSelectedIndex()==1);				
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue		
			ex.printStackTrace();
		}			
	}
	
	private void setJSMode(boolean flag){
		javaScript = flag;
		((GeoGebraEditorPane) inputPanel.getTextComponent()).setEditorKit(flag ? "javascript":"geogebra");
	}
	
	/**
	 * Inserts geo into text and creates the string for a dynamic text, e.g.
	 * "Length of a = " + a + "cm"
	 * @param geo
	 */
	public void insertGeoElement(GeoElement geo) {
		Application.debug("TODO: unimplemented");
	}
	
	
	/**
	 * @return the geo
	 */
	public GeoElement getGeo() {
		return geo;
	}


	private class TextInputHandler implements InputHandler {
		
		private Kernel kernel;
       
        private TextInputHandler() { 
        	kernel = app.getKernel();
        }        
        
        public boolean processInput(String inputValue) {
            if (inputValue == null) return false;                        
          
        
            if (global) {
            	app.getKernel().setLibraryJavaScript(inputValue);
            	return true;
            }

            if (getGeo() == null) {
            	setGeo(new GeoButton(kernel.getConstruction()));
            
            }
                    
            // change existing text
            	if (updateScript){            		
            		getGeo().setUpdateScript(inputValue, true);
            		getGeo().setUpdateJavaScript(javaScript);
            		//let's suppose fixing this script removed the reason why scripts were blocked
                    app.setBlockUpdateScripts(false);	
            	}
            	else{
            		getGeo().setClickScript(inputValue, true);
            		getGeo().setClickJavaScript(javaScript);
            	}            
            	return true;
    }

}
}
