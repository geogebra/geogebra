package org.geogebra.web.full.gui.inputbar;

import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.AlgebraInput;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.MyToggleButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.himamis.retex.editor.share.util.GWTKeycodes;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * InputBar for GeoGebraWeb
 *
 * @author gabor
 */
public class AlgebraInputW extends FlowPanel
		implements KeyUpHandler, FocusHandler, ClickHandler, BlurHandler,
		RequiresResize, AlgebraInput, HasHelpButton {
	/** app */
	protected AppW app;
	/** input panel */
	protected InputPanelW inputPanel;
	/** text component */
	protected AutoCompleteTextFieldW inputField;
	// protected FlowPanel innerPanel;
	/** button for help */
	protected MyToggleButton btnHelpToggle;
	/** help popup */
	protected InputBarHelpPopup helpPopup;
	// protected PopupPanel helpPopup;
	private boolean focused = false;

	/**
	 * @param app1
	 *            Application
	 * 
	 *            Attaches Application and creates the GUI of AlgebraInput
	 */
	public void init(AppW app1) {
		this.app = app1;
		addStyleName("AlgebraInput");
		addStyleName("AlgebraInput2");

		initGUI();
		app1.getGuiManager().addAlgebraInput(this);
	}

	/**
	 * Initialize the UI
	 */
	public void initGUI() {
		clear();
		inputPanel = new InputPanelW(app, 0, true);

		inputField = inputPanel.getTextComponent();
		inputField.requestToShowSymbolButton();

		inputField.getTextBox().addKeyUpHandler(this);
		inputField.getTextBox().addFocusHandler(this);
		inputField.getTextBox().addBlurHandler(this);

		inputField.addHistoryPopup(app.getInputPosition() == InputPosition.top);
		if (Browser.isTabletBrowser()) {
			inputField.enableGGBKeyboard();
		}

		updateIcons(false);
		btnHelpToggle.addStyleName("inputHelp-toggleButton");

		btnHelpToggle.addClickHandler(this);
		add(inputPanel);
		if (app.showInputHelpToggle()) {
			add(btnHelpToggle);
		}

		setLabels();
	}

	private void updateIcons(boolean warning) {
		if (btnHelpToggle == null) {
			btnHelpToggle = new MyToggleButton(app);
		}
		btnHelpToggle.getUpFace().setImage(new NoDragImage(
				(warning ? GuiResourcesSimple.INSTANCE.icon_dialog_warning()
						: GuiResources.INSTANCE.icon_help()).getSafeUri()
								.asString(),
				24));
		// new
		// Image(AppResources.INSTANCE.inputhelp_left_20x20().getSafeUri().asString()),
		btnHelpToggle.getDownFace().setImage(new NoDragImage(
				(warning ? GuiResourcesSimple.INSTANCE.icon_dialog_warning()
						: GuiResources.INSTANCE.icon_help()).getSafeUri()
								.asString(),
				24));
	}

	/**
	 * Sets the width of the text field so that the entire width of the parent
	 * container is used. (Really just a workaround because the nested gwt
	 * panels are not allowing 100% width to work as we would like).
	 */
	@Override
	public void setInputFieldWidth(int width) {
		// if the size is too small, use default size
		if (width > 100) {
			inputPanel.setWidth((width - 100) + "px");
		}
	}

	@Override
	public void onResize() {
		if (app == null) {
			return;
		}
		if (!app.isApplet()) {
			setInputFieldWidth((int) app.getWidth());
		}

		// hide the help popup
		setShowInputHelpPanel(false);
	}

	/**
	 * updates labels according to current locale
	 */
	public void setLabels() {
		if (app == null) {
			return;
		}
		Localization loc = app.getLocalization();

		if (btnHelpToggle != null) {
			btnHelpToggle.setTitle(loc.getMenu("InputHelp"));
		}

		if (helpPopup != null) {
			app.getGuiManager().getInputHelpPanel().setLabels();
		}

		inputField.setDictionary(false);
		inputField.getTextField().getElement().setAttribute("placeholder",
				loc.getMenu("InputLabel") + Unicode.ELLIPSIS);
	}

	/**
	 * Sets the content of the input textfield and gives focus to the input
	 * textfield.
	 * 
	 * @param str
	 *            replacement string
	 */
	public void replaceString(String str) {
		inputField.setText(str);
	}

	/**
	 * Insert string at caret position
	 * 
	 * @param str
	 *            string to be inserted
	 */
	public void insertString(String str) {
		if (str == null) {
			return;
		}

		int pos = inputField.getCaretPosition();
		String oldText = inputField.getText();
		String newText = oldText.substring(0, pos) + str
				+ oldText.substring(pos);

		inputField.setText(newText);
		inputField.setCaretPosition(pos + str.length());
		inputField.requestFocus();
	}

	@Override
	public void onFocus(FocusEvent event) {
		app.getSelectionManager().clearSelectedGeos();
		this.focused = true;
	}

	@Override
	public void onBlur(BlurEvent event) {
		this.focused = false;
		onEnterPressed(false);
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		// the input field may have consumed this event
		// for auto completion
		// then it don't come here if (e.isConsumed()) return;

		int keyCode = event.getNativeKeyCode();
		app.getKernel().getInputPreviewHelper().updatePreviewFromInputBar(
				inputField.getText(), getWarningHandler(this, app));
		if (keyCode == GWTKeycodes.KEY_ENTER
				&& !inputField.isSuggestionJustHappened()) {
			onEnterPressed(true);

		} else if (keyCode != GWTKeycodes.KEY_C && keyCode != GWTKeycodes.KEY_V
				&& keyCode != GWTKeycodes.KEY_A
				&& keyCode != GWTKeycodes.KEY_X) {
			app.getGlobalKeyDispatcher().handleGeneralKeys(event); // handle eg
																	// ctrl-tab
			if (keyCode == GWTKeycodes.KEY_ESCAPE) {
				inputField.setText(null);
			}
		}
		inputField.setIsSuggestionJustHappened(false);
	}

	private void onEnterPressed(final boolean explicit) {
		app.getKernel().clearJustCreatedGeosInViews();
		final String input = app.getKernel().getInputPreviewHelper()
				.getInput(getTextField().getText());
		boolean valid = app.getKernel().getInputPreviewHelper().isValid();
		app.getKernel().getInputPreviewHelper().updatePreviewFromInputBar("",
				app.getDefaultErrorHandler());
		if (input == null || input.length() == 0) {
			app.getActiveEuclidianView().requestFocusInWindow();
			return;
		}

		app.setScrollToShow(true);

		try {
			final int oldStep = app.getKernel().getConstructionStep();
			AsyncOperation<GeoElementND[]> callback = new AsyncOperation<GeoElementND[]>() {

				@Override
				public void callback(GeoElementND[] geos) {

					if (geos == null) {
						inputField.getTextBox().setFocus(true);
						return;
					}

					// need label if we type just eg
					// lnx
					if (geos.length == 1 && !geos[0].isLabelSet()) {
						geos[0].setLabel(geos[0].getDefaultLabel());
					}

					InputHelper.updateProperties(geos,
							app.getActiveEuclidianView(), oldStep);

					app.setScrollToShow(false);

					inputField.addToHistory(input);
					if (!getTextField().getText().equals(input)) {
						inputField.addToHistory(getTextField().getText());
					}
					inputField.setText(null);

					inputField.setIsSuggestionJustHappened(false);
				}

			};
			EvalInfo info = new EvalInfo(true, true).withSliders(true)
					.addDegree(app.getKernel().getAngleUnitUsesDegrees())
					.withUserEquation(true);
			app.getKernel().getAlgebraProcessor()
					.processAlgebraCommandNoExceptionHandling(input, true,
							getErrorHandler(valid, explicit), info, callback);

		} catch (Exception ee) {
			storeError();
			app.showGenericError(ee);
		} catch (MyError ee) {
			storeError();
			inputField.showError(ee);
		}
	}

	private void storeError() {
		inputField.addToHistory(getTextField().getText());
		if (app.getGuiManager() != null) {
			app.getGuiManager().getOptionPane()
					.setCaller(inputField.getTextBox());
		}
	}

	/**
	 * @param input
	 *            input bar (plaintext or editor)
	 * @param app2
	 *            app
	 * @return handler for preview errors
	 */
	public static ErrorHandler getWarningHandler(final HasHelpButton input,
			final App app2) {
		// TODO Auto-generated method stub
		return new WarningErrorHandler(app2, input);
	}

	@Override
	public String getCommand() {
		return inputField.getCommand();
	}

	private ErrorHandler getErrorHandler(final boolean valid,
			final boolean explicit) {
		return new ErrorHandler() {

			@Override
			public void showError(String msg) {
				if (explicit) {
					app.getDefaultErrorHandler().showError(msg);
				}

			}

			@Override
			public void resetError() {
				showError(null);
			}

			@Override
			public boolean onUndefinedVariables(String string,
					AsyncOperation<String[]> callback) {
				if (explicit) {
					if (valid) {
						return app.getGuiManager()
								.checkAutoCreateSliders(string, callback);
					} else if (app.getLocalization()
							.getReverseCommand(getCurrentCommand()) != null) {
						ErrorHelper.handleCommandError(app.getLocalization(),
								getCurrentCommand(),
								app.getDefaultErrorHandler());

						return false;
					}
					callback.callback(new String[] { "7" });
				}

				return false;
			}

			@Override
			public void showCommandError(String command, String message) {
				if (explicit) {
					app.getDefaultErrorHandler().showCommandError(command,
							message);
				}

			}

			@Override
			public String getCurrentCommand() {
				return inputField.getCommand();
			}
		};
	}

	@Override
	public void requestFocus() {
		inputField.requestFocus();
	}

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();

		if (source == btnHelpToggle) {
			if (btnHelpToggle.isDown()) {
				setShowInputHelpPanel(true);
			} else {
				setShowInputHelpPanel(false);
			}
		}
	}

	private void setHelpPopup() {
		if (helpPopup == null && app != null) {
			helpPopup = new InputBarHelpPopup(this.app, this.inputField,
					"helpPopup");
			helpPopup.addAutoHidePartner(this.getElement());
			if (btnHelpToggle != null) {
				helpPopup.setBtnHelpToggle(this.btnHelpToggle);
			}
		} else if (app != null && helpPopup.getWidget() == null) {
			helpPopup.add((InputBarHelpPanelW) app.getGuiManager()
					.getInputHelpPanel());
		}
	}

	/**
	 * 
	 * @param show
	 *            whether inputhelp should be shown
	 */
	public void setShowInputHelpPanel(boolean show) {

		if (show) {
			InputBarHelpPanelW helpPanel = (InputBarHelpPanelW) app
					.getGuiManager().getInputHelpPanel();
			helpPanel.updateGUI(((GuiManagerW) app.getGuiManager())
					.getRootComponent().getOffsetHeight());
			setHelpPopup();

			helpPopup.setPopupPositionAndShow(
					new GPopupPanel.PositionCallback() {
						@Override
						public void setPosition(int offsetWidth,
								int offsetHeight) {
							helpPopup.getElement().getStyle()
									.setProperty("left", "auto");
							helpPopup.getElement().getStyle().setProperty("top",
									"auto");
							helpPopup.getElement().getStyle().setRight(0,
									Unit.PX);
							helpPopup.getElement().getStyle()
									.setBottom(
											getOffsetHeight()
													* app.getGeoGebraElement()
															.getScaleX(),
											Unit.PX);
							helpPopup.show();
						}
					});
			((InputBarHelpPanelW) app.getGuiManager().getInputHelpPanel())
					.focusCommand(inputField.getCommand());

		} else if (helpPopup != null) {
			helpPopup.hide();
		}
	}

	@Override
	public void setText(String s) {
		this.inputField.setText(s);
	}

	/**
	 * @return whether this has focus
	 */
	public boolean hasFocus() {
		return this.focused;
	}

	/**
	 * @return text field
	 */
	public AutoCompleteTextFieldW getTextField() {
		return this.inputField;
	}

	@Override
	public MyToggleButton getHelpToggle() {
		return this.btnHelpToggle;
	}

	@Override
	public void setError(String msg) {
		updateIcons(msg != null);
		getHelpToggle().asWidget().getElement().setTitle(msg == null
				? app.getLocalization().getMenu("InputHelp") : msg);
	}

	@Override
	public void setCommandError(String command) {
		updateIcons(true);
	}

	@Override
	public void setUndefinedVariables(String vars) {
		// TODO Auto-generated method stub

	}

}
