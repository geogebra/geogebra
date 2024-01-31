package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.spreadsheet.core.ClipboardInterface;
import org.geogebra.common.spreadsheet.core.ContextMenuItem;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellEditor;
import org.geogebra.common.spreadsheet.core.SpreadsheetControlsDelegate;
import org.geogebra.common.spreadsheet.kernel.KernelDataSerializer;
import org.geogebra.common.spreadsheet.kernel.SpreadsheetEditorListener;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.web.full.gui.components.MathFieldEditor;
import org.geogebra.web.full.gui.view.probcalculator.MathTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.style.shared.TextAlign;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.Panel;

import com.google.gwt.core.client.Scheduler;

public class SpreadsheetControlsDelegateW implements SpreadsheetControlsDelegate {

	private final SpreadsheetCellEditorW editor;

	private static class SpreadsheetCellEditorW implements SpreadsheetCellEditor {
		private final MathFieldEditor mathField;
		private final Panel parent;
		private final AppW app;

		public SpreadsheetCellEditorW(AppW app, Panel parent, MathTextFieldW mathField) {
			this.mathField = mathField;
			mathField.addStyleName("spreadsheetEditor");
			this.parent = parent;
			this.app = app;
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
			mathField.getMathField().getInternal().setFieldListener(
					new SpreadsheetEditorListener(mathField.getMathField().getInternal(),
							app.getKernel(), row, column, this));
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
		}
	}

	public SpreadsheetControlsDelegateW(AppW app, Panel parent, MathTextFieldW mathTextField) {
		editor = new SpreadsheetCellEditorW(app, parent, mathTextField);
	}

	@Override
	public SpreadsheetCellEditor getCellEditor() {
		return editor;
	}

	@Override
	public void showContextMenu(List<ContextMenuItem> actions, GPoint coords) {
		//TODO APPS-5395
	}

	@Override
	public void hideContextMenu() {
		//TODO APPS-5395
	}

	@Override
	public ClipboardInterface getClipboard() {
		return null;
	}

}
