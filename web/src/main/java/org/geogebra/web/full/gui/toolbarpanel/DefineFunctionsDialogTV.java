package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.dialog.handler.DefineFunctionHandler;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.view.probcalculator.MathTextFieldW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class DefineFunctionsDialogTV extends ComponentDialog {
	private final TableValuesView view;
	private final DefineFunctionHandler defineFunctionHandler;
	private MathTextFieldW f;
	private MathTextFieldW g;

	/**
	 * dialog constructor
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 */
	public DefineFunctionsDialogTV(AppW app, DialogData dialogData, TableValuesView view) {
		super(app, dialogData, false, true);
		this.view = view;
		addStyleName("defineFunctionsDialog");
		buildGUI();
		if (!app.isWhiteboardActive()) {
			app.registerPopup(this);
		}
		this.addCloseHandler(event -> {
			app.unregisterPopup(this);
			app.hideKeyboard();
		});
		defineFunctionHandler = new DefineFunctionHandler(app);
	}

	private void buildGUI() {
		f = addFunctionRow("f(x) =");
		g = addFunctionRow("g(x) =");
	}

	private MathTextFieldW addFunctionRow(String functionLbl) {
		FlowPanel functionPanel = new FlowPanel();
		functionPanel.addStyleName("functionPanel");

		Label funcLbl = new Label(functionLbl);
		functionPanel.add(funcLbl);

		MathTextFieldW funcField = new MathTextFieldW(app);
		functionPanel.add(funcField);
		addDialogContent(functionPanel);

		return funcField;
	}

	@Override
	public void onPositiveAction() {
		boolean fHasError = processInput(f, 1);
		boolean gHasError = processInput(g, 2);
		if (!fHasError && !gHasError) {
			hide();
			app.storeUndoInfo();
		}
	}

	@Override
	public void hide() {
		super.hide();
		app.hideKeyboard();
	}

	@Override
	public void show() {
		showDirectly();
		f.requestFocus();
		Scheduler.get().scheduleDeferred(() -> {
			super.centerAndResize(((AppW) app).getAppletFrame().getKeyboardHeight());
		});
	}

	private boolean processInput(MathTextFieldW field, int idx) {
			try {
				defineFunctionHandler.handle(field.getText(), view.getEvaluatable(idx));
			} catch (Error e) {
				Log.error("Error happened on processing the input");
			} finally {
				Dom.toggleClass(field.asWidget().getParent(), "error",
						defineFunctionHandler.hasErrorOccurred());
			}
		return defineFunctionHandler.hasErrorOccurred();
	}

	/**
	 * reset field to empty
	 */
	public void resetFields() {
		f.setText("");
		g.setText("");
	}
}
