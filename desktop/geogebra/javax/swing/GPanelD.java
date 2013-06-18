package geogebra.javax.swing;

import geogebra.common.javax.swing.GPanel;

import javax.swing.JPanel;

/**
 * Wrapper for javax.swing.JPanel
 * @author judit
 */
public class GPanelD extends GPanel{
	private JPanel impl = null;
	
	public GPanelD(){
		impl = new JPanel();
	}

	public JPanel getImpl(){
		return impl;
	}
	
	@Override
	public void setVisible(boolean visible) {
		impl.setVisible(visible);
	}
}
