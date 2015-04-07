package org.geogebra.desktop.util;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;

/**
 * Action to dispose a JDialog.
 * 
 * @author Philipp Weissenbacher (materthron@users.sourceforge.net)
 */

public class DisposeDialogAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	JDialog dialog = null;

	public DisposeDialogAction(JDialog dialog) {
		this.dialog = dialog;
	}

	public void actionPerformed(ActionEvent e) {
		dialog.dispose();
	}

}
