package geogebra.common.cas.view;

public interface CASTableCellEditor {

	int getInputSelectionEnd();

	int getInputSelectionStart();

	String getInputSelectedText();

	String getInput();

	void setInputSelectionStart(int selStart);

	void setInputSelectionEnd(int selEnd);

}
