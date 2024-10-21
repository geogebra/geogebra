package org.geogebra.common.kernel.geos;

import java.util.function.Consumer;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.SymbolicEditor;
import org.geogebra.common.euclidian.TextRendererSettings;
import org.geogebra.common.io.MathFieldCommon;
import org.geogebra.common.main.App;

import com.himamis.retex.editor.share.editor.MathFieldInternal;

public class SymbolicEditorCommon extends SymbolicEditor {
	private final MathFieldCommon mf;
	private Consumer<String> keyListener;

	/**
	 * @param mf wrapped field
	 * @param app app
	 */
	public SymbolicEditorCommon(MathFieldCommon mf, App app) {
		super(app, app.getActiveEuclidianView());
		this.mf = mf;
		mf.getInternal().addMathFieldListener(this);
	}

	@Override
	public void onKeyTyped(String key) {
		if (keyListener != null) {
			keyListener.accept(key);
		}
	}

	@Override
	public boolean onArrowKeyPressed(int keyCode) {
		return false;
	}

	@Override
	public boolean onEscape() {
		return false;
	}

	@Override
	public boolean onTab(boolean shiftDown) {
		return true;
	}

	@Override
	protected MathFieldInternal getMathFieldInternal() {
		return mf.getInternal();
	}

	@Override
	protected void hide() {

	}

	@Override
	public boolean isClicked(GPoint point) {
		return false;
	}

	@Override
	public void attach(GeoInputBox geoInputBox, GRectangle bounds, TextRendererSettings settings) {
		setInputBox(geoInputBox);
		resetChanges();
	}

	@Override
	protected void selectEntryAt(int x, int y) {
		mf.getInternal().selectEntryAt(x, y);
	}

	@Override
	public void repaintBox(GGraphics2D g2) {

	}

	public void setKeyListener(Consumer<String> keyListener) {
		this.keyListener = keyListener;
	}
}
