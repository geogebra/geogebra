package org.geogebra.web.html5.awt;

import org.gwtproject.user.client.ui.FlowPanel;

/**
 * A component that can contribute to the printer output.
 */
public interface PrintableW {

	/**
	 * Add printable representation of this view to the panel.
	 * @param pPanel print panel
	 * @param enableBtn success callback
	 */
	void getPrintable(FlowPanel pPanel, Runnable enableBtn);
}
