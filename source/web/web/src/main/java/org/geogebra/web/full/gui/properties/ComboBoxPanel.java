package org.geogebra.web.full.gui.properties;

import org.geogebra.common.gui.dialog.options.model.GeoComboListener;
import org.geogebra.common.gui.dialog.options.model.MultipleOptionsModel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.util.ComboBoxW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public abstract class ComboBoxPanel extends OptionPanel
		implements ErrorHandler, GeoComboListener {

	private FormLabel label;
	private ComboBoxW comboBox;
	private String title;
	private Localization loc;
	private App app;
	private FlowPanel errorPanel;
	
	/**
	 * @param app
	 *            application
	 * @param title
	 *            title
	 */
	public ComboBoxPanel(App app, final String title) {
		this.loc = app.getLocalization();
		this.app = app;
		this.title = title;

		comboBox = new ComboBoxW((AppW) app) {

			@Override
			protected void onValueChange(String value) {
				onComboBoxChange();
			}
		};
		comboBox.setWidth("112px");
		label = new FormLabel("").setFor(comboBox);
		comboBox.setEnabled(true);
		FlowPanel mainWidget = new FlowPanel();
		mainWidget.setStyleName("listBoxPanel");

		mainWidget.add(label);
		mainWidget.add(comboBox);
		errorPanel = new FlowPanel();
		errorPanel.addStyleName("Dialog-errorPanel");
		mainWidget.add(errorPanel);
		setWidget(mainWidget);
	}

	MultipleOptionsModel getMultipleModel() {
		return (MultipleOptionsModel) getModel();
	}

	protected abstract void onComboBoxChange();

	@Override
	public void setLabels() {
		getLabel().setText(loc.getMenu(getTitle()) + ":");

		String text = comboBox.getValue();
		comboBox.getModel().clear();
		getMultipleModel().fillModes(loc);
		comboBox.setValue(text);
	}

	@Override
	public void setSelectedIndex(int index) {
		comboBox.setSelectedIndex(index);
	}

	@Override
	public void addItem(String item) {
		comboBox.addItem(item);
	}

	@Override
	public void addItem(GeoElement geo) {
		if (geo == null) {
			comboBox.addItem("");
			return;
		}
		comboBox.addItem(geo.getLabel(StringTemplate.editTemplate),
				geo);
	}

	/**
	 * @return title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * @param title
	 *            title
	 */
	public void setTitle(String title) {
		this.title = title;
		getLabel().setText(title);
	}

	public FormLabel getLabel() {
		return label;
	}

	@Override
	public final void setSelectedItem(String item) {
		getComboBox().setValue(item);
	}

	public ComboBoxW getComboBox() {
		return comboBox;
	}

	@Override
	public void clearItems() {
		comboBox.getModel().clear();
	}

	@Override
	public void showError(String msg) {
		if (msg == null) {
			errorPanel.clear();
			return;
		}
		errorPanel.clear();
		String[] lines = msg.split("\n");
		for (String item : lines) {
			errorPanel.add(new Label(item));
		}

	}

	@Override
	public void resetError() {
		errorPanel.clear();
	}

	@Override
	public void showCommandError(String command, String message) {
		app.getDefaultErrorHandler().showCommandError(command, message);

	}

	@Override
	public String getCurrentCommand() {
		return null;
	}
	
	@Override
	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		// TODO Auto-generated method stub
		return false;
	}

}
