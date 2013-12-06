package geogebra.web.gui.view.probcalculator;

import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.gui.view.probcalculator.StatisticsCalculator;
import geogebra.common.main.App;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
public class StatisticsCalculatorW extends StatisticsCalculator implements ChangeHandler, ClickHandler, ValueChangeHandler<Boolean>, KeyHandler, FocusHandler {

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
		fldNullHyp.addKeyHandler(this);
		fldNullHyp.addFocusHandler(this);

		lblConfLevel = new Label();
		fldConfLevel = new AutoCompleteTextFieldW(app);
		fldConfLevel.setColumns(fieldWidth);
		fldConfLevel.addKeyHandler(this);
		fldConfLevel.addFocusHandler(this);

		lblSigma = new Label();
		fldSigma = new AutoCompleteTextFieldW(app);
		fldSigma.setColumns(fieldWidth);
		fldSigma.addKeyHandler(this);
		fldSigma.addFocusHandler(this);

		lblSampleStat1 = new Label[3];
		for (int i = 0; i < lblSampleStat1.length; i++) {
			lblSampleStat1[i] = new Label();
		}

		fldSampleStat1 = new AutoCompleteTextFieldW[3];
		for (int i = 0; i < fldSampleStat1.length; i++) {
			fldSampleStat1[i] = new AutoCompleteTextFieldW(app);
			fldSampleStat1[i].setColumns(fieldWidth);
			fldSampleStat1[i].addKeyHandler(this);
			fldSampleStat1[i].addFocusHandler(this);
		}

		lblSampleStat2 = new Label[3];
		for (int i = 0; i < lblSampleStat2.length; i++) {
			lblSampleStat2[i] = new Label();
		}

		fldSampleStat2 = new AutoCompleteTextFieldW[3];
		for (int i = 0; i < fldSampleStat2.length; i++) {
			fldSampleStat2[i] = new AutoCompleteTextFieldW(app);
			fldSampleStat2[i].setColumns(fieldWidth);
			fldSampleStat2[i].addKeyHandler(this);
			fldSampleStat2[i].addFocusHandler(this);
		}	    
	    
    }

	@Override
	public void updateResult() {
		// TODO Auto-generated method stub

	}

	public void onChange(ChangeEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	public void onClick(ClickEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	public void onValueChange(ValueChangeEvent<Boolean> event) {
	    // TODO Auto-generated method stub
	    
    }

	public void keyReleased(KeyEvent e) {
	    // TODO Auto-generated method stub
	    
    }

	public void onFocus(FocusEvent event) {
	    // TODO Auto-generated method stub
	    
    }

}
