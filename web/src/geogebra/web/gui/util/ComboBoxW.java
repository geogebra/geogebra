package geogebra.web.gui.util;

import geogebra.common.main.App;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.main.AppW;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.advanced.client.datamodel.ListDataModel;
import geogebra.web.gui.advanced.client.datamodel.ListModelEvent;
import geogebra.web.gui.advanced.client.ui.widget.ComboBox;
import geogebra.web.gui.advanced.client.ui.widget.combo.DropDownPosition;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;


public abstract class ComboBoxW extends ComboBox<ListDataModel> {
	private static final int DEFAULT_VISIBLE_ROWS = 10;
	private static final String DEFAULT_WIDTH = "70px";
	private static final Image choiceImage = new Image(GuiResources.INSTANCE.toolbar_further_tools());
	private static final Image choiceImageActive = new Image(GuiResources.INSTANCE.toolbar_further_tools());
	public ComboBoxW() {
		this(null);
	}

	public ComboBoxW(AppW app) {
		super(app);
		setCustomTextAllowed(true);
		setDropDownPosition(DropDownPosition.UNDER);
		setEnterAction(EnterAction.DO_NOTHING);
		
		setVisibleRows(DEFAULT_VISIBLE_ROWS);
		setWidth(DEFAULT_WIDTH);
		
		this.prepareChoiceButton();
		this.setChoiceButtonVisible(true);
		
		addCloseHandler(new CloseHandler<PopupPanel>() {
			
			public void onClose(CloseEvent<PopupPanel> event) {
				onValueChange(getValue());
			}
		});

		addKeyDownHandler(new KeyDownHandler(){

			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					onValueChange(getValue());
				}
	        }});

		addBlurHandler(new BlurHandler() {

			public void onBlur(BlurEvent event) {
				AutoCompleteTextFieldW.showSymbolButtonIfExists(
				        event.getSource(), false);

			}
		});
		
		addFocusHandler(new FocusHandler() {
			
			public void onFocus(FocusEvent event) {
				App.debug("focus");
				getSelectedValue().showPopupSymbolButton(true);
				// AutoCompleteTextFieldW.showSymbolButtonIfExists(
				// event.getSource(), true);

			}
		});

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
}