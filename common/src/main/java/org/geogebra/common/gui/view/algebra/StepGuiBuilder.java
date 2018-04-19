package org.geogebra.common.gui.view.algebra;

public interface StepGuiBuilder {

	void addPlainRow(String equations);

	void addLatexRow(String equations);

	void startGroup();

	void endGroup();

	void startDefault();

	void switchToDetailed();

	void endDetailed();

	void linebreak();
}
