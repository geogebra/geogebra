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

import org.geogebra.web.full.gui.advanced.client.datamodel.ListDataModel;
import org.geogebra.web.full.gui.advanced.client.ui.AdvancedWidget;
import org.geogebra.web.full.gui.advanced.client.ui.widget.combo.ComboBoxChangeEvent;
import org.geogebra.web.full.gui.advanced.client.ui.widget.combo.DropDownPosition;
import org.geogebra.web.full.gui.advanced.client.ui.widget.combo.ListItemFactory;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget displays a scrollable list of items.
 * <p/>
 * Don't try to use it directly. It's just for the combo box widget.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @param <T>
 *            model type
 * @since 1.2.0
 */
public class ListPopupPanel<T extends ListDataModel> extends GPopupPanel
		implements AdvancedWidget, HasChangeHandlers {
	/** a list of items */
	private FlowPanel list;
	/** items scrolling widget */
	private ScrollPanel scrollPanel;
	/** a flag meaning whether this widget is hidden */
	private boolean hidden = true;
	/** a parent selection box */
	private ComboBox<T> comboBox;
	/** item click handler */
	private ClickHandler itemClickHandler;
	/** mouse event handler */
	private ListMouseHandler mouseEventsListener;
	/** list of displayed items scroll handler */
	private ScrollHandler listScrollHandler;
	/**
	 * the row that is currently highlight in the list but my be not selected in
	 * the model
	 */
	private int highlightRow = -1;
	/**
	 * number of visible rows in the scrollable area of the popup list. Limited
	 * by 30% of screen height by default
	 */
	private int visibleRows = -1;
	/** the top item index to be displayed in the visible area of the list */
	private int startItemIndex = 0;
	/** enables or disables lazy rendering of the items list */
	private boolean lazyRenderingEnabled;
	/**
	 * registration of the
	 * {@link org.gwt.advanced.client.ui.widget.ListPopupPanel.ClickSpyHandler}
	 */
	private HandlerRegistration clickSpyRegistration;
	/** drop down list position */
	private DropDownPosition dropDownPosition = DropDownPosition.AUTO;

	/**
	 * Creates an instance of this class and sets the parent combo box value.
	 *
	 * @param selectionTextBox
	 *            is a selection box value.
	 * @param app
	 *            application
	 */
	protected ListPopupPanel(ComboBox<T> selectionTextBox, AppW app) {
		super(false, false, app.getPanel(), app);
		this.comboBox = selectionTextBox;

		setStyleName("advanced-ListPopupPanel");

		setWidget(getScrollPanel());

		getList().setStyleName("list");

		Window.addResizeHandler(new ListWindowResizeHandler());
	}

	/**
	 * This method adds a handler that will be invoked on choice.
	 *
	 * @param handler
	 *            is a handler to be added.
	 */
	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return addHandler(handler, ChangeEvent.getType());
	}

	/**
	 * Getter for property 'hidden'.
	 *
	 * @return Value for property 'hidden'.
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * Gets a currently highlight row.
	 * <p/>
	 * Note that it may not be equal to the selected row index in the model.
	 *
	 * @return a row number that is currently highlight.
	 */
	public int getHighlightRow() {
		return highlightRow;
	}

	/**
	 * This method gets an actual number of items displayed in the drop down.
	 *
	 * @return an item count.
	 */
	public int getItemCount() {
		return getList().getWidgetCount();
	}

	/**
	 * Gets an item by its index
	 * <p/>
	 * If index < 0 or index >= {@link #getItemCount()} it throws an exception.
	 *
	 * @param index
	 *            is an index of the item to get.
	 * @return a found item.
	 * @throws IndexOutOfBoundsException
	 *             if index is invalid.
	 */
	public Widget getItem(int index) {
		return getList().getWidget(index);
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
		return getList().getWidgetIndex(item);
	}

	/**
	 * Sets the highlight row number.
	 *
	 * @param row
	 *            is a row number to become highlight.
	 */
	protected void setHighlightRow(int row) {
		if (row < getList().getWidgetCount()) {
			Widget widget = null;
			if (this.highlightRow >= 0
					&& getList().getWidgetCount() > this.highlightRow) {
				widget = getList().getWidget(this.highlightRow);
			}

			if (widget != null) {
				widget.removeStyleName("selected-row");
			}
			this.highlightRow = row;
			if (row >= 0) {
				widget = getList().getWidget(this.highlightRow);
				widget.addStyleName("selected-row");
			}
		}
	}

	/**
	 * Checks whether the specified item is visible in the scroll area.
	 * <p/>
	 * The result is <code>true</code> if whole item is visible.
	 *
	 * @param index
	 *            is an index of the item.
	 * @return a result of check.
	 */
	public boolean isItemVisible(int index) {
		Widget item = getList().getWidget(index);
		int itemTop = item.getAbsoluteTop();
		int top = getScrollPanel().getAbsoluteTop();
		return itemTop >= top && itemTop + item.getOffsetHeight() <= top
				+ getScrollPanel().getOffsetHeight();
	}

	/**
	 * Makes the item visible in the list according to the check done by the
	 * {@link #isItemVisible(int)} method.
	 *
	 * @param item
	 *            is an item to check.
	 */
	public void ensureVisible(Widget item) {
		if (!isItemVisible(getList().getWidgetIndex(item))) {
			getScrollPanel().ensureVisible(item);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void hide() {
		if (clickSpyRegistration != null) {
			clickSpyRegistration.removeHandler();
			clickSpyRegistration = null;
		}
		super.hide();
		setHidden(true);
	}

	/** {@inheritDoc} */
	@Override
	public void show() {
		clickSpyRegistration = Event
				.addNativePreviewHandler(new ClickSpyHandler());
		setHidden(false);
		super.show();

		adjustSize();

		setHighlightRow(getComboBox().getModel().getSelectedIndex());
		getComboBox().getDelegateHandler().onFocus(new FocusEvent() {
			// nothing to do
		});
	}

	/**
	 * Gets a number of visible rows.
	 * <p/>
	 * Values <= 0 interpreted as undefined.
	 *
	 * @return a visible rows to be displayed without scrolling.
	 */
	public int getVisibleRows() {
		return visibleRows;
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
		this.visibleRows = visibleRows;
		if (isShowing()) {
			adjustSize();
		}
	}

	/**
	 * Sets an item index that must be displayed on top.
	 * <p/>
	 * If the item is outside the currently visible area the list will be
	 * scrolled down to this item.
	 *
	 * @param index
	 *            is an index of the element to display.
	 */
	public void setStartItemIndex(int index) {

		this.startItemIndex = index < 0 ? 0 : index;
		if (isShowing()) {
			adjustSize();
		}
	}

	/**
	 * @return start index
	 */
	public int getStartItemIndex() {
		return startItemIndex;
	}

	/**
	 * Gets applied position of the drop down list.
	 *
	 * @return a drop down list position value.
	 */
	public DropDownPosition getDropDownPosition() {
		return dropDownPosition;
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
		this.dropDownPosition = dropDownPosition;
		if (isShowing()) {
			adjustSize();
		}
	}

	/**
	 * Adjusts drop down list sizes to make it take optimal area on the screen.
	 */
	protected void adjustSize() {
		ScrollPanel table = getScrollPanel();
		int rowsVisible = getVisibleRows();
		int delta = getElement().getOffsetWidth()
				- getElement().getClientWidth();
		getScrollPanel()
				.setWidth((getComboBox().getOffsetWidth() - delta) + "px");

		if (rowsVisible <= 0) {
			table.setHeight("");
			int spaceAbove = getComboBox().getAbsoluteTop();
			int spaceUnder = Window.getClientHeight()
					- getComboBox().getAbsoluteTop()
					- getComboBox().getOffsetHeight();
			setStyleAttribute(table.getElement(), "maxHeight",
					Math.min(Window.getClientHeight() * 0.3,
							Math.max(spaceAbove, spaceUnder)) + "px");
		} else if (getComboBox().getModel().getCount() > rowsVisible) {
			int index = getStartItemIndex();
			int count = getItemCount();

			if (index + rowsVisible > count) {
				index = count - rowsVisible + 1;
				if (index < 0) {
					index = 0;
				}
			}

			int listHeight = 0;
			int scrollPosition = 0;
			for (int i = 0; i < index + rowsVisible && i < count; i++) {
				int height = getList().getWidget(i).getOffsetHeight();
				if (i < index) {
					scrollPosition += height;
				} else {
					listHeight += height;
				}
			}
			table.setSize(table.getOffsetWidth() + "px", listHeight + "px");
			table.setVerticalScrollPosition(scrollPosition);
			setStyleAttribute(table.getElement(), "maxHeight", "");
		} else {
			table.setHeight("");
			setStyleAttribute(table.getElement(), "maxHeight", "");
		}

		resetPosition();
	}

	/** Chooses and sets a mostly appropriate position of the drop down list */
	protected void resetPosition() {
		int absTop = (int) ((getComboBox().getAbsoluteTop()
				- ((AppW) app).getAbsTop())
				/ ((AppW) app).getGeoGebraElement().getScaleX());

		int absLeft = (int) ((getComboBox().getAbsoluteLeft()
				- ((AppW) app).getAbsLeft())
				/ ((AppW) app).getGeoGebraElement().getScaleX());
		if (getDropDownPosition() == DropDownPosition.ABOVE
				|| getDropDownPosition() == DropDownPosition.AUTO
						&& Window.getClientHeight()
								- absTop
								- getComboBox()
										.getOffsetHeight() < getComboBox()
												.getAbsoluteTop()) {
			setPopupPosition(
					absLeft, absTop - getOffsetHeight());
		} else if (getDropDownPosition() == DropDownPosition.UNDER
				|| getDropDownPosition() == DropDownPosition.AUTO) {
			setPopupPosition(absLeft,
					absTop + getComboBox().getOffsetHeight());
		}
	}

	/**
	 * Checks whether the lazy rendering option is enabled.
	 *
	 * @return a result of check.
	 */
	protected boolean isLazyRenderingEnabled() {
		return lazyRenderingEnabled;
	}

	/**
	 * Enables or disables lazy rendering option.
	 * <p/>
	 * If this option is enabled the list displays only several items on lazily
	 * reders other ones on scroll down.
	 * <p/>
	 * By default lazy rendering is disabled. Switch it on for really large
	 * (over 500 items) lists only.
	 *
	 * @param lazyRenderingEnabled
	 *            is an option value.
	 */
	protected void setLazyRenderingEnabled(boolean lazyRenderingEnabled) {
		this.lazyRenderingEnabled = lazyRenderingEnabled;
	}

	/** This method prepares the list of items for displaying. */
	protected void prepareList() {
		FlowPanel panel = getList();

		if (!isLazyRenderingEnabled()
				|| getComboBox().getModel().getCount() != getItemCount()) {
			panel.clear();
		}

		fillList();

		int selected = getComboBox().getModel().getSelectedIndex();
		selectRow(selected);
		if (selected >= 0 && selected < getItemCount()) {
			ensureVisible(getItem(selected));
		}
	}

	/**
	 * Fills the list of items starting from the current position and ending
	 * with rendering limits
	 * <p/>
	 * See {2link #isRenderingLimitReached()} for additional details since it's
	 * used in the body of this method.
	 */
	protected void fillList() {
		FlowPanel panel = getList();
		ListDataModel model = getComboBox().getModel();
		ListItemFactory itemFactory = getComboBox().getListItemFactory();

		int count = getItemCount();
		int previouslyLoadedRows = count;
		while (!isRenderingLimitReached(previouslyLoadedRows)) {
			panel.add(adoptItemWidget(
					itemFactory.createWidget(model.get(count++))));
		}
	}

	/**
	 * This method checks whether the limit of displayed items reached.
	 * <p/>
	 * It takes into account different aspects including setting of the widget,
	 * geometrical size of the drop down list and selected value that must be
	 * displayed.
	 * <p/>
	 * This method optimally chooses a number of items to display.
	 *
	 * @param previouslyRenderedRows
	 *            is a number of rows previously loaded in the list (items count
	 *            before filling the list).
	 * @return a result of check.
	 */
	protected boolean isRenderingLimitReached(int previouslyRenderedRows) {
		ListDataModel model = getComboBox().getModel();
		int previousHeight = 0;

		if (previouslyRenderedRows > 0) {
			Widget last = getItem(previouslyRenderedRows - 1);
			Widget first = getItem(0);

			previousHeight = last.getOffsetHeight() + last.getAbsoluteTop()
					- first.getAbsoluteTop();
		}

		return model.getCount() <= 0 // no data
				// OR a selected value has already been displayed
				|| getItemCount() > getComboBox().getSelectedIndex()
						// AND one of the following conditions is true:
						&& (getItemCount() >= model.getCount() // no items any
																// more
								// OR no limit but there are too many items
								|| isLazyRenderingEnabled()
										&& getVisibleRows() <= 0
										&& getList().getOffsetHeight()
												- previousHeight >= Window
														.getClientHeight() * 0.6
								// OR visible rows number is limited and there
								// was a new page rendered excepting the first
								// page
								// since two pages may be displayed if the list
								// is rendered first time
								|| isLazyRenderingEnabled()
										&& getVisibleRows() > 0
										&& getItemCount()
												- previouslyRenderedRows > 0
										&& (getItemCount()
												- previouslyRenderedRows)
												% getVisibleRows() == 0
										&& (getItemCount()
												- previouslyRenderedRows)
												/ getVisibleRows() != 1);
	}

	/**
	 * This method higlights a selected row.
	 *
	 * @param newRow
	 *            a row for selection.
	 */
	protected void selectRow(int newRow) {
		ListDataModel model = getComboBox().getModel();
		model.setSelectedIndex(newRow);
	}

	/**
	 * This method wraps the specified widget into the focus panel and adds
	 * necessary listeners.
	 *
	 * @param widget
	 *            is an item widget to be wraped.
	 * @return a focus panel adopted for displaying.
	 */
	protected FocusPanel adoptItemWidget(Widget widget) {
		FocusPanel panel = new FocusPanel(widget);
		panel.addClickHandler(getItemClickHandler());
		panel.addMouseOverHandler(getMouseEventsHandler());
		panel.addMouseOutHandler(getMouseEventsHandler());
		panel.setStyleName("item");
		panel.getElement().getStyle().clearProperty("tabindex");
		return panel;
	}

	/**
	 * Setter for property 'hidden'.
	 *
	 * @param hidden
	 *            Value to set for property 'hidden'.
	 */
	protected void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	/**
	 * Getter for property 'comboBox'.
	 *
	 * @return Value for property 'comboBox'.
	 */
	protected ComboBox<T> getComboBox() {
		return comboBox;
	}

	/**
	 * Getter for property 'list'.
	 *
	 * @return Value for property 'list'.
	 */
	protected FlowPanel getList() {
		if (list == null) {
			list = new FlowPanel();
		}
		return list;
	}

	/**
	 * Getter for property 'scrollPanel'.
	 *
	 * @return Value for property 'scrollPanel'.
	 */
	public ScrollPanel getScrollPanel() {
		if (scrollPanel == null) {
			scrollPanel = new ScrollPanel();
			scrollPanel.setAlwaysShowScrollBars(false);
			scrollPanel.setWidget(getList());
			setStyleAttribute(scrollPanel.getElement(), "overflowX", "hidden");
			scrollPanel.addScrollHandler(getListScrollHandler());
		}
		return scrollPanel;
	}

	private static void setStyleAttribute(Element elem, String attr,
			String value) {
		elem.getStyle().setProperty(attr, value);

	}

	/**
	 * Getter for property 'itemClickHandler'.
	 *
	 * @return Value for property 'itemClickHandler'.
	 */
	protected ClickHandler getItemClickHandler() {
		if (itemClickHandler == null) {
			itemClickHandler = new ItemClickHandler();
		}
		return itemClickHandler;
	}

	/**
	 * Getter for property 'mouseEventsListener'.
	 *
	 * @return Value for property 'mouseEventsListener'.
	 */
	protected ListMouseHandler getMouseEventsHandler() {
		if (mouseEventsListener == null) {
			mouseEventsListener = new ListMouseHandler();
		}
		return mouseEventsListener;
	}

	/**
	 * Getter for property 'listScrollHandler'.
	 *
	 * @return Value for property 'listScrollHandler'.
	 */
	public ScrollHandler getListScrollHandler() {
		if (listScrollHandler == null) {
			listScrollHandler = new ListScrollHandler();
		}
		return listScrollHandler;
	}

	/** This is a click handler required to dispatch click events. */
	protected class ItemClickHandler implements ClickHandler {
		/** {@inheritDoc} */
		@Override
		public void onClick(ClickEvent event) {
			int row = getList().getWidgetIndex((Widget) event.getSource());
			selectRow(row);
			ChangeEvent changeEvent = new ComboBoxChangeEvent(row,
					ComboBoxChangeEvent.ChangeEventInputDevice.MOUSE);
			fireEvent(changeEvent);
		}
	}

	/**
	 * This listener is required to handle mouse moving events over the list.
	 */
	protected class ListMouseHandler
			implements MouseOverHandler, MouseOutHandler {
		/** {@inheritDoc} */
		@Override
		public void onMouseOut(MouseOutEvent event) {
			if (getComboBox().isKeyPressed()) {
				return;
			}
			((Widget) event.getSource()).removeStyleName("selected-row");
		}

		/** {@inheritDoc} */
		@Override
		public void onMouseOver(MouseOverEvent event) {
			if (getComboBox().isKeyPressed()) {
				return;
			}
			int index = getComboBox().getModel().getSelectedIndex();
			if (index >= 0) {
				getList().getWidget(index).removeStyleName("selected-row");
			}
			Widget sender = (Widget) event.getSource();
			sender.addStyleName("selected-row");
			setHighlightRow(getList().getWidgetIndex(sender));
		}
	}

	/**
	 * This scroll handler is invoked on any scrolling event caotured by the
	 * items list.
	 * <p/>
	 * It check whether the scrolling position value is equal to the last item
	 * position and tries to render the next page of data.
	 */
	protected class ListScrollHandler implements ScrollHandler {
		/** the list has been scrolled programatically */
		private boolean autoScrollingEnabled;

		/** see class docs */
		@Override
		public void onScroll(ScrollEvent event) {
			if (isLazyRenderingEnabled() && !autoScrollingEnabled
					&& getList().getOffsetHeight() - getScrollPanel()
							.getVerticalScrollPosition() <= getScrollPanel()
									.getOffsetHeight()) {
				int firstItemOnNextPage = getItemCount() - 1;
				fillList(); // next page of data
				if (firstItemOnNextPage >= 0
						&& firstItemOnNextPage < getItemCount()) {
					autoScrollingEnabled = true;
					ensureVisible(getItem(firstItemOnNextPage));
				}
			} else {
				autoScrollingEnabled = false;
			}
		}
	}

	/**
	 * This handler is invoked on window resize and changes opened list popup
	 * panel position according to new coordinates of the {@link ComboBox}.
	 */
	protected class ListWindowResizeHandler implements ResizeHandler {
		/** See class docs */
		@Override
		public void onResize(ResizeEvent resizeEvent) {
			if (!isShowing()) {
				return;
			}

			int delta = getElement().getOffsetWidth()
					- getElement().getClientWidth();
			getScrollPanel()
					.setWidth((getComboBox().getOffsetWidth() - delta) + "px");
			adjustSize();
		}
	}

	/**
	 * This handler spies for click events if the list is opened and hides it if
	 * there is any element clicked excepting the combo box elements and list
	 * elements.
	 */
	protected class ClickSpyHandler implements Event.NativePreviewHandler {
		/** See class docs */
		@Override
		public void onPreviewNativeEvent(
				Event.NativePreviewEvent nativePreviewEvent) {
			if (nativePreviewEvent.getTypeInt() != Event.ONCLICK) {
				return;
			}

			Element source = Element
					.as(nativePreviewEvent.getNativeEvent().getEventTarget());
			if (!getElement().isOrHasChild(source)
					&& !getComboBox().getElement().isOrHasChild(source)) {
				hide();
				getComboBox().getChoiceButton().setDown(false);
			}
		}
	}
}