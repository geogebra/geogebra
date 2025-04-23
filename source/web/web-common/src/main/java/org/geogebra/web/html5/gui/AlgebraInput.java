package org.geogebra.web.html5.gui;

/**
 * Plain text algebra input.
 */
public interface AlgebraInput {

	void setInputFieldWidth(int appletWidth);

	void setText(String string);

	void requestFocus();

}
