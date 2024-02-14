package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.main.Localization;
import org.geogebra.common.spreadsheet.core.ClipboardInterface;
import org.geogebra.common.spreadsheet.core.ContextMenuItem;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellEditor;
import org.geogebra.common.spreadsheet.core.SpreadsheetControlsDelegate;
import org.geogebra.common.spreadsheet.kernel.KernelDataSerializer;
import org.geogebra.common.spreadsheet.kernel.SpreadsheetEditorListener;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.MathFieldEditor;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.gui.view.probcalculator.MathTextFieldW;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.dom.style.shared.TextAlign;
import org.gwtproject.dom.style.shared.Unit;

import com.google.gwt.core.client.Scheduler;

public class SpreadsheetControlsDelegateW implements SpreadsheetControlsDelegate {

	private final SpreadsheetCellEditorW editor;
	private GPopupMenuW contextMenu;
	private Localization loc;

	private static class SpreadsheetCellEditorW implements SpreadsheetCellEditor {
		private final MathFieldEditor mathField;
		private final SpreadsheetPanel parent;
		private final AppW app;
		private SpreadsheetEditorListener listener;
		private final Spreadsheet spreadsheet;

		public SpreadsheetCellEditorW(AppW app, SpreadsheetPanel parent, MathTextFieldW mathField,
				Spreadsheet spreadsheet) {
			this.mathField = mathField;
			mathField.addStyleName("spreadsheetEditor");
			this.parent = parent;
			this.app = app;
			this.spreadsheet = spreadsheet;
		}

		@Override
		public void setBounds(Rectangle editorBounds) {
			mathField.attach(parent);
			mathField.getStyle().setLeft(editorBounds.getMinX(), Unit.PX);
			mathField.getStyle().setTop(editorBounds.getMinY(), Unit.PX);
			mathField.getStyle().setWidth(editorBounds.getWidth(), Unit.PX);
			mathField.getStyle().setProperty("minHeight", editorBounds.getHeight(), Unit.PX);
			mathField.setRightMargin(8);
			mathField.setVisible(true);
			mathField.requestFocus();
			Scheduler.get().scheduleDeferred(mathField::requestFocus);
		}

		@Override
		public void setTargetCell(int row, int column) {
			listener = new SpreadsheetEditorListener(mathField.getMathField().getInternal(),
					app.getKernel(), row, column, this, spreadsheet);
			mathField.getMathField().getInternal().setFieldListener(
					listener);
			mathField.setUnhandledArrowListener(listener);
		}

		@Override
		public void onEnter() {
			if (listener != null) {
				listener.onEnter();
			}
		}

		@Override
		public void setContent(Object content) {
			mathField.getMathField().parse(new KernelDataSerializer().getStringForEditor(content));
		}

		@Override
		public void setAlign(int align) {
			mathField.asWidget().getElement().getStyle().setTextAlign(
					align == CellFormat.ALIGN_LEFT ? TextAlign.LEFT : TextAlign.RIGHT);
		}

		@Override
		public void scrollHorizontally() {
			mathField.scrollHorizontally();
		}

		@Override
		public boolean isVisible() {
			return mathField.isVisible();
		}

		@Override
		public void hide() {
			mathField.setVisible(false);
			parent.requestFocus();
		}
	}

	/**
	 * Spreadsheet controls delegate
	 * @param app - application
	 * @param parent - parent panel
	 * @param mathTextField - math text field
	 * @param spreadsheet - spreadsheet
	 */
	public SpreadsheetControlsDelegateW(AppW app, SpreadsheetPanel parent,
			MathTextFieldW mathTextField, Spreadsheet spreadsheet) {
		editor = new SpreadsheetCellEditorW(app, parent, mathTextField, spreadsheet);
		contextMenu = new GPopupMenuW(app);
		loc = app.getLocalization();
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
				AriaMenuItem menuItem;

				if (image != null) {
					menuItem = new AriaMenuItem(MainMenu.getMenuBarHtml(image, itemText),
							true, () -> item.performAction());
				} else {
					menuItem = new AriaMenuItem(itemText, true, () -> item.performAction());
				}
				contextMenu.addItem(menuItem);
			}
		}
		contextMenu.showAtPoint(coords.x, coords.y);
		contextMenu.getPopupMenu().selectItem(0);
	}

	@Override
	public void hideContextMenu() {
		contextMenu.hide();
	}

	@Override
	public ClipboardInterface getClipboard() {
		return null;
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
