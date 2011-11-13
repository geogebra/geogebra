package geogebra.gui.inputfield;

import geogebra.gui.GuiManager;
import geogebra.gui.VirtualKeyboardListener;
import geogebra.gui.virtualkeyboard.VirtualKeyboard;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DateFormat;

import javax.swing.JFormattedTextField;

public class MyFormattedTextField extends JFormattedTextField implements FocusListener, VirtualKeyboardListener {
	
	GuiManager guiManager;
	
	public MyFormattedTextField(GuiManager guiManager) {
		super();
		this.guiManager = guiManager;
		addFocusListener(this);

		
	}

	public MyFormattedTextField(GuiManager guiManager, DateFormat date) {
		super(date);
		this.guiManager = guiManager;
		addFocusListener(this);
	}

	public void focusGained(FocusEvent e) {
		guiManager.setCurrentTextfield((VirtualKeyboardListener)this, false);
	}

	public void focusLost(FocusEvent e) {
		guiManager.setCurrentTextfield(null, !(e.getOppositeComponent() instanceof VirtualKeyboard));
		
	}
	
	public void insertString(String text) {

		int start = getSelectionStart();
		int end = getSelectionEnd();        
		//    clear selection if there is one
		if (start != end) {
			int pos = getCaretPosition();
			String oldText = getText();
			StringBuilder sb = new StringBuilder();
			sb.append(oldText.substring(0, start));
			sb.append(oldText.substring(end));            
			setText(sb.toString());
			if (pos < sb.length()) setCaretPosition(pos);
		}

		
		int pos = getCaretPosition();
		String oldText = getText();
		StringBuilder sb = new StringBuilder();
		sb.append(oldText.substring(0, pos));
		sb.append(text);
		sb.append(oldText.substring(pos));            
		setText(sb.toString());

		setCaretPosition(pos + text.length());
		
		
	}

}
