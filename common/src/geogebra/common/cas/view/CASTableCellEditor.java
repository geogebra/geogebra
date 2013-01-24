package geogebra.common.cas.view;

import geogebra.common.gui.SetLabels;

public interface CASTableCellEditor extends SetLabels {

	int getInputSelectionEnd();

	int getInputSelectionStart();

	String getInputSelectedText();

	String getInput();

	void setInputSelectionStart(int selStart);

	void setInputSelectionEnd(int selEnd);

}
