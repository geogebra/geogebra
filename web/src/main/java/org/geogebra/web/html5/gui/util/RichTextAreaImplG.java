package org.geogebra.web.html5.gui.util;

import com.google.gwt.user.client.ui.impl.RichTextAreaImplMozilla;

public class RichTextAreaImplG extends RichTextAreaImplMozilla {
	@Override
	public void setFocusImpl(boolean focus) {
		// makeEditable(false);
		super.setFocusImpl(focus);
		// makeEditable(true);
	}

}
