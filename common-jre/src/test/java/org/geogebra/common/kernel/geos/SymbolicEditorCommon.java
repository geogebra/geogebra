package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.SymbolicEditor;
import org.geogebra.common.io.MathFieldCommon;
import org.geogebra.common.main.App;

import com.himamis.retex.editor.share.editor.MathFieldInternal;

class SymbolicEditorCommon extends SymbolicEditor {
	private final MathFieldCommon mf;

	/**
	 * @param mf wrapped field
	 * @param app app
	 */
	public SymbolicEditorCommon(MathFieldCommon mf, App app) {
		super(app, app.getActiveEuclidianView());
		this.mf = mf;
	}

	@Override
	public void onKeyTyped(String key) {

	}

	@Override
	public boolean onEscape() {
		return false;
	}

	@Override
	public void onTab(boolean shiftDown) {

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
	public void attach(GeoInputBox geoInputBox, GRectangle bounds) {
		setInputBox(geoInputBox);
	}

	@Override
	protected void resetChanges() {

	}

	@Override
	public void repaintBox(GGraphics2D g2) {

	}
}
