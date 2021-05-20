/*
 * Copyright 2008-2013 Sergey Skladchikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.geogebra.web.full.gui.advanced.client.ui.widget;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.geogebra.web.full.gui.advanced.client.datamodel.ComboBoxDataModel;
import org.geogebra.web.full.gui.advanced.client.datamodel.ListDataModel;
import org.geogebra.web.full.gui.advanced.client.datamodel.ListModelEvent;
import org.geogebra.web.full.gui.advanced.client.datamodel.ListModelListener;
import org.geogebra.web.full.gui.advanced.client.ui.widget.combo.ComboBoxChangeEvent;
import org.geogebra.web.full.gui.advanced.client.ui.widget.combo.DefaultListItemFactory;
import org.geogebra.web.full.gui.advanced.client.ui.widget.combo.DropDownPosition;
import org.geogebra.web.full.gui.advanced.client.ui.widget.combo.ListItemFactory;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.GToggleButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a combo box widget implementation.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @param <T>
 *            model type
 * @since 1.2.0
 */
public class ComboBox<T extends ListDataModel> extends TextButtonPanel<String>
		implements HasAllFocusHandlers, HasAllKeyHandlers, HasClickHandlers,
		ListModelListener, HasChangeHandlers, HasCloseHandlers<GPopupPanel> {

	/** a combo box data model */
	private ListDataModel model;
	/** a list item factory */
	private ListItemFactory listItemFactory;
	/** a list popup panel */
	private ListPopupPanel<T> listPanel;
	/** a combo box delegate listener */
	private DelegateHandler delegateHandler;
	/**
	 * a keyboard events listener that switches off default browser handling and
	 * replaces it with conponents'
	 */
	private ComboBoxKeyboardManager keyboardManager;
	/** a flag that is <code>true</code> if any control key is pressed */
	private boolean keyPressed;
	
	/**
	 * @param app
	 *            application
	 */
	public ComboBox(AppW app) {
		super(app);
	}

	/**
	 * Setter for property 'model'.
	 *
	 * @param model
	 *            Value to set for property 'model'.
	 */
	public void setModel(T model) {
		if (model != null && this.model != model) {
			if (this.model != null) {
				this.model.removeListModelListener(this);
			}
			this.model = model;
			this.model.addListModelListener(this);
			prepareSelectedValue();
		}
	}

	/**
	 * Setter for property 'listItemFactory'.
	 *
	 * @param listItemFactory
	 *            Value to set for property 'listItemFactory'.
	 */
	public void setListItemFactory(ListItemFactory listItemFactory) {
		if (listItemFactory != null) {
			this.listItemFactory = listItemFactory;
		}
		if (isListPanelOpened()) {
			getListPanel().prepareList();
		}
	}

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return addHandler(handler, BlurEvent.getType());
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return addHandler(handler, FocusEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return addHandler(handler, KeyUpEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return addHandler(handler, KeyDownEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return addHandler(handler, KeyPressEvent.getType());
	}

	/**
	 * Note that handlers added by this method will receive
	 * {@link org.geogebra.web.full.gui.advanced.client.ui.widget.combo.ComboBoxChangeEvent}
	 * s.
	 *
	 * @param handler
	 *            the change handler
	 * @return a handler registration.
	 */
	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return addHandler(handler, ChangeEvent.getType());
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addHandler(handler, ClickEvent.getType());
	}

	/**
	 * Getter for property 'model'.
	 *
	 * @return Value for property 'model'.
	 */
	public ListDataModel getModel() {
		if (model == null) {
			model = new ComboBoxDataModel();
			model.addListModelListener(this);
		}
		return model;
	}

	/**
	 * Getter for property 'listItemFactory'.
	 *
	 * @return Value for property 'listItemFactory'.
	 */
	public ListItemFactory getListItemFactory() {
		if (listItemFactory == null) {
			listItemFactory = new DefaultListItemFactory();
		}
		return listItemFactory;
	}

	/**
	 * This method sets focus on this widget.
	 * <p/>
	 * But note that the combo box is not a focus event sourcer. It siply
	 * delegtes this functionality to the text box.
	 *
	 * @param focus
	 *            is a flag of focus.
	 */
	public void setFocus(boolean focus) {
		AutoCompleteTextFieldW tfValue = getSelectedValue();
		if (isCustomTextAllowed()) {
			tfValue.setFocus(focus);
		} else {
			getChoiceButton().setFocus(focus);
		}
	}

	/**
	 * This method check the list panel status.
	 *
	 * @return <code>true</code> if it's opened.
	 */
	public boolean isListPanelOpened() {
		return !getListPanel().isHidden();
	}

	/**
	 * This method returns a value currently displayed in the text box.
	 *
	 * @return a text value.
	 *
	 * @deprecated use {@link #getValue()} instead.
	 */
	@Deprecated
	public String getText() {
		return getSelectedValue().getText();
	}

	/**
	 * Gets a number of visible rows.
	 * <p/>
	 * Values <= 0 interpreted as undefined.
	 *
	 * @return a visible rows to be displayed without scrolling.
	 */
	public int getVisibleRows() {
		return getListPanel().getVisibleRows();
	}

	/**
	 * Sets visible rows number.
	 * <p/>
	 * You can pass a value <= 0. It will mean that this parameter in undefined.
	 *
	 * @param visibleRows
	 *            is a number of rows to be displayed without scrolling.
	 */
	public void setVisibleRows(int visibleRows) {
		getListPanel().setVisibleRows(visibleRows);
	}

	/**
	 * Sets an item index that must be displayed on top.
	 * <p/>
	 * If the item is outside the currently visible area the list will be
	 * scrolled down to this item.
	 *
	 * @param index
	 *            is an index of the item that must be displayed on top of the
	 *            visible area.
	 */
	public void setStartItemIndex(int index) {
		getListPanel().setStartItemIndex(index);
	}

	/**
	 * @return start index
	 */
	public int getStartItemIndex() {
		return getListPanel().getStartItemIndex();
	}

	/**
	 * Sets text to the selected value box but doesn't change anything in the
	 * list of items.
	 *
	 * @param text
	 *            is a text to set.
	 *
	 * @deprecated use {@link #setValue(String)} instead.
	 */
	@Deprecated
	public void setText(String text) {
		getSelectedValue().setText(text);
	}

	/**
	 * This method returns a selected item.
	 *
	 * @return is a selected item.
	 */
	public Object getSelected() {
		return getModel().getSelected();
	}

	/**
	 * This method returns a selected item index.
	 *
	 * @return is a selected item index.
	 */
	public int getSelectedIndex() {
		return getModel().getSelectedIndex();
	}

	/**
	 * This method returns a selected item ID.
	 *
	 * @return is a selected item ID.
	 */
	public String getSelectedId() {
		return getModel().getSelectedId();
	}

	/**
	 * This method sets the selected item ID.
	 *
	 * @param id
	 *            is an item ID to select.
	 */
	public void setSelectedId(String id) {
		getModel().setSelectedId(id);
	}

	/**
	 * This method sets the selected item index.
	 *
	 * @param index
	 *            a selected item index.
	 */
	public void setSelectedIndex(int index) {
		getModel().setSelectedIndex(index);
	}

	/**
	 * Opens / closes list popup panel by request.
	 *
	 * @param opened
	 *            <code>true</code> means "show".
	 */
	public void setListPopupOpened(boolean opened) {
		if (opened) {
			getListPanel().show();
		} else {
			getListPanel().hide();
		}
	}

	/**
	 * This method gets a widget that is currently selected in the drop down
	 * list.
	 *
	 * @return <code>null</code> if the drop down list is collapsed.
	 */
	public Widget getSelectedWidget() {
		if (isListPanelOpened() && getModel().getSelectedIndex() >= 0) {
			FlowPanel list = getListPanel().getList();
			if (list.getWidgetCount() > getModel().getSelectedIndex()) {
				return list.getWidget(getModel().getSelectedIndex());
			}
			return null;
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public void cleanSelection() {
		super.cleanSelection();
		getModel().clear();
	}

	/**
	 * Gets a highlight row number.
	 * <p/>
	 * Note that sometimes this value is not equal to the selected row.
	 *
	 * @return a highlight row number or <code>-1</code> if nothing is
	 *         highlight.
	 */
	public int getHighlightRow() {
		return getListPanel().getHighlightRow();
	}

	/**
	 * Sets a highlight row number and display the row as selected but not
	 * actually select it.
	 *
	 * @param row
	 *            is a row number to highlight. If it's out of range thus method
	 *            does nothing.
	 */
	public void setHighlightRow(int row) {
		getListPanel().setHighlightRow(row);
	}

	/**
	 * This method gets an actual number of items displayed in the drop down.
	 *
	 * @return an item count.
	 */
	public int getItemCount() {
		return getListPanel().getItemCount();
	}

	/**
	 * Gets an item by its index
	 * <p/>
	 * If index < 0 or index >= {@link #getItemCount()} it throws an exception.
	 *
	 * @param index
	 *            is an index of the item to get.
	 * @return a foudn item.
	 * @throws IndexOutOfBoundsException
	 *             if index is invalid.
	 */
	public Widget getItem(int index) {
		return getListPanel().getItem(index);
	}

	/**
	 * Gets an item index if it's displayed in the drop down list.
	 * <p/>
	 * Otherwise returns <code>-1</code>.
	 *
	 * @param item
	 *            an item that is required to return.
	 * @return an item index value or <code>-1</code>.
	 */
	public int getItemIndex(Widget item) {
		return getListPanel().getItemIndex(item);
	}

	/**
	 * This method shows the drop down list.
	 *
	 * @param prepareList
	 *            forces the list to be prepared (refreshed) before displaying.
	 */
	public void showList(boolean prepareList) {
		getListPanel().show();
		if (prepareList) {
			getListPanel().prepareList();
		}
		if (getItemCount() <= 0) {
			getListPanel().hide();
		}
	}

	/**
	 * Moves the cursor up or down.
	 *
	 * @param step
	 *            is a number of items relative to the current cursor position.
	 */
	public void moveCursor(int step) {
		int row = getListPanel().getHighlightRow();
		if (step == 0 || row + step < 0 || row + step >= getItemCount()) {
			return;
		}

		row += step;

		if (row != getListPanel().getHighlightRow()) {
			if (row >= getModel().getCount()) {
				row = getModel().getCount() - 1;
			}
			if (row < 0) {
				row = 0;
			}

			getListPanel().setHighlightRow(row);
			Widget item = getListPanel().getList().getWidget(row);
			getListPanel().ensureVisible(item);
		}
	}

	/** Hides the drop down list. */
	public void hideList() {
		getListPanel().hide();
		getChoiceButton().setDown(false);
	}

	/**
	 * Selects the specified item in the model and in the drop down list.
	 *
	 * @param row
	 *            is a row index to select
	 */
	public void select(int row) {
		getModel().setSelectedIndex(row);
		getListPanel().hide();
		getSelectedValue().removeStyleName("selected-row");
		getChoiceButton().setDown(false);
	}

	/** {@inheritDoc} */
	@Override
	public void onModelEvent(ListModelEvent event) {
		if (event.getType() == ListModelEvent.ADD_ITEM) {
			add(event);
		} else if (event.getType() == ListModelEvent.CLEAN) {
			clean(event);
		} else if (event.getType() == ListModelEvent.REMOVE_ITEM) {
			remove(event);
		} else if (event.getType() == ListModelEvent.SELECT_ITEM) {
			select(event);
		}
		getListPanel().adjustSize();
	}

	/**
	 * Checks whether the lazy rendering option is enabled.
	 *
	 * @return a result of check.
	 */
	public boolean isLazyRenderingEnabled() {
		return getListPanel().isLazyRenderingEnabled();
	}

	/**
	 * Enables or disables lazy rendering option.
	 * <p/>
	 * If this option is enabled the widget displays only several items on
	 * lazily renders other ones on scroll down.
	 * <p/>
	 * By default lazy rendering is disabled. Switch it on for really large
	 * (over 500 items) lists only.
	 * <p/>
	 * Note that <i>lazy rendering</i> is not <i>lazy data loading</i>. The
	 * second one means that the data is loaded into the model on request where
	 * as the first option assumes that all necessary data has already been
	 * loaded and put into the model. If you need <i>lazy loading</i> please
	 * consider using SuggestionBox
	 *
	 * @param lazyRenderingEnabled
	 *            is an option value.
	 */
	public void setLazyRenderingEnabled(boolean lazyRenderingEnabled) {
		getListPanel().setLazyRenderingEnabled(lazyRenderingEnabled);
	}

	/**
	 * Gets applied position of the drop down list.
	 *
	 * @return a drop down list position value.
	 */
	public DropDownPosition getDropDownPosition() {
		return getListPanel().getDropDownPosition();
	}

	/**
	 * Sets applied position of the drop down list.
	 * <p/>
	 * Being set the drop down is immediately applied if the list is opened.
	 *
	 * @param dropDownPosition
	 *            is a drop down list position value.
	 */
	public void setDropDownPosition(DropDownPosition dropDownPosition) {
		getListPanel().setDropDownPosition(dropDownPosition);
	}

	/**
	 * Adds a close handler to the popup list of items.
	 *
	 * @param handler
	 *            is a handler to add.
	 * @return a handler registration.
	 */
	@Override
	public HandlerRegistration addCloseHandler(
			CloseHandler<GPopupPanel> handler) {
		return getListPanel().addCloseHandler(handler);
	}

	/** Similar to {@link #getText()} */
	@Override
	public String getValue() {
		return getSelectedValue().getText();
	}

	/** Similar to {@link #setText(String)} and doesn't send any event */
	@Override
	public void setValue(String value) {
		setValue(value, false);
	}

	/**
	 * Similar to {@link #setText(String)} and sends {@code ValueChangeEvent} if
	 * {@code fireEvents = true}
	 */
	@Override
	public void setValue(String value, boolean fireEvents) {
		getSelectedValue().setText(value);
		if (fireEvents) {
			fireEvent(new ValueChangeEvent<String>(value) {
				// nothing to implement
			});
		}
	}

	/**
	 * Adds a value change handler to the component that will be invoked only if
	 * {@link #setValue(String, boolean)} has the second parameter =
	 * {@code true}.
	 * <p/>
	 *
	 * Note that the widget doesn't fire the event if you don't use the method
	 * specified above.
	 */
	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<String> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	/**
	 * Adds a new visual item into the drop down list every time when it's added
	 * into the data model.
	 *
	 * @param event
	 *            is an event containing data about the item.
	 */
	protected void add(ListModelEvent event) {
		if (isListPanelOpened()) {
			for (Map.Entry<String, Integer> entry : event.getItemIndexes()
					.entrySet()) {
				if (entry.getValue() <= getItemCount()) {
					Widget item = getListItemFactory().createWidget(
							event.getSource().get(entry.getKey()));
					item = getListPanel().adoptItemWidget(item);

					if (entry.getValue() < getListPanel().getList()
							.getWidgetCount()) {
						getListPanel().getList().insert(item, entry.getValue());
					} else {
						getListPanel().getList().add(item);
					}
				} else {
					getListPanel().prepareList();
					if (getItemCount() <= 0) {
						getListPanel().hide();
					}
				}
			}
		}
	}

	/**
	 * This method cleans the drop down list on each data clean.
	 *
	 * @param event
	 *            is a clean event.
	 */
	protected void clean(ListModelEvent event) {
		if (isListPanelOpened()) {
			getListPanel().getList().clear();
			getListPanel().hide();
		}
	}

	/**
	 * Removes a visual item from the drop down list if the remove event is
	 * received.
	 *
	 * @param event
	 *            is an event that contains data of the removed item.
	 */
	protected void remove(ListModelEvent event) {
		if (isListPanelOpened()) {
			for (Map.Entry<String, Integer> entry : event.getItemIndexes()
					.entrySet()) {
				if (entry.getValue() < getListPanel().getList()
						.getWidgetCount()) {
					getListPanel().remove(getListPanel().getList()
							.getWidget(entry.getValue()));
					if (getListPanel().getList().getWidgetCount() <= 0) {
						getListPanel().hide();
					}
				}
			}
		}
	}

	/**
	 * Highlights the visual item in the drop down list if it's selected in the
	 * model.
	 *
	 * @param event
	 *            is an event that contains data about selected item.
	 */
	protected void select(ListModelEvent event) {
		if (event.getItemIndex() >= 0) {
			if (isListPanelOpened()) {
				getListPanel().setHighlightRow(event.getItemIndex());
			}
			setValue(getListItemFactory().convert(model.getSelected()));
		}
	}

	/**
	 * Returns <code>true</code> if cursor is moved by a control key.
	 *
	 * @return a flag value.
	 */
	protected boolean isKeyPressed() {
		return keyPressed;
	}

	/**
	 * Sets the value of the key pressed flag.
	 *
	 * @param keyPressed
	 *            is a key pressed flag value.
	 */
	protected void setKeyPressed(boolean keyPressed) {
		this.keyPressed = keyPressed;
	}

	/** {@inheritDoc} */
	@Override
	protected void prepareSelectedValue() {
		super.prepareSelectedValue();
		getSelectedValue().setText(
				getListItemFactory().convert(getModel().getSelected()));
	}

	/** {@inheritDoc} */
	@Override
	protected void addComponentListeners() {
		AutoCompleteTextFieldW value = getSelectedValue();
		GToggleButton button = getChoiceButton();

		getListPanel().addChangeHandler(getDelegateHandler());

		// value.addChangeHandler(getDelegateHandler());
		button.addFocusHandler(getDelegateHandler());
		value.addFocusHandler(getDelegateHandler());
		button.addBlurHandler(getDelegateHandler());
		value.addBlurHandler(getDelegateHandler());
		// value.addClickHandler(getDelegateHandler());
		button.addClickHandler(getDelegateHandler());
		value.addKeyUpHandler(getDelegateHandler());
		// value.addKeyDownHandler(getDelegateHandler());
		value.addKeyPressHandler(getDelegateHandler());
	}

	/**
	 * Getter for property 'listPanel'.
	 *
	 * @return Value for property 'listPanel'.
	 */
	protected ListPopupPanel<T> getListPanel() {
		if (listPanel == null) {
			listPanel = new ListPopupPanel<>(this, getApp());
		}
		return listPanel;
	}

	/**
	 * Getter for property 'delegateHandler'.
	 *
	 * @return Value for property 'delegateHandler'.
	 */
	protected DelegateHandler getDelegateHandler() {
		if (delegateHandler == null) {
			delegateHandler = new DelegateHandler();
		}
		return delegateHandler;
	}

	/**
	 * This method gets a keybord manager implementation for the component.
	 *
	 * @return an instance of the manager.
	 */
	protected ComboBoxKeyboardManager getKeyboardManager() {
		if (keyboardManager == null) {
			keyboardManager = new ComboBoxKeyboardManager();
		}
		return keyboardManager;
	}

	/**
	 * Universal handler that delegates all events handling to custom handlers.
	 */
	protected class DelegateHandler
			implements FocusHandler, BlurHandler, ClickHandler, ChangeHandler,
			KeyUpHandler, KeyDownHandler, KeyPressHandler {
		/** a list of focused controls */
		private Set<Object> focuses;
		/** keyboard manager handler registration */
		private HandlerRegistration keyboardManagerRegistration;

		@Override
		public void onBlur(BlurEvent event) {
			if (!isFocus()) {
				return;
			}

			if (keyboardManagerRegistration != null) {
				keyboardManagerRegistration.removeHandler();
				keyboardManagerRegistration = null;
			}

			Object sender = event.getSource();
			getFocuses().remove(sender);

			AutoCompleteTextFieldW value = getSelectedValue();
			if (sender == value && !isCustomTextAllowed()) {
				value.removeStyleName("selected-row");
			}

			if (!isFocus()) {
				fireEvent(event);
			}
		}

		@Override
		public void onFocus(FocusEvent event) {
			Object sender = event.getSource();
			getFocuses().add(sender);

			if (keyboardManagerRegistration == null) {
				keyboardManagerRegistration = Event
						.addNativePreviewHandler(getKeyboardManager());
			}

			AutoCompleteTextFieldW value = getSelectedValue();
			if (sender == value) {
				if (!isCustomTextAllowed()) {
					value.addStyleName("selected-row");
					if (isChoiceButtonVisible()) {
						getChoiceButton().setFocus(true);
					}
				}

			} else if (sender == null || sender == getListPanel()) { // on drop
																		// down
																		// list
																		// show
				Widget widget = getSelectedWidget();
				if (widget != null) {
					getListPanel().ensureVisible(widget);
				}
			}

			if (focuses.size() == 1) {
				fireEvent(event);
			}
		}

		@Override
		public void onChange(ChangeEvent event) {
			if (event.getSource() == getListPanel()) {
				getSelectedValue().setText(
						getListItemFactory().convert(getModel().getSelected()));
				getListPanel().hide();
				getSelectedValue().removeStyleName("selected-row");
				getChoiceButton().setDown(false);
			}
			fireEvent(event);
		}

		@Override
		public void onClick(ClickEvent event) {
			int count = getModel().getCount();
			Object sender = event.getSource();
			if (sender instanceof GToggleButton || !isCustomTextAllowed()) {
				if (count > 0 && !getListPanel().isShowing()) {
					getListPanel().prepareList();
					getListPanel().show();
					if (getItemCount() <= 0) {
						getListPanel().hide();
					}
					getChoiceButton().setDown(true);
				} else {
					getListPanel().hide();
					getChoiceButton().setDown(false);
				}
			}
			fireEvent(event);
		}

		@Override
		public void onKeyUp(KeyUpEvent event) {
			fireEvent(event);
		}

		@Override
		public void onKeyPress(KeyPressEvent event) {
			fireEvent(event);
		}

		@Override
		public void onKeyDown(KeyDownEvent event) {
			fireEvent(event);
		}

		/**
		 * @return set of focuses
		 */
		protected Set<Object> getFocuses() {
			if (focuses == null) {
				focuses = new HashSet<>();
			}
			return focuses;
		}

		/**
		 * Getter for property 'focus'.
		 *
		 * @return Value for property 'focus'.
		 */
		protected boolean isFocus() {
			return getFocuses().size() > 0;
		}
	}

	/**
	 * This is a keyboard manager implementation developed for the widget.
	 * <p/>
	 * It prevents default browser event handling for system keys like arrow up
	 * / down, escape, enter and tab. This manager is activated on widget focus
	 * and is used for opening / closing the drop down list and switching a
	 * cursor position in the list.
	 * <p/>
	 * It also supports Shift+Tab combination but skips other modifiers.
	 */
	protected class ComboBoxKeyboardManager
			implements Event.NativePreviewHandler {
		/** See class docs */
		@Override
		public void onPreviewNativeEvent(Event.NativePreviewEvent event) {
			NativeEvent nativeEvent = event.getNativeEvent();
			EventTarget eventTarget = nativeEvent.getEventTarget();
			if (!Element.is(eventTarget)) {
				return;
			}

			Element target = Element.as(eventTarget);

			int type = event.getTypeInt();
			if (type == Event.ONKEYDOWN) {
				setKeyPressed(true);
				if (DOM.getCaptureElement() != null) {
					return;
				}

				boolean eventTargetsPopup = (target != null)
						&& getElement().isOrHasChild(target);
				int button = nativeEvent.getKeyCode();
				boolean alt = nativeEvent.getAltKey();
				boolean ctrl = nativeEvent.getCtrlKey();
				boolean shift = nativeEvent.getShiftKey();

				boolean hasModifiers = alt || ctrl || shift;

				if (eventTargetsPopup && isListPanelOpened()) {
					if (button == KeyCodes.KEY_UP && !hasModifiers) {
						moveCursor(-1);
						cancelAndPrevent(event);
					} else if (button == KeyCodes.KEY_DOWN && !hasModifiers) {
						moveCursor(1);
						cancelAndPrevent(event);
					} else if (button == KeyCodes.KEY_ENTER && !hasModifiers) {
						if (getEnterAction() == EnterAction.OPEN_DROP_DOWN) {
							select(getHighlightRow());
							getChoiceButton().setFocus(false);
							ChangeEvent changeEvent = new ComboBoxChangeEvent(
									getHighlightRow(),
									ComboBoxChangeEvent.ChangeEventInputDevice.KEYBOARD);
							fireEvent(changeEvent);
							cancelAndPrevent(event);
						}
						setKeyPressed(false);
					} else if (button == KeyCodes.KEY_ESCAPE && !hasModifiers) {
						hideList();
						setKeyPressed(false);
					} else if (button == KeyCodes.KEY_TAB
							&& (!hasModifiers || !alt && !ctrl)) {
						hideList();
						setKeyPressed(false);
					}
				} else if (eventTargetsPopup && !hasModifiers
						&& button == KeyCodes.KEY_ENTER
						&& getModel().getCount() > 0) {
					if (getEnterAction() == EnterAction.OPEN_DROP_DOWN) {
						showList(true);
					}
				}
			} else if (type == Event.ONKEYUP) {
				setKeyPressed(false);
			}
		}

		/**
		 * This method cancels and prevents default actions of the specified
		 * event.
		 * <p/>
		 * It's useful for stupid browsers like Opera.
		 *
		 * @param event
		 *            is an event to cancel and prevent.
		 */
		protected void cancelAndPrevent(Event.NativePreviewEvent event) {
			event.getNativeEvent().preventDefault();
			event.cancel();
		}
	}
}