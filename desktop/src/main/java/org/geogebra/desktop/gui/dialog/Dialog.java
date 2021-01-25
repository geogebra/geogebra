package org.geogebra.desktop.gui.dialog;

import java.awt.Frame;

import javax.swing.JDialog;

import org.geogebra.desktop.main.AppD;

public class Dialog extends JDialog {

	public Dialog() {
		super();
	}

	public Dialog(Frame owner) {
		super(owner);
	}

	public Dialog(Frame owner, boolean modal) {
		super(owner, modal);
	}

	public Dialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	@Override
	public void setResizable(boolean resizable) {
		// On Mac OS Big Sur resizable dialogs become tabs
		// and freeze the screen. See APPS-2581
		super.setResizable(resizable && !AppD.isMacOsBigSurOrLater());
	}
}
