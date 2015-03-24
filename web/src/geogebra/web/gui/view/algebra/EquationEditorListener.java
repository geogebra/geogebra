package geogebra.web.gui.view.algebra;

import geogebra.html5.gui.inputfield.AutoCompleteW;

import com.google.gwt.dom.client.SpanElement;

public interface EquationEditorListener extends AutoCompleteW {

	SpanElement getLaTeXSpan();

	void updatePosition(ScrollableSuggestionDisplay sug);

}
