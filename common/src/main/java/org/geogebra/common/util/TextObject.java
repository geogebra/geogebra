package org.geogebra.common.util;

public interface TextObject {
	String getText();

	void setText(String s);

	void setColumns(int fieldWidth);

	void setVisible(boolean b);

	void setEditable(boolean b);
}
