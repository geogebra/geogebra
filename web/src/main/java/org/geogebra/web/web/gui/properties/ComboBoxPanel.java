package org.geogebra.web.web.gui.properties;

import org.geogebra.common.gui.dialog.options.model.IComboListener;
import org.geogebra.common.gui.dialog.options.model.MultipleOptionsModel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.util.ComboBoxW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public abstract class ComboBoxPanel extends OptionPanel
		implements IComboListener {

	private Label label;
	private ComboBoxW comboBox;
	private String title;
	private Localization loc;
	
	public ComboBoxPanel(App app, final String title) {
		this.loc = app.getLocalization();
		this.title = title;
		label = new Label();
		comboBox = new ComboBoxW((AppW) app) {

			@Override
            protected void onValueChange(String value) {
	            onComboBoxChange();
            }};
		comboBox.setEnabled(true);
		FlowPanel mainWidget = new FlowPanel(); 
		mainWidget.setStyleName("listBoxPanel");

		mainWidget.add(label);
		mainWidget.add(comboBox);
		setWidget(mainWidget);
	}

	MultipleOptionsModel getMultipleModel() {
		return (MultipleOptionsModel)getModel();
	}
	
	protected abstract void onComboBoxChange();
	@Override
    public void setLabels() {
		getLabel().setText(loc.getPlain(getTitle()) + ":");

		String text = comboBox.getValue();
		comboBox.getModel().clear();
		getMultipleModel().fillModes(loc);
		comboBox.setValue(text);
	}

	public void setSelectedIndex(int index) {
		comboBox.setSelectedIndex(index);
    }

	public void addItem(String item) {
        comboBox.addItem(item);
    }

	public void addItem(GeoElement geo) {
		if (geo == null) {
			comboBox.addItem("");
			return;
		}
		comboBox.addItem(geo.getLabel(StringTemplate.editTemplate),
				geo);
	}

	public String getTitle() {
        return title;
    }
	
	public void setTitle(String title) {
        this.title = title;
        getLabel().setText(title);
    }
	
	public Label getLabel() {
        return label;
    }

	public void setLabel(Label label) {
        this.label = label;
    }

	public void setSelectedItem(String item) {
	    // TODO Auto-generated method stub
	    
    }

	public ComboBoxW getComboBox() {
	    return comboBox;
    }

	private void setComboBox(ComboBoxW comboBox) {
	    this.comboBox = comboBox;
    }

	public void clearItems() {
		comboBox.getModel().clear();

	}
	
}
