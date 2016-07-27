package org.geogebra.web.web.cas.view;

import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;

public interface CASEditorW extends CASTableCellEditor, AutoCompleteW {

	public void resetInput();

	public void setAutocomplete(boolean b);

	public void setLaTeX(String plain, String latex);

	public void ensureEditing();
}
