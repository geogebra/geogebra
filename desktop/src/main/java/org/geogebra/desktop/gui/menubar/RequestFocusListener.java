package org.geogebra.desktop.gui.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class RequestFocusListener implements ActionListener {

	private JFrame frame;

	public RequestFocusListener(JFrame frame) {
		this.frame = frame;
	}

	public void actionPerformed(ActionEvent arg0) {
		frame.toFront();
		frame.requestFocus();
	}
}
