/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */
 
package org.geogebra.common.kernel.geos;

import java.util.function.Consumer;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.SymbolicEditor;
import org.geogebra.common.euclidian.TextRendererSettings;
import org.geogebra.common.io.MathFieldCommon;
import org.geogebra.common.main.App;
import org.geogebra.editor.share.editor.MathFieldInternal;
import org.geogebra.editor.share.serializer.ScreenReaderSerializer;

public class SymbolicEditorCommon extends SymbolicEditor {
	private final MathFieldCommon mf;
	private Consumer<String> keyListener;
	private GColor foregroundColor;

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

	@Override
	public String getDescription() {
		return ScreenReaderSerializer.fullDescription(
				mf.getInternal().getEditorState().getRootNode(), null);
	}

	public void setKeyListener(Consumer<String> keyListener) {
		this.keyListener = keyListener;
	}

	public GColor getForegroundColor() {
		return foregroundColor;
	}

	@Override
	public void updateStyle() {
		foregroundColor = getGeoInputBox().getObjectColor();
	}
}
