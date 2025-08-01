package org.geogebra.web.full.gui.toolbarpanel.tableview;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.view.table.keyboard.TableValuesKeyboardNavigationController;
import org.geogebra.web.full.gui.view.probcalculator.MathTextFieldW;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.Node;
import org.gwtproject.dom.client.NodeList;
import org.gwtproject.user.client.DOM;

import com.himamis.retex.editor.share.editor.UnhandledArrowListener;
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.util.JavaKeyCodes;

import elemental2.dom.Event;
import elemental2.dom.MouseEvent;

public class TableEditor implements UnhandledArrowListener {
	private final StickyValuesTable table;
	private final AppW app;
	public TableValuesKeyboardNavigationController controller;
	private MathTextFieldW mathTextField;
	Element wrapper;
	private Event event;
	private boolean wasError;

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
	 * @param keepFocusIfEditing if set and the editor is already active, do not reset
	 */
	public void startEditing(int row, int column, boolean keepFocusIfEditing) {
		ensureMathTextFieldExists();
		app.invokeLater(() -> {
			if (mathTextField.asWidget().isAttached() && keepFocusIfEditing) {
				return;
			}
			if (row > table.tableModel.getRowCount() + 1) {
				return;
			}
			boolean newColumnAndRow = table.tableModel.getColumnCount() > column
					&& table.tableModel.getRowCount() > row;
			mathTextField.setText(newColumnAndRow
					? table.tableModel.getCellAt(row, column).getInput()
					: ""); // make sure we don't load content of previously edited cell
			Element cell = table.getCell(row, column);
			table.scrollIntoView(cell);
			table.getTableWrapper().add(mathTextField); // first add to GWT tree
			setChildrenDisplay(cell, "none");
			wasError = cell.removeClassName("errorCell");
			Element element = mathTextField.asWidget().getElement();
			if (wrapper == null) {
				wrapper = DOM.createDiv();
				wrapper.addClassName("tableEditorWrap");
			} else {
				wrapper.getStyle().setProperty("display", "");
			}
			wrapper.appendChild(element);
			cell.appendChild(wrapper); // then move in DOM

			mathTextField.editorClicked();
			if (event != null) {
				mathTextField.adjustCaret(((MouseEvent) event).x, ((MouseEvent) event).y);
				event = null;
			}
		});
	}

	private void setChildrenDisplay(Element cell, String display) {
		NodeList<Node> childNodes = cell.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Element.as(cell.getChild(i)).getStyle().setProperty("display", display);
		}
	}

	void stopEditing() {
		if (mathTextField != null) {
			mathTextField.asWidget().removeFromParent();
		}
		if (wrapper != null) {
			Element cell = wrapper.getParentElement();
			wrapper.removeFromParent();
			if (cell != null) {
				setChildrenDisplay(cell, "");
				if (wasError) {
					cell.addClassName("errorCell");
				}
			}
		}
		table.flush();
	}

	private void ensureMathTextFieldExists() {
		if (mathTextField == null) {
			mathTextField = new MathTextFieldW(app);
			mathTextField.setRightMargin(22);
			mathTextField.addChangeHandler((enter) -> {
				if (enter) {
					controller.keyPressed(TableValuesKeyboardNavigationController.Key.RETURN);
				} else {
					controller.deselect();
				}
			});
			mathTextField.setTextMode(true);
			mathTextField.asWidget().setStyleName("tableEditor");
			mathTextField.setUnhandledArrowListener(this);
			ClickStartHandler.init(mathTextField.asWidget(), new ClickStartHandler() {
				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					mathTextField.adjustCaret(x, y);
				}
			});
		}
	}

	public MathKeyboardListener getKeyboardListener() {
		return mathTextField == null ? null : mathTextField.getKeyboardListener();
	}

	@Override
	public void onArrow(int keyCode, KeyEvent.KeyboardType keyboardType) {
		switch (keyCode) {
		case JavaKeyCodes.VK_LEFT:
			controller.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_LEFT);
			break;
		case JavaKeyCodes.VK_RIGHT:
			controller.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_RIGHT);
			break;
		case JavaKeyCodes.VK_UP:
			controller.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_UP);
			break;
		default: // to make SpotBugs happy
		case JavaKeyCodes.VK_DOWN:
			controller.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_DOWN);
			break;
		}
	}

	public String getText() {
		return mathTextField.getText();
	}

	/**
	 * Make sure cursor is adjusted to the position given by the event
	 * once the editor appears.
	 * @param evt event
	 */
	public void adjustCursor(Event evt) {
		this.event = evt;
	}

	public boolean isAttached() {
		return mathTextField != null && mathTextField.asWidget().isAttached();
	}
}
