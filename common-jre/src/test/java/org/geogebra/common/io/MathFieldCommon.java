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

    public void setTeXIcon(TeXIcon icon) {
        // stub
    }

    public boolean showKeyboard() {
        // stub
        return false;
    }

    public void showCopyPasteButtons() {
        // stub
    }

    public void requestViewFocus() {
        // stub
    }

    public void setFocusListener(FocusListener focusListener) {
        // stub
    }

    public void setClickListener(ClickListener clickListener) {
        // stub
    }

    public void setKeyListener(KeyListener keyListener) {
        // stub

    }

    public void repaint() {
        // stub
    }

    public void requestLayout() {
        // stub
    }

    public boolean hasParent() {
        return false;
    }

    public boolean hasFocus() {
        // stub
        return false;
    }

    public MetaModel getMetaModel() {
        // stub
        return new MetaModel();
    }

    public void hideCopyPasteButtons() {
        // stub
    }

    public void scroll(int dx, int dy) {
        // stub
    }

    public void fireInputChangedEvent() {
        // stub
    }

    public void paste() {
        // stub
    }

    public void copy() {
        // stub
    }

    public void tab(boolean shiftDown) {
        // stub
    }

    public boolean useCustomPaste() {
        return false;
    }

    public MathFieldInternal getInternal() {
        return internal;
    }

    public void insertString(String text) {
        KeyboardInputAdapter.insertString(internal, text);
        internal.update();
    }

}
