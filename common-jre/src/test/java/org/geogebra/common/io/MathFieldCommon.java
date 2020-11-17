package org.geogebra.common.io;

import org.geogebra.common.util.SyntaxAdapterImpl;

import com.himamis.retex.editor.share.editor.MathField;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.editor.SyntaxAdapter;
import com.himamis.retex.editor.share.event.ClickListener;
import com.himamis.retex.editor.share.event.FocusListener;
import com.himamis.retex.editor.share.event.KeyListener;
import com.himamis.retex.editor.share.input.KeyboardInputAdapter;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.renderer.share.TeXIcon;

public class MathFieldCommon implements MathField {

	private final MetaModel model;
	private MathFieldInternal internal;

	/**
	 * @param adapter syntax adapter
	 */
	public MathFieldCommon(MetaModel model, SyntaxAdapter adapter) {
		this.model = model;
		internal = new MathFieldInternal(this);
		internal.setSyntaxAdapter(adapter);
		internal.setFormula(MathFormula.newFormula(model));
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
	public MetaModel getMetaModel() {
		return model;
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

	public void setFormatConverter(SyntaxAdapterImpl formatConverter) {
		internal.getInputController().setFormatConverter(formatConverter);
	}
}
