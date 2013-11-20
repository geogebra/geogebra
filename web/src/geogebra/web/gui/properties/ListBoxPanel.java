package geogebra.web.gui.properties;

import geogebra.common.gui.dialog.options.model.IComboListener;
import geogebra.common.gui.dialog.options.model.MultipleOptionsModel;
import geogebra.common.main.Localization;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class ListBoxPanel extends OptionPanel implements IComboListener {

	private Label label;
	private ListBox listBox;
	private String title;
	private Localization loc;
	
	public ListBoxPanel(Localization loc, final String title) {
		this.loc = loc;
		this.title = title;
		setLabel(new Label());
		listBox = new ListBox();
		FlowPanel mainWidget = new FlowPanel(); 
		
		mainWidget.add(getLabel());
		mainWidget.add(getListBox());
		
		getListBox().addChangeHandler(new ChangeHandler(){

			public void onChange(ChangeEvent event) {
				getMultipleModel().applyChanges(getListBox().getSelectedIndex());
            }});
		setWidget(mainWidget);
	}

	MultipleOptionsModel getMultipleModel() {
		return (MultipleOptionsModel)getModel();
	}
	
	@Override
    public void setLabels() {
		getLabel().setText(getTitle());

		int idx = getListBox().getSelectedIndex();
		getListBox().clear();
		getMultipleModel().fillModes(loc);
		getListBox().setSelectedIndex(idx);
	}

	public void setSelectedIndex(int index) {
		getListBox().setSelectedIndex(index);
    }

	public void addItem(String item) {
        getListBox().addItem(item);
    }

	public String getTitle() {
        return title;
    }
	
	public void setTitle(String title) {
        this.title = title;
        getLabel().setText(title);
    }
	
	public ListBox getListBox() {
        return listBox;
    }

	public Label getLabel() {
        return label;
    }

	public void setLabel(Label label) {
        this.label = label;
    }

	
}
