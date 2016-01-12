package org.geogebra.web.html5.awt;

import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public interface PrintableW {
	List<Widget> getPrintable(FlowPanel pPanel);
}
