package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.view.spreadsheet.statdialog.StatDialog.Regression;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class RegressionPanel extends JPanel implements ActionListener,
		StatPanelInterface {
	private static final long serialVersionUID = 1L;

	private AppD app;
	private StatDialog statDialog;

	// regression panel objects
	private JLabel lblRegEquation, lblEqn;
	
	@SuppressWarnings("rawtypes")
	private JComboBox cbRegression, cbPolyOrder;
	private JButton btnSwapXY;
	private JLabel lblEvaluate;
	private MyTextField fldInputX;
	private JLabel lblOutputY;

	private String[] regressionLabels;
	private MyTextField fldOutputY;
	private boolean isIniting = true;
	private JPanel predictionPanel;

	public RegressionPanel(AppD app, StatDialog statDialog) {

		this.app = app;
		this.statDialog = statDialog;
		this.setLayout(new BorderLayout());
		this.add(createRegressionPanel(), BorderLayout.CENTER);
		setLabels();
		updateRegressionPanel();
		updateGUI();
		isIniting = false;
	}

	private JPanel regressionPanel;

	@SuppressWarnings({ "unchecked", "rawtypes" })
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

		btnSwapXY = new JButton();
		btnSwapXY.setSelected(false);
		btnSwapXY.setMaximumSize(btnSwapXY.getPreferredSize());
		btnSwapXY.addActionListener(this);
		btnSwapXY.setFocusable(false);

		// panels
		JPanel cbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		cbPanel.add(cbRegression);
		cbPanel.add(cbPolyOrder);

		JPanel eqnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		eqnPanel.add(lblRegEquation);

		JPanel swapPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		swapPanel.add(btnSwapXY);

		JPanel modelPanel = new JPanel(new BorderLayout());
		modelPanel.add(cbPanel, BorderLayout.WEST);
		modelPanel.add(eqnPanel, BorderLayout.CENTER);
		JScrollPane scroller = new JScrollPane(modelPanel);
		scroller.setBorder(BorderFactory.createEmptyBorder());

		createPredictionPanel();
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(predictionPanel, BorderLayout.CENTER);
		southPanel.add(swapPanel, BorderLayout.WEST);

		regressionPanel = new JPanel(new BorderLayout());
		regressionPanel.add(scroller, BorderLayout.CENTER);
		regressionPanel.add(southPanel, BorderLayout.SOUTH);
		regressionPanel.setBorder(BorderFactory.createTitledBorder(app
				.getMenu("RegressionModel")));

		JPanel mainPanel = new JPanel(new BorderLayout());

		mainPanel.add(regressionPanel, BorderLayout.CENTER);
		mainPanel.setBorder(BorderFactory.createEtchedBorder());

		return mainPanel;
	}

	/**
	 * Creates a panel to evaluate the regression model for a given x value
	 */
	private void createPredictionPanel() {

		predictionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		lblEvaluate = new JLabel();
		fldInputX = new MyTextField(app);
		fldInputX.addActionListener(this);

		fldInputX.setColumns(6);
		lblOutputY = new JLabel();
		fldOutputY = new MyTextField(app);

		fldOutputY.setColumns(6);
		fldOutputY.setEditable(false);

		predictionPanel.add(lblEvaluate);
		predictionPanel.add(new JLabel("x = "));
		predictionPanel.add(fldInputX);
		predictionPanel.add(new JLabel("y = "));
		predictionPanel.add(lblOutputY);
		predictionPanel.add(fldOutputY);

	}

	public void updateRegressionPanel() {
		
		if (statDialog.getStatDialogController().isValidData()) {
			setRegressionEquationLabel();
			doTextFieldActionPerformed(fldInputX);
		} else {
			setRegressionEquationLabelEmpty();
		}
		updateGUI();

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

		String swapString = app.getMenu("Column.X") + " \u21C6 "
				+ app.getMenu("Column.Y");
		// btnSwapXY.setIcon(GeoGebraIcon.createLatexIcon(app, swapString,
		// app.getPlainFont(), false, Color.BLACK, null));
		btnSwapXY.setFont(app.getPlainFont());
		btnSwapXY.setText(swapString);
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
			if (statDialog.doSpecialNumberFormat()) {
				if (statDialog.getPrintDecimals() >= 0)
					highPrecision = StringTemplate.printDecimals(
							StringType.LATEX, statDialog.getPrintDecimals(),
							false);
				else
					highPrecision = StringTemplate.printFigures(
							StringType.LATEX, statDialog.getPrintFigures(),
							false);
			} else {
				highPrecision = StringTemplate.numericDefault;
			}
			
			// no regression
			if (statDialog.getRegressionMode().equals(Regression.NONE)
					|| statDialog.getRegressionModel() == null) {
				eqn = app.getPlain("");
			}

			// linear
			else if (statDialog.getRegressionMode().equals(Regression.LINEAR)) {
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
		ImageIcon icon = GeoGebraIcon.createLatexIcon(app, eqn, this.getFont(),
				false, Color.black, null);

		// set the label icon with our equation string
		lblRegEquation.setIcon(icon);
		lblRegEquation.revalidate();

		updateGUI();
	}

	/**
	 * Set the regression equation label to an empty string
	 */
	public void setRegressionEquationLabelEmpty() {
		lblRegEquation.setIcon(null);
		lblRegEquation.revalidate();

		updateGUI();
	}
	
	private void updateGUI() {

		cbPolyOrder.setVisible(statDialog.getRegressionMode().equals(
				Regression.POLY));
		predictionPanel.setVisible(!(statDialog.getRegressionMode()
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
			statDialog.setRegressionMode(cbRegression.getSelectedIndex());
			cbRegression.addActionListener(this);
		}

		else if (source == cbPolyOrder) {
			statDialog.setRegressionOrder(cbPolyOrder.getSelectedIndex() + 2);
			statDialog.getStatDialogController().setRegressionGeo();
			setRegressionEquationLabel();
		}

		else if (source == btnSwapXY) {
			statDialog.getStatDialogController().swapXY();
			// clear the prediction panel
			fldInputX.setText("");
			fldOutputY.setText("");
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

				// String str = "\"\" + " +
				// statDialog.getRegressionModel().getLabel() + "(" + value +
				// ")";
				// GeoText text =
				// app.getKernel().getAlgebraProcessor().evaluateToText(str,
				// false);

				double output = ((GeoFunctionable) statDialog
						.getRegressionModel()).getGeoFunction().evaluate(value);

				fldOutputY.setText(app.getKernel().format(output,
						StringTemplate.defaultTemplate));

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
