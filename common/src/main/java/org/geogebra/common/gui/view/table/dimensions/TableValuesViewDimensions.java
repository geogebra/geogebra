package org.geogebra.common.gui.view.table.dimensions;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.view.table.TableValuesDimensions;
import org.geogebra.common.gui.view.table.TableValuesListener;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;

/**
 * Implementation of TableValuesDimensions.
 */
public class TableValuesViewDimensions implements TableValuesDimensions, TableValuesListener {

	public static final int MIN_WIDTH = 120;
	public static final int MAX_WIDTH = 180;
	private static final int PADDING = 12;

	private AwtFactory factory;
	private GFontRenderContext context;
	private GFont font;

	private DimensionCache columnCache;

	/** Table values model */
	TableValuesModel tableModel;

	/**
	 * Construct a new TableValuesViewDimensions object.
	 * @param model table values model
	 * @param factory awt factory
	 * @param context font render context
	 */
	public TableValuesViewDimensions(TableValuesModel model, AwtFactory factory,
			GFontRenderContext context) {
		this.tableModel = model;
		this.factory = factory;
		this.context = context;
		this.columnCache = new DimensionCache(this);
	}

	@Override
	public void setFont(GFont font) {
		this.font = font;
		resetCache();
	}

	@Override
	public int getRowHeight(int row) {
		return font.getSize() + 2 * PADDING;
	}

	@Override
	public int getColumnWidth(int column) {
		return columnCache.getWidth(column);
	}

	/**
	 * @param column to get the width.
	 * @return the calculated width.
	 */
	int calculateExactColumnWidth(int column) {
		int maxWidth = MIN_WIDTH;
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String text = tableModel.getCellAt(i, column);
			int width = getWidth(text);
			maxWidth = Math.max(maxWidth, width);
		}

		return maxWidth;
	}

	private int getWidth(String text) {
		GTextLayout layout = factory.newTextLayout(text, font, context);
		GRectangle2D rectangle = layout.getBounds();
		int cellWidth = (int) Math.round(Math.ceil(rectangle.getWidth()));
		return Math.max(Math.min(MAX_WIDTH, cellWidth), MIN_WIDTH);
	}

	private void resetCache() {
		columnCache.resetCache();
	}

	@Override
	public void notifyColumnRemoved(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		columnCache.removeColumn(column);
	}

	@Override
	public void notifyColumnChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		columnCache.updateColumn(column);
	}

	@Override
	public void notifyColumnAdded(TableValuesModel model, GeoEvaluatable evaluatable, int column) {
		columnCache.addColumn(column);
	}

	@Override
	public void notifyColumnHeaderChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		// Ignore
	}

	@Override
	public void notifyDatasetChanged(TableValuesModel model) {
		resetCache();
	}
}
