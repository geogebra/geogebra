package org.geogebra.common.spreadsheet.core;

import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.BAR_CHART;
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
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.MEAN;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.PASTE;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.PIE_CHART;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.SUM;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geogebra.common.spreadsheet.core.ContextMenuItem.ActionableItem;
import org.geogebra.common.spreadsheet.core.ContextMenuItem.Divider;
import org.geogebra.common.spreadsheet.core.ContextMenuItem.SubMenuItem;

/**
 * A builder for (spreadsheet) context menus.
 */
public final class ContextMenuBuilder {

    static final int HEADER_INDEX = -1;
    private final SpreadsheetController spreadsheetController;

    /**
     * @param spreadsheetController {@link SpreadsheetController}
     */
    ContextMenuBuilder(SpreadsheetController spreadsheetController) {
        this.spreadsheetController = spreadsheetController;
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
                new SubMenuItem(CALCULATE, getCalculateItems()),
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

    private ContextMenuItem getChartMenuItem() {
        return new SubMenuItem(CREATE_CHART, getChartItems());
    }

    List<ContextMenuItem> getChartItems() {
        return List.of(
                new ActionableItem(LINE_CHART, () -> spreadsheetController.createChart(LINE_CHART)),
                new ActionableItem(BAR_CHART, () -> spreadsheetController.createChart(BAR_CHART)),
                new ActionableItem(HISTOGRAM, () -> spreadsheetController.createChart(HISTOGRAM)),
                new ActionableItem(BOX_PLOT, () -> spreadsheetController.createChart(BOX_PLOT)),
                new ActionableItem(PIE_CHART, () -> spreadsheetController.createChart(PIE_CHART)));
    }

    List<ContextMenuItem> getCalculateItems() {
        return List.of(
                new ActionableItem(SUM, () -> spreadsheetController.calculate(
                        SpreadsheetCommand.SUM)),
                new ActionableItem(MEAN, () -> spreadsheetController.calculate(
                        SpreadsheetCommand.MEAN)));
    }

    private List<ContextMenuItem> rowItems(int fromRow, int toRow) {
        boolean allRows = isAllRows(fromRow, toRow);
        return Stream.of(
                new ActionableItem(CUT, () -> spreadsheetController.cutCells(fromRow, -1)),
                new ActionableItem(COPY, () -> spreadsheetController.copyCells(fromRow, -1)),
                new ActionableItem(PASTE, () -> spreadsheetController.pasteCells(fromRow, -1)),
                new Divider(),
                new SubMenuItem(CALCULATE, getCalculateItems()),
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
                new SubMenuItem(CALCULATE, getCalculateItems()),
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
