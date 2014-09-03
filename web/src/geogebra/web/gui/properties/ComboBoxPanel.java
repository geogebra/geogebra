package geogebra.web.gui.properties;

import geogebra.common.gui.dialog.options.model.IComboListener;
import geogebra.common.gui.dialog.options.model.MultipleOptionsModel;
import geogebra.common.main.Localization;
import geogebra.web.gui.util.ComboBoxW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ComboBoxPanel extends OptionPanel implements IComboListener {

	private Label label;
	private ComboBoxW comboBox;
	private String title;
	private Localization loc;
	
	public ComboBoxPanel(Localization loc, final String title) {
		this.loc = loc;
		this.title = title;
		label = new Label();
		comboBox = new ComboBoxW(){

			@Override
            protected void onValueChange(String value) {
	            onComboBoxChange();
            }};
            
		FlowPanel mainWidget = new FlowPanel(); 
		mainWidget.setStyleName("listBoxPanel");

		mainWidget.add(label);
		mainWidget.add(comboBox);
		setWidget(mainWidget);
	}

	MultipleOptionsModel getMultipleModel() {
		return (MultipleOptionsModel)getModel();
	}
	
	protected void onComboBoxChange() {
		getMultipleModel().applyChanges(comboBox.getSelectedIndex());
        
	}
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

	
}
