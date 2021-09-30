package org.geogebra.web.full.gui.toolbarpanel.tableview;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.web.full.gui.view.probcalculator.MathTextFieldW;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;

import elemental2.dom.Event;
import elemental2.dom.MouseEvent;

public class TableEditor {
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
			boolean newColumn = table.tableModel.getColumnCount() > column;
				mathTextField.setText(newColumn
						? table.tableModel.getCellAt(row, column).getInput()
						: ""); // make sure we don't load content of previously edited cell
			Element cell = table.getCell(row, column);
			table.scrollIntoView(cell.getOffsetTop());
			table.getTableWrapper().add(mathTextField); // first add to GWT tree
			cell.removeAllChildren();
			cell.removeClassName("errorCell");
			cell.appendChild(mathTextField.asWidget().getElement()); // then move in DOM

			mathTextField.editorClicked();
			mathTextField.adjustCaret(((MouseEvent) event).x, ((MouseEvent) event).y);
			editRow = row;
			editColumn = column;
		});
	}

	private void stopEditing() {
		mathTextField.asWidget().removeFromParent();
		GeoEvaluatable evaluatable = table.view.getEvaluatable(editColumn);
		if (evaluatable instanceof GeoList) {
			GeoList list = (GeoList) evaluatable;
			table.view.getProcessor().processInput(mathTextField.getText(), list, editRow);
		}
		if (isNewColumnEdited(evaluatable)) {
			table.view.getProcessor().processInput(mathTextField.getText(), null, editRow);
		}
		editRow = -1;
		editColumn = -1;
	}

	private boolean isNewColumnEdited(GeoEvaluatable evaluatable) {
		return evaluatable == null && !mathTextField.getText().isEmpty() && editRow >= 0;
	}

	private void ensureMathTextFieldExists() {
		if (mathTextField == null) {
			mathTextField = new MathTextFieldW(app);
			mathTextField.setRightMargin(26);
			mathTextField.addChangeHandler(this::stopEditing);
			mathTextField.addBlurHandler(event -> stopEditing());
			mathTextField.setTextMode(true);
			mathTextField.asWidget().setStyleName("tableEditor");
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
}
