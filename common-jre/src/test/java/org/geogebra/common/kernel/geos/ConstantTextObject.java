package org.geogebra.common.kernel.geos;

import org.geogebra.common.util.TextObject;

public class ConstantTextObject implements TextObject {

	private String text;

	public ConstantTextObject(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setColumns(int fieldWidth) {
		// TODO Auto-generated method stub

	}

	public void setVisible(boolean b) {
		// TODO Auto-generated method stub

	}

	public void setEditable(boolean b) {
		// TODO Auto-generated method stub

	}

}
