package org.geogebra.common.io;

import java.util.ArrayList;

import org.geogebra.common.util.SyntaxAdapterImpl;
import org.geogebra.editor.share.controller.CursorController;
import org.geogebra.editor.share.editor.MathField;
import org.geogebra.editor.share.editor.MathFieldInternal;
import org.geogebra.editor.share.editor.SyntaxAdapter;
import org.geogebra.editor.share.event.ClickListener;
import org.geogebra.editor.share.event.FocusListener;
import org.geogebra.editor.share.event.KeyListener;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.tree.Formula;

import com.himamis.retex.renderer.share.TeXIcon;

public class MathFieldCommon implements MathField {

	private final TemplateCatalog catalog;
	private final MathFieldInternal internal;

	/**
	 * @param adapter syntax adapter
	 */
	public MathFieldCommon(TemplateCatalog catalog, SyntaxAdapter adapter) {
		this.catalog = catalog;
		internal = new MathFieldInternal(this);
		internal.setSyntaxAdapter(adapter);
		internal.setFormula(new Formula(catalog));
	}

	@Override
	public void setTeXIcon(TeXIcon icon) {
		// stub
	}

	@Override
	public boolean showKeyboard() {
		// stub
		return false;
	}

	@Override
	public void showCopyPasteButtons() {
		// stub
	}

	@Override
	public void requestViewFocus() {
		// stub
	}

	@Override
	public void setFocusListener(FocusListener focusListener) {
		// stub
	}

	@Override
	public void setClickListener(ClickListener clickListener) {
		// stub
	}

	@Override
	public void setKeyListener(KeyListener keyListener) {
		// stub
	}

	@Override
	public void repaint() {
		// stub
	}

	@Override
	public void requestLayout() {
		// stub
	}

	@Override
	public boolean hasParent() {
		return false;
	}

	@Override
	public boolean hasFocus() {
		// stub
		return true;
	}

	@Override
	public TemplateCatalog getCatalog() {
		return catalog;
	}

	@Override
	public void hideCopyPasteButtons() {
		// stub
	}

	@Override
	public void scroll(int dx, int dy) {
		// stub
	}

	@Override
	public void fireInputChangedEvent() {
		// stub
	}

	@Override
	public void paste() {
		// stub
	}

	@Override
	public void copy() {
		// stub
	}

	@Override
	public boolean useCustomPaste() {
		return false;
	}

	@Override
	public MathFieldInternal getInternal() {
		return internal;
	}

	/**
	 * @param text
	 *            text to be inserted
	 */
	public void insertString(String text) {
		internal.insertString(text);
		internal.update();
	}

	/**
	 * @param syntaxAdapter syntax adapter
	 */
	public void setFormatConverter(SyntaxAdapterImpl syntaxAdapter) {
		internal.setSyntaxAdapter(syntaxAdapter);
	}

	public ArrayList<Integer> getCaretPath() {
		return CursorController.getPath(internal.getEditorState());
	}
}
