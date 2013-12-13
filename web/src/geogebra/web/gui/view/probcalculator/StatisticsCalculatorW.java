package geogebra.web.gui.view.probcalculator;

import geogebra.common.gui.view.probcalculator.StatisticsCalculator;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.main.App;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RichTextArea;

/**
 * @author gabor
 * 
 * Statistics calculator for web
 *
 */
public class StatisticsCalculatorW extends StatisticsCalculator implements ChangeHandler, ClickHandler, ValueChangeHandler<Boolean>, FocusHandler, BlurHandler, KeyPressHandler {

	private FlowPanel wrappedPanel;
	private RichTextArea resultPane;
	private Label lblResult;
	private Label lblSampleHeader1;
	private Label lblSampleHeader2;
	private CheckBox ckPooled;
	private ListBox cbProcedure;
	private Button btnCalculate;
	private RadioButton btnLeft;
	private RadioButton btnRight;
	private RadioButton btnTwo;
	private Label lblNull;
	private Label lblHypParameter;
	private Label lblTailType;
	
	private AutoCompleteTextFieldW fldNullHyp;
	private int fieldWidth = 6;
	private Label lblConfLevel;
	private AutoCompleteTextFieldW fldConfLevel;
	private Label lblSigma;
	private AutoCompleteTextFieldW fldSigma;
	private Label[] lblSampleStat1;
	private AutoCompleteTextFieldW[] fldSampleStat1;
	private Label[] lblSampleStat2;
	private AutoCompleteTextFieldW[] fldSampleStat2;
	private FlowPanel panelControl;
	private FlowPanel panelBasicProcedures;
	private FlowPanel panelSample1;
	private FlowPanel panelSample2;
	private FlowPanel panelTestAndCI;
	private ChiSquarePanelW panelChiSquare;
	private FlowPanel scroller;
	private HandlerRegistration[] fldSampleStat1KeyHandlers;
	private HandlerRegistration[] fldSampleStat2KeyHandlers;

	public StatisticsCalculatorW(App app) {
		super(app);
		createGUI();
    }

	private void createGUI() {
	   
		this.wrappedPanel = new FlowPanel();
		
		createGUIElements();
		createControlPanel();
		setInputPanelLayout();
		
		panelChiSquare = new ChiSquarePanelW(app, this);
		
		//prepare result panel
		FlowPanel resultPanel = new FlowPanel();
		resultPanel.add(lblResult);
		resultPanel.add(resultPane);
		
		
		//procedure panel
		FlowPanel procedurePanel = new FlowPanel();
		procedurePanel.add(panelBasicProcedures);
		procedurePanel.add(panelChiSquare.getWrappedPanel());
		procedurePanel.add(resultPanel);
		
		FlowPanel procedureWrapper = new FlowPanel();
		procedureWrapper.add(procedurePanel);
		
		scroller = new FlowPanel(); //do with css
		scroller.add(procedureWrapper);
		
		//main content panel
		FlowPanel main = new FlowPanel();
		main.add(scroller);
		main.add(panelControl);
		
		wrappedPanel.add(main);
		
		setLabels();
		updateGUI();		
	    
    }

	void setLabels() {
		lblResult.setText(app.getMenu("Result"));
		lblNull.setText(app.getMenu("NullHypothesis"));
		lblTailType.setText(app.getMenu("AlternativeHypothesis"));
		lblConfLevel.setText(app.getMenu("ConfidenceLevel"));
		lblSigma.setText(app.getMenu("StandardDeviation.short"));
		btnCalculate.setText(app.getMenu("Calculate"));

		switch (selectedProcedure) {

		case ZMEAN2_TEST:
		case TMEAN2_TEST:
		case ZMEAN2_CI:
		case TMEAN2_CI:
		case ZPROP2_TEST:
		case ZPROP2_CI:
			lblSampleHeader1.setText(app.getMenu("Sample1"));
			break;

		default:
			lblSampleHeader1.setText(app.getMenu("Sample"));
		}

		lblSampleHeader2.setText(app.getMenu("Sample2"));

		ckPooled.setText(app.getMenu("Pooled"));

		setHypParameterLabel();
		setLabelStrings();
		setProcedureComboLabels();
		setSampleFieldLabels();

		panelChiSquare.setLabels();

		// reset the text in the result panel
		updateResult();

	}

	private void setHypParameterLabel() {
		switch (selectedProcedure) {

		case ZMEAN_TEST:
		case TMEAN_TEST:
			lblHypParameter.setText(app.getMenu("HypothesizedMean.short") + " = ");
			break;

		case ZMEAN2_TEST:
		case TMEAN2_TEST:
			lblHypParameter.setText(app.getMenu("DifferenceOfMeans.short")+ " = ");
			break;

		case ZPROP_TEST:
			lblHypParameter
					.setText(app.getMenu("HypothesizedProportion.short")+ " = ");
			break;

		case ZPROP2_TEST:
			lblHypParameter.setText(app
					.getMenu("DifferenceOfProportions.short")+ " = ");
			break;

		default:
			lblHypParameter.setText(app.getMenu(""));
		}
	}

	private void setProcedureComboLabels() {

		combolabelsPreprocess();

		cbProcedure.clear();
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZMEAN_TEST));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.TMEAN_TEST));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZMEAN2_TEST));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.TMEAN2_TEST));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZPROP_TEST));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZPROP2_TEST));

		//set separator here with css
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZMEAN_CI));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.TMEAN_CI));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZMEAN2_CI));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.TMEAN2_CI));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZPROP_CI));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZPROP2_CI));

		/// csscbProcedure.addItem(ListSeparatorRenderer.SEPARATOR);
		cbProcedure.addItem(mapProcedureToName.get(Procedure.GOF_TEST));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.CHISQ_TEST));

		//cbProcedure.setMaximumRowCount(cbProcedure.getItemCount());

		// TODO for testing only, remove later
		// cbProcedure.setSelectedItem(mapProcedureToName
		// .get(Procedure.CHISQ_TEST));

	}





	private void setSampleFieldLabels() {

		for (int i = 0; i < 3; i++) {
			lblSampleStat1[i].setText(null);
			lblSampleStat2[i].setText(null);
		}

		switch (selectedProcedure) {
		case ZMEAN_TEST:
		case ZMEAN_CI:
			lblSampleStat1[0].setText(strMean);
			lblSampleStat1[1].setText(strSigma);
			lblSampleStat1[2].setText(strN);
			break;

		case TMEAN_TEST:
		case TMEAN_CI:
			lblSampleStat1[0].setText(strMean);
			lblSampleStat1[1].setText(strSD);
			lblSampleStat1[2].setText(strN);
			break;

		case ZMEAN2_TEST:
		case ZMEAN2_CI:
			lblSampleStat1[0].setText(strMean);
			lblSampleStat1[1].setText(strSigma);
			lblSampleStat1[2].setText(strN);
			lblSampleStat2[0].setText(strMean);
			lblSampleStat2[1].setText(strSigma);
			lblSampleStat2[2].setText(strN);
			break;

		case TMEAN2_TEST:
		case TMEAN2_CI:
			lblSampleStat1[0].setText(strMean);
			lblSampleStat1[1].setText(strSD);
			lblSampleStat1[2].setText(strN);
			lblSampleStat2[0].setText(strMean);
			lblSampleStat2[1].setText(strSD);
			lblSampleStat2[2].setText(strN);
			break;

		case ZPROP_TEST:
		case ZPROP_CI:
			lblSampleStat1[0].setText(strSuccesses);
			lblSampleStat1[1].setText(strN);
			break;

		case ZPROP2_TEST:
		case ZPROP2_CI:
			lblSampleStat1[0].setText(strSuccesses);
			lblSampleStat1[1].setText(strN);
			lblSampleStat2[0].setText(strSuccesses);
			lblSampleStat2[1].setText(strN);
			break;

		}
	}

	private void setSampleFieldText() {

		for (int i = 0; i < 3; i++) {
			fldSampleStat1KeyHandlers[i].removeHandler();
			fldSampleStat2KeyHandlers[i].removeHandler();
			fldSampleStat1[i].setText(null);
			fldSampleStat2[i].setText(null);
		}

		switch (selectedProcedure) {
		case ZMEAN_TEST:
		case ZMEAN_CI:
		case TMEAN_TEST:
		case TMEAN_CI:
			fldSampleStat1[0].setText(format(sc.mean));
			fldSampleStat1[1].setText(format(sc.sd));
			fldSampleStat1[2].setText(format(sc.n));
			break;

		case ZMEAN2_TEST:
		case ZMEAN2_CI:
		case TMEAN2_TEST:
		case TMEAN2_CI:
			fldSampleStat1[0].setText(format(sc.mean));
			fldSampleStat1[1].setText(format(sc.sd));
			fldSampleStat1[2].setText(format(sc.n));
			fldSampleStat2[0].setText(format(sc.mean2));
			fldSampleStat2[1].setText(format(sc.sd2));
			fldSampleStat2[2].setText(format(sc.n2));
			break;

		case ZPROP_TEST:
		case ZPROP_CI:
			fldSampleStat1[0].setText(format(sc.count));
			fldSampleStat1[1].setText(format(sc.n));
			break;

		case ZPROP2_TEST:
		case ZPROP2_CI:
			fldSampleStat1[0].setText(format(sc.count));
			fldSampleStat1[1].setText(format(sc.n));
			fldSampleStat2[0].setText(format(sc.count2));
			fldSampleStat2[1].setText(format(sc.n2));
			break;
		}

		for (int i = 0; i < 3; i++) {
			fldSampleStat1KeyHandlers[i] = fldSampleStat1[i].addKeyPressHandler(this);
			fldSampleStat2KeyHandlers[i] = fldSampleStat2[i].addKeyPressHandler(this);
		}

		fldConfLevel.setText(format(sc.level));
		fldNullHyp.setText(format(sc.nullHyp));

	}

	private void updateGUI() {

		setHypParameterLabel();
		setSampleFieldLabels();
		setSampleFieldText();
		for (int i = 0; i < 3; i++) {
			lblSampleStat1[i].setVisible(lblSampleStat1[i].getText() != null);
			fldSampleStat1[i].setVisible(lblSampleStat1[i].getText() != null);
			lblSampleStat2[i].setVisible(lblSampleStat2[i].getText() != null);
			fldSampleStat2[i].setVisible(lblSampleStat2[i].getText() != null);
		}

		lblSampleHeader2.setVisible((lblSampleStat2[0].getText() != null));

		ckPooled.setVisible(selectedProcedure == Procedure.TMEAN2_TEST
				|| selectedProcedure == Procedure.TMEAN2_CI);

		setPanelLayout();

	}
	
	private void setPanelLayout() {

		panelBasicProcedures.setVisible(false);
		panelChiSquare.getWrappedPanel().setVisible(false);

		switch (selectedProcedure) {

		case CHISQ_TEST:
		case GOF_TEST:
			panelChiSquare.getWrappedPanel().setVisible(true);
			panelChiSquare.updateGUI();
			break;

		default:
			setInputPanelLayout();
			panelBasicProcedures.setVisible(true);
		}

	}

	private void setInputPanelLayout() {
	   if (panelBasicProcedures == null) {
		   panelBasicProcedures = new FlowPanel();
	   }
	   
	   if (panelSample1 == null) {
		   panelSample1 = new FlowPanel();
	   }
	   
	   if (panelSample2 == null) {
		   panelSample2 = new FlowPanel();
	   }
	   
	   if (panelTestAndCI == null) {
		   panelTestAndCI = new FlowPanel();
	   }
	   
	   // ---- add components
	   
	   panelBasicProcedures.clear();
	   panelSample1.clear();
	   panelSample2.clear();
	   panelTestAndCI.clear();
	   
	   panelSample1.add(lblSampleHeader1);
	   
	   for (int i = 0; i < lblSampleStat1.length; i++) {
			panelSample1.add(lblSampleStat1[i]);
			panelSample1.add(fldSampleStat1[i]);
		}
	   
	   panelSample2.add(new Label(" ")); //TODO: ?????????? CSS!!!!!
	   panelSample2.add(lblSampleHeader2);
	   
	   for (int i = 0; i < lblSampleStat2.length; i++) {
			panelSample2.add(lblSampleStat2[i]);
			panelSample2.add(fldSampleStat2[i]);
		}
	   
	   switch (selectedProcedure) {
		case ZMEAN_TEST:
		case ZMEAN2_TEST:
		case TMEAN_TEST:
		case TMEAN2_TEST:
		case ZPROP_TEST:
		case ZPROP2_TEST:

			if (app.getLocalization().isRightToLeftReadingOrder()) {
				// eg 1.1 = mu
				panelTestAndCI.add(fldNullHyp);
				panelTestAndCI.add(lblHypParameter);
			} else {
				// eg mu = 1.1
				panelTestAndCI.add(lblHypParameter);
				panelTestAndCI.add(fldNullHyp );				
			}
			panelTestAndCI.add(lblTailType);
			panelTestAndCI.add(btnLeft); 
			panelTestAndCI.add(btnRight);
			panelTestAndCI.add(btnTwo);
			panelTestAndCI.add(ckPooled);
			break;

		case ZMEAN_CI:
		case ZMEAN2_CI:
		case TMEAN_CI:
		case TMEAN2_CI:
		case ZPROP_CI:
		case ZPROP2_CI:

			panelTestAndCI.add(lblConfLevel);
			panelTestAndCI.add(fldConfLevel);
			panelTestAndCI.add(ckPooled);
			break;
		}

		
		panelBasicProcedures.add(panelSample1);

		
		panelBasicProcedures.add(panelSample2);

		

		panelBasicProcedures.add(panelTestAndCI);
	   
	   
	   
	   
    }

	private void createControlPanel() {
	   panelControl = new FlowPanel();
	   panelControl.add(cbProcedure);
    }

	private void createGUIElements() {
	    resultPane = new RichTextArea();
	    resultPane.setEnabled(false);
	    
	    s1 = new double[3];
	    s2 = new double[3];
	    
	    lblResult = new Label();
	    
	    lblSampleHeader1 = new Label();
	    lblSampleHeader2 = new Label();
	    
	    bodyText = new StringBuilder();
	    
	    ckPooled = new CheckBox();
	    ckPooled.setValue(false);
	    
	    cbProcedure = new ListBox();
	    cbProcedure.addChangeHandler(this);
	    
	    btnCalculate = new Button();
	    btnCalculate.addClickHandler(this);
	    
	    btnLeft = new RadioButton("tail");
	    btnLeft.setText(tail_left);
	    btnRight = new RadioButton("tail");
	    btnRight.setText(tail_right);
	    btnTwo = new RadioButton(tail_two);
	    btnTwo.setText(tail_two);
	    
	    FlowPanel group = new FlowPanel();
	    group.add(btnLeft);
	    group.add(btnRight);
	    group.add(btnTwo);
	    
	    btnLeft.addValueChangeHandler(this);
	    btnRight.addValueChangeHandler(this);
	    btnTwo.addValueChangeHandler(this);
	    btnTwo.setValue(true);
	    
	    lblNull = new Label();
		lblHypParameter = new Label();
		lblTailType = new Label();

		fldNullHyp = new AutoCompleteTextFieldW(app);
		fldNullHyp.setColumns(fieldWidth);
		fldNullHyp.addKeyPressHandler(this);
		fldNullHyp.addFocusHandler(this);

		lblConfLevel = new Label();
		fldConfLevel = new AutoCompleteTextFieldW(app);
		fldConfLevel.setColumns(fieldWidth);
		fldConfLevel.addKeyPressHandler(this);
		fldConfLevel.addFocusHandler(this);

		lblSigma = new Label();
		fldSigma = new AutoCompleteTextFieldW(app);
		fldSigma.setColumns(fieldWidth);
		fldSigma.addKeyPressHandler(this);
		fldSigma.addFocusHandler(this);

		lblSampleStat1 = new Label[3];
		for (int i = 0; i < lblSampleStat1.length; i++) {
			lblSampleStat1[i] = new Label();
		}

		fldSampleStat1 = new AutoCompleteTextFieldW[3];
		fldSampleStat1KeyHandlers = new HandlerRegistration[3];
		for (int i = 0; i < fldSampleStat1.length; i++) {
			fldSampleStat1[i] = new AutoCompleteTextFieldW(app);
			fldSampleStat1[i].setColumns(fieldWidth);
			fldSampleStat1KeyHandlers[i] = fldSampleStat1[i].addKeyPressHandler(this);
			fldSampleStat1[i].addFocusHandler(this);
		}

		lblSampleStat2 = new Label[3];
		for (int i = 0; i < lblSampleStat2.length; i++) {
			lblSampleStat2[i] = new Label();
		}

		fldSampleStat2 = new AutoCompleteTextFieldW[3];
		fldSampleStat2KeyHandlers = new HandlerRegistration[3];
		for (int i = 0; i < fldSampleStat2.length; i++) {
			fldSampleStat2[i] = new AutoCompleteTextFieldW(app);
			fldSampleStat2[i].setColumns(fieldWidth);
			fldSampleStat2KeyHandlers[i] = fldSampleStat2[i].addKeyPressHandler(this);
			fldSampleStat2[i].addFocusHandler(this);
		}	    
	    
    }

	@Override
	public void updateResult() {
		updateStatisticCollection();
		statProcessor.doCalculate();

		bodyText = new StringBuilder();
		bodyText.append(statHTML.getStatString());
		updateResultText();

		
	}
	
	private void updateResultText() {

		String htmlString = "<html><body>\n" + bodyText.toString()
				+ "</body>\n";
		resultPane.setText(htmlString);

	}
	
	private void updateStatisticCollection() {
		try {

			sc.level = parseNumberText(fldConfLevel.getText());
			sc.sd = parseNumberText(fldSigma.getText());
			sc.nullHyp = parseNumberText(fldNullHyp.getText());

			if (btnLeft.getValue()) {
				sc.tail = tail_left;
			} else if (btnRight.getValue()) {
				sc.tail = tail_right;
			} else {
				sc.tail = tail_two;
			}

			for (int i = 0; i < s1.length; i++) {
				s1[i] = (parseNumberText(fldSampleStat1[i].getText()));
			}
			for (int i = 0; i < s2.length; i++) {
				s2[i] = (parseNumberText(fldSampleStat2[i].getText()));
			}

			switch (selectedProcedure) {

			case ZMEAN_TEST:
			case ZMEAN_CI:
			case TMEAN_TEST:
			case TMEAN_CI:
				sc.mean = s1[0];
				sc.sd = s1[1];
				sc.n = s1[2];
				break;

			case ZMEAN2_TEST:
			case ZMEAN2_CI:
			case TMEAN2_TEST:
			case TMEAN2_CI:
				sc.mean = s1[0];
				sc.sd = s1[1];
				sc.n = s1[2];
				sc.mean2 = s2[0];
				sc.sd2 = s2[1];
				sc.n2 = s2[2];

				// force the null hypothesis to zero
				// TODO: allow non-zero values
				sc.nullHyp = 0;
				break;

			case ZPROP_TEST:
			case ZPROP_CI:
				sc.count = s1[0];
				sc.n = s1[1];
				break;

			case ZPROP2_TEST:
			case ZPROP2_CI:
				sc.count = s1[0];
				sc.n = s1[1];
				sc.count2 = s2[0];
				sc.n2 = s2[1];

				// force the null hypothesis to zero
				// TODO: allow non-zero values
				sc.nullHyp = 0;
				break;
			}

			sc.validate();
			setSampleFieldText();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}
	
	private double parseNumberText(String s) {

		if (s == null || s.length() == 0) {
			return Double.NaN;
		}

		try {
			String inputText = s.trim();

			// allow input such as sqrt(2)
			NumberValue nv;
			nv = cons.getKernel().getAlgebraProcessor()
					.evaluateToNumeric(inputText, false);
			return nv.getDouble();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return Double.NaN;
	}

	public void onChange(ChangeEvent event) {
		selectedProcedure = mapNameToProcedure.get(cbProcedure.getValue(cbProcedure.getSelectedIndex()));
		updateGUI();
		updateResult();
		//setLabels();

		// reset the scrollpane to the top
		//javax.swing.SwingUtilities.invokeLater(new Runnable() {
		//	public void run() {
		//		scroller.getVerticalScrollBar().setValue(0);
		//	}
		//});
    }

	public void onClick(ClickEvent event) {
	   doButtonEvents(event);
    }

	private void doButtonEvents(ClickEvent event) {
		updateResult();
    }

	public void onValueChange(ValueChangeEvent<Boolean> event) {
		if (event.getSource() == ckPooled) {
			sc.pooled = ckPooled.getValue();
			updateResult();
		}
	}

	public void onKeyPress(KeyPressEvent e) {
	   doTextFieldActionPerformed();
    }

	public void onFocus(FocusEvent event) {
	   if (event.getSource() instanceof AutoCompleteTextFieldW) {
		   ((AutoCompleteTextFieldW) event.getSource()).selectAll();
	   }
    }

	public void onBlur(BlurEvent event) {
	    if (event.getSource() instanceof AutoCompleteTextFieldW) {
	    	doTextFieldActionPerformed();
	    }
	    
    }

	private void doTextFieldActionPerformed() {
		 updateResult();
    }

	/**
	 * @return the wrapped Panel
	 */
	public FlowPanel getWrappedPanel() {
	    return wrappedPanel;
    }

}
