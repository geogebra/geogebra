package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.euclidian.event.FocusListenerDelegate;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.dialog.options.model.ObjectNameModel;
import org.geogebra.common.gui.dialog.options.model.ShowLabelModel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.properties.OptionPanel;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Class for Name, Definition and Caption settings panel.
 *
 * @author Laszlo
 */
class NamePanel extends OptionPanel
		implements ObjectNameModel.IObjectNameListener, ErrorHandler,
		FocusListenerDelegate {
	private final DynamicCaptionPanel dynamicCaptionPanel;
	ObjectNameModel model;
	private AutoCompleteTextFieldW tfName;
	private AutoCompleteTextFieldW tfDefinition;
	private AutoCompleteTextFieldW tfCaption;

	private FormLabel nameLabel;
	private FormLabel defLabel;
	private FormLabel captionLabel;

	private FlowPanel mainWidget;
	private FlowPanel nameStrPanel;
	private FlowPanel defPanel;
	private FlowPanel errorPanel;
	private FlowPanel captionPanel;
	/**
	 * current geo on which focus lost should apply (may be different to
	 * current geo, due to threads)
	 */
	private GeoElementND currentGeoForFocusLost = null;

	private String redefinitionForFocusLost = "";
	private AppW app;
	private ShowLabelModel showLabelModel;

	/**
	 *
	 * @param app
	 * 				The application.
	 * @param showLabelModel
	 * 				Model for showing/hiding label.
	 */
	NamePanel(final AppW app, ShowLabelModel showLabelModel) {
		this.app = app;
		this.showLabelModel = showLabelModel;

		this.model = new ObjectNameModel(app, this);
		setModel(model);

		// name field: no auto-completion
		InputPanelW inputPanelName = new InputPanelW(null, app, 1, -1, true);
		tfName = inputPanelName.getTextComponent();
		tfName.setAutoComplete(false);
		tfName.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				if (model.noLabelUpdateNeeded(tfName.getText())) {
					return;
				}
				applyName();
			}
		});
		tfName.addKeyHandler(new KeyHandler() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					applyName();
				}
			}
		});

		// definition field: non auto complete input panel
		InputPanelW inputPanelDef = new InputPanelW(null, app, 1, -1, true);
		tfDefinition = inputPanelDef.getTextComponent();
		tfDefinition.setAutoComplete(false);

		tfDefinition.addFocusListener(this);

		tfDefinition.addKeyHandler(new KeyHandler() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					model.applyDefinitionChange(tfDefinition.getText(),
							NamePanel.this);
				}

			}
		});

		// caption field: non auto complete input panel
		InputPanelW inputPanelCap = new InputPanelW(null, app, 1, -1, true);
		tfCaption = inputPanelCap.getTextComponent();
		tfCaption.setAutoComplete(false);

		tfCaption.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				doCaptionChanged();
			}
		});
		tfCaption.addKeyHandler(new KeyHandler() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					doCaptionChanged();
				}
			}
		});

		mainWidget = new FlowPanel();

		// name panel
		nameStrPanel = new FlowPanel();
		nameLabel = new FormLabel("").setFor(inputPanelName);

		nameStrPanel.add(nameLabel);
		nameStrPanel.add(inputPanelName);
		mainWidget.add(nameStrPanel);

		// definition panel
		defPanel = new FlowPanel();
		defLabel = new FormLabel("").setFor(inputPanelDef);
		defPanel.add(defLabel);
		defPanel.add(inputPanelDef);
		mainWidget.add(defPanel);
		errorPanel = new FlowPanel();
		errorPanel.addStyleName("Dialog-errorPanel");
		mainWidget.add(errorPanel);

		dynamicCaptionPanel = new DynamicCaptionPanel(app, tfCaption);
		// caption panel
		captionPanel = new FlowPanel();
		captionLabel = new FormLabel("").setFor(inputPanelCap);
		captionPanel.add(captionLabel);
		captionPanel.add(inputPanelCap);
		captionPanel.add(dynamicCaptionPanel.getWidget());
		mainWidget.add(captionPanel);

		nameStrPanel.setStyleName("optionsInput");
		defPanel.setStyleName("optionsInput");
		captionPanel.setStyleName("optionsInput");
		setWidget(mainWidget);
		updateGUI(true, true);
	}

	@Override
	public void focusGained() {
		// started to type something : store current geo if focus lost
		currentGeoForFocusLost = model.getCurrentGeo();
	}

	@Override
	public void focusLost() {
		if (model.getCurrentGeo() == currentGeoForFocusLost) {
			model.applyDefinitionChange(tfDefinition.getText(),
					app.getErrorHandler());
		} else {
			model.redefineCurrentGeo(currentGeoForFocusLost,
					tfDefinition.getText(), redefinitionForFocusLost,
					app.getErrorHandler());
		}
	}

	private void applyName() {
		model.applyNameChange(tfName.getText(),
				app.getErrorHandler());
	}

	@Override
	public void resetError() {
		showError(null);
	}

	private void doCaptionChanged() {
		if (!"".equals(tfCaption.getText())) {
			autoShowCaption();
		}
		model.applyCaptionChange(tfCaption.getText());
		((EuclidianViewW) app.getActiveEuclidianView()).doRepaint();
	}

	private void autoShowCaption() {
		GeoElement geo0 = showLabelModel.getGeoAt(0);
		geo0.setLabelVisible(true);
		geo0.setLabelMode(GeoElementND.LABEL_CAPTION);
		showLabelModel.updateProperties();
	}

	@Override
	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		return app.getGuiManager().checkAutoCreateSliders(string, callback);
	}

	@Override
	public void setLabels() {
		Localization loc = app.getLocalization();
		nameLabel.setText(loc.getMenu("Name"));
		defLabel.setText(loc.getMenu("Definition"));
		captionLabel.setText(loc.getMenu("Button.Caption"));
		dynamicCaptionPanel.setLabels();
	}

	@Override
	public void updateGUI(boolean showDefinition, boolean showCaption) {
		mainWidget.clear();
		mainWidget.add(nameStrPanel);

		if (showDefinition) {
			mainWidget.add(defPanel);
			mainWidget.add(errorPanel);
		}

		if (showCaption) {
			mainWidget.add(captionPanel);
		}
	}

	private void updateDef(GeoElementND geo) {
		errorPanel.clear();
		model.getDefInputHandler().setGeoElement(geo);
		tfDefinition.setText(ObjectNameModel.getDefText(geo));
	}

	@Override
	public void setNameText(final String text) {
		if ("".equals(text)) {
			Log.debug("name is empty");
		}
		tfName.setText(text);
		tfName.requestFocus();
	}

	@Override
	public void setDefinitionText(final String text) {
		tfDefinition.setText(text);
	}

	@Override
	public void setCaptionText(final String text) {
		tfCaption.setText(text);
		tfCaption.requestFocus();
	}

	@Override
	public void updateCaption(String text) {
		tfCaption.setText(text);
	}

	@Override
	public void updateDefLabel() {
		updateDef(model.getCurrentGeo());
		Localization loc = app.getLocalization();
		if (model.getCurrentGeo().isIndependent()) {
			defLabel.setText(loc.getMenu("Value") + ":");
		} else {
			defLabel.setText(loc.getMenu("Definition") + ":");
		}
	}

	@Override
	public void updateName(String text) {
		tfName.setText(text);
		// if a focus lost is called in between, we keep the current
		// definition text
		redefinitionForFocusLost = tfDefinition.getText();
	}

	@Override
	public void showError(String msg) {
		if (msg == null) {
			return;
		}
		errorPanel.clear();
		String[] lines = msg.split("\n");
		for (String item : lines) {
			errorPanel.add(new Label(item));
		}
	}

	@Override
	public void showCommandError(String command, String message) {
		app.getDefaultErrorHandler().showCommandError(command, message);
	}

	@Override
	public String getCurrentCommand() {
		return tfDefinition.getCommand();
	}

	@Override
	public OptionPanel updatePanel(Object[] geos) {
		dynamicCaptionPanel.updatePanel(geos);
		return super.updatePanel(geos);
	}

}
