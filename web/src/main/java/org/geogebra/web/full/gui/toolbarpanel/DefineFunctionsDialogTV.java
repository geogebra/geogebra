package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.view.probcalculator.MathTextFieldW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class DefineFunctionsDialogTV extends ComponentDialog implements ErrorHandler {
	private final TableValuesView view;
	private MathTextFieldW f;
	private MathTextFieldW g;
	private boolean errorOccured = false;

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
	}

	private void buildGUI() {
		f = addFunctionRow("f(x)");
		g = addFunctionRow("g(x)");
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
		GeoEvaluatable geo = view.getEvaluatable(1);
		if (geo instanceof GeoFunction) {
				EvalInfo info =  new EvalInfo(!app.getKernel().getConstruction().isSuppressLabelsActive(),
						false, false);
				app.getKernel().getAlgebraProcessor().changeGeoElementNoExceptionHandling(geo,
						f.getText(), info, false, null, this);
				Dom.toggleClass(f.asWidget(),"errorStyle", errorOccured);
				if (!errorOccured) {
					hide();
				}
		}
	}

	@Override
	public void showError(String msg) {
		errorOccured = true;
	}

	@Override
	public void showCommandError(String command, String message) {
		errorOccured = true;
	}

	@Override
	public String getCurrentCommand() {
		return null;
	}

	@Override
	public boolean onUndefinedVariables(String string, AsyncOperation<String[]> callback) {
		errorOccured = true;
		return false;
	}

	@Override
	public void resetError() {
		errorOccured = false;
	}
}
