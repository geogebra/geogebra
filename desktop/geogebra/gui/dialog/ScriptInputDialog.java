/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElement.ScriptType;
import geogebra.common.main.App;
import geogebra.gui.editor.GeoGebraEditorPane;
import geogebra.gui.view.algebra.InputPanelD.DialogType;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Input dialog for GeoText objects with additional option
 * to set a "LaTeX formula" flag
 * 
 * @author hohenwarter
 */
public class ScriptInputDialog extends InputDialog implements DocumentListener{
	
	private static final long serialVersionUID = 1L;

	private GeoElement geo;
	private boolean global = false;
	//private boolean javaScript = false;
	private ScriptType scriptType = ScriptType.GGBSCRIPT;
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ScriptInputDialog(AppD app,  String title, GeoButton button,
								int cols, int rows, boolean updateScript, boolean forceJavaScript) {	
		super(app.getFrame(), false);
		this.app = app;
		
		this.updateScript = updateScript;
		inputHandler = new TextInputHandler();
				
				
		createGUI(title, "", false, cols, rows, true, false, false, false, DialogType.GeoGebraEditor);		
		
		// init dialog using text
		
		JPanel centerPanel = new JPanel(new BorderLayout());		
		
		languageSelector = new JComboBox();
		languageSelector.addItem(app.getPlain("Script"));
		languageSelector.addItem(app.getPlain("JavaScript"));
		
		// don't show in 4.2 Webstart builds
		if (!AppD.isWebstart() || app.is3D()) {
			languageSelector.addItem(app.getPlain("Python"));
		}
		languageSelector.addActionListener(this);
		
		setGeo(button);

		if(forceJavaScript){
			languageSelector.setSelectedIndex(1);
			languageSelector.setEnabled(false);
			setScriptType(ScriptType.JAVASCRIPT);
		}
		btPanel.add(languageSelector,0);
			
		centerPanel.add(inputPanel, BorderLayout.CENTER);		

		getContentPane().add(centerPanel, BorderLayout.CENTER);
		
		
		
		centerOnScreen();	
		
		inputPanel.getTextComponent().getDocument().addDocumentListener(this);
	}
	
	public void setGeo(GeoElement geo) {
		
		//AbstractApplication.printStacktrace("");
		
		handlingDocumentEventOff = true;
		
		if (global) {
			setGlobal();
			handlingDocumentEventOff = false;
			return;
		}
		this.geo = geo;
		
		if (geo != null){
			App.debug(updateScript ? geo.getUpdateScript() : geo.getClickScript());
			inputPanel.setText(updateScript ? geo.getUpdateScript() : geo.getClickScript());
			//setJSMode(updateScript ? geo.updateJavaScript():geo.clickJavaScript());
			setScriptType(updateScript ? geo.getUpdateScriptType() : geo.getClickScriptType());
		}
		
		handlingDocumentEventOff = false;
	}
	
	/**
	 * edit global javascript
	 */
	public void setGlobal() {
		
		boolean currentHandlingDocumentEventOff = handlingDocumentEventOff;
		handlingDocumentEventOff = true;
		
		geo = null;
		global = true;

        inputPanel.setText(app.getKernel().getLibraryJavaScript());
        
        handlingDocumentEventOff=currentHandlingDocumentEventOff;
	}
	
	
	/**
	 * Returns the inputPanel and sets its preferred size from the given row
	 * and column value. Includes option to hide/show line numbering.
	 * 
	 * @param row
	 * @param column
	 * @param showLineNumbers 
	 * @return
	 */
	public JPanel getInputPanel(int row, int column, boolean showLineNumbers) {

		Dimension dim = ((GeoGebraEditorPane) inputPanel.getTextComponent())
				.getPreferredSizeFromRowColumn(row, column);
		inputPanel.setPreferredSize(dim);
		inputPanel.setShowLineNumbering(showLineNumbers);
		// add a small margin
		inputPanel.getTextComponent().setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		
		return inputPanel;
		
	}
	
	
	
	public JPanel getInputPanel() {	
		return inputPanel;
	}
	
	public JButton getApplyButton() {
		return btApply;
	}
	
	private boolean processInput(){
		inputText = inputPanel.getText();		
		return inputHandler.processInput(inputText);	
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		try {
			if (source == btOK || source == inputPanel.getTextComponent()) {
				
				boolean finished = processInput();	
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
				//setJSMode(languageSelector.getSelectedIndex()==1);	
				setScriptType(ScriptType.values()[languageSelector.getSelectedIndex()]);
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue		
			ex.printStackTrace();
		}			
	}
	
	//private void setJSMode(boolean flag){
	//	javaScript = flag;
	//	((GeoGebraEditorPane) inputPanel.getTextComponent()).setEditorKit(flag ? "javascript":"geogebra");
	//}
	
	private void setScriptType(ScriptType scriptType) {
		this.scriptType = scriptType;
		String scriptStr;
		int index;
		switch (scriptType) {
		default:
		case GGBSCRIPT:
			scriptStr = "geogebra";
			index = 0;
			break;
			
		case PYTHON:
			AppD.debug("TODO");
			scriptStr = "javascript";//python";
			index = 2;
			break;
			
		case JAVASCRIPT:
			scriptStr = "javascript";
			index = 1;
			break;
			
		}
		
		GeoGebraEditorPane editor = (GeoGebraEditorPane) inputPanel.getTextComponent();
		editor.getDocument().removeDocumentListener(this);
		languageSelector.setSelectedIndex(index);
		editor.setEditorKit(scriptStr);
		editor.getDocument().addDocumentListener(this);
	}
	
	/**
	 * Inserts geo into text and creates the string for a dynamic text, e.g.
	 * "Length of a = " + a + "cm"
	 * @param geo
	 */
	@Override
	public void insertGeoElement(GeoElement geo) {
		App.debug("TODO: unimplemented");
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
            		//getGeo().setUpdateJavaScript(javaScript);
            		getGeo().setUpdateScriptType(scriptType);
            		//let's suppose fixing this script removed the reason why scripts were blocked
                    app.setBlockUpdateScripts(false);	
            	}
            	else{
            		getGeo().setClickScript(inputValue, true);
            		//getGeo().setClickJavaScript(javaScript);
            		getGeo().setClickScriptType(scriptType);
            	}            
            	return true;
        }
	}
	
	/**
	 * apply edit modifications
	 */
	public void applyModifications(){
		if (editOccurred){
			editOccurred = false; 
			processInput();
		}
	}

	public void changedUpdate(DocumentEvent e) {
		// nothing to do
		
	}

	public void insertUpdate(DocumentEvent e) {
		handleDocumentEvent();
		
	}

	public void removeUpdate(DocumentEvent e) {
		handleDocumentEvent();
		
	}
	

	/**
	 * used for update to avoid several updates
	 */
	private boolean handlingDocumentEventOff = false;
	
	/**
	 * false on init, become true when an edit occurs
	 */
	private boolean editOccurred = false;  
	
	private void handleDocumentEvent() {
		
		if (handlingDocumentEventOff)
			return;

		editOccurred = true;
		
	}
	
}
