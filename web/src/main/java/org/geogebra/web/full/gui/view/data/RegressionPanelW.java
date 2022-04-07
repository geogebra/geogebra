package org.geogebra.web.full.gui.view.data;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.statistics.Regression;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.html5.main.LocalizationW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Panel to select and display the DataAnalysisView regression model.
 * 
 * @author G. Sturr, Laszlo Gal
 */
public class RegressionPanelW extends FlowPanel implements StatPanelInterfaceW {

	private final AppW app;
	private final LocalizationW loc;
	private final DataAnalysisViewW statDialog;

	// regression panel objects
	private Label lblEqn;

	private ListBox lbRegression;
	private ListBox lbPolyOrder;
	private Label lblEvaluate;
	private AutoCompleteTextFieldW fldInputX;

	private String[] regressionLabels;
	private Label fldOutputY;
	private boolean isIniting;
	private FlowPanel predictionPanel;
	private Canvas latexCanvas;
	private final GeoNumeric sample;

	private final DataAnalysisModel daModel;

	private Label regressionTitle;

	/**
	 * Construct a regression panel
	 * 
	 * @param app
	 *            application
	 * @param statDialog
	 *            invoking instance of DataAnalysisView
	 */
	public RegressionPanelW(AppW app, DataAnalysisViewW statDialog) {
		this.app = app;
		this.loc = app.getLocalization();
		this.statDialog = statDialog;
		this.daModel = statDialog.getModel();
		isIniting = true;
		setStyleName("daRegressionPanel");
		sample = new GeoNumeric(app.getKernel().getConstruction());
		sample.setObjColor(GColor.RED);
		createRegressionPanel();
		setLabels();
		updateRegressionPanel();
		updateGUI();
		isIniting = false;
	}

	private void createRegressionPanel() {
		// components
		String[] orders = { "2", "3", "4", "5", "6", "7", "8", "9" };
		lbPolyOrder = new ListBox();
		for (String item: orders) {
			lbPolyOrder.addItem(item);
		}
		
		lbPolyOrder.setSelectedIndex(0);
		lbPolyOrder.addChangeHandler(event -> onOrderChange());

		regressionLabels = new String[Regression.values().length];
		setRegressionLabels(app.getLocalization());
		lbRegression = new ListBox();
		for (String item: regressionLabels) {
			lbRegression.addItem(item);
		}
		
		lbRegression.addChangeHandler(event -> onRegressionChange());

		lblEqn = new Label();

		// regression combo panel
		FlowPanel lbPanel = new FlowPanel();
		lbPanel.add(lbRegression);
		lbPanel.add(lbPolyOrder);

		// regression label panel
		FlowPanel eqnPanel = new FlowPanel();

		latexCanvas = Canvas.createIfSupported();
		latexCanvas.setStyleName("daRegEquation");
		eqnPanel.add(latexCanvas);
		ScrollPanel scroller = new ScrollPanel();
		scroller.addStyleName("daEquationScrollPane");
		scroller.add(eqnPanel);

		// prediction panel
		createPredictionPanel();

		// model panel: equation + prediction
		FlowPanel modelPanel = new FlowPanel();
		modelPanel.add(scroller);
		modelPanel.add(predictionPanel);

		regressionTitle = new Label(loc.getMenu("RegressionModel"));
		regressionTitle.setStyleName("panelTitle");
		// put it all together
		FlowPanel regressionPanel = new FlowPanel();
		regressionPanel.add(regressionTitle);
		regressionPanel.add(LayoutUtilW.panelRow(lbPanel, modelPanel));
		
		add(regressionPanel);
	}

	/**
	 * Creates a panel to evaluate the regression model for a given x value
	 */
	private void createPredictionPanel() {
		lblEvaluate = new Label();
		fldInputX = new AutoCompleteTextFieldW(6, app);
		
		fldInputX.addKeyHandler(e -> {
			if (e.isEnterKey()) {
				doTextFieldActionPerformed(fldInputX);
			}
		});
		
		fldInputX.addBlurHandler(event -> doTextFieldActionPerformed(fldInputX));
		fldInputX.enableGGBKeyboard();

		Label lblOutputY = new Label();
		fldOutputY = new Label();

		predictionPanel = new FlowPanel();
		
		predictionPanel.add(LayoutUtilW.panelRow(lblEvaluate, new Label("x = "), fldInputX, 
				new Label("y = "), lblOutputY, fldOutputY));
	}

	/**
	 * Updates the regression equation label and the prediction panel
	 */
	public void updateRegressionPanel() {
		if (statDialog.getController().isValidData()) {
			setRegressionEquationLabel();
			doTextFieldActionPerformed(fldInputX);
		} else {
			setRegressionEquationLabelEmpty();
		}
		updateGUI();
	}

	/**
	 * Clears the X and Y fields of the prediction panel
	 */
	public void clearPredictionPanel() {
		fldInputX.setText("");
		fldOutputY.setText("");
	}

	private void setRegressionLabels(Localization loc) {
		for (Regression r : Regression.values()) {
			regressionLabels[r.ordinal()] = loc.getMenu(r.getLabel());
		}
	}

	/**
	 * Sets the labels according to current locale
	 */
	@Override
	public void setLabels() {
		regressionLabels = new String[Regression.values().length];
		setRegressionLabels(loc);

		// we need to remove old labels from combobox and we don't want the
		// listener to
		// be operational since it will call unnecessary Construction updates
		int j = lbRegression.getSelectedIndex();
		lbRegression.clear();

		for (String regressionLabel : regressionLabels) {
			lbRegression.addItem(regressionLabel);
		}

		lbRegression.setSelectedIndex(j);
		regressionTitle.setText(loc
				.getMenu("RegressionModel"));
		lblEqn.setText(loc.getMenu("Equation") + ":");

		lblEvaluate.setText(loc.getMenu("Evaluate") + ": ");
	}

	/**
	 * Draws the regression equation into the regression equation JLabel icon
	 */
	public void setRegressionEquationLabel() {
		// get the LaTeX string for the regression equation
		String eqn;

		try {
			// prepare number format
			StringTemplate highPrecision;
			if (daModel.getPrintDecimals() >= 0) {
				highPrecision = StringTemplate.printDecimals(StringType.LATEX,
						daModel.getPrintDecimals(), false);
			} else {
				highPrecision = StringTemplate.printFigures(StringType.LATEX,
						daModel.getPrintFigures(), false);
			}

			// no regression
			if (daModel.getRegressionMode().equals(Regression.NONE)
					|| statDialog.getRegressionModel() == null) {
				eqn = "";
			} else {
				eqn = "y = "
						+ statDialog.getRegressionModel().getFormulaString(
								highPrecision, true);
			}
		} catch (Exception e) {
			Log.debug(e);
			eqn = "\\text{" + loc.getMenu("NotAvailable") + "}";
		}

		DrawEquationW.paintOnCanvas(sample, eqn, latexCanvas,
				app.getFontSize());
		
		updateGUI();
	}

	/**
	 * Set the regression equation label to an empty string
	 */
	private void setRegressionEquationLabelEmpty() {
		updateGUI();
	}

	private void updateGUI() {
		lbPolyOrder.setVisible(daModel.getRegressionMode().equals(
				Regression.POLY));
		predictionPanel.setVisible(!(daModel.getRegressionMode()
				.equals(Regression.NONE)));
	}

	private void onRegressionChange() {
		daModel.setRegressionMode(lbRegression.getSelectedIndex());
		updateRegressionPanel();
	}

	private void onOrderChange() {
		daModel.setRegressionOrder(lbPolyOrder.getSelectedIndex() + 2);
		statDialog.getController().setRegressionGeo();
		statDialog.getController().updateRegressionPanel();
		setRegressionEquationLabel();

		// force update
		daModel.setRegressionMode(Regression.POLY.ordinal());
	}

	private void doTextFieldActionPerformed(AutoCompleteTextFieldW source) {
		if (isIniting) {
			return;
		}

		if (source == fldInputX) {
			try {
				String inputText = source.getText().trim();
				if (inputText.length() == 0) {
					return;
				}

				NumberValue nv;
				nv = app.getKernel().getAlgebraProcessor()
						.evaluateToNumeric(inputText, ErrorHelper.silent());
				double value = nv.getDouble();
				double output = ((GeoFunctionable) statDialog
						.getRegressionModel()).value(value);

				fldOutputY.setText(statDialog.format(output));

			} catch (Exception e) {
				Log.debug(e);
			}
		}
	}

	@Override
	public void updatePanel() {
		// TODO Auto-generated method stub
	}

	public int getRegressionIdx() {
	    return lbRegression.getSelectedIndex();
    }

	public void setRegressionIdx(int idx) {
	    lbRegression.setSelectedIndex(idx);
    }

}
