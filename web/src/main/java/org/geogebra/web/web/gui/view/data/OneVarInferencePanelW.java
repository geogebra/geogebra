package org.geogebra.web.web.gui.view.data;

import java.util.ArrayList;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.inference.TTestImpl;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.view.data.StatisticsModel;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.LayoutUtil;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.web.gui.view.algebra.InputPanelW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * Extended JPanel that contains interactive sub-panels for performing one
 * variable inference with the current data set.
 * 
 * @author G. Sturr
 * 
 */
public class OneVarInferencePanelW extends FlowPanel implements ClickHandler, BlurHandler, StatPanelInterfaceW {
	private static final long serialVersionUID = 1L;
	// ggb fields
	private AppW app;
	private Kernel kernel;
	private DataAnalysisViewW statDialog;
	private StatTableW resultTable;
	
	// GUI
	private Label lblHypParameter, lblTailType, lblNull, lblConfLevel,lblSigma, lblResultHeader;
	private Button btnCalculate;
	private AutoCompleteTextFieldW fldNullHyp, fldConfLevel, fldSigma;
	private RadioButton btnLeft, btnRight, btnTwo;
	private ListBox lbAltHyp;
	private FlowPanel testPanel, intPanel, mainPanel, resultPanel;
	private FlowPanel sigmaPanel;
	private int fieldWidth = 6;
	
	// test type (tail)
	private static final String tail_left = "<";
	private static final String tail_right = ">";
	private static final String tail_two = ExpressionNodeConstants.strNOT_EQUAL;
	private String tail = tail_two;

	// input fields
	private double confLevel = .95, hypMean = 0, sigma = 1;

	// statistics
	double testStat, P, df, lower, upper, mean, se, me, N;
	private TTestImpl tTestImpl;
	private TDistributionImpl tDist;
	private NormalDistributionImpl normalDist;

	// flags
	private boolean isIniting;
	private boolean isTest = true;
	private boolean isZProcedure;
	
	private int selectedPlot = StatisticsModel.INFER_TINT;
	private LocalizationW loc;

	private class ParamKeyHandler implements KeyHandler {
		private Object source;
		public ParamKeyHandler(Object source) {
			this.source = source;
		}
		public void keyReleased(KeyEvent e) {
	        if (e.isEnterKey()) {
	        	actionPerformed(source);
	        }
        }
	}
	
	private class ParamBlurHandler implements BlurHandler {
		private Object source;
		public ParamBlurHandler(Object source) {
			this.source = source;
		}
		public void onBlur(BlurEvent event) {
			   actionPerformed(source);
		            
        }
		
	}
	
	
	/***************************************
	 * Construct a OneVarInference panel
	 */
	public OneVarInferencePanelW(AppW app, DataAnalysisViewW statDialog){

		isIniting = true;
		this.app = app;
		this.loc = (LocalizationW)app.getLocalization();
		this.kernel = app.getKernel();
		this.statDialog = statDialog;
		this.statDialog.getController().loadDataLists(true);
		
		this.createGUIElements();
		
		this.updateGUI();
		this.setLabels();

		isIniting = false;
	}
	


	//============================================================
	//           Create GUI 
	//============================================================

	private void createGUIElements(){


		btnLeft = new RadioButton(tail_left);
		btnRight = new RadioButton(tail_right);
		btnTwo = new RadioButton(tail_two);
		FlowPanel group = new FlowPanel();
		group.add(btnLeft);
		group.add(btnRight);
		group.add(btnTwo);
		btnLeft.addClickHandler(this);
		btnRight.addClickHandler(this);
		btnTwo.addClickHandler(this);
		btnTwo.setValue(true);

		lbAltHyp = new ListBox();
		lbAltHyp.addChangeHandler(new ChangeHandler() {
			
			public void onChange(ChangeEvent event) {
				actionPerformed(lbAltHyp);
			}
		});


		lblNull = new Label();
		lblNull.setStyleName("panelTitle");
		lblHypParameter = new Label();
		lblTailType = new Label();
		lblTailType.setStyleName("panelTitle");
		
		fldNullHyp = (new InputPanelW(null, app, -1, false)).getTextComponent();
		fldNullHyp.setColumns(fieldWidth);
		fldNullHyp.setText("" + 0);
		fldNullHyp.addKeyHandler(new ParamKeyHandler(fldNullHyp));
		fldNullHyp.addBlurHandler(new ParamBlurHandler(fldNullHyp));
		lblConfLevel = new Label();
		lblConfLevel.setStyleName("panelTitle");
		fldConfLevel = (new InputPanelW(null, app, -1, false)).getTextComponent();
		fldConfLevel.setColumns(fieldWidth);
		fldConfLevel.addKeyHandler(new ParamKeyHandler(fldConfLevel));
		fldConfLevel.addBlurHandler(new ParamBlurHandler(fldConfLevel));

		lblSigma = new Label();
		fldSigma = (new InputPanelW(null, app, -1, false)).getTextComponent();
		fldSigma.setColumns(fieldWidth);
		fldSigma.addKeyHandler(new ParamKeyHandler(fldSigma));
		fldSigma.addBlurHandler(new ParamBlurHandler(fldSigma));

		btnCalculate = new Button();
		lblResultHeader = new Label();
		lblResultHeader.setStyleName("panelTitle");

		sigmaPanel = new FlowPanel();
		sigmaPanel.add(LayoutUtil.panelRowIndent(lblSigma, fldSigma));
//		// test panel	
		testPanel = new FlowPanel();
		testPanel.add(lblNull);
		testPanel.add(LayoutUtil.panelRowIndent(lblHypParameter, fldNullHyp));
		testPanel.add(lblTailType);
		testPanel.add(LayoutUtil.panelRowIndent(lbAltHyp));
//		// CI panel	
		intPanel = new FlowPanel();
		intPanel.add(lblConfLevel);
		intPanel.add(fldConfLevel);	
//
//		// result panel	
		resultTable = new StatTableW(app);
		resultTable.setStyleName("daStatistics");
		setResultTable();

		
		resultPanel = new FlowPanel();
		resultPanel.add(lblResultHeader);
		resultPanel.add(resultTable);
//
//
//
		// main panel
		mainPanel = new FlowPanel();
		add(mainPanel);
		add(resultPanel);
	}


	private void updateMainPanel(){
		App.debug("updateMainPanel()");
		mainPanel.clear();

		if(isZProcedure) {
			mainPanel.add(sigmaPanel);
		}

		if(isTest) {
			mainPanel.add(testPanel);
		}
		else {
			mainPanel.add(intPanel);
		}

		mainPanel.add(resultPanel);


	}



	private void  setResultTable(){

		ArrayList<String> nameList = new ArrayList<String>();

		switch (selectedPlot){
		case StatisticsModel.INFER_ZTEST:
			nameList.add(loc.getMenu("PValue"));
			nameList.add(loc.getMenu("ZStatistic")); 
			nameList.add(loc.getMenu(""));
			nameList.add(loc.getMenu("Length.short"));
			nameList.add(loc.getMenu("Mean"));

			break;

		case StatisticsModel.INFER_TTEST:
			nameList.add(loc.getMenu("PValue"));
			nameList.add(loc.getMenu("TStatistic"));
			nameList.add(loc.getMenu("DegreesOfFreedom.short"));
			nameList.add(loc.getMenu("StandardError.short"));
			nameList.add(loc.getMenu(""));
			nameList.add(loc.getMenu("Length.short"));
			nameList.add(loc.getMenu("Mean"));
			break;

		case StatisticsModel.INFER_ZINT:
			nameList.add(loc.getMenu("Interval"));
			nameList.add(loc.getMenu("LowerLimit"));
			nameList.add(loc.getMenu("UpperLimit"));
			nameList.add(loc.getMenu("MarginOfError"));
			nameList.add(loc.getMenu(""));
			nameList.add(loc.getMenu("Length.short"));
			nameList.add(loc.getMenu("Mean"));
			break;

		case StatisticsModel.INFER_TINT:
			nameList.add(loc.getMenu("Interval"));
			nameList.add(loc.getMenu("LowerLimit"));
			nameList.add(loc.getMenu("UpperLimit"));
			nameList.add(loc.getMenu("MarginOfError"));
			nameList.add(loc.getMenu("DegreesOfFreedom.short"));
			nameList.add(loc.getMenu("StandardError.short"));
			nameList.add(loc.getMenu(""));
			nameList.add(loc.getMenu("Length.short"));
			nameList.add(loc.getMenu("Mean"));
			break;
		}

		String[] rowNames = new String[nameList.size()];
		nameList.toArray(rowNames);
		resultTable.setStatTable(rowNames.length, rowNames, 2, null);

	}


	private void updateResultTable(){


		evaluate();
		String cInt = statDialog.format(mean) + " \u00B1 "  + statDialog.format(me);
		
		switch (selectedPlot){
		case StatisticsModel.INFER_ZTEST:
			resultTable.setValueAt(statDialog.format(P),0,1);
			resultTable.setValueAt(statDialog.format(testStat), 1, 1);
			resultTable.setValueAt("", 2, 1);
			resultTable.setValueAt(statDialog.format(N), 3, 1);
			resultTable.setValueAt(statDialog.format(mean), 4, 1);
			break;

		case StatisticsModel.INFER_TTEST:
			resultTable.setValueAt(statDialog.format(P),0,1);
			resultTable.setValueAt(statDialog.format(testStat), 1, 1);
			resultTable.setValueAt(statDialog.format(df), 2, 1);
			resultTable.setValueAt(statDialog.format(se), 3, 1);
			resultTable.setValueAt("", 4, 1);
			resultTable.setValueAt(statDialog.format(N), 5, 1);
			resultTable.setValueAt(statDialog.format(mean), 6, 1);	
			break;

		case StatisticsModel.INFER_ZINT:
			resultTable.setValueAt(cInt,0,1);
			resultTable.setValueAt(statDialog.format(lower),1,1);
			resultTable.setValueAt(statDialog.format(upper), 2, 1);
			resultTable.setValueAt(statDialog.format(me), 3, 1);
			resultTable.setValueAt("", 4, 1);
			resultTable.setValueAt(statDialog.format(N), 5, 1);
			resultTable.setValueAt(statDialog.format(mean), 6, 1);
			break;

		case StatisticsModel.INFER_TINT:
			resultTable.setValueAt(cInt,0,1);
			resultTable.setValueAt(statDialog.format(lower),1,1);
			resultTable.setValueAt(statDialog.format(upper), 2, 1);
			resultTable.setValueAt(statDialog.format(me), 3, 1);
			resultTable.setValueAt(statDialog.format(df), 4, 1);
			resultTable.setValueAt(statDialog.format(se), 5, 1);
			resultTable.setValueAt("", 6, 1);
			resultTable.setValueAt(statDialog.format(N), 7, 1);
			resultTable.setValueAt(statDialog.format(mean), 8, 1);
			break;
		}

	}




	//============================================================
	//           Updates and Event Handlers
	//============================================================

	
	public void setLabels() {

		lblHypParameter.setText(loc.getMenu("HypothesizedMean.short") + " = " );
		lblNull.setText(loc.getMenu("NullHypothesis") + ": ");
		lblTailType.setText(loc.getMenu("AlternativeHypothesis") + ": ");
		lblConfLevel.setText(loc.getMenu("ConfidenceLevel") + ": ");
		lblResultHeader.setText(loc.getMenu("Result") + ": ");
		lblSigma.setText(loc.getMenu("StandardDeviation.short") + " = ");
		btnCalculate.setText(loc.getMenu("Calculate"));
	}


	/** Helper method for updateGUI() */
	private void updateNumberField(AutoCompleteTextFieldW fld,  double n){
		fld.setText(statDialog.format(n));
		//fld.setCaretPosition(0);
	}

	private void updateGUI(){

		isTest = (selectedPlot == StatisticsModel.INFER_ZTEST
				|| selectedPlot == StatisticsModel.INFER_TTEST);

		isZProcedure = selectedPlot == StatisticsModel.INFER_ZTEST
		|| selectedPlot == StatisticsModel.INFER_ZINT;

		updateNumberField(fldNullHyp, hypMean);
		updateNumberField(fldConfLevel, confLevel);
		updateNumberField(fldSigma, sigma);
		updateCBAlternativeHyp();
		setResultTable();
		updateResultTable();	
		updateMainPanel();
	}


	private void updateCBAlternativeHyp(){
		lbAltHyp.clear();
		lbAltHyp.addItem(loc.getMenu("HypothesizedMean.short") + " " + tail_right + " " + statDialog.format(hypMean));
		lbAltHyp.addItem(loc.getMenu("HypothesizedMean.short") + " " + tail_left + " " + statDialog.format(hypMean));
		lbAltHyp.addItem(loc.getMenu("HypothesizedMean.short") + " " + tail_two + " " + statDialog.format(hypMean));

		if(tail == tail_right)
			lbAltHyp.setSelectedIndex(0);
		else if(tail == tail_left)
			lbAltHyp.setSelectedIndex(1);
		else
			lbAltHyp.setSelectedIndex(2);


	}



	public void actionPerformed(Object source) {
		if(isIniting) {
			return;
		}

		if (source instanceof AutoCompleteTextFieldW) {
			doTextFieldActionPerformed((AutoCompleteTextFieldW)source);
		}

		else if(source == lbAltHyp){

			if(lbAltHyp.getSelectedIndex() == 0)
				tail = tail_right;
			else if(lbAltHyp.getSelectedIndex() == 1)
				tail = tail_left;
			else
				tail = tail_two;

			evaluate();
			updateResultTable();
		}

	}

	private void doTextFieldActionPerformed(AutoCompleteTextFieldW source) {
		if(isIniting) return;

		Double value = Double.parseDouble(source.getText().trim());

		if(source == fldConfLevel){
			confLevel = value;
			evaluate();
			updateGUI();
		}

		else if(source == fldNullHyp){
			hypMean = value;
			evaluate();
			updateGUI();
		}

		else if(source == fldSigma){
			sigma = value;
			evaluate();
			updateGUI();
		}

	}


	public void setSelectedPlot(int selectedPlot){
		this.selectedPlot = selectedPlot;
		updateGUI();
	}

	public void updatePanel(){
		evaluate();
		updateGUI();
		updateResultTable();
	}





	//============================================================
	//          Computation
	//============================================================


	private void evaluate(){

		GeoList dataList = statDialog.getController().getDataSelected();
		double[] sample = statDialog.getController().getValueArray(dataList);

		mean = StatUtils.mean(sample);
		N = sample.length;

		try {
			switch (selectedPlot){

			case StatisticsModel.INFER_ZTEST:
			case StatisticsModel.INFER_ZINT:
				normalDist = new NormalDistributionImpl(0,1);
				se = sigma/Math.sqrt(N);
				testStat = (mean - hypMean)/se;
				P = 2.0 * normalDist.cumulativeProbability(-Math.abs(testStat));
				P = adjustedPValue(P, testStat, tail);

				double zCritical = normalDist.inverseCumulativeProbability((confLevel + 1d)/2);
				me  =  zCritical * se;
				upper = mean + me;
				lower = mean - me;
				break;

			case StatisticsModel.INFER_TTEST:
			case StatisticsModel.INFER_TINT:
				if(tTestImpl == null)
					tTestImpl = new TTestImpl();
				se = Math.sqrt(StatUtils.variance(sample)/N);
				df = N-1;
				testStat = tTestImpl.t(hypMean, sample);
				P = tTestImpl.tTest(hypMean, sample);
				P = adjustedPValue(P, testStat, tail);

				tDist = new TDistributionImpl(N - 1);
				double tCritical = tDist.inverseCumulativeProbability((confLevel + 1d)/2);
				me  =  tCritical * se;
				upper = mean + me;
				lower = mean - me;
				break;
			}


		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MathException e) {
			e.printStackTrace();
		}

	}


	private double adjustedPValue(double p, double testStatistic, String tail){

		// two sided test
		if(tail.equals(tail_two)) 
			return p;

		// one sided test
		else if((tail.equals(tail_right) && testStatistic > 0)
				|| (tail.equals(tail_left) && testStatistic < 0))
			return p/2;
		else
			return 1 - p/2;
	}


	protected double evaluateExpression(String expr){

		NumberValue nv;

		try {
			nv = kernel.getAlgebraProcessor().evaluateToNumeric(expr, false);
		} catch (Exception e) {
			e.printStackTrace();
			return Double.NaN;
		}	
		return nv.getDouble();
	}



	//============================================================
	//           GUI  Utilities
	//============================================================





	public void onFocus(FocusEvent event) {
	    // TODO Auto-generated method stub
    }



	public void onClick(ClickEvent event) {
	    actionPerformed(event.getSource());
    }



	public void onBlur(BlurEvent event) {
	    // TODO Auto-generated method stub
		doTextFieldActionPerformed((AutoCompleteTextFieldW)(event.getSource()));
   
    }


}
