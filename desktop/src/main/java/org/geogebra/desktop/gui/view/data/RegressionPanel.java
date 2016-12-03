package org.geogebra.desktop.gui.view.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.gui.view.data.DataAnalysisModel.Regression;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.gui.util.GeoGebraIconD;
import org.geogebra.desktop.gui.util.LayoutUtil;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

/**
 * Panel to select and display the DataAnalysisView regression model.
 * 
 * @author G. Sturr
 */
public class RegressionPanel extends JPanel implements ActionListener,
		StatPanelInterface {
	private static final long serialVersionUID = 1L;

	private AppD app;
	private final LocalizationD loc;
	private DataAnalysisViewD statDialog;

	// regression panel objects
	private JLabel lblRegEquation, lblEqn;

	private JComboBox cbRegression, cbPolyOrder;
	private JLabel lblEvaluate;
	private MyTextFieldD fldInputX;
	private JLabel lblOutputY;

	private String[] regressionLabels;
	private JLabel fldOutputY;
	private boolean isIniting = true;
	private JPanel predictionPanel;

	private DataAnalysisModel daModel;

	/**
	 * Construct a regression panel
	 * 
	 * @param app
	 *            application
	 * @param statDialog
	 *            invoking instance of DataAnalysisView
	 */
	public RegressionPanel(AppD app, DataAnalysisViewD statDialog) {

		this.app = app;
		this.loc = app.getLocalization();
		this.statDialog = statDialog;
		this.daModel = statDialog.getModel();
		this.setLayout(new BorderLayout());
		this.add(createRegressionPanel(), BorderLayout.CENTER);
		setLabels();
		updateRegressionPanel();
		updateGUI();
		isIniting = false;
	}

	private JPanel regressionPanel;

	private JPanel createRegressionPanel() {

		// components
		String[] orders = { "2", "3", "4", "5", "6", "7", "8", "9" };
		cbPolyOrder = new JComboBox(orders);
		cbPolyOrder.setSelectedIndex(0);
		cbPolyOrder.addActionListener(this);
		cbPolyOrder.setFocusable(false);

		regressionLabels = new String[Regression.values().length];
		setRegressionLabels();
		cbRegression = new JComboBox(regressionLabels);
		cbRegression.addActionListener(this);
		cbRegression.setFocusable(false);

		lblRegEquation = new JLabel();
		lblEqn = new JLabel();

		// regression combo panel
		JPanel cbPanel = new JPanel();
		cbPanel.setLayout(new BoxLayout(cbPanel, BoxLayout.Y_AXIS));
		cbPanel.add(LayoutUtil.flowPanel(cbRegression));
		cbPanel.add(LayoutUtil.flowPanel(cbPolyOrder));

		// regression label panel
		JPanel eqnPanel = new JPanel(new BorderLayout());
		eqnPanel.add(lblRegEquation, BorderLayout.CENTER);
		JScrollPane scroller = new JScrollPane(eqnPanel);
		scroller.setBorder(BorderFactory.createEmptyBorder());

		// prediction panel
		createPredictionPanel();

		// model panel: equation + prediction
		JPanel modelPanel = new JPanel();
		modelPanel.setLayout(new BoxLayout(modelPanel, BoxLayout.Y_AXIS));
		modelPanel.add(scroller);
		modelPanel.add(predictionPanel);

		// put it all together
		regressionPanel = new JPanel(new BorderLayout(30, 0));
		regressionPanel.add(modelPanel, BorderLayout.CENTER);
		regressionPanel.add(cbPanel, loc.borderWest());
		regressionPanel.setBorder(BorderFactory.createTitledBorder(loc
				.getMenu("RegressionModel")));

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(regressionPanel, BorderLayout.CENTER);

		return mainPanel;
	}

	/**
	 * Creates a panel to evaluate the regression model for a given x value
	 */
	private void createPredictionPanel() {

		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		lblEvaluate = new JLabel();
		fldInputX = new MyTextFieldD(app);
		fldInputX.addActionListener(this);

		fldInputX.setColumns(6);
		lblOutputY = new JLabel();
		fldOutputY = new JLabel();

		p.add(lblEvaluate);
		p.add(new JLabel("x = "));
		p.add(fldInputX);
		p.add(new JLabel("y = "));
		p.add(lblOutputY);
		p.add(fldOutputY);

		predictionPanel = new JPanel(new BorderLayout());
		predictionPanel.add(p, loc.borderWest());

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
		int j = cbRegression.getSelectedIndex();
		ActionListener al = cbRegression.getActionListeners()[0];
		cbRegression.removeActionListener(al);
		cbRegression.removeAllItems();

		for (int i = 0; i < regressionLabels.length; i++) {
			cbRegression.addItem(regressionLabels[i]);
		}

		cbRegression.setSelectedIndex(j);
		cbRegression.addActionListener(al);
		((TitledBorder) regressionPanel.getBorder()).setTitle(app
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
				eqn = "";
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
			eqn = "\\text{" + loc.getPlain("NotAvailable") + "}";

		}

		// create an icon with the LaTeX string
		ImageIcon icon = GeoGebraIconD.createLatexIcon(app, eqn, this.getFont(),
				false, Color.RED, null);

		// set the label icon with our equation string
		lblRegEquation.setIcon(icon);
		lblRegEquation.revalidate();

		updateGUI();
	}

	/**
	 * Set the regression equation label to an empty string
	 */
	private void setRegressionEquationLabelEmpty() {
		lblRegEquation.setIcon(null);
		lblRegEquation.revalidate();

		updateGUI();
	}

	private void updateGUI() {

		cbPolyOrder.setVisible(daModel.getRegressionMode().equals(
				Regression.POLY));
		predictionPanel.setVisible(!(daModel.getRegressionMode()
				.equals(Regression.NONE)));
		repaint();

	}

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField) source);
		}

		else if (source == cbRegression) {
			cbRegression.removeActionListener(this);
			daModel.setRegressionMode(cbRegression.getSelectedIndex());
			updateRegressionPanel();
			cbRegression.addActionListener(this);
		}

		else if (source == cbPolyOrder) {
			daModel.setRegressionOrder(cbPolyOrder.getSelectedIndex() + 2);
			statDialog.getController().setRegressionGeo();
			statDialog.getController().updateRegressionPanel();
			setRegressionEquationLabel();

			// force update
			daModel.setRegressionMode(Regression.POLY.ordinal());
		}

	}

	private void doTextFieldActionPerformed(JTextField source) {
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

	public void updateFonts(Font font) {
		// TODO Auto-generated method stub

	}

	public void updatePanel() {
		// TODO Auto-generated method stub

	}

}
