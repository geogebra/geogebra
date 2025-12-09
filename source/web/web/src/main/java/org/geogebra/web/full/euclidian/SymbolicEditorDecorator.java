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

package org.geogebra.web.full.euclidian;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.web.full.gui.components.MathFieldEditor;
import org.geogebra.web.full.gui.components.MathFieldEditorDecoratorBase;

public class SymbolicEditorDecorator extends MathFieldEditorDecoratorBase {

	public static final int LEFT_OFFSET = -1; // TODO: clean this up once...
	private final int fixMargin;

	/**
	 * @param editor to decorate.
	 * @param fixMargin to subtract from width.
	 */
	SymbolicEditorDecorator(MathFieldEditor editor, int fixMargin) {
		super(editor);
		this.fixMargin = fixMargin;
		editor.addStyleName("evInputEditor");
	}

	/**
	 * Updates colors and font size according to the input box construction element.
	 *
	 * @param geoInputBox the currently edited geo.
	 * @param fontSize base font size.
	 */
	void update(GeoInputBox geoInputBox, int fontSize) {
		setForegroundColor(geoInputBox.getObjectColor());
		setBackgroundColor(geoInputBox.getBackgroundColor());
		setFontSize(fontSize * geoInputBox.getFontSizeMultiplier() + 3);
	}

	/**
	 * Updates editor bounds.
	 */
	public void updateBounds(GRectangle bounds) {
		double fieldWidth = bounds.getWidth() - fixMargin;
		setLeft(bounds.getX() + LEFT_OFFSET);
		setTop(bounds.getY());
		setWidth(fieldWidth);
		setHeight(bounds.getHeight());
	}
}
