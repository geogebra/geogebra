package org.geogebra.common.io;

import com.himamis.retex.editor.share.editor.MathField;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.event.ClickListener;
import com.himamis.retex.editor.share.event.FocusListener;
import com.himamis.retex.editor.share.event.KeyListener;
import com.himamis.retex.editor.share.input.KeyboardInputAdapter;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.renderer.share.TeXIcon;

public class MathFieldCommon implements MathField {

	private MathFieldInternal internal = new MathFieldInternal(this);

	public MathFieldCommon() {
		internal.setFormula(MathFormula.newFormula(new MetaModel()));
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
		return false;
	}

	@Override
	public MetaModel getMetaModel() {
		// stub
		return new MetaModel();
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
	public void tab(boolean shiftDown) {
		// stub
	}

	@Override
	public boolean useCustomPaste() {
		return false;
	}

	/**
	 * @return common implementation
	 */
	public MathFieldInternal getInternal() {
		return internal;
	}

	/**
	 * @param text
	 *            text to be inserted
	 */
	public void insertString(String text) {
		KeyboardInputAdapter.insertString(internal, text);
		internal.update();
	}

}
