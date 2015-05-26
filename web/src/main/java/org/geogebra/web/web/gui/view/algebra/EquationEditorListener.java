package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;

import com.google.gwt.dom.client.SpanElement;

public interface EquationEditorListener extends AutoCompleteW {

	SpanElement getLaTeXSpan();

	void updatePosition(ScrollableSuggestionDisplay sug);

	void keypress(char c, boolean alt, boolean ctrl, boolean shift);

	void keydown(int keycode, boolean alt, boolean ctrl, boolean shift);

	void keyup(int keycode, boolean alt, boolean ctrl, boolean shift);

	boolean popupSuggestions();

	void scrollCursorIntoView();

}
