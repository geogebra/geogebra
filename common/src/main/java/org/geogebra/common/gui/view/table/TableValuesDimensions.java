package org.geogebra.common.gui.view.table;

import org.geogebra.common.awt.GFont;

/**
 * Has information about table cell dimensions. Make sure to call
 *  * {@link TableValuesDimensions#setFont(GFont)} before
 *  * using this object, to make width calculations more specific.
 */
public interface TableValuesDimensions {

    /**
     * Set the font which is used for the calculations.
     *
     * @param font font
     */
    void setFont(GFont font);

    /**
     * Returns the row height.
     *
     * @param row row
     * @return the height of the row
     */
    int getRowHeight(int row);

    /**
     * Returns the column width.
     *
     * @param column column
     * @return the width of the column
     */
    int getColumnWidth(int column);

    /**
     * Returns the header height.
     *
     * @return the header height
     */
    int getHeaderHeight();

    /**
     * Returns the header width.
     *
     * @param header the header index
     * @return the header width
     */
    int getHeaderWidth(int header);
}
