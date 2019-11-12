package org.geogebra.common.kernel.geos;

import org.geogebra.common.util.TextObject;

public class ConstantTextObject implements TextObject {

	private String text;

	public ConstantTextObject(String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public void setVisible(boolean b) {
		// stub
	}

	@Override
	public void setEditable(boolean b) {
		// stub
	}

}
