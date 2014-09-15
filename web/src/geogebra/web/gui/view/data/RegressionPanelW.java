package geogebra.web.gui.view.data;

import geogebra.common.gui.view.data.DataAnalysisModel;
import geogebra.common.gui.view.data.DataAnalysisModel.Regression;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.main.AppW;
import geogebra.html5.main.LocalizationW;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Panel to select and display the DataAnalysisView regression model.
 * 
 * @author G. Sturr
 */
public class RegressionPanelW extends FlowPanel implements //ActionListener,
		ChangeHandler, StatPanelInterfaceW {
	private static final long serialVersionUID = 1L;

	private AppW app;
	private final LocalizationW loc;
	private DataAnalysisViewW statDialog;

	// regression panel objects
	private Label lblRegEquation, lblEqn;

	private ListBox lbRegression, lbPolyOrder;
	private Label lblEvaluate;
	private AutoCompleteTextFieldW fldInputX;
	private Label lblOutputY;

	private String[] regressionLabels;
	private Label fldOutputY;
	private boolean isIniting = true;
	private FlowPanel predictionPanel;

	private DataAnalysisModel daModel;

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
		this.loc = (LocalizationW) app.getLocalization();
		this.statDialog = statDialog;
		this.daModel = statDialog.getModel();
		add(createRegressionPanel());
		setLabels();
		updateRegressionPanel();
		updateGUI();
		isIniting = false;
	}

	private FlowPanel regressionPanel;

	private Label regressionTitle;

	private FlowPanel createRegressionPanel() {

		// components
		String[] orders = { "2", "3", "4", "5", "6", "7", "8", "9" };
		lbPolyOrder = new ListBox();
		for (String item: orders) {
			lbPolyOrder.addItem(item);
		}
		
		lbPolyOrder.setSelectedIndex(0);
		lbPolyOrder.addChangeHandler(this);

		regressionLabels = new String[Regression.values().length];
		setRegressionLabels();
		lbRegression = new ListBox();
		for (String item: regressionLabels) {
			lbRegression.addItem(item);
		}
		
		lbRegression.addChangeHandler(this);

		lblRegEquation = new Label();
		lblEqn = new Label();

		// regression combo panel
		FlowPanel cbPanel = new FlowPanel();
		cbPanel.add(lbRegression);
		cbPanel.add(lbPolyOrder);

		// regression label panel
		FlowPanel eqnPanel = new FlowPanel();
		eqnPanel.add(lblRegEquation);
		ScrollPanel scroller = new ScrollPanel();
		scroller.add(eqnPanel);

		// prediction panel
		createPredictionPanel();

		// model panel: equation + prediction
		FlowPanel modelPanel = new FlowPanel();
		modelPanel.add(scroller);
		modelPanel.add(predictionPanel);

		regressionTitle = new Label(loc.getMenu("RegressionModel"));
		// put it all together
		regressionPanel = new FlowPanel();
		regressionPanel.add(regressionTitle);
		regressionPanel.add(modelPanel);
		regressionPanel.add(cbPanel);

		FlowPanel mainPanel = new FlowPanel();
		mainPanel.add(regressionPanel);
//
		return mainPanel;
	}

	/**
	 * Creates a panel to evaluate the regression model for a given x value
	 */
	private void createPredictionPanel() {

		FlowPanel p = new FlowPanel();
		lblEvaluate = new Label();
		fldInputX = new AutoCompleteTextFieldW(6, app);
		
		//fldInputX.addActionListener(this);

		lblOutputY = new Label();
		fldOutputY = new Label();

		p.add(lblEvaluate);
		p.add(new Label("x = "));
		p.add(fldInputX);
		p.add(new Label("y = "));
		p.add(lblOutputY);
		p.add(fldOutputY);

		predictionPanel = new FlowPanel();
		predictionPanel.add(p);

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

	private void setRegressionLabels() {

		for (Regression r : Regression.values()) {

			regressionLabels[r.ordinal()] = app.getMenu(r.getLabel());
		}

	}

	/**
	 * Sets the labels according to current locale
	 */
	public void setLabels() {
		regressionLabels = new String[Regression.values().length];
		setRegressionLabels();

		// we need to remove old labels from combobox and we don't want the
		// listener to
		// be operational since it will call unnecessary Construction updates
		int j = lbRegression.getSelectedIndex();
		lbRegression.clear();

		for (int i = 0; i < regressionLabels.length; i++) {
			lbRegression.addItem(regressionLabels[i]);
		}

		lbRegression.setSelectedIndex(j);
		regressionTitle.setText(app
				.getMenu("RegressionModel"));
		lblEqn.setText(app.getMenu("Equation") + ":");

		lblEvaluate.setText(app.getMenu("Evaluate") + ": ");

	}

	/**
	 * Draws the regression equation into the regression equation JLabel icon
	 */
	public void setRegressionEquationLabel() {

		// get the LaTeX string for the regression equation

		String eqn;
		// GeoElement geoRegression = statDialog.getRegressionModel();

		try {
			// prepare number format
			StringTemplate highPrecision;
			if (daModel.getPrintDecimals() >= 0)
				highPrecision = StringTemplate.printDecimals(StringType.LATEX,
						daModel.getPrintDecimals(), false);
			else
				highPrecision = StringTemplate.printFigures(StringType.LATEX,
						daModel.getPrintFigures(), false);

			// no regression
			if (daModel.getRegressionMode().equals(Regression.NONE)
					|| statDialog.getRegressionModel() == null) {
				eqn = app.getPlain("");
			}

			// linear
			else if (daModel.getRegressionMode().equals(Regression.LINEAR)) {
				((GeoLine) statDialog.getRegressionModel()).setToExplicit();
				eqn = statDialog.getRegressionModel().getFormulaString(
						highPrecision, true);
			}

			// nonlinear
			else {
				eqn = "y = "
						+ statDialog.getRegressionModel().getFormulaString(
								highPrecision, true);
			}

		}

		catch (Exception e) {
			e.printStackTrace();
			eqn = "\\text{" + app.getPlain("NotAvailable") + "}";

		}

		// create an icon with the LaTeX string
//		ImageIcon icon = GeoGebraIcon.createLatexIcon(app, eqn, this.getFont(),
//				false, Color.RED, null);
//
//		// set the label icon with our equation string
//		lblRegEquation.setIcon(icon);
//		lblRegEquation.revalidate();

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

	public void actionPerformed(Object source) {

		if (source instanceof AutoCompleteTextFieldW) {
			doTextFieldActionPerformed((AutoCompleteTextFieldW) source);
		}

		else if (source == lbRegression) {
			daModel.setRegressionMode(lbRegression.getSelectedIndex());
		}

		else if (source == lbPolyOrder) {
			daModel.setRegressionOrder(lbPolyOrder.getSelectedIndex() + 2);
			statDialog.getController().setRegressionGeo();
			setRegressionEquationLabel();

			// force update
			daModel.setRegressionMode(Regression.POLY.ordinal());
		}

	}

	private void doTextFieldActionPerformed(AutoCompleteTextFieldW source) {
		if (isIniting)
			return;

		if (source == fldInputX) {
			try {
				String inputText = source.getText().trim();
				if (inputText == null || inputText.length() == 0)
					return;

				NumberValue nv;
				nv = app.getKernel().getAlgebraProcessor()
						.evaluateToNumeric(inputText, true);
				double value = nv.getDouble();
				double output = ((GeoFunctionable) statDialog
						.getRegressionModel()).getGeoFunction().evaluate(value);

				fldOutputY.setText(statDialog.format(output));

			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void updatePanel() {
		// TODO Auto-generated method stub

	}

	public void onChange(ChangeEvent event) {
	    // TODO Auto-generated method stub
	    
    }

}
