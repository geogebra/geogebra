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

package org.geogebra.common.spreadsheet.core;

import javax.annotation.Nonnull;

import org.geogebra.common.io.MathFieldCommon;
import org.geogebra.common.spreadsheet.kernel.DefaultSpreadsheetCellDataSerializer;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.editor.MathFieldInternal;

final class TestSpreadsheetCellEditor implements SpreadsheetCellEditor {

	private final MathFieldCommon mathField = new MathFieldCommon(new TemplateCatalog(), null);

	private final SpreadsheetCellDataSerializer cellDataSerializer =
			new DefaultSpreadsheetCellDataSerializer();

	private final TabularData<?> tabularData;
	private boolean showing;

	TestSpreadsheetCellEditor(TabularData<?> tabularData) {
		this.tabularData = tabularData;
	}

	@Override
	public void show(@Nonnull Rectangle editorBounds, @Nonnull Rectangle viewport, int textAlignment) {
		showing = true;
	}

	@Override
	public void updatePosition(@Nonnull Rectangle editorBounds, @Nonnull Rectangle viewport) {
		// not needed in tests
	}

	@Override
	public void hide() {
		showing = false;
	}

	@Override
	public @Nonnull MathFieldInternal getMathField() {
		return mathField.getInternal();
	}

	@Override
	public @Nonnull SpreadsheetCellProcessor getCellProcessor() {
		return tabularData.getCellProcessor();
	}

	@Override
	public @Nonnull SpreadsheetCellDataSerializer getCellDataSerializer() {
		return cellDataSerializer;
	}

	/**
	 * @return whether this is visible; keeps track of {@link #show} and {@link #hide} calls.
	 */
	public boolean isShowing() {
		return showing;
	}
}
