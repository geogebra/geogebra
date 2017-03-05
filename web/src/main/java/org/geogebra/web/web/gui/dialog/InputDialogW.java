package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.InputDialog;
import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.Unicode;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.gui.GDialogBox;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.view.algebra.InputPanelW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class InputDialogW extends InputDialog implements ClickHandler,
		SetLabels, KeyUpHandler, KeyPressHandler {

	protected final AppW app;

	public static final int DEFAULT_COLUMNS = 30;
	public static final int DEFAULT_ROWS = 10;

	protected InputPanelW inputPanel;

	protected Button btApply, btProperties, btCancel, btOK, btHelp;
	protected GDialogBox wrappedPopup;

	protected GeoElement geo;

	private String title;

	protected VerticalPanel messagePanel, errorPanel;

	protected FlowPanel btPanel;

	protected final Localization loc;
	
	public InputDialogW(boolean modal, AppW app, boolean hasKeyboard){
		this.app = app;
		this.loc = app.getLocalization();
		if (hasKeyboard){
			wrappedPopup = new DialogBoxKbW(false, modal, this, app.getPanel());	
		} else {
			wrappedPopup = new DialogBoxW(false, modal, this, app.getPanel());
		}
		if (app.has(Feature.DIALOGS_OVERLAP_KEYBOARD)) {
			wrappedPopup.setOverlapFeature(true);
		}
	}

	public InputDialogW(AppW app, String message, String title,
	        String initString, boolean autoComplete, InputHandler handler,
			boolean modal, boolean selectInitText) {

		this(app, message, title, initString, autoComplete, handler, modal,
				selectInitText, DialogType.GeoGebraEditor);
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
			boolean modal, final boolean selectInitText,
			DialogType type) {

		this(modal, app, true);

		this.setInputHandler(handler);
		setInitString(initString);

		createGUI(title, message, autoComplete, DEFAULT_COLUMNS, 1, true,
				selectInitText, false, false, type);

		centerAndFocus(selectInitText);

	}
	
	public static class DialogBoxKbW extends DialogBoxW
			implements HasKeyboardPopup {

		public DialogBoxKbW(boolean b, boolean modal, InputDialogW inputDialogW, Panel panel) {
			super(b, modal, inputDialogW, panel);
		}
		
	}

	protected void centerAndFocus(final boolean selectInitText) {
		wrappedPopup.center();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				if (inputPanel != null) {
					inputPanel.getTextComponent().setFocus(true);
					// Firefox: correct cursor position #5419
					if (!selectInitText) {
						inputPanel.getTextComponent().setCaretPosition(
								inputPanel.getText().length());
					}
				}

			}
		});

	}

	public InputDialogW(AppW app, String message, String title,
			String initString, InputHandler handler, GeoElement geo) {

		this(false, app, false);

		this.geo = geo;
		this.setInputHandler(handler);
		setInitString(initString);

		createGUI(title, message, true, DEFAULT_COLUMNS, 1, true, false,
				geo != null, false,
				DialogType.GeoGebraEditor);

		centerAndFocus(false);

	}

	public InputDialogW(AppW app, String message, String title,
			String initString, boolean autoComplete, InputHandler handler) {
		this(app, message, title, initString, autoComplete, handler, false,
				false);
	}

	public InputDialogW(AppW app2, String message, String title2, String initString,
            boolean autoComplete, InputHandler handler, boolean selectInitText) {
		this(app2, message, title2, initString, autoComplete, handler, false,
				selectInitText);
    }

	/**
	 * @param title1
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
	protected void createGUI(String title1, String message,
	        boolean autoComplete, int columns, int rows,
			boolean showSymbolPopupIcon, boolean selectInitText,
	        boolean showProperties, boolean showApply, DialogType type) {

		this.title = title1;

		// Create components to be displayed
		inputPanel = new InputPanelW(getInitString(), app, rows, columns,
		        showSymbolPopupIcon/* , type */);
		
		if (app.has(Feature.KEYBOARD_BEHAVIOUR)) {

			app.registerPopup(wrappedPopup);
		}

		// add key handler for ENTER if inputPanel uses a text field
		if (inputPanel.getTextComponent() != null) {
			inputPanel.getTextComponent().getTextField().getValueBox().addKeyUpHandler(this);
			inputPanel.getTextComponent().addFocusListener(new FocusListenerW(this));
			inputPanel.getTextComponent().getTextField().getValueBox().addKeyPressHandler(this);
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
		btCancel.addStyleName("cancelBtn");

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
    @Override
	public void onClick(ClickEvent e) {
		actionPerformed(e);
	}
	
	protected void actionPerformed(DomEvent event) {
		Widget source = (Widget) event.getSource();
		if (source == btOK
		        || sourceShouldHandleOK(source)) {
			inputText = inputPanel.getText();
			processInputHandler(new AsyncOperation<Boolean>() {

				@Override
				public void callback(Boolean ok) {
					setVisible(!ok);
					if (ok) {
						resetMode();
					}

				}
			});
		} else if (source == btApply) {
			inputText = inputPanel.getText();
			processInputHandler(null);
		} else if (source == btProperties && geo != null) {
			setVisible(false);
			openProperties(app, geo);
		} else if (source == btCancel) {
			cancel();
			resetMode();
		}
	}

	protected void resetMode() {
		// only needed for texts

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
		}
	}
	
	private native void forceHideKeyboard() /*-{
		if ($wnd.android && $wnd.android.callPlugin) {
			$wnd.android.callPlugin("CloseKeyboard", []);
		}

	}-*/;

	@Override
	public void setLabels() {
		wrappedPopup.setText(title);
		btOK.setText(loc.getPlain("OK"));
		btApply.setText(loc.getPlain("Apply"));
		btCancel.setText(loc.getPlain("Cancel"));
		btProperties.setText(loc.getPlain("Properties") + Unicode.ellipsis);
	}

	@Override
    public void onKeyUp(KeyUpEvent event) {
		//enter press
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER){
			actionPerformed(event);
			return;
		}

	    
    }

	private boolean showingError = false;
	@Override
    public void showError(String msg) {
		if(msg == null){
			showingError = false;

		} else if (!showingError) {
			showingError = true;
			errorPanel.clear();
			String[] lines = msg.split("\n");
			for (String item : lines) {
				errorPanel.add(new Label(item));
			}
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
	public void showCommandError(String command, String message) {
		app.getDefaultErrorHandler().showCommandError(command, message);
	}

	@Override
	public String getCurrentCommand() {
		return inputPanel.getTextComponent().getCommand();
	}

	@Override
	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		return app.getGuiManager().checkAutoCreateSliders(string, callback);
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		// overridden in angle dialog
	}

	@Override
	public void resetError() {
		showError(null);
	}
}
