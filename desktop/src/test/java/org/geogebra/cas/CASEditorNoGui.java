package org.geogebra.cas;

import org.geogebra.common.cas.view.CASTableCellEditor;

public class CASEditorNoGui implements CASTableCellEditor {

	private String content;

	public CASEditorNoGui(String string) {
		this.content = string;
	}

	public void setLabels() {
		// TODO Auto-generated method stub

	}

	public int getInputSelectionEnd() {
		return -1;
	}

	public int getInputSelectionStart() {
		return -1;
	}

	public String getInputSelectedText() {
		return "";
	}

	public String getInput() {
		return content;
	}

	public void setInputSelectionStart(int selStart) {
		// TODO Auto-generated method stub
	}

	public void setInputSelectionEnd(int selEnd) {
		// TODO Auto-generated method stub
	}

	public void clearInputText() {
		// TODO Auto-generated method stub

	}

	public void setInput(String string) {
		// TODO Auto-generated method stub

	}

	public void ensureEditing() {
		// TODO Auto-generated method stub
	}

	public void onEnter(boolean explicit) {
		// TODO Auto-generated method stub
	}

	public boolean hasFocus() {
		// TODO Auto-generated method stub
		return false;
	}

}
