/**
 * This panel is for the input.
 */

package geogebra.cas.view;

import geogebra.gui.inputfield.AutoCompleteTextField;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

public class CASInputPanel extends JPanel {

	private AutoCompleteTextField inputArea;
	
	private Application app;
	
	public CASInputPanel(Application app) {
		this.app = app;
		
		setBackground(Color.white);
		setLayout(new BorderLayout(0,0));
		
		// use autocomplete text field from input bar 
		// but ignore Escape, Up, Down keys
		inputArea = new AutoCompleteTextField(1, app, false, app.getCommandDictionaryCAS());
		inputArea.setCASInput(true);
		inputArea.setAutoComplete(true);
		inputArea.showPopupSymbolButton(true);
		inputArea.setBorder(BorderFactory.createEmptyBorder());						
		add(inputArea, BorderLayout.CENTER);
		
	
//		inputArea.addFocusListener(new FocusListener() {
//
//			public void focusGained(FocusEvent e) {
////				String text = inputArea.getText();
////				int pos = text != null ? text.length() : 0;
////				inputArea.setCaretPosition(pos);
////				inputArea.setSelectionStart(pos);
////				inputArea.setSelectionEnd(pos);
//				
//				System.out.println("inputArea focus GAINED: " + e);
//			}
//
//			public void focusLost(FocusEvent e) {
//				//System.out.println("inputArea focus LOST: " + e);	
//				Application.printStacktrace("inputArea focus LOST: " + e);
//			}
//			
//		});
		
//		KeyboardFocusManager focusManager =
//		    KeyboardFocusManager.getCurrentKeyboardFocusManager();
//		focusManager.addPropertyChangeListener(
//		    new PropertyChangeListener() {
//		        public void propertyChange(PropertyChangeEvent e) {
//		        	  System.out.println(e.getPropertyName() + ": old: " + e.getOldValue() + ", new: "+ e.getNewValue() + ", source: " + e.getSource());
//		        	  
//		        	  if (e.getNewValue() instanceof JButton) {
//		        		  JButton bt = (JButton) e.getNewValue();
//		        		  System.out.println("BUTTON: " + bt.getText() + ", " + bt.getToolTipText());
//		        	  }
//		        	  
////		            String prop = e.getPropertyName();
////		            if (("focusOwner".equals(prop))) {
////		            	e.get
////		                Component comp = (Component)e.getNewValue();
////		                String name = comp.getName();
////		                System.out.println("focus owner: " + name + ", " + comp);
////		            }
//		        }
//		    }
//		);
	}

	public void setInput(String inValue) {
		inputArea.setText(inValue);
	}

	public String getInput() {
		return inputArea.getText();
	}

	public JTextComponent getInputArea() {
		return inputArea;
	}

	public void setInputAreaFocused() {
		boolean success = inputArea.requestFocusInWindow();
	}

	final public void setFont(Font ft) {
		super.setFont(ft);

		if (inputArea != null)
			inputArea.setFont(ft);
	}

	public void setLabels() {
		inputArea.setDictionary(app.getCommandDictionaryCAS());
	}
	
}
