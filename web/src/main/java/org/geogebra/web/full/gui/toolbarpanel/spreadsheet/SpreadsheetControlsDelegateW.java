package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.Localization;
import org.geogebra.common.spreadsheet.core.ClipboardInterface;
import org.geogebra.common.spreadsheet.core.ContextMenuItem;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellDataSerializer;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellEditor;
import org.geogebra.common.spreadsheet.core.SpreadsheetControlsDelegate;
import org.geogebra.common.spreadsheet.kernel.DefaultSpreadsheetCellDataSerializer;
import org.geogebra.common.spreadsheet.kernel.DefaultSpreadsheetCellProcessor;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.MathFieldEditor;
import org.geogebra.web.full.gui.view.probcalculator.MathTextFieldW;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.util.ClipboardW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.dom.style.shared.TextAlign;
import org.gwtproject.dom.style.shared.Unit;

import com.google.gwt.core.client.Scheduler;
import com.himamis.retex.editor.share.editor.MathFieldInternal;

public class SpreadsheetControlsDelegateW implements SpreadsheetControlsDelegate {

	private final SpreadsheetCellEditorW editor;
	private final GPopupMenuW contextMenu;
	private final Localization loc;
	private final static int CONTEXT_MENU_PADDING = 8;
	private final static int MARGIN_FROM_SCREEN_EDGE = 16;
	private final ClipboardInterface clipboard;

	private static class SpreadsheetCellEditorW implements SpreadsheetCellEditor {
		private final MathFieldEditor mathField;
		private final SpreadsheetPanel parent;
		private final AppW app;

		public SpreadsheetCellEditorW(AppW app, SpreadsheetPanel parent, MathTextFieldW mathField) {
			this.mathField = mathField;
			this.mathField.getMathField().setForegroundColor(
					GColor.getColorString(GeoGebraColorConstants.NEUTRAL_900));
			mathField.addStyleName("spreadsheetEditor");
			this.parent = parent;
			this.app = app;
			mathField.addBlurHandler(blur -> getSpreadsheetPanel().saveContentAndHideCellEditor());
		}

		public SpreadsheetPanel getSpreadsheetPanel() {
			return parent;
		}

		@Override
		public void show(Rectangle editorBounds, Rectangle viewport, int textAlignment) {
			mathField.attach(parent);
			updatePosition(editorBounds, viewport);
			mathField.setRightMargin(8);
			mathField.asWidget().getElement().getStyle().setTextAlign(
					textAlignment == CellFormat.ALIGN_LEFT ? TextAlign.LEFT : TextAlign.RIGHT);
			mathField.setVisible(true);
			mathField.editorClicked();
			Scheduler.get().scheduleDeferred(mathField::requestFocus);
		}

		@Override
		public void updatePosition(Rectangle editorBounds, Rectangle viewport) {
			Rectangle bounds = editorBounds.insetBy(-2, -2);
			mathField.getStyle().setLeft(bounds.getMinX(), Unit.PX);
			mathField.getStyle().setTop(bounds.getMinY(), Unit.PX);
			mathField.getStyle().setWidth(bounds.getWidth(), Unit.PX);
			mathField.getStyle().setProperty("minHeight", bounds.getHeight(), Unit.PX);
		}

		@Override
		public void hide() {
			mathField.setVisible(false);
			parent.requestFocus();
			app.hideKeyboard();
		}

		@Override
		public @Nonnull MathFieldInternal getMathField() {
			return mathField.getMathField().getInternal();
		}

		@Override
		public @Nonnull DefaultSpreadsheetCellProcessor getCellProcessor() {
			return new DefaultSpreadsheetCellProcessor(app.getKernel().getAlgebraProcessor());
		}

		@Nonnull
		@Override
		public SpreadsheetCellDataSerializer getCellDataSerializer() {
			return new DefaultSpreadsheetCellDataSerializer();
		}
	}

	/**
	 * Spreadsheet controls delegate
	 * @param app - application
	 * @param parent - parent panel
	 * @param mathTextField - math text field
	 */
	public SpreadsheetControlsDelegateW(AppW app, SpreadsheetPanel parent,
			MathTextFieldW mathTextField) {
		editor = new SpreadsheetCellEditorW(app, parent, mathTextField);
		contextMenu = new GPopupMenuW(app);
		loc = app.getLocalization();
		clipboard = new ClipboardW();
	}

	@Override
	public SpreadsheetCellEditor getCellEditor() {
		return editor;
	}

	@Override
	public void showContextMenu(List<ContextMenuItem> actions, GPoint coords) {
		contextMenu.clearItems();
		for (ContextMenuItem item : actions) {
			if (ContextMenuItem.Identifier.DIVIDER.equals(item.getIdentifier())) {
				contextMenu.addVerticalSeparator();
			} else {
				SVGResource image = getActionIcon(item.getIdentifier());
				String itemText = loc.getMenu(item.getLocalizationKey());

				AriaMenuItem menuItem = new AriaMenuItem(itemText, image, item::performAction);
				contextMenu.addItem(menuItem);
			}
		}

		positionContextMenu(coords.x, coords.y);
		contextMenu.getPopupMenu().selectItem(0);
	}

	private void positionContextMenu(int x, int y) {
		int left = x + getAbsoluteSpreadsheetLeft() - getAbsoluteAppLeft();
		int top = y + getAbsoluteSpreadsheetTop() - getAbsoluteAppTop();

		contextMenu.showAtPoint(0, 0);

		if (!popupFitsHorizontally(left)) {
			left = (int) (contextMenu.getApp().getWidth() - contextMenu.getPopupMenu()
					.getElement().getClientWidth() - MARGIN_FROM_SCREEN_EDGE);
		}

		if (!popupFitsVertically(top)) {
			top = (int) (contextMenu.getApp().getHeight() - contextMenu.getPopupMenu().getElement()
					.getClientHeight() - 2 * CONTEXT_MENU_PADDING - MARGIN_FROM_SCREEN_EDGE);
		}

		contextMenu.showAtPoint(left, top);
	}

	private boolean popupFitsHorizontally(int originalPopupLeft) {
		return contextMenu.getApp().getWidth()
				> originalPopupLeft + contextMenu.getPopupMenu().getElement().getOffsetWidth();
	}

	private boolean popupFitsVertically(int originalPopupTop) {
		return contextMenu.getApp().getHeight()
				> originalPopupTop + contextMenu.getPopupMenu().getElement().getOffsetHeight();
	}

	private int getAbsoluteAppLeft() {
		return (int) contextMenu.getApp().getAbsLeft();
	}

	private int getAbsoluteSpreadsheetLeft() {
		return editor.getSpreadsheetPanel().getAbsoluteLeft();
	}

	private int getAbsoluteAppTop() {
		return (int) contextMenu.getApp().getAbsTop();
	}

	private int getAbsoluteSpreadsheetTop() {
		return editor.getSpreadsheetPanel().getAbsoluteTop();
	}

	@Override
	public void hideContextMenu() {
		contextMenu.hide();
	}

	@Override
	public ClipboardInterface getClipboard() {
		return clipboard;
	}

	private SVGResource getActionIcon(ContextMenuItem.Identifier action) {
		MaterialDesignResources res = MaterialDesignResources.INSTANCE;
		switch (action) {
		case CUT:
			return res.cut_black();
		case COPY:
			return res.copy_black();
		case PASTE:
			return res.paste_black();
		case DELETE:
			return res.delete_black();
		case INSERT_ROW_ABOVE:
		case INSERT_ROW_BELOW:
		case DELETE_ROW:
		case INSERT_COLUMN_LEFT:
		case INSERT_COLUMN_RIGHT:
		case DELETE_COLUMN:
		default:
			return null;
		}
	}
}
