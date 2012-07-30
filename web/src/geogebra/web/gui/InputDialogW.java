package geogebra.web.gui;

import geogebra.common.gui.InputHandler;
import geogebra.common.gui.dialog.InputDialog;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.web.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.gui.view.algebra.InputPanelW.DialogType;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InputDialogW extends  InputDialog implements ClickHandler{
	
	protected AppW app;

	public static final int DEFAULT_COLUMNS = 30;
	public static final int DEFAULT_ROWS = 10;
	
	protected InputPanelW inputPanel;
		
	protected Button btCancel, btOK;
	protected String initString;

	protected PopupPanel wrappedPopup;
	
	public InputDialogW(boolean modal) {
	    this.wrappedPopup = new PopupPanel(false, modal);
    }
	
	public InputDialogW(AppW app, String message, String title,
			String initString, boolean autoComplete, InputHandler handler,
			boolean modal, boolean selectInitText, GeoElement geo) {
		this(app, message, title, initString, autoComplete, handler, modal,
				selectInitText, geo, null, DialogType.GeoGebraEditor);
	}
	/**
	 * @param app
	 * @param message
	 * @param title
	 * @param initString
	 * @param autoComplete
	 * @param handler
	 * @param modal
	 * @param selectInitText
	 * @param geo
	 * @param checkBox
	 * @param type
	 */
	public InputDialogW(AppW app, String message, String title,
			String initString, boolean autoComplete, InputHandler handler,
			boolean modal, boolean selectInitText, GeoElement geo,
			CheckBox checkBox, DialogType type) {
		//continue here
		
	}
	
	

	/**
	 * @param title
	 * @param message
	 * @param autoComplete
	 * @param columns
	 * @param rows
	 * @param showSymbolPopupIcon
	 * @param selectInitText
	 * @param showProperties
	 * @param showApply
	 * @param type
	 */
	protected void createGUI(String title, String message,
			boolean autoComplete, int columns, int rows,
			boolean showSymbolPopupIcon, boolean selectInitText,
			boolean showProperties, boolean showApply, DialogType type) {

		// Create components to be displayed
		inputPanel = new InputPanelW(initString, app, rows, columns,
				showSymbolPopupIcon/*, type*/);

		
		// create buttons
//		btProperties = new JButton();
//		btProperties.setActionCommand("OpenProperties");
//		btProperties.addActionListener(this);
		btOK = new Button(app.getPlain("OK"));
		btOK.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btOK.addClickHandler(this);
		btCancel = new Button(app.getPlain("Cancel"));
		btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btCancel.addClickHandler(this);
//		btApply = new JButton();
//		btApply.setActionCommand("Apply");
//		btApply.addActionListener(this);

		// create button panel
		HorizontalPanel btPanel = new HorizontalPanel();
		btPanel.add(btOK);
		btPanel.add(btCancel);


		// =====================================================================
		// Create the optionPane: a panel with message label on top, button
		// panel on bottom. The center panel holds the inputPanel, which is
		// added later.
		// =====================================================================

		VerticalPanel centerPanel = new VerticalPanel();
		centerPanel.add(inputPanel);
		centerPanel.add(btPanel);
		
		wrappedPopup.setWidget(centerPanel);
	}
	
	public void onClick(ClickEvent event) {
	    // do nothing - overriden method
	    
    }
	
	public void setVisible(boolean visible) {
		wrappedPopup.setVisible(visible);
	}

}
