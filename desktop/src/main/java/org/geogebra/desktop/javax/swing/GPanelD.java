package org.geogebra.desktop.javax.swing;

import javax.swing.JPanel;

import org.geogebra.common.javax.swing.GPanel;

/**
 * Wrapper for javax.swing.JPanel
 * 
 * @author judit
 */
public class GPanelD extends GPanel {
	private JPanel impl = null;

	public GPanelD() {
		impl = new JPanel();
	}

	public JPanel getImpl() {
		return impl;
	}

	@Override
	public void setVisible(boolean visible) {
		impl.setVisible(visible);
	}
}
