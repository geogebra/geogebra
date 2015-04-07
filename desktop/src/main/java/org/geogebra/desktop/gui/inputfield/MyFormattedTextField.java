package org.geogebra.desktop.gui.inputfield;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DateFormat;

import javax.swing.JFormattedTextField;

import org.geogebra.common.gui.VirtualKeyboardListener;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.virtualkeyboard.VirtualKeyboard;

public class MyFormattedTextField extends JFormattedTextField implements
		FocusListener, VirtualKeyboardListener {

	private static final long serialVersionUID = 1L;

	GuiManagerD guiManager;

	public MyFormattedTextField(GuiManagerD guiManager) {
		super();
		this.guiManager = guiManager;
		addFocusListener(this);

	}

	public MyFormattedTextField(GuiManagerD guiManager, DateFormat date) {
		super(date);
		this.guiManager = guiManager;
		addFocusListener(this);
	}

	public void focusGained(FocusEvent e) {
		guiManager.setCurrentTextfield(this, false);
	}

	public void focusLost(FocusEvent e) {
		guiManager.setCurrentTextfield(null,
				!(e.getOppositeComponent() instanceof VirtualKeyboard));

	}

	public void insertString(String text) {

		int start = getSelectionStart();
		int end = getSelectionEnd();
		// clear selection if there is one
		if (start != end) {
			int pos = getCaretPosition();
			String oldText = getText();
			StringBuilder sb = new StringBuilder();
			sb.append(oldText.substring(0, start));
			sb.append(oldText.substring(end));
			setText(sb.toString());
			if (pos < sb.length())
				setCaretPosition(pos);
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
