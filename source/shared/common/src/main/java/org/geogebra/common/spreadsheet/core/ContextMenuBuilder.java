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

import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.BAR_CHART;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.BOX_PLOT;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.CALCULATE;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.COPY;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.CREATE_CHART;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.CUT;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.DELETE_COLUMN;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.DELETE_ROW;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.HISTOGRAM;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.INSERT_COLUMN_LEFT;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.INSERT_COLUMN_RIGHT;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.INSERT_ROW_ABOVE;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.INSERT_ROW_BELOW;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.LINE_CHART;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.MAX;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.MEAN;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.MEDIAN;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.MIN;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.PASTE;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.PIE_CHART;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.Q1;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.Q3;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.SAMPLE_SD;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.SD;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.SUM;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.statistics.Statistic;
import org.geogebra.common.spreadsheet.core.ContextMenuItem.ActionableItem;
import org.geogebra.common.spreadsheet.core.ContextMenuItem.Divider;
import org.geogebra.common.spreadsheet.core.ContextMenuItem.SubMenuItem;

import com.google.j2objc.annotations.Weak;

/**
 * A builder for (spreadsheet) context menus.
 */
public final class ContextMenuBuilder {

    static final int HEADER_INDEX = -1;

    @Weak
    private SpreadsheetController spreadsheetController;
    @Weak
    private SpreadsheetConstructionDelegate constructionDelegate;

    /**
     * @param spreadsheetController {@link SpreadsheetController}
     */
    ContextMenuBuilder(SpreadsheetController spreadsheetController) {
        this.spreadsheetController = spreadsheetController;
    }

    /**
     * Set the construction delegate (to check if certain features are available).
     */
    public void setSpreadsheetConstructionDelegate(
            @CheckForNull SpreadsheetConstructionDelegate constructionDelegate) {
        this.constructionDelegate = constructionDelegate;
    }

    /**
     * Gets the context menu items for the specific <b>single</b> cell / row / column
     * @param row of the cell.
     * @param column of the cell.
     * @return list of menu items.
     */
    public List<ContextMenuItem> build(int row, int column) {
        return build(row, row, column, column);
    }

    /**
     * Gets the context menu items for the specific <b>multiple</b> cells / rows / columns
     * @param fromRow Index of the uppermost row
     * @param toRow Index of the bottommost row
     * @param fromCol Index of the leftmost column
     * @param toCol Index of the rightmost column
     * @return list of the menu key and its action.
     */
    public List<ContextMenuItem> build(int fromRow, int toRow, int fromCol, int toCol) {
        if (shouldShowTableItems(fromRow, fromCol)) {
            return tableItems(fromRow, fromCol);
        } else if (fromRow == HEADER_INDEX) {
            return columnItems(fromCol, toCol);
        } else if (fromCol == HEADER_INDEX) {
            return rowItems(fromRow, toRow);
        }
        return cellItems(fromRow, toRow, fromCol, toCol);
    }

    /**
     * @param fromRow Index of the uppermost row
     * @param fromCol Index of the leftmost column
     * @return Whether the table items should be shown. This is the case if either all cells are
     * selected or the user clicked the top left cell (between A and 1).
     */
    private boolean shouldShowTableItems(int fromRow, int fromCol) {
        return spreadsheetController.areAllCellsSelected()
                || (fromRow == HEADER_INDEX && fromCol == HEADER_INDEX);
    }

    private List<ContextMenuItem> tableItems(int row, int column) {
        return List.of(
                new ActionableItem(CUT, () -> spreadsheetController.cutCells(row, column)),
                new ActionableItem(COPY, () -> spreadsheetController.copyCells(row, column)),
                new ActionableItem(PASTE, () -> spreadsheetController.pasteCells(row, column))
        );
    }

    private List<ContextMenuItem> cellItems(int fromRow, int toRow, int fromCol, int toCol) {
        boolean allRows = isAllRows(fromRow, toRow);
        boolean allColumns = isAllColumns(fromCol, toCol);
        return Stream.of(
                new ActionableItem(CUT, () -> spreadsheetController.cutCells(fromRow, fromCol)),
                new ActionableItem(COPY, () -> spreadsheetController.copyCells(fromRow, fromCol)),
                new ActionableItem(PASTE, () -> spreadsheetController.pasteCells(fromRow, fromCol)),
                new Divider(),
                getCalculateItem(),
                getChartMenuItem(),
                new Divider(),
                new ActionableItem(INSERT_ROW_ABOVE,
                        () -> spreadsheetController.insertRowAt(fromRow, false)),
                new ActionableItem(INSERT_ROW_BELOW,
                        () -> spreadsheetController.insertRowAt(toRow + 1, true)),
                new ActionableItem(INSERT_COLUMN_LEFT,
                        () -> spreadsheetController.insertColumnAt(fromCol, false)),
                new ActionableItem(INSERT_COLUMN_RIGHT,
                        () -> spreadsheetController.insertColumnAt(toCol + 1, true)),
                allRows && allColumns ? null : new Divider(),
                allRows ? null : new ActionableItem(DELETE_ROW,
                        () -> spreadsheetController.deleteRowAt(fromRow)),
                allColumns ? null : new ActionableItem(DELETE_COLUMN,
                        () -> spreadsheetController.deleteColumnAt(fromCol))
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private @CheckForNull ContextMenuItem getCalculateItem() {
        List<ContextMenuItem> items = getCalculateItems();
        return items.isEmpty() ? null : new SubMenuItem(CALCULATE, items);
    }

    private @CheckForNull ContextMenuItem getChartMenuItem() {
        List<ContextMenuItem> items = getChartItems();
        return items.isEmpty() ? null : new SubMenuItem(CREATE_CHART, items);
    }

    List<ContextMenuItem> getChartItems() {
        List<ContextMenuItem> chartItems = new ArrayList<>();
        if (constructionDelegate == null || constructionDelegate.supportsLineGraph()) {
            chartItems.add(new ActionableItem(LINE_CHART,
                    () -> spreadsheetController.createChart(LINE_CHART)));
        }
        if (constructionDelegate == null || constructionDelegate.supportsBarChart()) {
            chartItems.add(new ActionableItem(BAR_CHART,
                    () -> spreadsheetController.createChart(BAR_CHART)));
        }
        if (constructionDelegate == null || constructionDelegate.supportsHistogram()) {
            chartItems.add(new ActionableItem(HISTOGRAM,
                    () -> spreadsheetController.createChart(HISTOGRAM)));
        }
        if (constructionDelegate == null || constructionDelegate.supportsBoxPlot()) {
            chartItems.add(new ActionableItem(BOX_PLOT,
                    () -> spreadsheetController.createChart(BOX_PLOT)));
        }
        if (constructionDelegate == null || constructionDelegate.supportsPieChart()) {
            chartItems.add(new ActionableItem(PIE_CHART,
                    () -> spreadsheetController.createChart(PIE_CHART)));
        }
        return chartItems;
    }

    /**
     * @return a (possibly empty) list of context menu items for the "Calculate" submenu
     */
    List<ContextMenuItem> getCalculateItems() {
        List<ContextMenuItem> items = new ArrayList<>();
        if (supportsStatistic(Statistic.SUM)) {
            items.add(new ActionableItem(SUM, () ->
                    spreadsheetController.calculate1VarStatistics(Statistic.SUM)));
        }
        if (supportsStatistic(Statistic.MEAN)) {
            items.add(new ActionableItem(MEAN, () ->
                    spreadsheetController.calculate1VarStatistics(Statistic.MEAN)));
        }
        if (supportsStatistic(Statistic.SAMPLE_SD)) {
            items.add(new ActionableItem(SAMPLE_SD, () ->
                    spreadsheetController.calculate1VarStatistics(Statistic.SAMPLE_SD)));
        }
        if (supportsStatistic(Statistic.SD)) {
            items.add(new ActionableItem(SD, () ->
                    spreadsheetController.calculate1VarStatistics(Statistic.SD)));
        }
        if (supportsStatistic(Statistic.MIN)) {
            items.add(new ActionableItem(MIN, () ->
                    spreadsheetController.calculate1VarStatistics(Statistic.MIN)));
        }
        if (supportsStatistic(Statistic.Q1)) {
            items.add(new ActionableItem(Q1, () ->
                    spreadsheetController.calculate1VarStatistics(Statistic.Q1)));
        }
        if (supportsStatistic(Statistic.MEDIAN)) {
            items.add(new ActionableItem(MEDIAN, () ->
                    spreadsheetController.calculate1VarStatistics(Statistic.MEDIAN)));
        }
        if (supportsStatistic(Statistic.Q3)) {
            items.add(new ActionableItem(Q3, () ->
                    spreadsheetController.calculate1VarStatistics(Statistic.Q3)));
        }
        if (supportsStatistic(Statistic.MAX)) {
            items.add(new ActionableItem(MAX, () ->
                    spreadsheetController.calculate1VarStatistics(Statistic.MAX)));
        }
        return items;
    }
	
	private boolean supportsStatistic(Statistic statistic) {
		return constructionDelegate == null || constructionDelegate.supportsStatistic(statistic);
	}

    private List<ContextMenuItem> rowItems(int fromRow, int toRow) {
        boolean allRows = isAllRows(fromRow, toRow);
        return Stream.of(
                new ActionableItem(CUT, () -> spreadsheetController.cutCells(fromRow, -1)),
                new ActionableItem(COPY, () -> spreadsheetController.copyCells(fromRow, -1)),
                new ActionableItem(PASTE, () -> spreadsheetController.pasteCells(fromRow, -1)),
                new Divider(),
                getCalculateItem(),
                getChartMenuItem(),
                new Divider(),
                new ActionableItem(INSERT_ROW_ABOVE,
                        () -> spreadsheetController.insertRowAt(fromRow, false)),
                new ActionableItem(INSERT_ROW_BELOW,
                        () -> spreadsheetController.insertRowAt(toRow + 1, true)),
                allRows ? null : new Divider(),
                allRows ? null : new ActionableItem(DELETE_ROW,
                        () -> spreadsheetController.deleteRowAt(fromRow))
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private List<ContextMenuItem> columnItems(int fromCol, int toCol) {
        boolean allColumns = isAllColumns(fromCol, toCol);
        return Stream.of(
                new ActionableItem(CUT, () -> spreadsheetController.cutCells(-1, fromCol)),
                new ActionableItem(COPY, () -> spreadsheetController.copyCells(-1, fromCol)),
                new ActionableItem(PASTE, () -> spreadsheetController.pasteCells(-1, fromCol)),
                new Divider(),
                getCalculateItem(),
                getChartMenuItem(),
                new Divider(),
                new ActionableItem(INSERT_COLUMN_LEFT,
                        () -> spreadsheetController.insertColumnAt(fromCol, false)),
                new ActionableItem(INSERT_COLUMN_RIGHT,
                        () -> spreadsheetController.insertColumnAt(toCol + 1, true)),
                allColumns ? null : new Divider(),
                allColumns ? null : new ActionableItem(DELETE_COLUMN,
                        () -> spreadsheetController.deleteColumnAt(fromCol))
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private boolean isAllColumns(int fromCol, int toCol) {
        return fromCol == 0 && toCol == spreadsheetController.getLayout().numberOfColumns() - 1;
    }

    private boolean isAllRows(int fromRow, int toRow) {
        return fromRow == 0 && toRow == spreadsheetController.getLayout().numberOfRows() - 1;
    }

}
