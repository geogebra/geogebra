package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;

import com.google.gwt.dom.client.SpanElement;

public interface EquationEditorListener extends AutoCompleteW {

	SpanElement getLaTeXSpan();

	void updatePosition(ScrollableSuggestionDisplay sug);

	void keypress(int circumflex, boolean b, boolean c, boolean d);

	void keydown(int leftArrow, boolean b, boolean c, boolean d);

	void keyup(int enter, boolean b, boolean c, boolean d);

	boolean popupSuggestions();

	void scrollCursorIntoView();

}
