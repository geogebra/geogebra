package org.geogebra.web.web.cas.view;

import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;

public interface CASEditorW extends CASTableCellEditor {
	public AutoCompleteW getWidget();

	public void resetInput();

	public void setAutocomplete(boolean b);
}
