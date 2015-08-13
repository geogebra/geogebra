package org.geogebra.web.web.gui.toolbar;

import java.util.ArrayList;
import java.util.Vector;

import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.euclidian.IsEuclidianController;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ListItem;
import org.geogebra.web.html5.gui.util.UnorderedList;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.NoDragImage;
import org.geogebra.web.web.gui.app.GGWToolBar;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;


public class ModeToggleMenu extends ListItem implements MouseDownHandler, MouseUpHandler, 
TouchStartHandler, TouchEndHandler, MouseOutHandler, MouseOverHandler, KeyUpHandler{


	private FlowPanel tbutton;
	protected ToolbarSubemuW submenu;

	private AppW app;

	protected ToolBarW toolbar;

	private final Vector<Integer> menu;
	
	private String toolTipText;
	
	private boolean wasMenuShownOnMouseDown;

	private int order;

	public ModeToggleMenu(AppW appl, Vector<Integer> menu1, ToolBarW tb, int order) {
		super();
		this.order = order;
		this.app = appl;
		this.toolbar = tb;
		this.menu = menu1;
		this.addStyleName("toolbar_item");
		buildButton();
	}

	private void buildButton() {
		tbutton = new FlowPanel();
		tbutton.addStyleName("toolbar_button");
		Image toolbarImg = new NoDragImage(((GGWToolBar)app.getToolbar()).getImageURL(menu.get(0).intValue()),32);
		toolbarImg.addStyleName("toolbar_icon");
		tbutton.add(toolbarImg);
		tbutton.getElement().setAttribute("mode",menu.get(0).intValue()+"");	
		addDomHandlers(tbutton);
		tbutton.addDomHandler(this, MouseOutEvent.getType());
		tbutton.addDomHandler(this, KeyUpEvent.getType());
		tbutton.addDomHandler(this, MouseOverEvent.getType());
		addPasteHandlerTo(tbutton.getElement());
		this.add(tbutton);
		setToolTipText(app.getToolTooltipHTML(menu.get(0).intValue()));
    }

	public native void addPasteHandlerTo(Element elem) /*-{
		var self = this;
		var wind = $wnd;
		var docu = $doc;
		elem.onkeydown = function(event) {
			event.stopPropagation();
		};
		elem.onkeypress = function(event) {
			event.stopPropagation();
		};

		if (wind.onpastehandleradded === undefined) {
			// onpastehandler has nothing to do with
			// "elem", so it's enough to register it once
			// but remember whether it's registered
			wind.onpastehandleradded = function(event) {
				event.stopPropagation();
				var cbd;
				if (event.clipboardData) {
					// all the other browsers
					cbd = event.clipboardData;
				} else if (wind.clipboardData) {
					// Windows Internet Explorer
					cbd = wind.clipboardData;
				}
				if (cbd.items) {
					var is = cbd.items;
					for (var cv = 0; cv < is.length; cv++) {
						if (is[cv].type.indexOf('image') > -1) {
							var bb = is[cv].getAsFile();
							var ur = wind.URL || wind.webkitURL;
							var sr = ur.createObjectURL(bb);
							var pi = new Image();
							pi.onload = function() {
								// some code to get the data URL
								// which GeoGebra knows how to convert
								var ca = docu.createElement('canvas');
								ca.width = pi.width;
								ca.height = pi.height;
								var ct = ca.getContext("2d");
								ct.drawImage(pi, 0, 0);
								var dl = ca.toDataURL("image/png");
								self.@org.geogebra.web.web.gui.toolbar.ModeToggleMenu::onPaste(Ljava/lang/String;)(dl);
							}
							pi.src = sr;

							// do not paste more images at once!
							break;
						}
					}
				}
				return false;
			};

			// this seems to run in Chrome!
			wind.addEventListener("paste", wind.onpastehandleradded);
		}
	}-*/;

	public void onPaste(String str) {
		app.getGgbApi().insertImage(str);
	}

	private void buildGui(){
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
					subLi.addDomHandler(this, MouseOverEvent.getType());
					subLi.addDomHandler(this, MouseOutEvent.getType());
					subLi.addDomHandler(this, KeyUpEvent.getType());
				}
			}
		}

		hideMenu();
	}

	protected ToolbarSubemuW createToolbarSubmenu(AppW app, int order) {
		return new ToolbarSubemuW(app, order);
	}

	public void setButtonTabIndex(int index){
		tbutton.getElement().setTabIndex(index);
	}

	public UnorderedList getItemList(){
		if (submenu != null) {
			return submenu.getItemList();
		}
		return null;
	}
	
	public void addDomHandlers(Widget w){
		w.addDomHandler(this, MouseDownEvent.getType());
		w.addDomHandler(this, MouseUpEvent.getType());
		w.addDomHandler(this, TouchStartEvent.getType());
		w.addDomHandler(this, TouchEndEvent.getType());
	}
	
	/**
	 * Sets the menu visible if it exists
	 */
	public void showMenu() {
		if(this.submenu == null){
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
	 * @param visible if true sets the menu visible, otherwise it hides it
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
	
	public boolean selectMode(int mode, ModeSetter m) {
		String modeText = mode + "";

		//If there is only one menuitem, there is no submenu -> set the button selected, if the mode is the same.
		if (menu.size() == 1 ){
			if (menu.get(0) == mode) {
				

				showToolTipBottom(app.getGuiManager().getTooltipURL(mode), m);
				this.setCssToSelected();
				toolbar.update(); //TODO! needed to regenerate the toolbar, if we want to see the border.
								//remove, if it will be updated without this.
				return true;
			}
			return false;
		}
		
		if(this.getItemList() == null){
			if (menu.get(0) == mode){
				
				this.setCssToSelected();
				toolbar.update(); //TODO! needed to regenerate the toolbar, if we want to see the border.
								//remove, if it will be updated without this.
				return true;
			}
			for(Integer i: this.menu){
				if(i == mode){
					return true;
				}
			}
			return false;
		}
		
		for (int i = 0; i < this.getItemList().getWidgetCount(); i++) {
			Widget mi = this.getItemList().getWidget(i);
			// found item for mode?
			if (mi.getElement().getAttribute("mode").equals(modeText)) {
				selectItem(mi);
				
				showToolTipBottom(app.getGuiManager().getTooltipURL(mode), m);
				return true;
			}
		}
//		tbutton.getElement().setAttribute("isSelected", "false");
		return false;
	}

	


	public int getFirstMode() {
		if (menu.size() == 0){
			return -1;
		}
		
		int firstmode = menu.get(0);
		return firstmode;
	}
	
	void selectItem(Widget mi) {
		
		final String miMode = mi.getElement().getAttribute("mode");
		App.debug(miMode);
		// check if the menu item is already selected
		if (tbutton.getElement().getAttribute("isSelected").equals(true)
				&& tbutton.getElement().getAttribute("mode").equals(miMode)) {
			return;
		}
		
		tbutton.getElement().setAttribute("mode",miMode);
		tbutton.clear();
		Image buttonImage = new NoDragImage(((GGWToolBar)app.getToolbar()).getImageURL(Integer.parseInt(miMode)),32);
		buttonImage.addStyleName("toolbar_icon");
		tbutton.add(buttonImage);
		
//		tbutton.getElement().setInnerHTML(new Image(((GGWToolBar)app.getToolbar()).getImageURL(Integer.parseInt(miMode)))+"");
		toolbar.update();
		setCssToSelected();

		//toolbar.update(); //TODO remove later
		//tbutton.setToolTipText(app.getToolTooltipHTML(Integer.parseInt(miMode)));
		setToolTipText(app.getToolTooltipHTML(Integer.parseInt(miMode)));
	}
	
	
	private void setCssToSelected(){
		ArrayList<ModeToggleMenu> modeToggleMenus = toolbar.getModeToggleMenus();
		for (int i = 0; i < modeToggleMenus.size(); i++) {
			ModeToggleMenu mtm = modeToggleMenus.get(i);
			if (mtm != this) {
				mtm.getToolbarButtonPanel().getElement().getStyle().setBorderWidth(1, Unit.PX);
				mtm.getToolbarButtonPanel().getElement().setAttribute("isSelected","false");
			}
		}
		//Set border width explicitly to make sure browser actually does that 
		// (otherwise the thicker border applies on next browser event)
		getToolbarButtonPanel().getElement().setAttribute("isSelected","true");
		getToolbarButtonPanel().getElement().getStyle().setBorderWidth(2, Unit.PX);
	}
	
	public void addSeparator(){
		//TODO
	}

	public void onEnd(DomEvent<?> event) {
		int mode = Integer.parseInt(event.getRelativeElement().getAttribute(
				"mode"));
		if (mode < 999 || mode > 2000) {
			app.hideKeyboard();
		}
		tbutton.getElement().focus();
		event.stopPropagation();
		if (event.getSource() == tbutton) { // if click ended on the button
			// if enter was pressed
			if ((event instanceof KeyUpEvent) && ((KeyUpEvent)event).getNativeKeyCode() == KeyCodes.KEY_ENTER){
				setMenuVisibility(!isMenuShown());
			}
			// if submenu was open
			if (wasMenuShownOnMouseDown && !(event instanceof TouchEndEvent && app.getLAF().isSmart())) {
				hideMenu();
			}
		} else { // click ended on menu item
			hideMenu();
			event.stopPropagation();
		}

		ToolTipManagerW.sharedInstance().setBlockToolTip(false);
		//if we click the toolbar button, only interpret it as real click if there is only one tool in this menu
		app.setMode(
				mode,
				event.getSource() == tbutton && menu.size() > 1 ? ModeSetter.DOCK_PANEL : ModeSetter.TOOLBAR);
		ToolTipManagerW.sharedInstance().setBlockToolTip(true);

		tbutton.getElement().focus();
	}

	@Override
    public void onTouchStart(TouchStartEvent event) {
	    if (event.getSource() == tbutton){
	    	onStart(event);
	    	CancelEventTimer.touchEventOccured();
	    } else { // clicked on a submenu list item
	    	event.stopPropagation(); // the submenu doesn't close as a popup, see GeoGebraAppFrame init()
	    }
	    event.preventDefault();
    }

	@Override
    public void onTouchEnd(TouchEndEvent event) {
		onEnd(event);
		CancelEventTimer.touchEventOccured();
    }

	@Override
    public void onMouseUp(MouseUpEvent event) {
		if(CancelEventTimer.cancelMouseEvent()){
			return;
		}
		onEnd(event);
		if (event.getSource() == tbutton) {
			if (this.app.getActiveEuclidianView() != null
					&& this.app.getActiveEuclidianView()
							.getEuclidianController() != null) {
				((IsEuclidianController) this.app.getActiveEuclidianView()
						.getEuclidianController())
			        .setActualSticky(
			                event.getNativeButton() == NativeEvent.BUTTON_RIGHT);
			}
		}
	}

	@Override
    public void onMouseDown(MouseDownEvent event) {
		if (event.getSource() == tbutton
		        && !CancelEventTimer.cancelMouseEvent()) {
	    	onStart(event);
	    } else { // clicked on a submenu list item
	    	event.stopPropagation(); // the submenu doesn't close as a popup, see GeoGebraAppFrame init()
	    }
	    event.preventDefault();
    }
	
	/**
	 * Handles the touchstart and mousedown events on main tools.
	 * @param event
	 */
	public void onStart(DomEvent<?> event){
		event.preventDefault();
		event.stopPropagation();
		this.setFocus(true);
		if (isMenuShown()) {
			wasMenuShownOnMouseDown = true;
		} else {
			toolbar.closeAllSubmenu();
			wasMenuShownOnMouseDown = false;
			showMenu();
		}
	}
	
	public void setToolTipText(String string) {
		toolTipText = string;
    }

	public void showToolTipBottom(String helpURL, ModeSetter m) {
		if (m != ModeSetter.CAS_VIEW
 && app.showToolBarHelp()) {
			ToolTipManagerW.sharedInstance().showBottomInfoToolTip(toolTipText,
					helpURL, ToolTipLinkType.Help, app);
		}
	}

	/*
	public void hideToolTip(){
		ToolTipManagerW.sharedInstance().hideToolTip();
	}*/
	
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
		if(event.getSource() == tbutton){
			return;
		}
		//submenu's menuitem won't be highlighted
		setHovered(event.getRelativeElement(), false);
    }

	private void setHovered(Element el, boolean hovered){
		if (hovered){
			el.addClassName("hovered");
		} else {
			el.removeClassName("hovered");
		}
	}

	public void onKeyUp(KeyUpEvent event) {
		int keyCode = event.getNativeKeyCode();
	
		switch (keyCode){
		case KeyCodes.KEY_ENTER:
			onEnd(event);
			break;
		case KeyCodes.KEY_RIGHT:
		case KeyCodes.KEY_LEFT:
			int indexOfButton = toolbar.getModeToggleMenus().indexOf(this);
			if (keyCode == KeyCodes.KEY_RIGHT){
				indexOfButton++;
			} else {
				indexOfButton--;
			}				
			
			if (indexOfButton >= 0 && indexOfButton < toolbar.getModeToggleMenus().size()){
				selectMenu(indexOfButton);
			}else{
				toolbar.selectMenuBotton(indexOfButton < 0 ? -1 : 0);
			}
			break;
		case KeyCodes.KEY_DOWN:
			if (event.getSource() == tbutton){
				if(isMenuShown()){
					this.getItemList().getWidget(0).getElement().focus();
				} else {
					showMenu();
					this.getItemList().getWidget(0).getElement().focus();
				}
			} else {
				Element nextSiblingElement = event.getRelativeElement().getNextSiblingElement();
				if (nextSiblingElement != null){
					nextSiblingElement.focus();
				} else event.getRelativeElement().getParentElement().getFirstChildElement().focus();
			}
			break;
		case KeyCodes.KEY_UP:
			if (event.getSource() instanceof ListItem){
				Element previousSiblingElement = event.getRelativeElement().getPreviousSiblingElement();
				if (previousSiblingElement != null){
					previousSiblingElement.focus();
				} else {
					UnorderedList parentUL = (UnorderedList)((ListItem)(event.getSource())).getParent(); 
					parentUL.getWidget(parentUL.getWidgetCount()-1).getElement().focus();
				}
				
			}
			break;
		}
    }

	private void selectMenu(int index){
		ModeToggleMenu mtm2 = toolbar.getModeToggleMenus().get(index);
	
		mtm2.tbutton.getElement().focus();
		if(isMenuShown()){
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

	public void addModes(Vector<Integer> menu2) {
		if(this.submenu == null){
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
					subLi.addDomHandler(this, MouseOverEvent.getType());
					subLi.addDomHandler(this, MouseOutEvent.getType());
					subLi.addDomHandler(this, KeyUpEvent.getType());
				} else {
					App.debug("BADMODE: " + addMode);
				}
			}
		}
	    
    }

	public void setMaxHeight(double d) {
	    if(submenu != null){
	    	this.submenu.setMaxHeight((int)d);
	    }
	    
    }
}
