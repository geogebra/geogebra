package geogebra.web.gui.inputbar;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.GWTKeycodes;
import geogebra.common.main.MyError;
import geogebra.web.Web;
import geogebra.web.Web.GuiToLoad;
import geogebra.web.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * @author gabor
 * 
 * InputBar for GeoGebraWeb
 *
 */
public class AlgebraInputW extends HorizontalPanel implements KeyUpHandler, FocusHandler, ClickHandler, BlurHandler {
	
	private AppW app;
	private Label inputLabel;
	private InputPanelW inputPanel;
	private AutoCompleteTextFieldW inputField;
	private ToggleButton btnHelpToggle;

	/**
	 * Creates AlgebraInput for Web
	 */
	AlgebraInputW() {
		super();	
	}
	
	/**
	 * @param app Application
	 * 
	 * Attaches Application and creates the GUI of AlgebraInput
	 */
	public void init(AppW app) {
		this.app = app;
		//AG I dont think we need this app.removeTraversableKeys(this);
		addStyleName("AlgebraInput");
		initGUI();
		app.getGuiManager().addAlgebraInput(this);
	}

	private void initGUI() {
	    clear();
	    inputLabel = new Label();
	    inputPanel = new InputPanelW(null,app,30,true);
	    
	    inputField = inputPanel.getTextComponent();
	    int inputWidth;
	    if(Web.currentGUI.equals(GuiToLoad.VIEWER)){	    
	    	inputWidth = app.getDataParamWidth()-120;
	    } else {
	    	inputWidth = Window.getClientWidth() - 120;
	    }
	    inputField.getTextBox().setWidth(inputWidth+"px");
	    
	    inputField.getTextBox().addKeyUpHandler(this);
	    inputField.getTextBox().addFocusHandler(this);
	    inputField.getTextBox().addBlurHandler(this);
	    
	    inputField.addHistoryPopup(app.showInputTop());
	    
	    //AG updateFonts()
	    
	    //AG not needed yet btnHelpToggle = new ToggleButton();
	    ///btnHelpToggle.addStyleName("btnHelpToggle");
	    
	    //btnHelpToggle.addClickHandler(this);
	    
	   //in CSS btnHelpToggle.setIcon(app.getImageIcon("inputhelp_left_18x18.png"));
	   //in CSS	btnHelpToggle.setSelectedIcon(app.getImageIcon("inputhelp_right_18x18.png"));
	    
	    HorizontalPanel labelPanel = new HorizontalPanel();
	    labelPanel.setHorizontalAlignment(ALIGN_RIGHT);
	    labelPanel.setVerticalAlignment(ALIGN_MIDDLE);
	    labelPanel.add(inputLabel);
	    
	    HorizontalPanel eastPanel = new HorizontalPanel();
	    eastPanel.setHorizontalAlignment(ALIGN_RIGHT);
	    eastPanel.setVerticalAlignment(ALIGN_MIDDLE);
	    /*AGif (app.showInputHelpToggle()) {
	    	eastPanel.add(btnHelpToggle);
	    }*/
	    
	    add(labelPanel);
	    setCellHorizontalAlignment(labelPanel, ALIGN_RIGHT);
	    setCellVerticalAlignment(labelPanel, ALIGN_MIDDLE);
	    add(inputPanel);
	    setCellHorizontalAlignment(inputPanel, ALIGN_LEFT);
	    setCellVerticalAlignment(inputPanel, ALIGN_MIDDLE);
	    add(eastPanel);
	    setCellHorizontalAlignment(eastPanel, ALIGN_LEFT);
	    setCellVerticalAlignment(eastPanel, ALIGN_MIDDLE);
	    
	    setLabels();
	    
    }
	
	/**
	 * updates labels according to current locale
	 */
	public void setLabels() {
		if (inputLabel != null)
			inputLabel.setText( app.getPlain("InputLabel") + ":");

		if(btnHelpToggle!=null)
			btnHelpToggle.setTitle(app.getMenu("InputHelp"));
		
		inputField.setDictionary(app.getCommandDictionary());
	}	
	
	
	/**
	 * Sets the content of the input textfield and gives focus
	 * to the input textfield.
	 * @param str 
	 */
	public void replaceString(String str) {
		inputField.setText(str);
	}
	
	// see actionPerformed
		public void insertCommand(String cmd) {
			if (cmd == null) return;

			int pos = inputField.getCaretPosition();
			String oldText = inputField.getText();
			String newText = 
				oldText.substring(0, pos) + 
				cmd + "[]" +
				oldText.substring(pos);			 			

			inputField.setText(newText);
			inputField.setCaretPosition(pos + cmd.length() + 1);		
			inputField.requestFocus();
		}

		public void insertString(String str) {
			if (str == null) return;

			int pos = inputField.getCaretPosition();
			String oldText = inputField.getText();
			String newText = 
				oldText.substring(0, pos) + str +
				oldText.substring(pos);			 			

			inputField.setText(newText);
			inputField.setCaretPosition(pos + str.length());		
			inputField.requestFocus();
		}

	public void onFocus(FocusEvent event) {
		Object source = event.getSource();
		AutoCompleteTextFieldW.showSymbolButtonIfExists(source, true);
		app.clearSelectedGeos();
    }
	
	public void onBlur(BlurEvent event) {
		Object source = event.getSource();
		AutoCompleteTextFieldW.showSymbolButtonIfExists(source, false);
	}

	public void onKeyUp(KeyUpEvent event) {
				// the input field may have consumed this event
				// for auto completion
				//then it don't come here if (e.isConsumed()) return;

				int keyCode = event.getNativeKeyCode();
				if (keyCode == GWTKeycodes.KEY_ENTER && !inputField.isSuggestionJustHappened()) {
					app.getKernel().clearJustCreatedGeosInViews();
					String input = inputField.getText();					   
					if (input == null || input.length() == 0)
					{
						app.getActiveEuclidianView().requestFocusInWindow(); // Michael Borcherds 2008-05-12
						return;
					}

					app.setScrollToShow(true);
					GeoElement[] geos;
					try {
						if (input.startsWith("/")) {
							String cmd = input.substring(1);
							app.getPythonBridge().eval(cmd);
							geos = new GeoElement[0];
						} else {
							geos = app.getKernel().getAlgebraProcessor().processAlgebraCommandNoExceptionHandling( input, true, false, true );
							
							// need label if we type just eg
							// lnx
							if (geos.length == 1 && !geos[0].labelSet) {
								geos[0].setLabel(geos[0].getDefaultLabel());
							}

						}
					} catch (Exception ee) {
						inputField.showError(ee);
						return;
					}
				 catch (MyError ee) {
					inputField.showError(ee);
					return;
				 }
					
					// create texts in the middle of the visible view
					// we must check that size of geos is not 0 (ZoomIn, ZoomOut, ...)
					if (geos.length > 0 && geos[0] != null && geos[0].isGeoText()) {
						GeoText text = (GeoText)geos[0];
						if (!text.isTextCommand() && text.getStartPoint() == null) {

							Construction cons = text.getConstruction();
							EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();

							boolean oldSuppressLabelsStatus = cons.isSuppressLabelsActive();
							cons.setSuppressLabelCreation(true);
							GeoPoint p = new GeoPoint(text.getConstruction(), null, ( ev.getXmin() + ev.getXmax() ) / 2, ( ev.getYmin() + ev.getYmax() ) / 2, 1.0);
							cons.setSuppressLabelCreation(oldSuppressLabelsStatus);

							try {
								text.setStartPoint(p);
								text.update();
							} catch (CircularDefinitionException e1) {
								e1.printStackTrace();
							}
						}
					}

					app.setScrollToShow(false);

											   
					inputField.addToHistory(input);
					inputField.setText(null);  							  			   
								  
				} else app.getGlobalKeyDispatcher().handleGeneralKeys(event); // handle eg ctrl-tab
				inputField.setIsSuggestionJustHappened(false);
    }

	public void onClick(ClickEvent event) {
		Object source = event.getSource();

		if (source == btnHelpToggle) { 
			if(btnHelpToggle.isDown()){
				InputBarHelpPanelW helpPanel = (InputBarHelpPanelW) app.getGuiManager().getInputHelpPanel();
				helpPanel.setLabels();
				helpPanel.setCommands();
				app.setShowInputHelpPanel(true);
			}else{
				app.setShowInputHelpPanel(false);
			}
		}
    }

}
