package org.geogebra.web.html5.gui.util;

import org.geogebra.common.util.StringUtil;
import org.geogebra.web.web.gui.advanced.client.ui.widget.TextButtonPanel;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.UIObject;

public class FormLabel extends FlowPanel {
	public FormLabel(String string) {
		super(LabelElement.TAG);
		getElement().setInnerText(string);
		addStyleName("gwt-Label");
	}

	public void setText(String string) {
		getElement().setInnerText(string);
	}

	public FormLabel setFor(UIObject ui) {
		Element target = ui.getElement();
		if (ui instanceof TextButtonPanel) {
			target = ((TextButtonPanel) ui).getInputElement();
		}

		if (StringUtil.empty(target.getId())) {
			target.setId(DOM.createUniqueId());
		}
		getElement().setAttribute("for", target.getId());
		return this;
	}
}
