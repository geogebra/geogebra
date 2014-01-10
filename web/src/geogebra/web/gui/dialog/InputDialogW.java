package geogebra.web.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.common.gui.SetLabels;
import geogebra.common.gui.dialog.InputDialog;
import geogebra.common.gui.view.algebra.DialogType;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.OptionType;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class InputDialogW extends InputDialog implements ClickHandler,
        SetLabels, KeyUpHandler {

	protected AppW app;

	public static final int DEFAULT_COLUMNS = 30;
	public static final int DEFAULT_ROWS = 10;

	protected InputPanelW inputPanel;

	protected Button btApply, btProperties, btCancel, btOK, btHelp;
	protected DialogBox wrappedPopup;

	protected GeoElement geo;

	private String title;

	protected VerticalPanel messagePanel;

	
	public InputDialogW(boolean modal) {

		wrappedPopup = new DialogBox(false, false){
			
			// close dialog on ESC 
			@Override
			protected void onPreviewNativeEvent(final NativePreviewEvent event) {
				super.onPreviewNativeEvent(event);
				if (event.getTypeInt() == Event.ONKEYUP && event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {	
					setVisible(false);
					}
			}};
			
		wrappedPopup.addStyleName("DialogBox");
	}

	public InputDialogW(AppW app, String message, String title,
	        String initString, boolean autoComplete, InputHandler handler,
	        boolean modal, boolean selectInitText, GeoElement geo) {

		this(app, message, title, initString, autoComplete, handler, modal,
		        selectInitText, geo, DialogType.GeoGebraEditor);
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
	        boolean modal, boolean selectInitText, GeoElement geo, DialogType type) {

		this(modal);

		this.app = app;
		this.geo = geo;
		this.inputHandler = handler;
		this.initString = initString;

		createGUI(title, message, autoComplete, DEFAULT_COLUMNS, 1, true,
		        selectInitText, geo != null, geo != null, type);

		centerOnScreen();

	}

	public InputDialogW(AppW app, String message, String title,
	        String initString, boolean autoComplete, InputHandler handler,
	        GeoElement geo) {
		this(app, message, title, initString, autoComplete, handler, false,
		        false, geo);
	}

	public InputDialogW(AppW app2, String message, String title2, String initString,
            boolean autoComplete, InputHandler handler, boolean selectInitText) {
		this(app2, message, title2, initString, autoComplete, handler, false,
				selectInitText, null);
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

		this.title = title;

		// Create components to be displayed
		inputPanel = new InputPanelW(initString, app, rows, columns,
		        showSymbolPopupIcon/* , type */);
		
		// add key handler for ENTER if inputPanel uses a text field
		if (inputPanel.getTextComponent() != null) {
			inputPanel.getTextComponent().getTextField().addKeyUpHandler(this);
		}
		
		// message panel 
		messagePanel = new VerticalPanel();
		String[] lines = message.split("\n");
		for (String item : lines) {
			messagePanel.add(new Label(item));
		}
		messagePanel.addStyleName("Dialog-messagePanel");
		
		// create buttons
		btProperties = new Button();
		btProperties.addClickHandler(this);
		// btProperties.setActionCommand("OpenProperties");
		// btProperties.addActionListener(this);

		btOK = new Button();
		btOK.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btOK.addClickHandler(this);

		btCancel = new Button();
		btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btCancel.addClickHandler(this);

		btApply = new Button();
		btApply.addClickHandler(this);
		// btApply.setActionCommand("Apply");
		// btApply.addActionListener(this);

		// create button panel
		FlowPanel btPanel = new FlowPanel();
		btPanel.addStyleName("DialogButtonPanel");
		btPanel.add(btOK);
		btPanel.add(btCancel);
		// just tmp.
		if (showApply) {
			btPanel.add(btApply);
		}
		// if (showProperties) {
		// btPanel.add(btProperties);
		// }

		setLabels();


		VerticalPanel centerPanel = new VerticalPanel();
		centerPanel.addStyleName("Dialog-content");
		centerPanel.add(messagePanel);
		centerPanel.add(inputPanel);
		centerPanel.add(btPanel);

		wrappedPopup.setWidget(centerPanel);

	}
	
	/**
	 * Handles button clicks for dialog.
	 */	
    public void onClick(ClickEvent e) {
		actionPerformed(e);
	}
	
	protected void actionPerformed(DomEvent event) {
		Widget source = (Widget) event.getSource();
		if (source == btOK || source == inputPanel.getTextComponent().getTextField()) {
			inputText = inputPanel.getText();
			setVisible(!processInputHandler());
		} else if (source == btApply) {
			inputText = inputPanel.getText();
			processInputHandler();
		} else if (source == btProperties && geo != null) {
			setVisible(false);
			tempArrayList.clear();
			tempArrayList.add(geo);
			app.getDialogManager().showPropertiesDialog(OptionType.OBJECTS,
			        tempArrayList);
		} else if (source == btCancel) {
			setVisible(false);
		}
	}

	public void setVisible(boolean visible) {
		wrappedPopup.setVisible(visible);
		inputPanel.setVisible(visible);
		if (visible)
			inputPanel.setTextComponentFocus();
	}
	
	public void setLabels() {
		wrappedPopup.setText(title);
		btOK.setText(app.getPlain("OK"));
		btApply.setText(app.getPlain("Apply"));
		btCancel.setText(app.getPlain("Cancel"));
		btProperties.setText(app.getPlain("Properties") + "...");
	}

	@Override
    public void onKeyUp(KeyUpEvent event) {
		//enter press
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER){
			actionPerformed(event);
			return;
		}

	    
    }

}
