package org.geogebra.common.gui.view.table.dimensions;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Class holding the cache values for the dimensions object.
 */
class DimensionCache {

    private TableValuesViewDimensions dimensions;
    private LinkedList<Integer> widths;
    private Integer medianWidth;

    /**
     * Construct a cache object.
     *
     * @param dimensions dimensions
     */
    DimensionCache(TableValuesViewDimensions dimensions) {
        this.dimensions = dimensions;
        resetCache();
    }

    /**
     * Reset the cache.
     */
    void resetCache() {
        widths = new LinkedList<>();
        for (int i = 0; i < dimensions.maxColumns; i++) {
            widths.add(null);
        }
        medianWidth = null;
    }

    /**
     * Get width from the cache.
     *
     * @param index column
     * @return width
     */
    int getWidth(int index) {
        int width;
        if (index >= dimensions.maxColumns) {
            width = getMedianWidth();
        } else {
            width = getExactWidth(index);
        }
        return width;
    }

    private int getMedianWidth() {
        if (medianWidth == null) {
            medianWidth = calculateMedianWidth();
        }
        return medianWidth;
    }

    private int calculateMedianWidth() {
        int[] exactWidths = new int[dimensions.maxColumns];
        for (int i = 0; i < dimensions.maxColumns; i++) {
            exactWidths[i] = getExactWidth(i);
        }
        return median(exactWidths);
    }

    private int median(int[] array) {
        int median;
        int center = dimensions.maxColumns / 2;
        Arrays.sort(array);
        if (array.length % 2 == 0) {
            median = (array[center] + array[center - 1]) / 2;
        } else {
            median = array[center];
        }
        return median;
    }

    private int getExactWidth(int index) {
        Integer width = widths.get(index);
        if (width == null) {
            width = calculateExactWidth(index);
            widths.set(index, width);
        }
        return width;
    }

    private int calculateExactWidth(int column) {
        return dimensions.calculateExactWidth(this, column);
    }

    /**
     * Remove a column from cache.
     *
     * @param column column
     */
    void removeColumn(int column) {
        widths.remove(column);
        medianWidth = null;
    }

    /**
     * Add a column to the cache.
     *
     * @param column column
     */
    void addColumn(int column) {
        widths.add(column, null);
        medianWidth = null;
    }

    /**
     * Update a column of the cache
     *
     * @param column column
     */
    void updateColumn(int column) {
        widths.set(column, null);
        medianWidth = null;
    }
}
