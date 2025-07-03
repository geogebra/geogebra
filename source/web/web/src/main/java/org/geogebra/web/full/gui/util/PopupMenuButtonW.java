package org.geogebra.web.full.gui.util;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.Slider;
import org.geogebra.web.html5.gui.util.SliderInputHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.DomEvent;
import org.gwtproject.event.dom.client.TouchEndEvent;
import org.gwtproject.event.logical.shared.CloseEvent;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

/**
 * Table popup for selecting properties of objects
 *
 */
public class PopupMenuButtonW extends StandardButton
		implements SliderInputHandler {
	protected AppW app;
	/**
	 * Icons / texts in the table
	 */
	protected ImageOrText[] data;
	private ButtonPopupMenu myPopup;
	private PopupMenuHandler popupHandler;
	private Slider mySlider;
	private Label sliderLabel;
	private SelectionTableW myTable;
	private final boolean hasTable;
	/** flag to determine if the popup should persist after a mouse click */
	private boolean keepVisible = true;
	private boolean isIniting = true;
	private ImageOrText fixedIcon;
	private boolean isFixedIcon = false;
	private SliderEventHandler changeEventHandler;

	/**
	 * panel for slider
	 */
	protected FlowPanel sliderPanel;
	private static ButtonPopupMenu currentPopup = null;

	/**
	 * @param app
	 *            {@link AppW}
	 * @param data
	 *            {@link ImageOrText}
	 * @param rows
	 *            {@code Integer}
	 * @param columns
	 *            {@code Integer}
	 * @param mode
	 *            {@link SelectionTableW}
	 */
	public PopupMenuButtonW(AppW app, ImageOrText[] data, Integer rows,
			Integer columns, SelectionTable mode) {
		this(app, data, rows, columns, mode, true, false);
	}

	/**
	 * @param app
	 *            {@link AppW}
	 * @param data
	 *            {@link ImageOrText}
	 * @param rows
	 *            {@code Integer}
	 * @param columns
	 *            {@code Integer}
	 * @param mode
	 *            {@link SelectionTableW}
	 * @param hasTable
	 *            whether popup has table
	 * @param hasSlider
	 *            whether popup has slider
	 */
	public PopupMenuButtonW(AppW app, ImageOrText[] data, Integer rows,
			Integer columns, SelectionTable mode, final boolean hasTable,
			boolean hasSlider) {
		super(24);
		this.app = app;
		this.hasTable = hasTable;

		createPopup();
		// merge mousedown + touchstart
		ClickStartHandler.init(this, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				handleClick();
			}
		});

		addBitlessDomHandler(DomEvent::stopPropagation, TouchEndEvent.getType());

		if (hasTable) {
			createSelectionTable(data, rows, columns, mode);
		}

		// create slider
		if (hasSlider) {
			initSlider();
		}
		isIniting = false;
	}

	/**
	 * Hide current popup if visible
	 */
	public static void hideCurrentPopup() {
		if (currentPopup != null) {
			currentPopup.hide();
		}
	}

	/**
	 * Reset current popup reference.
	 */
	public static void resetCurrentPopup() {
		currentPopup = null;
	}

	/**
	 * creates a new {@link ButtonPopupMenu}
	 */
	private void createPopup() {
		myPopup = new ButtonPopupMenu(app.getAppletFrame(), app) {
			@Override
			public void setVisible(boolean visible) {
				super.setVisible(visible);

				// if another button is pressed only the visibility is changed,
				// by firing the event we can react as if it was closed
				CloseEvent.fire(this, this, false);
			}

			@Override
			public void hide() {
				super.hide();
				if (currentPopup != null
						&& currentPopup.equals(this)) {
					currentPopup = null;
				}
			}
		};
		myPopup.addStyleName("matPopupPanel");
		myPopup.setAutoHideEnabled(true);
	}

	/**
	 * handle click on {@link PopupMenuButtonW this button}
	 */
	void handleClick() {
		onClickAction();
		if (currentPopup != myPopup) {
			if (currentPopup != null) {
				currentPopup.hide();
			}
			currentPopup = myPopup;
			app.registerPopup(myPopup);
			myPopup.showRelativeTo(this);
			myPopup.getFocusPanel().getElement().focus();
		} else {
			myPopup.setVisible(false);
			currentPopup = null;
		}
	}

	/**
	 * @param newData
	 *            icons
	 * @param rows
	 *            number of rows
	 * @param columns
	 *            number of columns
	 * @param mode
	 *            selection mode
	 */
	private void createSelectionTable(ImageOrText[] newData, Integer rows,
			Integer columns, SelectionTable mode) {
		this.data = newData;

		myTable = new SelectionTableW(newData, rows, columns, mode);
		if (app.isUnbundledOrWhiteboard()) {
			myTable.addStyleName("matSelectionTable");
		}
		setSelectedIndex(0);

		myTable.addClickHandler(event -> handlePopupActionEvent());

		myPopup.getPanel().add(myTable);
	}

	/**
	 * @param popupMenuHandler
	 *            {@link PopupMenuHandler}
	 */
	public void addPopupHandler(PopupMenuHandler popupMenuHandler) {
		this.popupHandler = popupMenuHandler;
	}

	/**
	 * @return {@link GPopupPanel}
	 */
	public GPopupPanel getMyPopup() {
		return myPopup;
	}

	/**
	 * @return {@link SelectionTableW}
	 */
	public SelectionTableW getMyTable() {
		return myTable;
	}

	/**
	 * called by click on button
	 */
	protected void onClickAction() {
		// called by click on button
		// overridden in (e.g.) EuclidianStayleBar3DW
	}

	/**
	 * Pass a popup action event up to the button invoker. If the first button
	 * click triggered our popup (the click was in the triangle region), then we
	 * must pass action events from the popup to the invoker
	 */
	public void handlePopupActionEvent() {
		if (popupHandler != null) {
			popupHandler.fireActionPerformed(getSelectedIndex());
		} else {
			Log.debug("PopupMenubutton has null popupHandler");
		}

		updateGUI();
		if (!keepVisible) {
			myPopup.hide();
		}
	}

	private void updateGUI() {
		if (isIniting) {
			return;
		}

		if (hasTable) {
			setIcon(getButtonIcon());
		}
	}

	// ==============================================
	// Icon Handling
	// ==============================================

	/**
	 * @return {@link ImageOrText}
	 */
	public @CheckForNull ImageOrText getButtonIcon() {
		int selectedIndex = getSelectedIndex();
		return selectedIndex < 0 ? null : data[selectedIndex];
	}

	/**
	 * Append a downward triangle image to the right hand side of an input icon.
	 */
	@Override
	public void setIcon(ImageOrText icon) {
		if (isFixedIcon) {
			super.setIcon(fixedIcon);
			return;
		}

		// add a down_triangle image to the left of the icon
		if (icon != null) {
			super.setIcon(icon);
		} else {
			setIcon(GuiResources.INSTANCE.toolbar_further_tools());
		}
	}

	/**
	 * @param icon
	 *            {@link ImageOrText}
	 */
	public void setFixedIcon(ImageOrText icon) {
		isFixedIcon = true;
		this.fixedIcon = icon;
		setIcon(icon);
	}

	/**
	 * @param selectedIndex
	 *            {@code Integer}
	 */
	public void setSelectedIndex(Integer selectedIndex) {
		myTable.setSelectedIndex(selectedIndex == null ? -1 : selectedIndex);
		updateGUI();
	}

	@Override
	public void onSliderInput() {
		if (mySlider != null) {
			setSliderValue(mySlider.getValue());
		}
		if (changeEventHandler != null) {
			changeEventHandler.onSliderInput();
		} else {
			Log.debug("Change handler not set");
		}
		fireActionPerformed();
		updateGUI();
	}

	/**
	 * Fires on index change
	 */
	protected void fireActionPerformed() {
		// implemented in subclass
	}

	public Slider getSlider() {
		return mySlider;
	}

	/**
	 * @param show
	 *            true if slider should be shown
	 */
	public void showSlider(boolean show) {
		mySlider.setVisible(show);
		sliderLabel.setVisible(show);
		Widget parent = mySlider.getParent();
		if (parent != null) {
			parent.setStyleName("showSlider", show);
			parent.setStyleName("hideSlider", !show);
		}
	}

	private void initSlider() {
		mySlider = new Slider(0, 100);
		mySlider.setTickSpacing(5);
		mySlider.addValueChangeHandler(evt -> {
			onSliderInput();
			if (changeEventHandler != null) {
				changeEventHandler.onValueChange();
			}
		});

		mySlider.addInputHandler(this);

		sliderLabel = new Label();
		sliderPanel = new FlowPanel();
		sliderPanel.add(mySlider);
		sliderPanel.add(sliderLabel);
		sliderLabel.addStyleName("popupSliderLabel");
		sliderPanel.addStyleName("panelRow");
		sliderPanel.addStyleName("panelRow2");

		myPopup.getPanel().add(sliderPanel);
	}

	@Override
	public void setTitle(String title) {
		if (title == null || title.isEmpty()) {
			getElement().removeAttribute("title");
		} else {
			getElement().setAttribute("title", title);
		}
	}

	/**
	 * @param value
	 *            {@code int}
	 */
	public void setSliderValue(int value) {
		if (mySlider == null) {
			return;
		}
		mySlider.setValue(value);
		setSliderText(value + getSliderPostfix());
		updateGUI();
	}

	/**
	 * Sets the value next to the slider.
	 * 
	 * @param text
	 *            the value string.
	 */
	protected void setSliderText(String text) {
		sliderLabel.setText(text);
	}

	/**
	 * 
	 * @return The postix string after the value of the slider.
	 */
	protected String getSliderPostfix() {
		return "";
	}

	/**
	 * @return selected index of the table
	 */
	public int getSelectedIndex() {
		return myTable.getSelectedIndex();
	}

	/**
	 * @param geos
	 *            GeoElements whose state is displayed in
	 *            this table
	 */
	public void update(List<GeoElement> geos) {
		// will be overwritten from instances
	}

	/**
	 * @return selected Object of the {@link SelectionTableW table}
	 */
	public Object getSelectedValue() {
		return myTable.getSelectedValue();
	}

	/**
	 * @return {@code int} or {@code -1} if the {@link Slider slider} is null
	 */
	public int getSliderValue() {
		return mySlider == null ? -1 : mySlider.getValue();
	}

	/**
	 * @return application
	 */
	public AppW getApp() {
		return app;
	}

	/**
	 * @param keepVisible
	 *            {@code boolean}
	 */
	public void setKeepVisible(boolean keepVisible) {
		this.keepVisible = keepVisible;
	}

	/**
	 * @return {@code true} if it has a slider, {@code false} otherwise
	 */
	protected boolean hasSlider() {
		return mySlider != null;
	}

	/**
	 * explicitly sets who should receive the change events
	 * 
	 * @param handler
	 *            change handler
	 */
	public void setChangeEventHandler(SliderEventHandler handler) {
		this.changeEventHandler = handler;
	}
}
