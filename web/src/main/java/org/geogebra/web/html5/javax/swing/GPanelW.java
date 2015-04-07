package org.geogebra.web.html5.javax.swing;

import org.geogebra.common.javax.swing.GPanel;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Wrapper for com.google.gwt.user.client.ui.FlowPanel
 * 
 * @author judit
 *
 */
public class GPanelW extends GPanel {
	private FlowPanel impl = null;

	public GPanelW() {
		impl = new FlowPanel();
	}

	public FlowPanel getImpl() {
		return impl;
	}

	@Override
	public void setVisible(boolean visible) {
		impl.setVisible(visible);

	}

}
