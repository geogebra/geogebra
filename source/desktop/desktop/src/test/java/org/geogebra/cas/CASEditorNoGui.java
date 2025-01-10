package org.geogebra.cas;

import org.geogebra.common.cas.view.CASTableCellEditor;

public class CASEditorNoGui implements CASTableCellEditor {

	private String content;

	public CASEditorNoGui(String string) {
		this.content = string;
	}

	@Override
	public void setLabels() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getInputSelectionEnd() {
		return -1;
	}

	@Override
	public int getInputSelectionStart() {
		return -1;
	}

	@Override
	public String getInputSelectedText() {
		return "";
	}

	@Override
	public String getInput() {
		return content;
	}

	@Override
	public void setInputSelectionStart(int selStart) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setInputSelectionEnd(int selEnd) {
		// TODO Auto-generated method stub
	}

	@Override
	public void clearInputText() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setInput(String string) {
		// TODO Auto-generated method stub
	}

	@Override
	public void ensureEditing() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onEnter(boolean explicit) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean hasFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPixelRatio(double ratio) {
		// only in web
	}

}
