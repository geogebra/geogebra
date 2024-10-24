package org.geogebra.common.spreadsheet.core;

import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.DELETE_COLUMN;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.DELETE_ROW;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.INSERT_COLUMN_LEFT;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.INSERT_COLUMN_RIGHT;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.INSERT_ROW_ABOVE;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.INSERT_ROW_BELOW;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.io.FactoryProviderCommon;
import org.geogebra.common.spreadsheet.TestTabularData;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.common.util.shape.Size;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class SpreadsheetControllerTest implements SpreadsheetControlsDelegate {
    private final int cellHeight = TableLayout.DEFAULT_CELL_HEIGHT;
    private final int cellWidth = 40;
    private final int rowHeaderCellWidth = TableLayout.DEFAULT_ROW_HEADER_WIDTH;

    private SpreadsheetController controller;
    private TabularData tabularData;
    private SpreadsheetCellEditor cellEditor;
    private ClipboardInterface clipboard;
    private Rectangle viewport;

    @BeforeClass
    public static void setupOnce() {
        FactoryProvider.setInstance(new FactoryProviderCommon()); // required by MathField
    }

    @Before
    public void setup() {
        tabularData = new TestTabularData();
        clipboard = new TestClipboard();
        cellEditor = new TestSpreadsheetCellEditor(tabularData);

        controller = new SpreadsheetController(tabularData);
        controller.setControlsDelegate(this);
        controller.getLayout().setHeightForRows(cellHeight, 0, 5);
        controller.getLayout().setWidthForColumns(cellWidth, 0, 5);
        setViewport(new Rectangle(0, 100, 0, 120));
        controller.setViewportAdjustmentHandler(new ViewportAdjusterDelegate() {
            @Override
            public void setScrollPosition(int x, int y) {
                viewport = viewport.translatedBy(x, y);
            }

            @Override
            public int getScrollBarWidth() {
                return 5;
            }

            @Override
            public void updateScrollableContentSize(Size size) {
                // not needed
            }
        });
    }

    @Test
    public void testViewportIsAdjustedRightwardsWithArrowKey() {
        controller.selectCell(1, 1, false, false);
        fakeRightArrowPress();
        assertNotEquals(0, viewport.getMinX(), 0);
    }

    @Test
    public void testViewportIsAdjustedRightwardsWithMouseClick() {
        controller.handlePointerDown(rowHeaderCellWidth + 90, cellHeight + 10, Modifiers.NONE);
        assertNotEquals(0, viewport.getMinX(), 0);
    }

    @Test
    public void testViewportIsNotAdjustedRightwardsWithArrowKey() {
        setViewport(new Rectangle(0, 500, 0, 500));
        controller.selectCell(2, 0, false, false);
        fakeRightArrowPress();
        assertEquals(0, viewport.getMinX(), 0);
    }

    @Test
    public void testViewportIsNotAdjustedRightwardsWithMouseClick() {
        setViewport(new Rectangle(0, 140, 0, 100));
        controller.handlePointerDown(rowHeaderCellWidth + 60, cellHeight + 5, Modifiers.NONE);
        assertEquals(0, viewport.getMinX(), 0);
    }

    @Test
    public void testViewportShouldNotBeAdjustedWhenMovingLeftAtLeftmostPositionWithArrowKey() {
        controller.selectCell(0, 0, false, false);
        fakeLeftArrowPress();
        assertEquals(0, viewport.getMinX(), 0);
    }

    @Test
    public void testViewportIsNotAdjustedHorizontallyWithArrowKey() {
        setViewport(new Rectangle(0, 300, 0, 300));
        controller.selectCell(2, 0, false, false);
        fakeRightArrowPress();
        assertEquals(0, viewport.getMinX(), 0);
        fakeLeftArrowPress();
        assertEquals(0, viewport.getMinX(), 0);
    }

    @Test
    public void testViewportIsAdjustedDownwardsWithArrowKey() {
        setViewport(new Rectangle(0, 300, 0, 100));
        controller.selectCell(1, 1, false, false);
        fakeDownArrowPress();
        assertNotEquals(0, viewport.getMinY(), 0);
    }

    @Test
    public void testViewportIsAdjustedDownwardsWithMouseClick() {
        controller.handlePointerDown(rowHeaderCellWidth + 10, cellHeight + 80, Modifiers.NONE);
        assertNotEquals(0, viewport.getMinY(), 0);
    }

    @Test
    public void testViewportIsNotAdjustedDownwardsWithArrowKey() {
        controller.selectCell(0, 0, false, false);
        fakeDownArrowPress();
        assertEquals(0, viewport.getMinY(), 0);
    }

    @Test
    public void testViewportIsNotAdjustedDownwardsWithMouseClick() {
        controller.handlePointerDown(rowHeaderCellWidth + 10, cellHeight + 30, Modifiers.NONE);
        assertEquals(0, viewport.getMinY(), 0);
    }

    @Test
    public void testViewportShouldNotBeAdjustedWhenMovingUpAtTopmostPositionWithArrowKey() {
        controller.selectCell(0, 2, false, false);
        fakeUpArrowPress();
        assertEquals(0, viewport.getMinY(), 0);
    }

    @Test
    public void testViewportIsNotAdjustedUpwardsWithArrowKey() {
        setViewport(new Rectangle(0, 120, 0, 200));
        controller.selectCell(2, 1, false, false);
        fakeDownArrowPress();
        double verticalScrollPosition = viewport.getMinY();
        fakeUpArrowPress();
        assertEquals(verticalScrollPosition, viewport.getMinY(), 0);
    }

    @Test
    public void testViewportIsAdjustedForRightmostColumnWithArrowKey() {
        controller.selectCell(0, 99, false, false);
        fakeRightArrowPress();
        assertNotEquals(0, viewport.getMinX(), 0);
    }

    @Test
    public void testViewportIsAdjustedForLowermostRowWithArrowKey() {
        controller.selectCell(99, 0, false, false);
        fakeDownArrowPress();
        assertNotEquals(0, viewport.getMinY(), 0);
    }

    @Test
    public void testTappingOnResizeRowWhileEverythingIsSelected() {
        // Tap of top left corner (select everything)
        controller.handlePointerDown(30, 30, Modifiers.NONE);
        controller.handlePointerUp(30, 30, Modifiers.NONE);

        try {
            // Tap on the edge of row 1 and 2 in the row header
            controller.handlePointerDown(30, cellHeight * 2, Modifiers.NONE);
            controller.handlePointerUp(30, cellHeight * 2, Modifiers.NONE);
        } catch (Exception exception) {
            fail("Tapping on the edge of row 1 and 2 in the row header caused exception: "
                    + exception);
        }
    }

    @Test
    public void testTappingOnResizeRowWhileRowIsSelected() {
        // Select row 1 and 2
        controller.selectRow(1, false, false);
        controller.selectRow(2, true, false);

        double initialSpreadsheetHeight = controller.getLayout().getTotalHeight();

        // Tap on the edge of row 1 and 2 in the row header
        controller.handlePointerDown(30, cellHeight * 2, Modifiers.NONE);
        controller.handlePointerUp(30, cellHeight * 2, Modifiers.NONE);

        assertEquals(initialSpreadsheetHeight, controller.getLayout().getTotalHeight(), 0.0);
    }

    @Test
    public void testMovingLeftSelectsOnlySingleCell() {
        controller.select(TabularRange.range(1, 1, 2, 1), false, false);
        fakeLeftArrowPress();
        assertTrue(controller.getLastSelection().contains(1, 1));
    }

    @Test
    public void testMovingDownSelectsOnlySingleCell() {
        controller.select(TabularRange.range(2, 3, 2, 3), false, false);
        fakeDownArrowPress();
        assertTrue(controller.getLastSelection().contains(3, 2));
    }

    @Test
    public void testMovingRightAndDownSelectsMultipleCells() {
        controller.select(TabularRange.range(1, 3, 1, 3), false, false);
        controller.handleKeyPressed(JavaKeyCodes.VK_RIGHT, "",
                new Modifiers(false, false, true, false));
        controller.handleKeyPressed(JavaKeyCodes.VK_DOWN, "",
                new Modifiers(false, false, true, false));
        assertEquals(4, controller.getLastSelection().getRange().getToColumn());
        assertEquals(4, controller.getLastSelection().getRange().getToRow());
    }

    @Test
    public void testInsertingColumnAppliesCorrectWidth() {
        runContextItemAt(1, 1, DELETE_COLUMN);
        controller.getLayout().setWidthForColumns(150, 1, 1);
        runContextItemAt(1, 1, ContextMenuItem.Identifier.INSERT_COLUMN_RIGHT);
        assertEquals("The inserted column should apply the width of the previously selected one!",
                150, controller.getLayout().getWidth(2), 0);
    }

    @Test
    public void testInsertingRowAppliesCorrectHeight() {
        runContextItemAt(1, 1, DELETE_ROW);
        controller.getLayout().setHeightForRows(80, 2, 2);
        runContextItemAt(2, 2, INSERT_ROW_BELOW);
        assertEquals("The inserted row should apply the width of the previously selected one!",
                80, controller.getLayout().getHeight(3), 0);
    }

    @Test
    public void testDeletingColumnResizesColumnWidth() {
        controller.getLayout().setWidthForColumns(150, 2, 2);
        controller.getLayout().setWidthForColumns(200, 3, 3);
        runContextItemAt(2, 2, DELETE_COLUMN);
        assertEquals("After deleting the 3rd column, it should change its width!",
                200, controller.getLayout().getWidth(2), 0);
    }

    @Test
    public void testInsertingColumnRightwardShouldChangeSelection() {
        runContextItemAt(2, 2, DELETE_COLUMN);
        selectCells(0, 1, 0, 1);
        runContextItemAt(0, 1, INSERT_COLUMN_RIGHT);
        assertTrue("The current selection should move when a column is inserted!",
                controller.getLastSelection().contains(0, 2));
        assertTrue("There should be no more than 1 cell selected!",
                controller.isOnlyCellSelected(0, 2));
    }

    @Test
    public void testInsertingColumnLeftwardShouldNotChangeSelection() {
        runContextItemAt(1, 1, DELETE_COLUMN);
        selectCells(1, 2, 1, 2);
        runContextItemAt(1, 2, INSERT_COLUMN_LEFT);
        assertTrue("The current selection should stay when a column is inserted to the left!",
                controller.getLastSelection().contains(1, 2));
        assertTrue("There should be no more than 1 cell selected!",
                controller.isOnlyCellSelected(1, 2));
    }

    @Test
    public void testInsertingRowBelowShouldChangeSelection() {
        runContextItemAt(2, 2, DELETE_ROW);
        selectCells(1, 1, 1, 1);
        runContextItemAt(1, 1, INSERT_ROW_BELOW);
        assertTrue("The current selection should move when a row is inserted below!",
                controller.getLastSelection().contains(2, 1));
        assertTrue("There should be no more than 1 cell selected!",
                controller.isOnlyCellSelected(2, 1));
    }

    @Test
    public void testInsertingRowAboveShouldNotChangeSelection() {
        runContextItemAt(2, 2, DELETE_ROW);
        selectCells(2, 2, 2, 2);
        runContextItemAt(2, 2, INSERT_ROW_ABOVE);
        assertTrue("The current selection should stay when a row is inserted above!",
                controller.getLastSelection().contains(2, 2));
        assertTrue("There should be no more than 1 cell selected!",
                controller.isOnlyCellSelected(2, 2));
    }

    @Test
    public void testDeletingRowResizesRowHeight() {
        controller.getLayout().setHeightForRows(80, 1, 1);
        controller.getLayout().setHeightForRows(120, 2, 2);
        runContextItemAt(1, 1, DELETE_ROW);
        assertEquals("After deleting the 2nd row, it should change its height!",
                120, controller.getLayout().getHeight(1), 0);
    }

    @Test
    public void testRightClickingMultiCellSelectionShouldNotChangeSelection() {
        selectCells(0, 0, 1, 1);
        controller.handlePointerDown(rowHeaderCellWidth + 10, cellHeight + 10,
                new Modifiers(false, false, false, true));
        assertEquals(1, controller.getSelections().count());
		assertEquals(controller.getSelections().findFirst().get().getRange(),
                new TabularRange(0, 0, 1, 1));
    }

    private void runContextItemAt(int row, int column, Identifier identifier) {
        controller.getContextMenuItems().get(row, column).stream()
                .filter(item -> item.getIdentifier() == identifier)
                .findFirst().ifPresentOrElse(ContextMenuItem::performAction,
                        () -> fail("There was a problem performing this action!"));
    }

    @Test
    public void testRightClickingMultiRowAndColumnSelectionShouldChangeSelection() {
        controller.selectRow(0, false, false);
        controller.selectColumn(2, false, true);
        controller.handlePointerDown(rowHeaderCellWidth + 10, cellHeight + 10,
                new Modifiers(false, false, false, true));
        assertEquals(1, controller.getSelections().count());
        assertEquals(controller.getSelections().findFirst().get().getRange(),
                new TabularRange(0, 0, 0, 0));
    }

    // Cell editing

    @Test
    public void testActivateCellEditorByClickOnSelectedCell() {
        controller.selectCell(0, 0, false, false);
        simulateCellMouseClick(0, 0, 1);
        assertTrue(controller.isEditorActive());
    }

    @Test
    public void testActivateCellEditorByDoubleClickingCell() {
        simulateCellMouseClick(0, 0, 2);
        assertTrue(controller.isEditorActive());
    }

    @Test
    public void testActivateCellEditorByTypingInSelectedCell() {
        controller.selectCell(0, 0, false, false);
        controller.handleKeyPressed(JavaKeyCodes.VK_EQUALS, "=", Modifiers.NONE);
        assertTrue(controller.isEditorActive());
    }

    @Test
    public void testReturnCommitsCellEditorChanges() {
        tabularData.setContent(0, 0, "1");
        simulateCellMouseClick(0, 0, 2);
        assertTrue(controller.isEditorActive());
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_0);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_ENTER);
        assertFalse(controller.isEditorActive());
        assertEquals("10", tabularData.contentAt(0, 0));
    }

    @Test
    public void testTabCommitsCellEditorChanges() {
        tabularData.setContent(0, 0, "1");
        simulateCellMouseClick(0, 0, 2);
        assertTrue(controller.isEditorActive());
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_0);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_TAB);
        assertFalse(controller.isEditorActive());
        assertEquals("10", tabularData.contentAt(0, 0));
    }

    @Test
    public void testCancelDoesntCommitCellEditorChanges() {
        tabularData.setContent(0, 0, "1");
        simulateCellMouseClick(0, 0, 2);
        assertTrue(controller.isEditorActive());
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_0);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_ESCAPE);
        assertFalse(controller.isEditorActive());
        assertEquals("1", tabularData.contentAt(0, 0));
    }

    @Test
    public void arrowsShouldExtendDataSize() {
        controller.selectCell(99, 99, false, false);
        fakeRightArrowPress();
        fakeRightArrowPress();
        fakeDownArrowPress();
        assertEquals(101, tabularData.numberOfRows());
        assertEquals(102, tabularData.numberOfColumns());
    }

    @Test
    public void deleteShouldRemoveCells() {
        tabularData.setContent(1, 3, "1");
        selectCells(1, 3, 1, 3);
        controller.handleKeyPressed(JavaKeyCodes.VK_DELETE, "", Modifiers.NONE);
        assertNull(tabularData.contentAt(1, 3));
    }

    // Helpers

    private void setViewport(Rectangle viewport) {
        this.viewport = viewport;
        controller.setViewport(viewport);
    }

    private void fakeLeftArrowPress() {
        controller.handleKeyPressed(37, "", Modifiers.NONE);
    }

    private void fakeUpArrowPress() {
        controller.handleKeyPressed(38, "", Modifiers.NONE);
    }

    private void fakeRightArrowPress() {
        controller.handleKeyPressed(39, "", Modifiers.NONE);
    }

    private void fakeDownArrowPress() {
        controller.handleKeyPressed(40, "", Modifiers.NONE);
    }

    private void simulateCellMouseClick(int row, int column, int nrClicks) {
        TableLayout layout = controller.getLayout();
        Rectangle cellBounds = layout.getBounds(row, column)
                .translatedBy(layout.getRowHeaderWidth(), layout.getColumnHeaderHeight());
        double midX = cellBounds.getMinX() + cellBounds.getWidth() / 2;
        double midY = cellBounds.getMinY() + cellBounds.getWidth() / 2;
        for (int click = 0; click < nrClicks; click++) {
            controller.handlePointerDown((int) midX, (int) midY, Modifiers.NONE);
            controller.handlePointerUp((int) midX, (int) midY, Modifiers.NONE);
        }
    }

    /**
     * Simulates a key press in the cell editor's underlying MathField.
     * Note: Depending on the key code, onKeyTyped needs to be invoked or not. Currently,
     * only the ranges a..z, A..Z, 0..9 will trigger onKeyTyped. You may need to extend
     * these ranges.
     * @param keyCode See {@link JavaKeyCodes}
     */
    private void simulateKeyPressInCellEditor(int keyCode) {
        MathFieldInternal mathField = cellEditor.getMathField();
        KeyEvent keyEvent = new KeyEvent(keyCode, 0, (char) keyCode);
        mathField.onKeyPressed(keyEvent);
        if ((keyCode >= JavaKeyCodes.VK_A && keyCode <= JavaKeyCodes.VK_Z)
            || (keyCode >= JavaKeyCodes.VK_0 && keyCode <= JavaKeyCodes.VK_9)) {
            mathField.onKeyTyped(keyEvent);
        }
        mathField.onKeyReleased(keyEvent);
    }

    private void selectCells(int fromRow, int fromColumn, int toRow, int toColumn) {
        controller.select(new TabularRange(fromRow, fromColumn, toRow, toColumn), false, false);
    }

    // SpreadsheetControlsDelegate

    @Override
    public SpreadsheetCellEditor getCellEditor() {
        return cellEditor;
    }

    @Override
    public void showContextMenu(List<ContextMenuItem> actions, GPoint coords) {
        // Not needed
    }

    @Override
    public void hideContextMenu() {
        // Not needed
    }

    @Override
    public ClipboardInterface getClipboard() {
        return clipboard;
    }
}
