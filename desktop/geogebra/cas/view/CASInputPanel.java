package geogebra.cas.view;

import geogebra.gui.inputfield.AutoCompleteTextFieldD;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
/**
 * This panel is for the input.
 */
public class CASInputPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private AutoCompleteTextFieldD inputArea;
	
	private AppD app;
	
	/**
	 * @param app application
	 */
	public CASInputPanel(AppD app) {
		this.app = app;
		
		setBackground(Color.white);
		
		setLayout(new BorderLayout(0,0));
		
		// use autocomplete text field from input bar 
		// but ignore Escape, Up, Down keys
		inputArea = new AutoCompleteTextFieldD(1, app, false, app.getCommandDictionaryCAS());
		inputArea.setCASInput(true);
		inputArea.setAutoComplete(true);
		inputArea.showPopupSymbolButton(true);
		inputArea.setBorder(BorderFactory.createEmptyBorder());						
		add(inputArea, BorderLayout.CENTER);
	}

	/**
	 * @param inValue input text
	 */
	public void setInput(String inValue) {
		inputArea.setText(inValue);
	}
	/**
	 * @return input text
	 */
	public String getInput() {
		return inputArea.getText();
	}

	/**
	 * @return input component
	 */
	public AutoCompleteTextFieldD getInputArea() {
		return inputArea;
	}

	/**
	 * 
	 * @return true if the InputArea has been set focused successfully, false otherwise
	 */
	public boolean setInputAreaFocused() {
		return inputArea.requestFocusInWindow();
	}

	@Override
	final public void setFont(Font ft) {
		super.setFont(ft);

		if (inputArea != null) {
			inputArea.setFont(ft);
		}
		
	}
	
	/**
	 * @param col color
	 */
	public void setCommentColor(Color col){
		if(col!=null){
			inputArea.setForeground(col);
		}
	}

	/**
	 * Updates autocomplete dictionary
	 */
	public void setLabels() {
		inputArea.setDictionary(app.getCommandDictionaryCAS());
	}
	
}
