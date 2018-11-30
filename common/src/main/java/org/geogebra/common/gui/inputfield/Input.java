package org.geogebra.common.gui.inputfield;

public interface Input {
	String getText();
	void showError(String errorMessage);
	void setErrorResolved();
}
