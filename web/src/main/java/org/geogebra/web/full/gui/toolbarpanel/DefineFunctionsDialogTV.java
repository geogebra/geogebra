package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.view.probcalculator.MathTextFieldW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class DefineFunctionsDialogTV extends ComponentDialog implements ErrorHandler {
	private final TableValuesView view;
	private MathTextFieldW f;
	private MathTextFieldW g;
	private boolean errorOccurred = false;

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
	}

	private void buildGUI() {
		f = addFunctionRow("f(x)=");
		g = addFunctionRow("g(x)=");
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
		resetError();
		GeoEvaluatable geo = view.getEvaluatable(idx);
		String input = field.getText();
		if (input.isEmpty()) {
			input = geo.getLabel(StringTemplate.defaultTemplate) + "("
					+ ((VarString) geo).getVarString(StringTemplate.defaultTemplate) + ")=?";
		}

		if (geo instanceof GeoFunction) {
			EvalInfo info = new EvalInfo(!app.getKernel().getConstruction()
					.isSuppressLabelsActive(), false, false);
			try {
				app.getKernel().getAlgebraProcessor().setEnableStructures(true);
				app.getKernel().getAlgebraProcessor().changeGeoElementNoExceptionHandling(geo,
						input, info, false, null, this);
				app.storeUndoInfo();
			} catch (Error e) {
				Log.error("Error happened on processing the input");
			} finally {
				Dom.toggleClass(field.asWidget().getParent(), "error", errorOccurred);
				app.getKernel().getAlgebraProcessor().setEnableStructures(false);
			}
		}
		return errorOccurred;
	}

	@Override
	public void showError(String msg) {
		errorOccurred = true;
	}

	@Override
	public void showCommandError(String command, String message) {
		errorOccurred = true;
	}

	@Override
	public String getCurrentCommand() {
		return null;
	}

	@Override
	public boolean onUndefinedVariables(String string, AsyncOperation<String[]> callback) {
		errorOccurred = true;
		return false;
	}

	@Override
	public void resetError() {
		errorOccurred = false;
	}

	/**
	 * reset field to empty
	 */
	public void resetFields() {
		f.setText("");
		g.setText("");
	}
}
