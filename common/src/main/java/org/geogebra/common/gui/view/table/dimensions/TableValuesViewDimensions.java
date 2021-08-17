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

	private static final int PADDING = 12;
    public static final int HEADER_HEIGHT = 48;

    private AwtFactory factory;
    private GFontRenderContext context;
    private GFont font;
    private int maxRows;

    private DimensionCache columnCache;
    private DimensionCache headerCache;

    /** Table values model */
    TableValuesModel tableModel;
    /** Max columns, see {@link #setMaxColumns(int)} */
    int maxColumns;

    /**
     * Construct a new TableValuesViewDimensions object.
     *
     * @param model table values model
     * @param factory awt factory
     * @param context font render context
     */
	public TableValuesViewDimensions(TableValuesModel model, AwtFactory factory,
			GFontRenderContext context) {
        this.tableModel = model;
        this.factory = factory;
        this.context = context;
        this.maxRows = 50;
        this.maxColumns = 20;
        this.columnCache = new DimensionCache(this);
        this.headerCache = new DimensionCache(this);
    }

    /**
     * Specify the maximum number of rows, on which to base column width calculation.
     * The assumption is that rows over the index maxRows in a specific column
     * will not be larger than the largest under the index.
     *
     * @param maxRows number of rows, must be larger than 0
     */
    public void setMaxRows(int maxRows) {
        assertValidValue(maxRows, 1, "maxRows");
        this.maxRows = maxRows;
        resetCache();
    }

    /**
     * Specify the maximum number of columns, for which to calculate column width.
     * For every column after maxColumns, we return the median value of the previous
     * columns.
     *
     * @param maxColumns number of columns, must be larger than 1
     */
    public void setMaxColumns(int maxColumns) {
        assertValidValue(maxColumns, 2, "maxColumns");
        this.maxColumns = maxColumns;
        resetCache();
    }

	private static void assertValidValue(int value, int minValue, String variableName) {
        if (value < minValue) {
            throw new RuntimeException(variableName + " must be larger than 0");
        }
    }

    @Override
    public void setFont(GFont font) {
        this.font = font;
        resetCache();
    }

    @Override
    public int getRowHeight(int row) {
        return getHeaderHeight();
    }

    @Override
    public int getColumnWidth(int column) {
        return columnCache.getWidth(column);
    }

    @Override
    public int getHeaderHeight() {
		return font.getSize() + 2 * PADDING;
    }

    @Override
    public int getHeaderWidth(int column) {
        return headerCache.getWidth(column);
    }

	/**
	 *
	 * @param column
	 *            to get the width.
	 * @return the calculated width.
	 */
	protected int calculateExactColumnWidth(int column) {
		int maxWidth = 0;
        int rows = Math.min(tableModel.getRowCount(), maxRows);
        for (int i = 0; i < rows; i++) {
            String text = tableModel.getCellAt(i, column);
            int width = getWidth(text);
            maxWidth = Math.max(maxWidth, width);
        }

		return maxWidth;
	}

    private int calculateExactHeaderWidth(int column) {
		String header = tableModel.getHeaderAt(column);
        return getWidth(header);
    }

    private int getWidth(String text) {
        GTextLayout layout = factory.newTextLayout(text, font, context);
        GRectangle2D rectangle = layout.getBounds();
        return (int) Math.round(Math.ceil(rectangle.getWidth()));
    }

    /**
     * Calculate the exact width of a column.
     *
     * @param cahce which cache is requesting the calculation
     * @param column which column
     * @return the width of the column
     */
    int calculateExactWidth(DimensionCache cahce, int column) {
        if (cahce == columnCache) {
            return calculateExactColumnWidth(column);
        } else if (cahce == headerCache) {
            return calculateExactHeaderWidth(column);
        } else {
            throw new RuntimeException("Unknown cache");
        }
    }

    private void resetCache() {
        columnCache.resetCache();
        headerCache.resetCache();
    }

    @Override
	public void notifyColumnRemoved(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
        columnCache.removeColumn(column);
        headerCache.removeColumn(column);
    }

    @Override
	public void notifyColumnChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
        columnCache.updateColumn(column);
        headerCache.updateColumn(column);
    }

    @Override
    public void notifyColumnAdded(TableValuesModel model, GeoEvaluatable evaluatable, int column) {
        columnCache.addColumn(column);
        headerCache.addColumn(column);
    }

    @Override
	public void notifyColumnHeaderChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
        headerCache.updateColumn(column);
    }

    @Override
    public void notifyDatasetChanged(TableValuesModel model) {
        resetCache();
    }
}
