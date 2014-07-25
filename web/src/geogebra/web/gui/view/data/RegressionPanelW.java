package geogebra.web.gui.view.data;

import geogebra.common.gui.view.data.DataAnalysisModel;
import geogebra.common.gui.view.data.DataAnalysisModel.Regression;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.main.LocalizationW;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Panel to select and display the DataAnalysisView regression model.
 * 
 * @author G. Sturr
 */
public class RegressionPanelW extends FlowPanel implements //ActionListener,
		StatPanelInterfaceW {
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
//		this.setLayout(new BorderLayout());
//		this.add(createRegressionPanel(), BorderLayout.CENTER);
		setLabels();
		updateRegressionPanel();
		updateGUI();
		isIniting = false;
	}

	private FlowPanel regressionPanel;

	private FlowPanel createRegressionPanel() {

		// components
//		String[] orders = { "2", "3", "4", "5", "6", "7", "8", "9" };
//		lbPolyOrder = new JComboBox(orders);
//		lbPolyOrder.setSelectedIndex(0);
//		lbPolyOrder.addActionListener(this);
//		lbPolyOrder.setFocusable(false);
//
//		regressionLabels = new String[Regression.values().length];
//		setRegressionLabels();
//		lbRegression = new JComboBox(regressionLabels);
//		lbRegression.addActionListener(this);
//		lbRegression.setFocusable(false);
//
//		lblRegEquation = new JLabel();
//		lblEqn = new JLabel();
//
//		// regression combo panel
//		JPanel cbPanel = new JPanel();
//		cbPanel.setLayout(new BoxLayout(cbPanel, BoxLayout.Y_AXIS));
//		cbPanel.add(LayoutUtil.flowPanel(lbRegression));
//		cbPanel.add(LayoutUtil.flowPanel(lbPolyOrder));
//
//		// regression label panel
//		JPanel eqnPanel = new JPanel(new BorderLayout());
//		eqnPanel.add(lblRegEquation, BorderLayout.CENTER);
//		JScrollPane scroller = new JScrollPane(eqnPanel);
//		scroller.setBorder(BorderFactory.createEmptyBorder());
//
//		// prediction panel
//		createPredictionPanel();
//
//		// model panel: equation + prediction
//		JPanel modelPanel = new JPanel();
//		modelPanel.setLayout(new BoxLayout(modelPanel, BoxLayout.Y_AXIS));
//		modelPanel.add(scroller);
//		modelPanel.add(predictionPanel);
//
//		// put it all together
//		regressionPanel = new JPanel(new BorderLayout(30, 0));
//		regressionPanel.add(modelPanel, BorderLayout.CENTER);
//		regressionPanel.add(cbPanel, loc.borderWest());
//		regressionPanel.setBorder(BorderFactory.createTitledBorder(loc
//				.getMenu("RegressionModel")));
//
		FlowPanel mainPanel = new FlowPanel();
//		mainPanel.add(regressionPanel, BorderLayout.CENTER);
//
		return mainPanel;
	}

	/**
	 * Creates a panel to evaluate the regression model for a given x value
	 */
	private void createPredictionPanel() {
//
//		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
//		lblEvaluate = new JLabel();
//		fldInputX = new MyTextField(app);
//		fldInputX.addActionListener(this);
//
//		fldInputX.setColumns(6);
//		lblOutputY = new JLabel();
//		fldOutputY = new JLabel();
//
//		p.add(lblEvaluate);
//		p.add(new JLabel("x = "));
//		p.add(fldInputX);
//		p.add(new JLabel("y = "));
//		p.add(lblOutputY);
//		p.add(fldOutputY);
//
//		predictionPanel = new JPanel(new BorderLayout());
//		predictionPanel.add(p, loc.borderWest());
//
	}

	/**
	 * Updates the regression equation label and the prediction panel
	 */
	public void updateRegressionPanel() {

//		if (statDialog.getController().isValidData()) {
//			setRegressionEquationLabel();
//			doTextFieldActionPerformed(fldInputX);
//		} else {
//			setRegressionEquationLabelEmpty();
//		}
//		updateGUI();
//
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
//		ActionListener al = lbRegression.getActionListeners()[0];
//		lbRegression.removeActionListener(al);
//		lbRegression.removeAllItems();

		for (int i = 0; i < regressionLabels.length; i++) {
			lbRegression.addItem(regressionLabels[i]);
		}

		lbRegression.setSelectedIndex(j);
//		lbRegression.addActionListener(al);
//		((TitledBorder) regressionPanel.getBorder()).setTitle(app
//				.getMenu("RegressionModel"));
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
//		lblRegEquation.setIcon(null);
//		lblRegEquation.revalidate();
//
		updateGUI();
	}

	private void updateGUI() {

		lbPolyOrder.setVisible(daModel.getRegressionMode().equals(
				Regression.POLY));
		predictionPanel.setVisible(!(daModel.getRegressionMode()
				.equals(Regression.NONE)));
//		repaint();

	}

//	public void actionPerformed(ActionEvent e) {
//
//		Object source = e.getSource();
//
//		if (source instanceof JTextField) {
//			doTextFieldActionPerformed((JTextField) source);
//		}
//
//		else if (source == lbRegression) {
//			lbRegression.removeActionListener(this);
//			daModel.setRegressionMode(lbRegression.getSelectedIndex());
//			lbRegression.addActionListener(this);
//		}
//
//		else if (source == lbPolyOrder) {
//			daModel.setRegressionOrder(lbPolyOrder.getSelectedIndex() + 2);
//			statDialog.getController().setRegressionGeo();
//			setRegressionEquationLabel();
//
//			// force update
//			daModel.setRegressionMode(Regression.POLY.ordinal());
//		}
//
//	}
//
//	private void doTextFieldActionPerformed(JTextField source) {
//		if (isIniting)
//			return;
//
//		if (source == fldInputX) {
//			try {
//				String inputText = source.getText().trim();
//				if (inputText == null || inputText.length() == 0)
//					return;
//
//				NumberValue nv;
//				nv = app.getKernel().getAlgebraProcessor()
//						.evaluateToNumeric(inputText, true);
//				double value = nv.getDouble();
//				double output = ((GeoFunctionable) statDialog
//						.getRegressionModel()).getGeoFunction().evaluate(value);
//
//				fldOutputY.setText(statDialog.format(output));
//
//			} catch (NumberFormatException e) {
//				e.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}

	public void updatePanel() {
		// TODO Auto-generated method stub

	}

}
