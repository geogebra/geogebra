package org.geogebra.common.gui.popup.autocompletion;

import org.geogebra.common.kernel.geos.GeoElement;

import com.himamis.retex.editor.share.controller.EditorState;
import com.himamis.retex.editor.share.editor.MathField;

/**
 * Class to decide if a text typed into a MathField should have suggestions.
 */
public class InputSuggestions {
	private final GeoElement geo;
	private boolean forceAsText = false;

	/**
	 *
	 * @param geo to check.
	 */
	public InputSuggestions(GeoElement geo) {
		this.geo = geo;
	}

	/**
	 *
	 * @param editorState {@link EditorState}
	 * @return if suggestion is prevented for the given editorState
	 */
	public boolean isPreventedFor(EditorState editorState) {
		return isTextInput() || editorState.isInsideQuotes()
				|| editorState.isInScript();
	}

	/**
	 *
	 * @return if the input is text type.
	 */
	public boolean isTextInput() {
		return forceAsText || (geo != null && geo.isGeoText()
				&& geo.isTextCommand());
	}

	/**
	 * Sets input to text type directly.
	 * @param value to set.
	 */
	public void setForceAsText(boolean value) {
		this.forceAsText = value;
	}

	/**
	 * @param mf math input field
	 * @return current command: in context where commands are allowed that means current word,
	 * otherwise empty string.
	 */
	public String getCommand(MathField mf) {
		return mf == null || isPreventedFor(mf.getInternal().getEditorState())
				? "" : mf.getInternal().getCharactersLeftOfCursor();
	}
}