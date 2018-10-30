package org.geogebra.common.gui.view.table;

/**
 * Has information about table cell dimensions.
 */
public interface TableValuesDimensions {

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
     * @return the width of the colum
     */
    int getColumnWidth(int column);

    /**
     * Returns the header height.
     *
     * @param header the header index
     * @return the header height
     */
    int getHeaderHeight(int header);
}
