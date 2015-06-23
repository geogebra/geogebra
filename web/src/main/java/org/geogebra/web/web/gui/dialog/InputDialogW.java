package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.InputDialog;
import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.ErrorHandler;
import org.geogebra.web.web.gui.view.algebra.InputPanelW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class InputDialogW extends InputDialog implements ClickHandler,
        SetLabels, KeyUpHandler, ErrorHandler {

	protected AppW app;

	public static final int DEFAULT_COLUMNS = 30;
	public static final int DEFAULT_ROWS = 10;

	protected InputPanelW inputPanel;

	protected Button btApply, btProperties, btCancel, btOK, btHelp;
	protected DialogBox wrappedPopup;

	protected GeoElement geo;

	private String title;

	protected VerticalPanel messagePanel, errorPanel;

	protected FlowPanel btPanel;

	
	public InputDialogW(boolean modal) {

		wrappedPopup = new DialogBoxW(false, modal, this);
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
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			public void execute() {
				inputPanel.getTextComponent().setFocus(true);

			}
		});

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

	protected void centerOnScreen() {
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
			inputPanel.getTextComponent().getTextField().getValueBox().addKeyUpHandler(this);
			inputPanel.getTextComponent().addFocusListener(new FocusListenerW(this));
		}
		
		
		// message panel 
		messagePanel = new VerticalPanel();
		String[] lines = message.split("\n");
		for (String item : lines) {
			messagePanel.add(new Label(item));
		}
		messagePanel.addStyleName("Dialog-messagePanel");
		
		errorPanel = new VerticalPanel();
		errorPanel.addStyleName("Dialog-errorPanel");
		
		// create buttons
		btProperties = new Button();
		btProperties.addClickHandler(this);
	
		btOK = new Button();
		btOK.addClickHandler(this);

		btCancel = new Button();
		btCancel.addClickHandler(this);

		btApply = new Button();
		btApply.addClickHandler(this);
	
		// create button panel
		btPanel = new FlowPanel();
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
		centerPanel.add(errorPanel);
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
		if (source == btOK
		        || sourceShouldHandleOK(source)) {
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
			cancel();
		}
	}

	protected void cancel() {
		setVisible(false);
	}
	public void setVisible(boolean visible) {
		
		inputPanel.setVisible(visible);		
		if (visible){
			wrappedPopup.show();
			inputPanel.setTextComponentFocus();
		}else{
			forceHideKeyboard();
			wrappedPopup.hide();
			if(app!=null){
				app.setErrorHandler(null);
			}
		}
	};
	
	private native void forceHideKeyboard() /*-{
		if ($wnd.android && $wnd.android.callPlugin) {
			$wnd.android.callPlugin("CloseKeyboard", []);
		}

	}-*/;

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

	@Override
    public void showError(String msg) {
		if(msg == null){
			return;
		}
		errorPanel.clear();
		String[] lines = msg.split("\n");
		for (String item : lines) {
			errorPanel.add(new Label(item));
		}
	    
    }

	/**
	 * @param source the event source
	 * @return true if the source widget should handle the OK event
	 */
	protected boolean sourceShouldHandleOK(Object source) {
		return (inputPanel.getTextComponent() != null && source == inputPanel
                .getTextComponent().getTextField().getValueBox());
	}

	@Override
    public void setActive(boolean b) {
	    if(app != null){
	    	app.setErrorHandler(b ? this : null);
	    }
	    
    }
}
