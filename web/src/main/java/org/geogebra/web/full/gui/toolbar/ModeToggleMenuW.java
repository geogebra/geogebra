package org.geogebra.web.full.gui.toolbar;

import java.util.ArrayList;
import java.util.Vector;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.html5.euclidian.IsEuclidianController;
import org.geogebra.web.html5.gui.tooltip.ComponentSnackbar;
import org.geogebra.web.html5.gui.tooltip.ToolTip;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ListItem;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.util.UnorderedList;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.NativeEvent;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.event.dom.client.DomEvent;
import org.gwtproject.event.dom.client.HumanInputEvent;
import org.gwtproject.event.dom.client.KeyCodes;
import org.gwtproject.event.dom.client.KeyUpEvent;
import org.gwtproject.event.dom.client.KeyUpHandler;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseDownHandler;
import org.gwtproject.event.dom.client.MouseOutEvent;
import org.gwtproject.event.dom.client.MouseOutHandler;
import org.gwtproject.event.dom.client.MouseOverEvent;
import org.gwtproject.event.dom.client.MouseOverHandler;
import org.gwtproject.event.dom.client.MouseUpEvent;
import org.gwtproject.event.dom.client.MouseUpHandler;
import org.gwtproject.event.dom.client.TouchEndEvent;
import org.gwtproject.event.dom.client.TouchEndHandler;
import org.gwtproject.event.dom.client.TouchStartEvent;
import org.gwtproject.event.dom.client.TouchStartHandler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

public class ModeToggleMenuW extends ListItem
		implements MouseDownHandler, MouseUpHandler, TouchStartHandler,
		TouchEndHandler, MouseOutHandler, MouseOverHandler, KeyUpHandler {

	protected FlowPanel tbutton;
	protected ToolbarSubmenuW submenu;

	protected AppW app;

	protected ToolBarW toolbar;

	protected final Vector<Integer> menu;

	private boolean wasMenuShownOnMouseDown;

	protected int order;

	/**
	 * @param appl
	 *            application
	 * @param menu1
	 *            list of tools
	 * @param tb
	 *            parent toolbar
	 * @param order
	 *            tool order
	 */
	public ModeToggleMenuW(AppW appl, Vector<Integer> menu1, ToolBarW tb,
			int order) {
		super();
		this.order = order;
		this.app = appl;
		this.toolbar = tb;
		this.menu = menu1;
		this.addStyleName("toolbar_item");
		buildButton();
	}

	/**
	 * Create the (top level) button
	 */
	protected void buildButton() {
		tbutton = new FlowPanel();
		tbutton.addStyleName("toolbar_button");
		NoDragImage toolbarImg = new NoDragImage(AppResources.INSTANCE.empty(),
				32);
		GGWToolBar.getImageResource(menu.get(0).intValue(), app, toolbarImg);
		toolbarImg.addStyleName("toolbar_icon");
		tbutton.add(toolbarImg);
		tbutton.getElement().setAttribute("mode", menu.get(0).intValue() + "");
		tbutton.getElement().setAttribute("isMobile", "false");
		addDomHandlers(tbutton);
		this.add(tbutton);
	}

	/**
	 * Create and add UI elements
	 */
	protected void buildGui() {
		submenu = createToolbarSubmenu(app, order);
		add(submenu);

		for (int k = 0; k < menu.size(); k++) {
			final int addMode = menu.get(k).intValue();
			if (addMode < 0) { // TODO
				// // separator within menu:
				// tm.addSeparator();
			} else { // standard case: add mode
				// check mode
				if (!"".equals(app.getToolName(addMode))) {
					ListItem subLi = submenu.addItem(addMode);
					addDomHandlers(subLi);
				}
			}
		}
		hideMenu();
	}

	/**
	 * @param app1
	 *            application
	 * @param order1
	 *            order in top level toolbar
	 * @return submenu
	 */
	protected ToolbarSubmenuW createToolbarSubmenu(AppW app1, int order1) {
		return new ToolbarSubmenuW(app1, order1);
	}

	/**
	 * @param index
	 *            tab index
	 */
	public void setButtonTabIndex(int index) {
		tbutton.getElement().setTabIndex(index);
	}

	/**
	 * @return item list
	 */
	public UnorderedList getItemList() {
		if (submenu != null) {
			return submenu.getItemList();
		}
		return null;
	}

	/**
	 * Add event handlers for the widget.
	 * 
	 * @param w
	 *            widget
	 */
	public void addDomHandlers(Widget w) {
		w.addDomHandler(this, MouseDownEvent.getType());
		w.addDomHandler(this, MouseUpEvent.getType());
		w.addBitlessDomHandler(this, TouchStartEvent.getType());
		w.addBitlessDomHandler(this, TouchEndEvent.getType());
		w.addDomHandler(this, MouseOverEvent.getType());
		w.addDomHandler(this, MouseOutEvent.getType());
		w.addDomHandler(this, KeyUpEvent.getType());
	}

	/**
	 * Sets the menu visible if it exists
	 */
	public void showMenu() {
		if (this.submenu == null) {
			this.buildGui();
		}
		if (submenu != null) {
			submenu.setVisible(true);
			app.registerPopup(submenu);
		}
	}

	/**
	 * Hides the menu if it exists
	 */
	public void hideMenu() {
		if (submenu != null) {
			app.unregisterPopup(submenu);
			submenu.setVisible(false);
		}
	}

	/**
	 * @param visible
	 *            if true sets the menu visible, otherwise it hides it
	 */
	public void setMenuVisibility(boolean visible) {
		if (submenu == null) {
			return;
		}
		if (visible) {
			showMenu();
		} else {
			hideMenu();
		}
	}

	/**
	 * @param mode
	 *            mode
	 * @param m
	 *            mode setting event type
	 * @return whether the mode is available in this menu
	 */
	public boolean selectMode(int mode, ModeSetter m) {
		String modeText = mode + "";
		boolean imageDialog = mode == EuclidianConstants.MODE_IMAGE;
		// If there is only one menuitem, there is no submenu -> set the button
		// selected, if the mode is the same.
		if (menu.size() == 1 && !imageDialog) {
			if (menu.get(0) == mode) {

				showToolTipBottom(mode, m);
				this.setCssToSelected();
				toolbar.update(); // TODO! needed to regenerate the toolbar, if
									// we want to see the border.
									// remove, if it will be updated without
									// this.
				return true;
			}
			return false;
		}
		boolean needsGUI = false;
		if (getItemList() == null) {
			if (menu.get(0) == mode) {

				this.setCssToSelected();
				toolbar.update(); // TODO! needed to regenerate the toolbar, if
									// we want to see the border.
									// remove, if it will be updated without
									// this.
				return true;
			}
			for (Integer i : this.menu) {
				if (i == mode) {
					needsGUI = true;
				}
			}
			if (!needsGUI) {
				return false;
			}
		}
		if (needsGUI) {
			buildGui();
		}

		for (int i = 0; i < getItemList().getWidgetCount(); i++) {
			Widget mi = getItemList().getWidget(i); // submenuitems
			// found item for mode?
			if (mi.getElement().getAttribute("mode").equals(modeText)) {

				if (!imageDialog) {
					selectItem(mi);
				}

				showToolTipBottom(mode, m);
				return true;
			}
		}
		return false;
	}

	/**
	 * @return first mode of this menu
	 */
	public int getFirstMode() {
		if (menu.size() == 0) {
			return -1;
		}
		return menu.get(0);
	}

	void selectItem(Widget mi) {
		final String miMode = mi.getElement().getAttribute("mode");
		// check if the menu item is already selected
		if (tbutton.getElement().getAttribute("isSelected").equals("true")
				&& tbutton.getElement().getAttribute("mode").equals(miMode)) {
			return;
		}

		tbutton.getElement().setAttribute("mode", miMode);
		//
		tbutton.clear();
		NoDragImage buttonImage = new NoDragImage(AppResources.INSTANCE.empty(),
				32);
		GGWToolBar
				.getImageResource(Integer.parseInt(miMode), app, buttonImage);
		buttonImage.addStyleName("toolbar_icon");
		if (Integer.parseInt(miMode) == EuclidianConstants.MODE_DELETE) {
			buttonImage.addStyleName("plusPadding");
		}
		tbutton.add(buttonImage);

		toolbar.update();
		setCssToSelected();

	}

	private void setCssToSelected() {
		ArrayList<ModeToggleMenuW> modeToggleMenus = toolbar
				.getModeToggleMenus();
		for (int i = 0; i < modeToggleMenus.size(); i++) {
			ModeToggleMenuW mtm = modeToggleMenus.get(i);
			if (mtm != this) {
				mtm.getToolbarButtonPanel().getElement().getStyle()
						.setBorderWidth(1, Unit.PX);
				mtm.getToolbarButtonPanel().getElement()
						.setAttribute("isSelected", "false");
			}
		}
		// Set border width explicitly to make sure browser actually does that
		// (otherwise the thicker border applies on next browser event)
		getToolbarButtonPanel().getElement().setAttribute("isSelected", "true");
		getToolbarButtonPanel().getElement().getStyle().setBorderWidth(2,
				Unit.PX);
	}

	/**
	 * Handle pointer up: set app mode.
	 * 
	 * @param event
	 *            mouse up / touch end event
	 */
	public void onEnd(DomEvent<?> event) {
		int mode = Integer
				.parseInt(event.getRelativeElement().getAttribute("mode"));
		if (mode < 999 || mode > 2000) {
			app.hideKeyboard();
		}
		tbutton.getElement().focus();
		event.stopPropagation();
		if (event.getSource() == tbutton) { // if click ended on the button
			// if enter was pressed
			if ((event instanceof KeyUpEvent) && ((KeyUpEvent) event)
					.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				setMenuVisibility(!isMenuShown());
			}
			// if submenu was open
			if (wasMenuShownOnMouseDown && !(event instanceof TouchEndEvent
					&& app.getLAF().isSmart())) {
				hideMenu();
			}
		} else { // click ended on menu item
			hideMenu();
			event.stopPropagation();
		}

		focusViewAndSetMode(mode, event);

		tbutton.getElement().focus();
	}

	protected void focusViewAndSetMode(int mode, DomEvent<?> event) {
		app.getToolTipManager().setBlockToolTip(false);
		// if we click the toolbar button, only interpret it as real click if
		// there is only one tool in this menu
		if (app.getActiveEuclidianView() != null) {
			app.getActiveEuclidianView().requestFocus();
		}
		app.setMode(mode, event.getSource() == tbutton && menu.size() > 1
				? ModeSetter.DOCK_PANEL : ModeSetter.TOOLBAR);
		app.getToolTipManager().setBlockToolTip(true);
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		if (event.getSource() == tbutton) {
			onStart(event);
			CancelEventTimer.touchEventOccurred();
		} else { // clicked on a submenu list item
			event.stopPropagation(); // the submenu doesn't close as a popup,
										// see GeoGebraAppFrame init()
		}
		event.preventDefault();
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		onEnd(event);
		CancelEventTimer.touchEventOccurred();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		onEnd(event);
		if (event.getSource() == tbutton) {
			if (this.app.getActiveEuclidianView() != null
					&& this.app.getActiveEuclidianView()
							.getEuclidianController() != null) {
				((IsEuclidianController) this.app.getActiveEuclidianView()
						.getEuclidianController()).setActualSticky(event
								.getNativeButton() == NativeEvent.BUTTON_RIGHT);
			}
		}
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (event.getSource() == tbutton
				&& !CancelEventTimer.cancelMouseEvent()) {
			onStart(event);
		} else {
			event.stopPropagation(); // the submenu doesn't close as a popup,
										// see GeoGebraAppFrame init()
		}
		event.preventDefault();
	}

	/**
	 * Handles the touchstart and mousedown events on main tools.
	 * 
	 * @param event
	 *            mouse or touch event
	 */
	public void onStart(HumanInputEvent<?> event) {
		event.preventDefault();
		event.stopPropagation();
		this.setFocus(true);
		if (isMenuShown()) {
			wasMenuShownOnMouseDown = true;
		} else {
			toolbar.closeAllSubmenu();
			wasMenuShownOnMouseDown = false;
			showMenu();
			if (menu.size() == 1) {
				showTooltipFor(event);
			}
		}
	}

	/**
	 * Show tooltip after tool taped.
	 * 
	 * @param event
	 *            tap event
	 */
	protected void showTooltipFor(HumanInputEvent<?> event) {
		app.getToolTipManager().setBlockToolTip(false);
		int mode = -1;
		if (event.getSource() == tbutton) {
			mode = menu.get(0);
		} else {
			mode = Integer
					.parseInt(event.getRelativeElement().getAttribute("mode"));
		}
		if (mode >= 0) {
			// if we click the toolbar button, only interpret it as real
			// click if there is only one tool in this menu
			showToolTipBottom(mode, ModeSetter.TOOLBAR);
		}
		app.getToolTipManager().setBlockToolTip(true);

	}

	/**
	 * @param mode
	 *            mode number
	 * @param m
	 *            mode change event type
	 */
	public void showToolTipBottom(int mode, ModeSetter m) {
		if (m != ModeSetter.CAS_VIEW && app.showToolBarHelp()) {
			app.getToolTipManager().showBottomInfoToolTip(new ToolTip(app.getToolName(mode),
					app.getToolHelp(mode), "Help",
					app.getGuiManager().getTooltipURL(mode)), app,
					ComponentSnackbar.TOOL_TOOLTIP_DURATION);
		}
	}

	/**
	 * @return true if the menu is open
	 */
	public boolean isMenuShown() {
		if (submenu != null) {
			return submenu.isVisible();
		}
		return false;
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		if (event.getSource() != tbutton) {
			setHovered(event.getRelativeElement(), true);
			showTooltipFor(event);
			return;
		}
		if (!isMenuShown() && toolbar.isAnyOtherSubmenuOpen(this)) {
			toolbar.closeAllSubmenu();
			showMenu();
		}
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		// Avoid opening submenu, if a user presses a button for a while,
		// then move on an another button without mouseup.
		if (event.getSource() == tbutton) {
			return;
		}
		// submenu's menuitem won't be highlighted
		setHovered(event.getRelativeElement(), false);
	}

	private static void setHovered(Element el, boolean hovered) {
		if (hovered) {
			el.addClassName("hovered");
		} else {
			el.removeClassName("hovered");
		}
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		int keyCode = event.getNativeKeyCode();

		switch (keyCode) {
		default:
			// do nothing
			break;
		case KeyCodes.KEY_ENTER:
			onEnd(event);
			break;
		case KeyCodes.KEY_RIGHT:
		case KeyCodes.KEY_LEFT:
			int indexOfButton = toolbar.getModeToggleMenus().indexOf(this);
			if (keyCode == KeyCodes.KEY_RIGHT) {
				indexOfButton++;
			} else {
				indexOfButton--;
			}

			if (indexOfButton >= 0
					&& indexOfButton < toolbar.getModeToggleMenus().size()) {
				selectMenu(indexOfButton);
			} else {
				toolbar.selectMenuButton(indexOfButton < 0 ? -1 : 0);
			}
			break;
		case KeyCodes.KEY_DOWN:
			if (event.getSource() == tbutton) {
				if (isMenuShown()) {
					this.getItemList().getWidget(0).getElement().focus();
				} else {
					showMenu();
					this.getItemList().getWidget(0).getElement().focus();
				}
			} else {
				Element nextSiblingElement = event.getRelativeElement()
						.getNextSiblingElement();
				if (nextSiblingElement != null) {
					nextSiblingElement.focus();
				} else {
					event.getRelativeElement().getParentElement()
							.getFirstChildElement().focus();
				}
			}
			break;
		case KeyCodes.KEY_UP:
			if (event.getSource() instanceof ListItem) {
				Element previousSiblingElement = event.getRelativeElement()
						.getPreviousSiblingElement();
				if (previousSiblingElement != null) {
					previousSiblingElement.focus();
				} else {
					UnorderedList parentUL = (UnorderedList) ((ListItem) (event
							.getSource())).getParent();
					parentUL.getWidget(parentUL.getWidgetCount() - 1)
							.getElement().focus();
				}

			}
			break;
		}
	}

	private void selectMenu(int index) {
		ModeToggleMenuW mtm2 = toolbar.getModeToggleMenus().get(index);

		mtm2.tbutton.getElement().focus();
		if (isMenuShown()) {
			hideMenu();
			mtm2.showMenu();
		}
	}

	/**
	 * @return the panel containing the toolbar button
	 */
	public FlowPanel getToolbarButtonPanel() {
		return tbutton;
	}

	/**
	 * Add modes to the menu, ignore horizontal separators.
	 * 
	 * @param menu2
	 *            list of modes
	 */
	public void addModes(Vector<Integer> menu2) {
		if (this.submenu == null) {
			this.buildGui();
		}
		for (int k = 0; k < menu2.size(); k++) {
			final int addMode = menu2.get(k).intValue();
			if (addMode < 0) { // TODO
				// // separator within menu:
				// tm.addSeparator();
			} else { // standard case: add mode
				// check mode

				if (app.isModeValid(addMode)) {
					ListItem subLi = submenu.addItem(addMode);
					addDomHandlers(subLi);
				} else {
					Log.debug("Invalid toolbar mode: " + addMode);
				}
			}
		}

	}

	/**
	 * Set submenu max height (to force scrolling in small applets)
	 * 
	 * @param maxHeight
	 *            max height in px
	 */
	public void setMaxHeight(double maxHeight) {
		if (submenu != null) {
			this.submenu.setMaxHeight((int) maxHeight);
		}
	}
}
