package org.geogebra.web.full.gui.toolbarpanel.tableview;

import org.geogebra.keyboard.base.KeyboardType;
import org.geogebra.web.full.gui.keyboard.KeyboardManager;
import org.geogebra.web.full.gui.view.probcalculator.MathTextFieldW;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.keyboard.KeyboardManagerInterface;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;

public class TableEditor {
	private static final int HEADER_HEIGHT = 56;
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
	 * @param cell the TD element
	 */
	public void startEditing(int row, int column, Element cell) {
		ensureMathTextFieldExists();

		mathTextField.setText(table.tableModel.getCellAt(row, column));

		setPosition(cell);
		table.getTableWrapper().add(mathTextField);

		mathTextField.editorClicked();
		KeyboardManagerInterface keyboardManager = app.getKeyboardManager();
		if (keyboardManager != null) {
			((KeyboardManager) keyboardManager).selectTab(KeyboardType.NUMBERS);
		}
		editRow = row;
		editColumn = column;
	}

	private void setPosition(Element cell) {
		table.scrollIntoView(cell.getOffsetTop());
		Style style = mathTextField.asWidget().getElement().getStyle();
		mathTextField.asWidget().setStyleName("tableEditor");
		style.setTop(cell.getOffsetTop(), Style.Unit.PX);
		style.setLeft(cell.getOffsetLeft(), Style.Unit.PX);
		mathTextField.setPxWidth(cell.getOffsetWidth() - 1);
	}

	private void ensureMathTextFieldExists() {
		if (mathTextField == null) {
			mathTextField = new MathTextFieldW(app);
			mathTextField.addChangeHandler(() -> {
				mathTextField.asWidget().removeFromParent();
				table.tableModel.setCell(editRow, editColumn,
						mathTextField.getText());
			});
		}
	}

	public MathKeyboardListener getKeyboardListener() {
		return mathTextField == null ? null : mathTextField.getKeyboardListener();
	}
}
