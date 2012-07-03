/**
 * This panel is for the input.
 */

package geogebra.cas.view;

import geogebra.gui.inputfield.AutoCompleteTextFieldD;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

public class CASInputPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private AutoCompleteTextFieldD inputArea;
	
	private AppD app;
	
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
	
	public void setCommentColor(Color col){
		if(col!=null){
			inputArea.setForeground(col);
		}
	}

	public void setLabels() {
		inputArea.setDictionary(app.getCommandDictionaryCAS());
	}
	
}
