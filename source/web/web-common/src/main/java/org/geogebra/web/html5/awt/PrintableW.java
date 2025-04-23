package org.geogebra.web.html5.awt;

import org.gwtproject.user.client.ui.FlowPanel;

/**
 * A component that can contribute to the printer output.
 */
public interface PrintableW {
	void getPrintable(FlowPanel pPanel, Runnable enableBtn);
}
