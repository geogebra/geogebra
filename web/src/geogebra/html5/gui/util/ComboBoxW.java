package geogebra.html5.gui.util;

import geogebra.html5.css.GuiResources;

import org.gwt.advanced.client.datamodel.ListDataModel;
import org.gwt.advanced.client.datamodel.ListModelEvent;
import org.gwt.advanced.client.ui.widget.ComboBox;
import org.gwt.advanced.client.ui.widget.combo.DropDownPosition;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;


public abstract class ComboBoxW extends ComboBox<ListDataModel> {
	public ComboBoxW() {
		setCustomTextAllowed(true);
		setDropDownPosition(DropDownPosition.UNDER);
		setEnterAction(EnterAction.DO_NOTHING);
		
		setVisibleRows(10);
		setWidth("70px");
		
		this.setChoiceButtonImage(new Image(GuiResources.INSTANCE.triangle_down()));
		this.prepareChoiceButton();
		this.setChoiceButtonVisible(true);
		
		addCloseHandler(new CloseHandler<PopupPanel>() {
			
			public void onClose(CloseEvent<PopupPanel> event) {
				onValueChange(getValue());
			}
		});
//		
//		addChangeHandler(new ChangeHandler(){
//
//			public void onChange(ChangeEvent event) {
//				onValueChange(getValue());
//			}
//		});


		addKeyDownHandler(new KeyDownHandler(){

			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					onValueChange(getValue());
				}
	        }});
		
		


	}

	@Override 
    protected void select(ListModelEvent event) {
		if (isListPanelOpened()) {
			return;
		}
		super.select(event);
	}


	
	protected abstract void onValueChange(String value);

	public void addItem(String item) {
		getModel().add(item, item);
	}


    public void onClick(ClickEvent event) {
//        int count = getModel().getCount();
//        Object sender = event.getSource();
//        if (sender instanceof ToggleButton || !isCustomTextAllowed()) {
//            if (count > 0 && !getListPanel().isShowing()) {
//                getListPanel().show();
//                getListPanel().prepareList();
//                if (getItemCount() <= 0)
//                    getListPanel().hide();
//                getChoiceButton().setDown(true);
//            } else {
//                getListPanel().hide();
//                getChoiceButton().setDown(false);
//            }
//        }
//        fireEvent(event);
    }
}