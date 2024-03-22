package org.geogebra.common.spreadsheet.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.event.KeyEvent;

import org.geogebra.common.spreadsheet.TestTabularData;
import org.geogebra.common.util.shape.Rectangle;
import org.junit.Before;
import org.junit.Test;

public class SpreadsheetControllerTest {
    private final int cellHeight = TableLayout.DEFAUL_CELL_HEIGHT;
    private final int rowHeaderCellWidth = TableLayout.DEFAULT_ROW_HEADER_WIDTH;

    private final SpreadsheetController controller =
            new SpreadsheetController(new TestTabularData(), null);
    private Rectangle viewport;

    @Before
    public void setup() {
        controller.getLayout().setHeightForRows(cellHeight, 0, 5);
        controller.getLayout().setWidthForColumns(40, 0, 5);
        setViewport(new Rectangle(0, 100, 0, 120));
        controller.setViewportAdjustmentHandler(new ViewportAdjustmentHandler() {
            @Override
            public void setScrollPosition(int x, int y) {
                viewport = viewport.translatedBy(x, y);
            }

            @Override
            public int getScrollBarWidth() {
                return 5;
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
        KeyEvent rightArrow = fakeKeyEvent(39);
        controller.handleKeyPressed(rightArrow.getKeyCode(), rightArrow.getKeyChar() + "",
                new Modifiers(false, false, true, false));
        KeyEvent downArrow = fakeKeyEvent(40);
        controller.handleKeyPressed(downArrow.getKeyCode(), downArrow.getKeyChar() + "",
                new Modifiers(false, false, true, false));
        assertEquals(4, controller.getLastSelection().getRange().getToColumn());
        assertEquals(4, controller.getLastSelection().getRange().getToRow());
    }

    private void setViewport(Rectangle viewport) {
        this.viewport = viewport;
        controller.setViewport(viewport);
    }

    private void fakeLeftArrowPress() {
        KeyEvent e = fakeKeyEvent(37);
        controller.handleKeyPressed(e.getKeyCode(), e.getKeyChar() + "", Modifiers.NONE);
    }

    private void fakeUpArrowPress() {
        KeyEvent e = fakeKeyEvent(38);
        controller.handleKeyPressed(e.getKeyCode(), e.getKeyChar() + "", Modifiers.NONE);
    }

    private void fakeRightArrowPress() {
        KeyEvent e = fakeKeyEvent(39);
        controller.handleKeyPressed(e.getKeyCode(), e.getKeyChar() + "", Modifiers.NONE);
    }

    private void fakeDownArrowPress() {
        KeyEvent e = fakeKeyEvent(40);
        controller.handleKeyPressed(e.getKeyCode(), e.getKeyChar() + "", Modifiers.NONE);
    }

    private KeyEvent fakeKeyEvent(int keyCode) {
        KeyEvent event = mock(KeyEvent.class);
        when(event.getKeyCode()).thenReturn(keyCode);
        when(event.getKeyChar()).thenReturn(' ');
        return event;
    }
}
