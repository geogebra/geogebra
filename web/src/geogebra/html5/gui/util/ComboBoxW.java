package geogebra.html5.gui.util;

import org.gwt.advanced.client.datamodel.ListDataModel;
import org.gwt.advanced.client.ui.widget.ComboBox;
import org.gwt.advanced.client.ui.widget.combo.DropDownPosition;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;


public abstract class ComboBoxW extends ComboBox<ListDataModel> {
	public ComboBoxW() {
		setCustomTextAllowed(true);
		setDropDownPosition(DropDownPosition.UNDER);
		setEnterAction(EnterAction.DO_NOTHING);
		
		setVisibleRows(10);
		setWidth("70px");
		
		addChangeHandler(new ChangeHandler(){

			public void onChange(ChangeEvent event) {
				onValueChange(getValue());
			}
		});

		addKeyDownHandler(new KeyDownHandler(){

			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					onValueChange(getValue());
				}
	        }});
		
		
		
	}

	protected abstract void onValueChange(String value);

	public void addItem(String item) {
		getModel().add(item, item);
	}
}