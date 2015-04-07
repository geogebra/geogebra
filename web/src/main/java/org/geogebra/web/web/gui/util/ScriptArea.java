package org.geogebra.web.web.gui.util;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.TextArea;

// Class for future syntax highlighting, line numbering and so on.
public class ScriptArea extends TextArea {

	public ScriptArea() {
		setStyleName("scriptArea");
	}

	public ScriptArea(Element element) {
		super(element);
		// TODO Auto-generated constructor stub
	}

}
