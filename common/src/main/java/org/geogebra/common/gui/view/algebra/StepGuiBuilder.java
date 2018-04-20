package org.geogebra.common.gui.view.algebra;

import java.util.List;

public interface StepGuiBuilder {

	void addRow(List<String> equations);

	void startGroup();

	void endGroup();

	void startDefault();

	void switchToDetailed();

	void endDetailed();

	void linebreak();
}
