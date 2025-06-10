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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.io.FactoryProviderCommon;
import org.geogebra.common.jre.factory.FormatFactoryJre;
import org.geogebra.common.jre.util.UtilFactoryJre;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.spreadsheet.TestTabularData;
import org.geogebra.common.spreadsheet.kernel.ChartBuilder;
import org.geogebra.common.spreadsheet.settings.SpreadsheetSettingsAdapter;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.geogebra.common.util.shape.Point;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.common.util.shape.Size;
import org.geogebra.test.annotation.Issue;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class SpreadsheetControllerTest implements SpreadsheetControlsDelegate,
        SpreadsheetConstructionDelegate {

    private SpreadsheetController controller;
    private TabularData<String> tabularData;
    private SpreadsheetStyling spreadsheetStyling;
    private SpreadsheetCellEditor cellEditor;
    private TableLayout layout;
    private ClipboardInterface clipboard;
    private Rectangle viewport;
    private boolean autoCompleteShown = false;
    private String autoCompleteSearchPrefix = "";
    private final List<Integer> receivedKeys = new ArrayList<>();
    private String chartCommand = "";
    private String chartError = "";

    @BeforeClass
    public static void setupOnce() {
        // required by MathField
        FactoryProvider.setInstance(new FactoryProviderCommon());
        // required by StringTemplate static initializer
        FormatFactory.setPrototypeIfNull(new FormatFactoryJre());
    }

    @BeforeClass
    public static void enablePreviewFeatures() {
        PreviewFeature.setPreviewFeaturesEnabled(true);
    }

    @AfterClass
    public static void disablePreviewFeatures() {
        PreviewFeature.setPreviewFeaturesEnabled(false);
    }

    @Before
    public void setup() {
        tabularData = new TestTabularData();
        spreadsheetStyling = new SpreadsheetStyling();
        clipboard = new TestClipboard();
        cellEditor = new TestSpreadsheetCellEditor(tabularData);

        controller = new SpreadsheetController(tabularData, spreadsheetStyling);
        controller.setControlsDelegate(this);
        controller.setSpreadsheetConstructionDelegate(this);
        layout = controller.getLayout();
        setViewport(new Rectangle(0,
                layout.getRowHeaderWidth() + layout.defaultColumnWidth * 2.5,
                0,
                layout.getColumnHeaderHeight() + layout.defaultRowHeight * 2.5));
        controller.setViewportAdjustmentHandler(new ViewportAdjusterDelegate() {
            @Override
            public void setScrollPosition(double x, double y) {
                viewport = viewport.translatedBy(x, y);
            }

            @Override
            public double getScrollBarWidth() {
                return 5;
            }

            @Override
            public void updateScrollableContentSize(Size size) {
                // not needed
            }
        });
        UtilFactoryJre.setupRegexFactory();
    }

    @Test
    public void testViewportIsAdjustedRightwardsWithArrowKey() {
        controller.selectCell(1, 1, false, false);
        fakeRightArrowPress();
        assertNotEquals(0, viewport.getMinX(), 0);
    }

    @Test
    public void testViewportIsAdjustedRightwardsWithMouseClick() {
        setViewport(new Rectangle(0,
                layout.getRowHeaderWidth() + layout.defaultColumnWidth + 10,
                0,
                layout.getColumnHeaderHeight() + layout.defaultRowHeight + 10));
        controller.handlePointerDown(layout.getRowHeaderWidth() + layout.defaultColumnWidth + 10,
                layout.getColumnHeaderHeight() + 10, Modifiers.NONE);
        assertNotEquals(0, viewport.getMinX(), 0);
    }

    @Test
    public void testViewportIsNotAdjustedRightwardsWithArrowKey() {
        setViewport(new Rectangle(0,
                layout.getRowHeaderWidth() + layout.defaultColumnWidth * 3 + 10,
                0,
                layout.getColumnHeaderHeight() + layout.defaultRowHeight + 10));
        controller.selectCell(2, 0, false, false);
        fakeRightArrowPress();
        assertEquals(0, viewport.getMinX(), 0);
    }

    @Test
    public void testViewportIsNotAdjustedRightwardsWithMouseClick() {
        setViewport(new Rectangle(0,
                layout.getRowHeaderWidth() + layout.defaultColumnWidth + 10,
                0,
                layout.getColumnHeaderHeight() + layout.defaultRowHeight + 10));
        controller.handlePointerDown(layout.getRowHeaderWidth() + 10,
                layout.getColumnHeaderHeight() + 5, Modifiers.NONE);
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
        setViewport(new Rectangle(0,
                layout.getRowHeaderWidth() + 2 * layout.defaultColumnWidth + 10,
                0,
                layout.getColumnHeaderHeight() + layout.defaultRowHeight + 10));
        controller.selectCell(2, 0, false, false);
        fakeRightArrowPress();
        assertEquals(0, viewport.getMinX(), 0);
        fakeLeftArrowPress();
        assertEquals(0, viewport.getMinX(), 0);
    }

    @Test
    public void testViewportIsAdjustedDownwardsWithArrowKey() {
        setViewport(new Rectangle(0,
                layout.getRowHeaderWidth() + layout.defaultColumnWidth + 10,
                0,
                layout.getColumnHeaderHeight() + layout.defaultRowHeight + 10));
        controller.selectCell(1, 1, false, false);
        fakeDownArrowPress();
        assertNotEquals(0, viewport.getMinY(), 0);
    }

    @Test
    public void testViewportIsAdjustedDownwardsWithMouseClick() {
        controller.handlePointerDown(layout.getRowHeaderWidth() + 10,
                layout.getColumnHeaderHeight() + 80, Modifiers.NONE);
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
        controller.handlePointerDown(layout.getRowHeaderWidth() + 10,
                layout.getColumnHeaderHeight() + 30, Modifiers.NONE);
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
        setViewport(new Rectangle(0,
                layout.getRowHeaderWidth() + layout.defaultColumnWidth * 2 + 10,
                0,
                layout.getColumnHeaderHeight() + layout.defaultRowHeight * 3 + 10));
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
        double x = layout.getRowHeaderWidth() / 2;
        double y = layout.getRowHeaderWidth() / 2;
        controller.handlePointerDown(x, y, Modifiers.NONE);
        controller.handlePointerUp(x, y, Modifiers.NONE);

        try {
            // Tap on the edge of row 1 and 2 in the row header
            y = layout.getColumnHeaderHeight() + layout.defaultRowHeight;
            controller.handlePointerDown(x, y, Modifiers.NONE);
            controller.handlePointerUp(x, y, Modifiers.NONE);
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
        double x = layout.getRowHeaderWidth() / 2;
        double y = layout.getColumnHeaderHeight() + layout.defaultRowHeight;
        controller.handlePointerDown(x, y, Modifiers.NONE);
        controller.handlePointerUp(x, y, Modifiers.NONE);

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
        controller.handlePointerDown(layout.getRowHeaderWidth() + 10,
                layout.getColumnHeaderHeight() + 10,
                new Modifiers(false, false, false, true));
        assertEquals(1, controller.getSelections().count());
		assertEquals(controller.getSelections().findFirst().get().getRange(),
                new TabularRange(0, 0, 1, 1));
    }

    private void runContextItemAt(int row, int column, Identifier id) {
        ContextMenuItem contextMenuItem = new ContextMenuBuilder(controller).build(row, column)
                .stream().filter(item -> item.getIdentifier().equals(id)).findAny().orElse(null);
        if (contextMenuItem == null) {
            fail("No such menu item at (" + row + ", " + column + "): " + id);
        }
        if (!(contextMenuItem instanceof ContextMenuItem.ActionableItem)) {
            fail("Item at (" + row + ", " + column + "): " + id + " is not runnable");
        }
        ((ContextMenuItem.ActionableItem) contextMenuItem).performAction();
    }

    @Test
    public void testRightClickingMultiRowAndColumnSelectionShouldChangeSelection() {
        controller.selectRow(0, false, false);
        controller.selectColumn(2, false, true);
        controller.handlePointerDown(layout.getRowHeaderWidth() + 10,
                layout.getColumnHeaderHeight() + 10,
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
    public void testReturnCommitsCellEditorChangesAndMovesSelection() {
        tabularData.setContent(0, 0, "1");
        simulateCellMouseClick(0, 0, 2);
        assertTrue(controller.isEditorActive());
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_0);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_ENTER);
        assertEquals("10", tabularData.contentAt(0, 0));
        assertTrue(controller.isEditorActive());
        assertTrue(controller.isOnlyCellSelected(1, 0));
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
    public void testEscapeDoesntCommitCellEditorChanges() {
        tabularData.setContent(0, 0, "1");
        simulateCellMouseClick(0, 0, 2);
        assertTrue(controller.isEditorActive());
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_0);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_ESCAPE);
        assertFalse(controller.isEditorActive());
        assertEquals("1", tabularData.contentAt(0, 0));
    }

    @Test
    public void testTypingOverwritesExistingContent() {
        tabularData.setContent(0, 0, "123");
        simulateCellMouseClick(0, 0, 1);
        controller.handleKeyPressed(JavaKeyCodes.VK_EQUALS, "=", Modifiers.NONE);
        assertTrue(controller.isEditorActive());
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_ENTER); // commit input
        assertEquals("=", tabularData.contentAt(0, 0));
    }

    @Test
    public void testEscapeAfterTypinRestoresOriginalContent() {
        tabularData.setContent(0, 0, "123");
        simulateCellMouseClick(0, 0, 1);
        controller.handleKeyPressed(JavaKeyCodes.VK_EQUALS, "=", Modifiers.NONE);
        assertTrue(controller.isEditorActive());
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_ESCAPE); // discard input
        assertEquals("123", tabularData.contentAt(0, 0));
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

    @Test
    public void testCalculatePartOfColumn() {
        tabularData.setContent(0, 0, "1");
        tabularData.setContent(1, 0, "2");
        tabularData.setContent(2, 0, "3");
        selectCells(0, 0, 2, 0);
        controller.calculate(SpreadsheetCommand.MEAN);

        assertEquals("=mean(A1:A3)", tabularData.contentAt(3, 0));
    }

    @Test
    public void testCalculateMoreColumns() {
        tabularData.setContent(0, 0, "1");
        tabularData.setContent(1, 0, "2");
        tabularData.setContent(2, 0, "3");
        tabularData.setContent(1, 1, "4");
        tabularData.setContent(2, 2, "5");
        selectCells(0, 0, 2, 2);
        controller.calculate(SpreadsheetCommand.SUM);

        assertNull(tabularData.contentAt(3, 0));
        assertNull(tabularData.contentAt(3, 1));
        assertEquals("=Sum(A1:C3)", tabularData.contentAt(3, 2));
    }

    @Test
    public void testCalculateWholeColumn() {
        tabularData.setContent(0, 0, "1");
        tabularData.setContent(1, 0, "2");
        tabularData.setContent(2, 0, "3");
        selectCells(0, 0, tabularData.numberOfRows() - 2, 0);
        controller.calculate(SpreadsheetCommand.SUM);

        assertEquals("=Sum(A1:A99)", tabularData.contentAt(tabularData.numberOfRows() - 1, 0));
    }

    @Test
    public void testCalculatePartOfRow() {
        tabularData.setContent(0, 0, "1");
        tabularData.setContent(0, 1, "2");
        tabularData.setContent(0, 2, "3");
        selectCells(0, 0, 0, 2);
        controller.calculate(SpreadsheetCommand.MEAN);

        assertEquals("=mean(A1:C1)", tabularData.contentAt(0, 3));
    }

    @Test
    public void testThreeCharactersShowAutoCompleteSuggestions() {
        tabularData.setContent(0, 0, "=SU");
        simulateCellMouseClick(0, 0, 2);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_M);

        assertTrue(autoCompleteShown);
        assertEquals(autoCompleteSearchPrefix, "SUM");
    }

    @Test
    public void testDeleteFromThreeCharactersShouldHideAutoCompleteSuggestions() {
        tabularData.setContent(0, 0, "=SU");
        simulateCellMouseClick(0, 0, 2);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_M);
        assertTrue(autoCompleteShown);
        assertEquals(autoCompleteSearchPrefix, "SUM");

        simulateKeyPressInCellEditor(JavaKeyCodes.VK_BACK_SPACE);
        assertFalse(autoCompleteShown);
    }

    @Test
    public void testShowAutoCompleteSuggestionOnlyOnThirdLetter() {
        simulateCellMouseClick(0, 0, 2);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_EQUALS);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_S);
        assertFalse(autoCompleteShown);

        simulateKeyPressInCellEditor(JavaKeyCodes.VK_U);
        assertFalse(autoCompleteShown);

        simulateKeyPressInCellEditor(JavaKeyCodes.VK_M);
        assertTrue(autoCompleteShown);
        assertEquals(autoCompleteSearchPrefix, "SUM");
    }

    @Test
    public void testAutoCompleteArrowShouldNotMoveCursorOrChangeSelectedCell() {
        tabularData.setContent(0, 0, "=CO");
        simulateCellMouseClick(0, 0, 2);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_E);
        int cursorPosBeforeArrows = getCellEditor().getMathField().getEditorState()
                .getCurrentOffset();
        int selectedRow = getSelectedRow();
        int selectedColumn = getSelectedColumn();

        simulateKeyPressInCellEditor(JavaKeyCodes.VK_LEFT);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_DOWN);
        int cursorPosAfterArrows = getCellEditor().getMathField().getEditorState()
                .getCurrentOffset();

        assertEquals(cursorPosBeforeArrows, cursorPosAfterArrows);
        assertEquals(selectedRow, getSelectedRow());
        assertEquals(selectedColumn, getSelectedColumn());
        assertEquals(List.of(JavaKeyCodes.VK_LEFT, JavaKeyCodes.VK_DOWN), receivedKeys);
    }

    @Test
    public void testDismissAutoCompleteSuggestionsOnCellSelectionChange() {
        simulateCellMouseClick(0, 0, 2);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_EQUALS);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_S);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_U);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_M);
        assertTrue(autoCompleteShown);

        simulateCellMouseClick(0, 1, 1);
        assertFalse(autoCompleteShown);
    }

    @Test
    public void testDismissAutoCompleteSuggestionsOnEscape() {
        simulateCellMouseClick(0, 0, 2);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_EQUALS);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_S);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_U);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_M);
        assertTrue(autoCompleteShown);

        simulateKeyPressInCellEditor(JavaKeyCodes.VK_ESCAPE);
        assertFalse(autoCompleteShown);
    }
    
    @Test
    public void testInsertingCellReferenceByClick() {
        tabularData.setContent(0, 0, "=2");
        simulateCellMouseClick(1, 0, 2);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_EQUALS);
        simulateCellMouseClick(0, 0, 1);
        assertEquals("=A1", cellEditor.getMathField().getText());
    }

    @Test
    public void testInsertingCellReferenceByClickWithText() {
        tabularData.setContent(0, 0, "=2");
        simulateCellMouseClick(1, 0, 2);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_EQUALS);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_P);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_I);
        simulateCellMouseClick(0, 0, 1);
        assertEquals("=PI A1", cellEditor.getMathField().getText());
    }

    @Test
    public void testInsertingCellReferenceByDrag() {
        tabularData.setContent(0, 0, "=2");
        simulateCellMouseClick(1, 0, 2);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_EQUALS);
        Point center = getCenter(0, 0);
        controller.handlePointerDown(center.x, center.y, Modifiers.NONE);
        center = getCenter(0, 1);
        controller.handlePointerMove(center.x, center.y, Modifiers.NONE);
        center = getCenter(0, 2);
        controller.handlePointerMove(center.x, center.y, Modifiers.NONE);
        center = getCenter(0, 3);
        controller.handlePointerUp(center.x, center.y, Modifiers.NONE);
        assertEquals("=A1:D1", cellEditor.getMathField().getText());
    }

    @Test
    public void testInsertingCellReferenceByDragBackwards() {
        setViewport(new Rectangle(0, 500, 0, 500));
        tabularData.setContent(0, 3, "=2");
        simulateCellMouseClick(1, 0, 2);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_EQUALS);
        Point center = getCenter(0, 3);
        controller.handlePointerDown(center.x, center.y, Modifiers.NONE);
        center = getCenter(0, 2);
        controller.handlePointerMove(center.x, center.y, Modifiers.NONE);
        center = getCenter(0, 1);
        controller.handlePointerMove(center.x, center.y, Modifiers.NONE);
        center = getCenter(0, 0);
        controller.handlePointerUp(center.x, center.y, Modifiers.NONE);
        assertEquals("=A1:D1", cellEditor.getMathField().getText());
    }

    @Test
    public void testPieChartValidDataHasNoError() {
        tabularData.setContent(0, 0, "1");
        tabularData.setContent(1, 0, "2");
        tabularData.setContent(2, 0, "3");
        selectCells(0, 0, 2, 0);
        controller.createChart(Identifier.PIE_CHART);
        assertEquals("PieChart(A1:A3,(0,0))", chartCommand);
        assertEquals("", chartError);
    }

    @Test
    public void testPieChartSkipsInvalidData() {
        tabularData.setContent(0, 0, "1");
        tabularData.setContent(1, 0, "2");
        tabularData.setContent(2, 0, "b");
        tabularData.setContent(3, 0, "3");
        tabularData.setContent(4, 0, "");
        tabularData.setContent(5, 0, "a");
        selectCells(0, 0, 5, 0);
        controller.createChart(Identifier.PIE_CHART);
        assertEquals("PieChart(A1:A6,(0,0))", chartCommand);
        assertEquals("", chartError);
    }

    @Test
    public void testPieChartNotEnoughDataError() {
        tabularData.setContent(0, 0, "1");
        selectCells(0, 0, 0, 0);
        controller.createChart(Identifier.PIE_CHART);
		assertEquals("", chartCommand);
        assertEquals("StatsDialog.NoData", chartError);
    }

    @Test
    public void testPieChartMultipleColumnError() {
        tabularData.setContent(0, 0, "1");
        tabularData.setContent(0, 1, "2");
        selectCells(0, 0, 0, 1);
        controller.createChart(Identifier.PIE_CHART);
        assertEquals("", chartCommand);
        assertEquals("ChartError.OneColumn", chartError);
    }

    @Test
    public void testBarChartValidDataHasNoError() {
        tabularData.setContent(0, 0, "1");
        tabularData.setContent(1, 0, "2");
        tabularData.setContent(2, 0, "3");
        tabularData.setContent(0, 1, "4");
        tabularData.setContent(1, 1, "5");
        tabularData.setContent(2, 1, "6");
        selectCells(0, 0, 2, 1);
        controller.createChart(Identifier.BAR_CHART);
        assertEquals("BarChart(A1:A3,B1:B3)", chartCommand);
        assertEquals("", chartError);
    }

    @Test
    public void testBarChartNotEnoughDataError() {
        tabularData.setContent(0, 0, "1");
        selectCells(0, 0, 0, 0);
        controller.createChart(Identifier.BAR_CHART);
        assertEquals("", chartCommand);
        assertEquals("StatsDialog.NoData", chartError);
    }

    @Test
    public void testBarChartTwoColumnNeededError() {
        tabularData.setContent(0, 0, "1");
        tabularData.setContent(0, 1, "2");
        tabularData.setContent(0, 2, "3");
        selectCells(0, 0, 0, 2);
        controller.createChart(Identifier.BAR_CHART);
        assertEquals("", chartCommand);
        assertEquals("ChartError.TwoColumns", chartError);
    }

    @Test
    public void testHistogramValidDataHasNoError() {
        tabularData.setContent(0, 0, "1");
        tabularData.setContent(1, 0, "2");
        tabularData.setContent(2, 0, "3");
        tabularData.setContent(3, 0, "4");
        tabularData.setContent(0, 1, "4");
        tabularData.setContent(1, 1, "5");
        tabularData.setContent(2, 1, "6");
        selectCells(0, 0, 3, 1);
        controller.createChart(Identifier.HISTOGRAM);
        assertEquals("Histogram(A1:A4,B1:B4)", chartCommand);
        assertEquals("", chartError);
    }

    @Test
    public void testHistogramNotEnoughDataError() {
        tabularData.setContent(0, 0, "1");
        selectCells(0, 0, 0, 0);
        controller.createChart(Identifier.HISTOGRAM);
        assertEquals("", chartCommand);
        assertEquals("StatsDialog.NoData", chartError);
    }

    @Test
    public void testHistogramTwoColumnNeededError() {
        tabularData.setContent(0, 0, "1");
        tabularData.setContent(0, 1, "2");
        tabularData.setContent(0, 2, "3");
        selectCells(0, 0, 0, 2);
        controller.createChart(Identifier.HISTOGRAM);
        assertEquals("", chartCommand);
        assertEquals("ChartError.TwoColumns", chartError);
    }

    @Test
    public void testLineGraphValidDataHasNoError() {
        tabularData.setContent(0, 0, "1");
        tabularData.setContent(1, 0, "2");
        tabularData.setContent(2, 0, "3");
        tabularData.setContent(0, 1, "4");
        tabularData.setContent(1, 1, "5");
        tabularData.setContent(2, 1, "6");
        selectCells(0, 0, 2, 1);
        controller.createChart(Identifier.LINE_CHART);
        assertEquals("LineGraph(A1:A3,B1:B3)", chartCommand);
        assertEquals("", chartError);
    }

    @Test
    public void testLineGraphNotEnoughDataError() {
        tabularData.setContent(0, 0, "1");
        selectCells(0, 0, 0, 0);
        controller.createChart(Identifier.LINE_CHART);
        assertEquals("", chartCommand);
        assertEquals("StatsDialog.NoData", chartError);
    }

    @Test
    public void testLineGraphTwoColumnNeededError() {
        tabularData.setContent(0, 0, "1");
        tabularData.setContent(0, 1, "2");
        tabularData.setContent(0, 2, "3");
        selectCells(0, 0, 2, 0);
        controller.createChart(Identifier.LINE_CHART);
        assertEquals("", chartCommand);
        assertEquals("ChartError.TwoColumns", chartError);
    }

    @Test
    public void testMultipleLineGraphForMultipleColumn() {
        tabularData.setContent(0, 0, "1");
        tabularData.setContent(1, 0, "2");
        tabularData.setContent(2, 0, "3");
        tabularData.setContent(0, 1, "4");
        tabularData.setContent(1, 1, "5");
        tabularData.setContent(2, 1, "6");
        tabularData.setContent(0, 2, "7");
        tabularData.setContent(1, 2, "8");
        tabularData.setContent(2, 2, "9");
        tabularData.setContent(0, 3, "10");
        tabularData.setContent(1, 3, "11");
        tabularData.setContent(2, 3, "12");
        selectCells(0, 0, 2, 3);
        controller.createChart(Identifier.LINE_CHART);
        assertEquals("LineGraph(A1:A3,B1:B3)LineGraph(A1:A3,C1:C3)LineGraph(A1:A3,D1:D3)",
                chartCommand);
        assertEquals("", chartError);
    }

    @Test
    @Issue("APPS-6533")
    public void testEmptyInputInCellWithoutContent() {
        simulateCellMouseClick(0, 0, 2);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_ENTER);
        assertNull(controller.contentAt(0, 0));
    }

    @Test
    @Issue("APPS-6533")
    public void testEmptyInputInCellWithContent() {
        simulateCellMouseClick(0, 0, 2);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_1);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_ENTER);
        assertNotNull(controller.contentAt(0, 0));

        simulateCellMouseClick(0, 0, 2);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_BACK_SPACE);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_BACK_SPACE);
        simulateKeyPressInCellEditor(JavaKeyCodes.VK_ENTER);
        assertNull(controller.contentAt(0, 0));
    }

    @Test
    @Issue("APPS-6533")
    public void testClickAwayFromEmptyCellShouldntCreateContent() {
        simulateCellMouseClick(0, 0, 2);
        simulateCellMouseClick(0, 1, 1);
        assertNull(controller.contentAt(0, 0));
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
        Point center = getCenter(row, column);
        for (int click = 0; click < nrClicks; click++) {
            controller.handlePointerDown(center.x, center.y, Modifiers.NONE);
            controller.handlePointerUp(center.x, center.y, Modifiers.NONE);
        }
    }

    private Point getCenter(int row, int column) {
        TableLayout layout = controller.getLayout();
        Rectangle cellBounds = layout.getBounds(row, column)
                .translatedBy(layout.getRowHeaderWidth(), layout.getColumnHeaderHeight());
        return new Point(cellBounds.getMinX() + cellBounds.getWidth() / 2,
            cellBounds.getMinY() + cellBounds.getHeight() / 2);
    }

    /**
     * Simulates a key press in the cell editor's underlying MathField.
     * Note: Depending on the key code, onKeyTyped needs to be invoked or not. Currently,
     * only the ranges "a".."z", "A".."Z", "0".."9", as well as the symbol "=" will trigger
     * onKeyTyped. You may need to extend these ranges and symbols.
     * @param keyCode See {@link JavaKeyCodes}
     */
    private void simulateKeyPressInCellEditor(int keyCode) {
        MathFieldInternal mathField = cellEditor.getMathField();
        KeyEvent keyEvent = new KeyEvent(keyCode, 0, (char) keyCode);
        mathField.onKeyPressed(keyEvent);
        if ((keyCode >= JavaKeyCodes.VK_A && keyCode <= JavaKeyCodes.VK_Z)
            || (keyCode >= JavaKeyCodes.VK_0 && keyCode <= JavaKeyCodes.VK_9)
            || keyCode == JavaKeyCodes.VK_EQUALS) {
            mathField.onKeyTyped(keyEvent);
        }
        mathField.onKeyReleased(keyEvent);
    }

    private void selectCells(int fromRow, int fromColumn, int toRow, int toColumn) {
        controller.select(new TabularRange(fromRow, fromColumn, toRow, toColumn), false, false);
    }

    private int getSelectedRow() {
        if (controller.getLastSelection() != null) {
            TabularRange range = controller.getLastSelection().getRange();
            return range.getFromRow();
        }
        return -1;
    }

    private int getSelectedColumn() {
        if (controller.getLastSelection() != null) {
            TabularRange range = controller.getLastSelection().getRange();
            return range.getFromColumn();
        }
        return -1;
    }

    // SpreadsheetControlsDelegate

    @Override
    public @Nonnull SpreadsheetCellEditor getCellEditor() {
        return cellEditor;
    }

    @Override
    public void showContextMenu(@Nonnull List<ContextMenuItem> items, @Nonnull Point point) {
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

    @Override
    public void showAutoCompleteSuggestions(@Nonnull String input,
            @Nonnull Rectangle editorBounds) {
        autoCompleteShown = true;
        autoCompleteSearchPrefix = input;
    }

    @Override
    public void hideAutoCompleteSuggestions() {
        autoCompleteShown = false;
        receivedKeys.clear();
    }

    @Override
    public boolean isAutoCompleteSuggestionsVisible() {
        return autoCompleteShown;
    }

    @Override
    public boolean handleKeyPressForAutoComplete(int keyCode) {
        if (autoCompleteShown && (keyCode == JavaKeyCodes.VK_LEFT
                || keyCode == JavaKeyCodes.VK_RIGHT || keyCode == JavaKeyCodes.VK_UP
                || keyCode == JavaKeyCodes.VK_DOWN || keyCode == JavaKeyCodes.VK_ENTER
                || keyCode == JavaKeyCodes.VK_ESCAPE)) {
            receivedKeys.add(keyCode);
            return true;
        }
        return false;
    }

    @Override
    public void showSnackbar(@Nonnull String messageKey) {
        this.chartError = messageKey;
    }

    @Override
    public void createPieChart(@Nonnull TabularData<?> data, @Nonnull TabularRange range) {
        chartCommand = ChartBuilder.getPieChartCommand(data, range);
    }

    @Override
    public void createBarChart(@Nonnull TabularData<?> data, @Nonnull TabularRange range) {
        chartCommand = ChartBuilder.getBarChartCommand(data, range);
    }

    @Override
    public void createHistogram(@Nonnull TabularData<?> data, @Nonnull TabularRange range) {
        chartCommand = ChartBuilder.getHistogramCommand(data, range);
    }

    @Override
    public void createLineGraph(TabularData<?> data, TabularRange range) {
        StringBuilder strBuilder = new StringBuilder();
        for (int col = range.getFromColumn() + 1; col <= range.getToColumn(); col++) {
            strBuilder.append(ChartBuilder.getLineGraphCommand(data, range, col));
        }

        chartCommand = strBuilder.toString();
    }
}
