package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.InputDialog;
import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.util.WindowsNativeUIController;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.GDialogBox;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

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
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.util.Unicode;

public class InputDialogW extends InputDialog
		implements ClickHandler, SetLabels, KeyUpHandler, KeyPressHandler {

	protected final AppW app;

	public static final int DEFAULT_COLUMNS = 30;

	protected InputPanelW inputPanel;

	protected Button btApply;
	protected Button btProperties;
	protected Button btCancel;
	protected Button btOK;
	protected GDialogBox wrappedPopup;

	protected GeoElement geo;

	private String title;

	protected VerticalPanel messagePanel;
	protected VerticalPanel errorPanel;

	protected FlowPanel btPanel;

	protected final Localization loc;
	private boolean showingError = false;

	/**
	 * @param modal
	 *            whether to block UI
	 * @param app
	 *            application
	 * @param hasKeyboard
	 *            whether keyboard can be used
	 */
	public InputDialogW(boolean modal, AppW app, boolean hasKeyboard) {
		this.app = app;
		this.loc = app.getLocalization();
		if (hasKeyboard) {
			wrappedPopup = new DialogBoxKbW(false, modal, this, app.getPanel(),
					app);
		} else {
			wrappedPopup = new DialogBoxW(false, modal, this, app.getPanel(), app);
		}
	}

	/**
	 * @param app
	 *            application
	 * @param message
	 *            message text
	 * @param title
	 *            title
	 * @param initString
	 *            initial content
	 * @param autoComplete
	 *            whether to allow autocompletion
	 * @param handler
	 *            input callback
	 * @param modal
	 *            whether it's modal
	 * @param selectInitText
	 *            whether to select text after opening
	 */
	public InputDialogW(AppW app, String message, String title,
			String initString, boolean autoComplete, InputHandler handler,
			boolean modal, boolean selectInitText) {
		this(app, message, title, initString, autoComplete, handler, modal,
				selectInitText, DialogType.GeoGebraEditor);
	}

	/**
	 * @param app
	 *            application
	 * @param message
	 *            message text
	 * @param title
	 *            title
	 * @param initString
	 *            initial content
	 * @param autoComplete
	 *            whether to allow autocompletion
	 * @param handler
	 *            input callback
	 * @param modal
	 *            whether it's modal
	 * @param selectInitText
	 *            whether to select text after opening
	 * @param type
	 *            dialog type
	 */
	public InputDialogW(AppW app, String message, String title,
			String initString, boolean autoComplete, InputHandler handler,
			boolean modal, final boolean selectInitText, DialogType type) {

		this(modal, app, true);

		this.setInputHandler(handler);
		setInitString(initString);

		createGUI(title, message, autoComplete, DEFAULT_COLUMNS, 1, true,
				selectInitText, false, false, type);

		centerAndFocus(selectInitText);
	}

	/**
	 * Dialog with keyboard support
	 */
	public static class DialogBoxKbW extends DialogBoxW
			implements HasKeyboardPopup {

		/**
		 * @param autoHide
		 *            automatically hide
		 * @param modal
		 *            modal
		 * @param inputDialogW
		 *            input dialog
		 * @param panel
		 *            root panel for positioning
		 * @param app
		 *            app
		 */
		public DialogBoxKbW(boolean autoHide, boolean modal, InputDialogW inputDialogW,
				Panel panel, App app) {
			super(autoHide, modal, inputDialogW, panel, app);
		}

	}

	protected void centerAndFocus(final boolean selectInitText) {
		wrappedPopup.center();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				AutoCompleteTextFieldW textComponent = getTextComponent();
				if (textComponent != null) {
					textComponent.setFocus(true);
					// Firefox: correct cursor position #5419
					if (!selectInitText) {
						textComponent.setCaretPosition(
								inputPanel.getText().length());
					}
				}
			}
		});
	}

	/**
	 * @param app
	 *            application
	 * @param message
	 *            description
	 * @param title
	 *            title
	 * @param initString
	 *            initial value
	 * @param handler
	 *            input handler
	 * @param geo
	 *            geo
	 */
	public InputDialogW(AppW app, String message, String title,
			String initString, InputHandler handler, GeoElement geo) {
		this(false, app, false);

		this.geo = geo;
		this.setInputHandler(handler);
		setInitString(initString);

		createGUI(title, message, true, DEFAULT_COLUMNS, 1, true, false,
				geo != null, false, DialogType.GeoGebraEditor);

		centerAndFocus(false);
	}

	/**
	 * @param app
	 *            application
	 * @param message
	 *            message
	 * @param title
	 *            title
	 * @param initString
	 *            default value
	 * @param autoComplete
	 *            whether to use autocomplete
	 * @param handler
	 *            input handler
	 */
	public InputDialogW(AppW app, String message, String title,
			String initString, boolean autoComplete, InputHandler handler) {
		this(app, message, title, initString, autoComplete, handler, false,
				false);
	}

	/**
	 * @param app
	 *            application
	 * @param message
	 *            message
	 * @param title
	 *            title
	 * @param initString
	 *            default value
	 * @param autoComplete
	 *            whether to use autocomplete
	 * @param handler
	 *            input handler
	 * @param selectInitText
	 *            whether text should be selected initially
	 */
	public InputDialogW(AppW app, String message, String title,
			String initString, boolean autoComplete, InputHandler handler,
			boolean selectInitText) {
		this(app, message, title, initString, autoComplete, handler, false,
				selectInitText);
	}

	/**
	 * @param title1
	 *            title
	 * @param message
	 *            message
	 * @param autoComplete
	 *            whether to use autocomplete
	 * @param columns
	 *            number of columns
	 * @param rows
	 *            number of rows (for text tool)
	 * @param showSymbolPopupIcon
	 *            whether to show smbol popup
	 * @param selectInitText
	 *            whether to select text
	 * @param showProperties
	 *            whether to show properties button
	 * @param showApply
	 *            whether to show apply button
	 * @param type
	 *            dialog type
	 */
	protected void createGUI(String title1, String message,
			boolean autoComplete, int columns, int rows,
			boolean showSymbolPopupIcon, boolean selectInitText,
			boolean showProperties, boolean showApply, DialogType type) {

		this.title = title1;

		// Create components to be displayed
		inputPanel = new InputPanelW(getInitString(), app, rows, columns,
				showSymbolPopupIcon/* , type */);

		if (!app.isWhiteboardActive()) {

			app.registerPopup(wrappedPopup);
		}

		wrappedPopup.addCloseHandler(new CloseHandler<GPopupPanel>() {
			@Override
			public void onClose(CloseEvent<GPopupPanel> event) {
				app.unregisterPopup(wrappedPopup);
				app.hideKeyboard();
			}
		});

		// add key handler for ENTER if inputPanel uses a text field
		AutoCompleteTextFieldW textComponent = getTextComponent();
		if (textComponent != null) {
			textComponent.getTextField().getValueBox()
					.addKeyUpHandler(this);
			textComponent.getTextField().getValueBox()
					.addKeyPressHandler(this);
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
	public final void onClick(ClickEvent e) {
		actionPerformed(e);
	}

	protected void closeIOSKeyboard() {
		// implemented in TextInputDialog
	}

	protected void actionPerformed(DomEvent<?> event) {
		Widget source = (Widget) event.getSource();
		if (source == btOK || sourceShouldHandleOK(source)) {
			closeIOSKeyboard();
			String inputText = inputPanel.getText();
			processInputHandler(inputText, new AsyncOperation<Boolean>() {

				@Override
				public void callback(Boolean ok) {
					setVisible(!ok);
					if (ok) {
						resetMode();
					}

				}
			});
		} else if (source == btApply) {
			String inputText = inputPanel.getText();
			processInputHandler(inputText, null);
		} else if (source == btProperties && geo != null) {
			setVisible(false);
			openProperties(app, geo);
		} else if (source == btCancel) {
			closeIOSKeyboard();
			cancel();
			resetMode();
		}
	}

	protected void actionPerformedSimple(DomEvent<?> e) {
		Object source = e.getSource();

		try {
			if (source == btOK || sourceShouldHandleOK(source)) {
				processInput();
			} else if (source == btCancel) {
				setVisible(false);
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue
			setVisible(false);
		}
	}

	protected void resetMode() {
		// only needed for texts

	}

	protected void cancel() {
		setVisible(false);
	}

	/**
	 * Hide or show this.
	 * 
	 * @param visible
	 *            whether this should be visible
	 */
	public void setVisible(boolean visible) {
		if (visible) {
			wrappedPopup.show();
			inputPanel.setTextComponentFocus();
		} else {
			new WindowsNativeUIController(app).hideKeyboard();
			wrappedPopup.hide();
			AutoCompleteTextFieldW textComponent = getTextComponent();
			if (textComponent != null) {
				textComponent.hideTablePopup();
			}
			app.getActiveEuclidianView().requestFocusInWindow();
		}
	}

	@Override
	public void setLabels() {
		wrappedPopup.setText(title);
		btOK.setText(loc.getMenu("OK"));
		btApply.setText(loc.getMenu("Apply"));
		btCancel.setText(loc.getMenu("Cancel"));
		btProperties.setText(loc.getMenu("Properties") + Unicode.ELLIPSIS);
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		// enter press
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			actionPerformed(event);
			return;
		}
	}

	@Override
	public void showError(String msg) {
		if (msg == null) {
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
	 * @param source
	 *            the event source
	 * @return true if the source widget should handle the OK event
	 */
	protected boolean sourceShouldHandleOK(Object source) {
		AutoCompleteTextFieldW textComponent = getTextComponent();
		return textComponent != null
				&& source == textComponent.getTextField().getValueBox();
	}

	@Override
	public void showCommandError(String command, String message) {
		app.getDefaultErrorHandler().showCommandError(command, message);
	}

	@Override
	public String getCurrentCommand() {
		AutoCompleteTextFieldW textComponent = getTextComponent();
		if (textComponent != null) {
			return textComponent.getCommand();
		}
		return null;
	}

	/**
	 * Note: package visibility to make this accessible from anonymous classes
	 * 
	 * @return single line text input
	 */
	AutoCompleteTextFieldW getTextComponent() {
		return inputPanel == null ? null : inputPanel.getTextComponent();
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

	private void processInput() {
		// avoid labeling of num
		final Construction cons = app.getKernel().getConstruction();
		final boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		getInputHandler().processInput(inputPanel.getText(), this,
				new AsyncOperation<Boolean>() {

					@Override
					public void callback(Boolean ok) {
						cons.setSuppressLabelCreation(oldVal);

						if (ok) {
							toolAction();
						}
						setVisible(!ok);
					}
				});
	}

	/**
	 * Callback for tool dialogs
	 */
	protected void toolAction() {
		// overridden in subclasses
	}

}
