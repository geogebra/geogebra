package geogebra.web.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.common.gui.dialog.InputDialog;
import geogebra.common.gui.view.algebra.DialogType;
import geogebra.common.main.OptionType;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class InputDialogW extends  InputDialog implements ClickHandler{
	
	protected AppW app;

	public static final int DEFAULT_COLUMNS = 30;
	public static final int DEFAULT_ROWS = 10;
	
	protected InputPanelW inputPanel;
		
	protected Button btApply, btProperties, btCancel, btOK, btHelp;
	protected PopupPanel wrappedPopup;

	protected GeoElement geo;

	private CheckBox checkBox;

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
		this(modal);
		this.app = app;
		this.geo = geo;
		this.inputHandler = handler;
		this.initString = initString;
		this.checkBox = checkBox;
		
		createGUI(title, message, autoComplete, DEFAULT_COLUMNS, 1, true, selectInitText, geo != null, geo != null, type);
		
		centerOnScreen();
		
		
	}
	
	

	private void centerOnScreen() {
	    wrappedPopup.center();
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
		btProperties = new Button(app.getPlain("OpenProperties"));
		btProperties.addClickHandler(this);
//		btProperties.setActionCommand("OpenProperties");
//		btProperties.addActionListener(this);
		btOK = new Button(app.getPlain("OK"));
		btOK.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btOK.addClickHandler(this);
		btCancel = new Button(app.getPlain("Cancel"));
		btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btCancel.addClickHandler(this);
		btApply = new Button(app.getPlain("Apply"));
		btApply.addClickHandler(this);
//		btApply.setActionCommand("Apply");
//		btApply.addActionListener(this);

		Hyperlink linkDownload = new Hyperlink();
		linkDownload.setText(app.getPlain("Download"));
		
		linkDownload.setStyleName("gwt-Button");
		linkDownload.addStyleName("linkDownload");
		linkDownload.getElement().setAttribute("style", "display:inline");
		
		// create button panel
		FlowPanel btPanel = new FlowPanel();
		btPanel.addStyleName("DialogButtonPanel");
		btPanel.add(btOK);
		btPanel.add(linkDownload);
		btPanel.add(btCancel);
		//just tmp.
		if (showApply) {
			btPanel.add(btApply);
		}
		if (showProperties) {
			btPanel.add(btProperties);
		}


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
	    Widget source = (Widget) event.getSource();
	    if (source == btOK) {
	    	inputText = inputPanel.getText();
	    	setVisible(!processInputHandler());
	    } else if (source == btApply) {
	    	inputText = inputPanel.getText();
	    	processInputHandler();
	    } else if (source == btProperties && geo != null) {
	    	setVisible(false);
	    	tempArrayList.clear();
	    	tempArrayList.add(geo);
	    	app.getDialogManager().showPropertiesDialog(OptionType.OBJECTS, tempArrayList);
	    } else if (source == btCancel) {
	    	setVisible(false);
	    }
    }
	
	public void setVisible(boolean visible) {
		wrappedPopup.setVisible(visible);
	}

}
