/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
