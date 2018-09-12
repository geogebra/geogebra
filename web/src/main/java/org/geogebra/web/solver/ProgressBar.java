package org.geogebra.web.solver;

import com.google.gwt.user.client.ui.HTML;

public class ProgressBar extends HTML {

	public ProgressBar() {
		super("<progress></progress>");
		addStyleName("practiceProgressBar");
	}

	public void setMax(int max) {
		getElement().getFirstChildElement().setAttribute("max", String.valueOf(max));
	}

	public void setValue(int value) {
		getElement().getFirstChildElement().setAttribute("value", String.valueOf(value));
	}

	public void setProgress(String progress) {
		getElement().getFirstChildElement().setAttribute("progress", progress);
	}
}
