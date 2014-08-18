package geogebra.web.gui.toolbar;

import geogebra.common.main.App;
import geogebra.html5.css.GuiResources;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.gui.util.CancelEventTimer;
import geogebra.html5.gui.util.ListItem;
import geogebra.html5.gui.util.UnorderedList;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.main.AppW;

import java.util.ArrayList;
import java.util.Vector;

import com.google.gwt.dom.client.Element;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;


public class ModeToggleMenu extends ListItem implements MouseDownHandler, MouseUpHandler, 
TouchStartHandler, TouchEndHandler, MouseOutHandler, MouseOverHandler, KeyUpHandler{

	private static final long serialVersionUID = 1L;

	FlowPanel tbutton;
	FlowPanel submenu;
	FlowPanel submenuArrow;
	UnorderedList itemList;

	private AppW app;

	private ToolBarW toolbar;

	private final Vector<Integer> menu;
	
	private String toolTipText;
	
	private boolean wasMenuShownOnMouseDown;

	public ModeToggleMenu(AppW appl, Vector<Integer> menu1, ToolBarW tb) {
		super();
		this.app = appl;
		this.toolbar = tb;
		this.menu = menu1;
		this.addStyleName("toolbar_item");
		buildButton();
		
		
	}
	
	private void buildButton() {
		tbutton = new FlowPanel();
		tbutton.addStyleName("toolbar_button");
		Image toolbarImg = new Image(((GGWToolBar)app.getToolbar()).getImageURL(menu.get(0).intValue()));
		toolbarImg.addStyleName("toolbar_icon");
		tbutton.add(toolbarImg);
		tbutton.getElement().setAttribute("mode",menu.get(0).intValue()+"");	
		addDomHandlers(tbutton);
		tbutton.addDomHandler(this, MouseOutEvent.getType());
		tbutton.addDomHandler(this, KeyUpEvent.getType());
		tbutton.addDomHandler(this, MouseOverEvent.getType());
		this.add(tbutton);
		addNativeToolTipHandler(tbutton.getElement(), this);
		setToolTipText(app.getToolTooltipHTML(menu.get(0).intValue()));
    }

	private void buildGui(){
		
		
		//Adding submenus if needed.
		if (menu.size()>1){
			
			submenu = new FlowPanel();
			this.add(submenu);
			submenu.setStyleName("toolbar_submenu");
			
			submenuArrow = new FlowPanel();
			Image arrow = new Image(GuiResources.INSTANCE.arrow_submenu_up());
			submenuArrow.add(arrow);
			submenuArrow.setStyleName("submenuArrow");
			submenu.add(submenuArrow);
			
			itemList = new UnorderedList();
			itemList.setStyleName("submenuContent");
			//addNativeTouchHandlers(this, toolbar.getElement());
		
			for (int k = 0; k < menu.size(); k++) {
				final int addMode = menu.get(k).intValue();
				if (addMode < 0) {	//TODO
	//				// separator within menu:
	//				tm.addSeparator();
				} else { // standard case: add mode
					// check mode
					if (!"".equals(app.getToolName(addMode))) {
						ListItem subLi = new ListItem();
						Image modeImage = new Image(((GGWToolBar)app.getToolbar()).getImageURL(addMode));
						//modeImage.getElement().setId("img_"+addMode);
						Label lb = new Label(app.getToolName(addMode));
						subLi.add(modeImage);
						subLi.add(lb);
						//subLi.getElement().setId(addMode+"");
						subLi.getElement().setAttribute("mode", addMode+"");
						addDomHandlers(subLi);
						subLi.addDomHandler(this, MouseOverEvent.getType());
						subLi.addDomHandler(this, MouseOutEvent.getType());
						subLi.addDomHandler(this, KeyUpEvent.getType());
						itemList.add(subLi);
					}
				}
			}
			this.submenu.add(itemList);
		
			hideMenu();
		}
	}
	
	public void setButtonTabIndex(int index){
		tbutton.getElement().setTabIndex(index);
	}

	public UnorderedList getItemList(){
		return itemList;
	}
	
	public void addDomHandlers(Widget w){
		w.addDomHandler(this, MouseDownEvent.getType());
		w.addDomHandler(this, MouseUpEvent.getType());
		w.addDomHandler(this, TouchStartEvent.getType());
		if(!app.getLAF().isSmart()){//TODO may need android detection etc.
			w.addDomHandler(this, TouchEndEvent.getType());
		}
	}
	
	/**
	 * Sets the menu visible if it exists
	 */
	public void showMenu() {
		if(this.submenu == null){
			this.buildGui();
		}
		if (submenu != null) {
			submenu.addStyleName("visible");
		}
	}

	/**
	 * Hides the menu if it exists
	 */
	public void hideMenu() {
		if (submenu != null) {
			submenu.removeStyleName("visible");
		}
	}
	
	/**
	 * @param visible if true sets the menu visible, otherwise it hides it
	 */
	public void setMenuVisibility(boolean visible) {
		if (submenu != null) {
			if (visible) {
				submenu.addStyleName("visible");
			} else {
				submenu.removeStyleName("visible");
			}
		}
	}
	
	public boolean selectMode(int mode) {
		String modeText = mode + "";

		//If there is only one menuitem, there is no submenu -> set the button selected, if the mode is the same.
		if (menu.size() == 1 ){
			if (menu.get(0) == mode){
				
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
		Image buttonImage = new Image(((GGWToolBar)app.getToolbar()).getImageURL(Integer.parseInt(miMode)));
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
				mtm.tbutton.getElement().getStyle().setBorderWidth(1, Unit.PX);
				mtm.tbutton.getElement().setAttribute("isSelected","false");
			}
		}
		//Set border width explicitly to make sure browser actually does that 
		// (otherwise the thicker border applies on next browser event)
		tbutton.getElement().setAttribute("isSelected","true");
		tbutton.getElement().getStyle().setBorderWidth(2, Unit.PX);
	}
	
	public void addSeparator(){
		//TODO
	}

	@Override
    public void onTouchEnd(TouchEndEvent event) {
		onEnd(event);
		CancelEventTimer.touchEventOccured();
    }
	
	public void onEnd(DomEvent<?> event) {
		tbutton.getElement().focus();
		if (event.getSource() == tbutton) { // if click ended on the button
			// if enter was pressed
			if ((event instanceof KeyUpEvent) && ((KeyUpEvent)event).getNativeKeyCode() == KeyCodes.KEY_ENTER){
				setMenuVisibility(!isMenuShown());
			}
			// if submenu was open
			if (wasMenuShownOnMouseDown) {
				hideMenu();
			}
		} else { // click ended on menu item
			hideMenu();
		}
		app.setMode(Integer.parseInt(event.getRelativeElement().getAttribute("mode")));
		tbutton.getElement().focus();
	}

	@Override
    public void onTouchStart(TouchStartEvent event) {
	    if (event.getSource() == tbutton){
	    	onStart(event);
	    	CancelEventTimer.touchEventOccured();
	    }
	    
    }

	@Override
    public void onMouseUp(MouseUpEvent event) {
		if(CancelEventTimer.cancelMouseEvent()){
			return;
		}
		onEnd(event);
    }

	@Override
    public void onMouseDown(MouseDownEvent event) {
	    if (event.getSource() == tbutton && !CancelEventTimer.cancelMouseEvent()){
	    	onStart(event);
	    }    
    }
	
	/**
	 * Handles the touchstart and mousedown events on main tools.
	 * @param event
	 */
	public void onStart(DomEvent event){
		event.preventDefault();
		// stop the propagation so that :ToolBarW.OutsideClickHandler
		// does not processes the event
		event.stopPropagation();
		this.setFocus(true);
		if (isMenuShown()) {
			wasMenuShownOnMouseDown = true;
			if (event.getSource() != tbutton) {
				hideMenu();
			}
		} else {
			wasMenuShownOnMouseDown = false;
			showMenu();
		}
	}
	
	public void setToolTipText(String string) {
		toolTipText = string;
    }
	
	public void showToolTip(){
		
		if (toolbar.hasPopupOpen()) return;
		
		ToolTipManagerW.sharedInstance().setEnableDelay(false);
		ToolTipManagerW.sharedInstance().showToolTip(this.getElement(), toolTipText);
		ToolTipManagerW.sharedInstance().setEnableDelay(true);
	}
	
	public void hideToolTip(){
		ToolTipManagerW.sharedInstance().hideToolTip();
	}
	
	/**
	 * @return true if the menu is open
	 */
	public boolean isMenuShown() {
		if (submenu != null) {
			return submenu.getElement().hasClassName("visible");
		}
		return false;
	}
	
	private native void addNativeToolTipHandler(Element element, ModeToggleMenu mtm) /*-{
		element.addEventListener("mouseout",function() {
			mtm.@geogebra.web.gui.toolbar.ModeToggleMenu::hideToolTip()();
		});
		element.addEventListener("mouseover",function() {
			mtm.@geogebra.web.gui.toolbar.ModeToggleMenu::showToolTip()();
		});
	}-*/;

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
			if (event.getSource() == tbutton) hideToolTip();
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
					this.itemList.getWidget(0).getElement().focus();
				} else {
					hideToolTip();
					showMenu();
					this.itemList.getWidget(0).getElement().focus();
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
}
