package org.geogebra.web.full.gui.toolbarpanel.tableview;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.web.full.gui.view.probcalculator.MathTextFieldW;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.himamis.retex.editor.share.editor.UnhandledArrowListener;
import com.himamis.retex.editor.share.util.JavaKeyCodes;

import elemental2.dom.Event;
import elemental2.dom.MouseEvent;

public class TableEditor implements UnhandledArrowListener {
	private final StickyValuesTable table;
	private final AppW app;
	private MathTextFieldW mathTextField;
	private int editRow = -1;
	private int editColumn = -1;

	/**
	 * @param table table
	 * @param app app
	 */
	public TableEditor(StickyValuesTable table, AppW app) {
		this.table = table;
		this.app = app;
	}

	/**
	 * @param row row
	 * @param column column
	 */
	public void startEditing(int row, int column, Event event) {
		ensureMathTextFieldExists();
		app.invokeLater(() -> {
			boolean newColumnAndRow = table.tableModel.getColumnCount() > column
					&& table.tableModel.getRowCount() > row;
			mathTextField.setText(newColumnAndRow
					? table.tableModel.getCellAt(row, column).getInput()
					: ""); // make sure we don't load content of previously edited cell
			Element cell = table.getCell(row, column);
			table.scrollIntoView(cell);
			table.getTableWrapper().add(mathTextField); // first add to GWT tree
			cell.removeAllChildren();
			cell.removeClassName("errorCell");
			Element wrap = DOM.createDiv();
			wrap.addClassName("tableEditorWrap");
			wrap.appendChild(mathTextField.asWidget().getElement());
			cell.appendChild(wrap); // then move in DOM

			mathTextField.editorClicked();
			if (event != null) {
				mathTextField.adjustCaret(((MouseEvent) event).x, ((MouseEvent) event).y);
			}
			editRow = row;
			editColumn = column;
		});
	}

	private void stopEditing() {
		Element wrapper = mathTextField.asWidget().getElement().getParentElement();
		mathTextField.asWidget().removeFromParent();
		wrapper.removeFromParent();
		GeoEvaluatable evaluatable = table.view.getEvaluatable(editColumn);
		if (evaluatable instanceof GeoList) {
			GeoList list = (GeoList) evaluatable;
			processInputAndFocusNextCell(list);
		}
		if (isNewColumnEdited(evaluatable)) {
			processInputAndFocusNextCell(null);
		}
		if (wasEnterPressed()) {
			mathTextField.getMathField().getInternal().setEnterPressed(false);
			if (isLastInputRowEmpty()) {
				app.hideKeyboard();
			}
		}
		editRow = -1;
		editColumn = -1;
	}

	private void processInputAndFocusNextCell(GeoList list) {
		table.view.getProcessor().processInput(mathTextField.getText(), list, editRow);
		if (wasEnterPressed() && !isLastInputRowEmpty()) {
			moveFocus(editRow + 1, editColumn);
		}
	}

	private void moveFocus(final int focusRow, final int focusCol) {
		app.invokeLater(() -> startEditing(focusRow, focusCol, null));
	}

	private boolean wasEnterPressed() {
		return mathTextField.getMathField().getInternal().isEnterPressed();
	}

	private boolean isLastInputRowEmpty() {
		return mathTextField.getText().isEmpty() && (editRow == table.tableModel.getRowCount()
				|| table.getColumnsChange() < 0 || table.getRowsChange() < 0);
	}

	private boolean isNewColumnEdited(GeoEvaluatable evaluatable) {
		return evaluatable == null && !mathTextField.getText().isEmpty() && editRow >= 0;
	}

	private void ensureMathTextFieldExists() {
		if (mathTextField == null) {
			mathTextField = new MathTextFieldW(app);
			mathTextField.setRightMargin(22);
			mathTextField.addChangeHandler(this::stopEditing);
			mathTextField.setTextMode(true);
			mathTextField.asWidget().setStyleName("tableEditor");
			mathTextField.setUnhandledArrowListener(this);
			ClickStartHandler.init(mathTextField.asWidget(), new ClickStartHandler() {
				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					mathTextField.adjustCaret(x, y);
				}
			});
		} else if (editRow >= 0) {
			stopEditing();
			table.flush();
		}
	}

	public MathKeyboardListener getKeyboardListener() {
		return mathTextField == null ? null : mathTextField.getKeyboardListener();
	}

	@Override
	public void onArrow(int keyCode) {
		int dx = 0;
		int dy = 0;
		switch (keyCode) {
		case JavaKeyCodes.VK_LEFT:
			dx = -1;
			break;
		case JavaKeyCodes.VK_RIGHT:
			dx = 1;
			break;
		case JavaKeyCodes.VK_UP:
			dy = -1;
			break;
		case JavaKeyCodes.VK_DOWN:
			dy = 1;
			break;
		default:
			return; // to make SpotBugs happy
		}
		int focusColumn = editColumn;
		int focusRow = editRow;
		do {
			focusColumn = focusColumn + dx;
			focusRow = focusRow + dy;
		} while (table.hasCell(focusColumn, focusRow)
				&& table.columnNotEditable(focusColumn));
		if (table.hasCell(focusColumn, focusRow)) {
			stopEditing();
			moveFocus(focusRow, focusColumn);
		}
	}
}
