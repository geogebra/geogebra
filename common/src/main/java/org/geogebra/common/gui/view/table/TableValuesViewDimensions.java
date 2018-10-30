package org.geogebra.common.gui.view.table;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.factories.AwtFactory;

/**
 * Implementation of TableValuesDimensions.
 */
class TableValuesViewDimensions implements TableValuesDimensions {

    private static final int MAX_WIDTH = 280;
    private static final int PADDING = 16;

    private TableValuesModel model;
    private AwtFactory factory;
    private GFontRenderContext context;
    private GFont font;

    /**
     * Construct a new TableValuesViewDimensions object.
     *
     * @param model table values model
     * @param factory awt factory
     * @param context font render context
     * @param font font
     */
    TableValuesViewDimensions(TableValuesModel model, AwtFactory factory, GFontRenderContext context, GFont font) {
        this.model = model;
        this.factory = factory;
        this.context = context;
        this.font = font;
    }

    @Override
    public int getRowHeight(int row) {
        return font.getSize() + 2 * PADDING;
    }

    @Override
    public int getColumnWidth(int column) {
        int maxWidth = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            String text = model.getCellAt(i, column);
            int width = getWidth(text);
            maxWidth = Math.max(maxWidth, width);
        }
        String header = model.getHeaderAt(column);
        maxWidth = Math.max(maxWidth, getWidth(header));
        return maxWidth;
    }

    private int getWidth(String text) {
        GTextLayout layout = factory.newTextLayout(text, font, context);
        GRectangle2D rectangle = layout.getBounds();
        long width = Math.min(MAX_WIDTH, Math.round(Math.ceil(rectangle.getWidth())));
        return (int) width;
    }

    @Override
    public int getHeaderHeight(int header) {
        return font.getSize() + 2 * PADDING;
    }
}
