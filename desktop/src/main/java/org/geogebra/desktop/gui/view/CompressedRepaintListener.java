package org.geogebra.desktop.gui.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * @author Lucas Binter
 */
public class CompressedRepaintListener implements ActionListener {

	private CompressedView view;

	/**
	 * @param view
	 *            the compressedView attached this ActionListener is attached to
	 */
	public CompressedRepaintListener(CompressedView view) {
		this.view = view;
	}

	public void actionPerformed(ActionEvent e) {
		view.repaintNow();
	}

}
