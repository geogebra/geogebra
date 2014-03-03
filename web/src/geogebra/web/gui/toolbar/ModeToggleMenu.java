package geogebra.web.gui.toolbar;

import geogebra.html5.css.GuiResources;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.gui.util.ListItem;
import geogebra.html5.gui.util.UnorderedList;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.main.AppW;

import java.util.ArrayList;
import java.util.Vector;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.GestureEndEvent;
import com.google.gwt.event.dom.client.GestureEndHandler;
import com.google.gwt.event.dom.client.LoseCaptureEvent;
import com.google.gwt.event.dom.client.LoseCaptureHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;


public class ModeToggleMenu extends ListItem implements MouseDownHandler, MouseUpHandler, 
TouchStartHandler, TouchEndHandler, GestureEndHandler, LoseCaptureHandler,
MouseOutHandler{

	private static final long serialVersionUID = 1L;

	FlowPanel tbutton;
	FlowPanel submenu;
	FlowPanel submenuArrow;
	UnorderedList itemList;

	private AppW app;

	private ToolBarW toolbar;

	boolean keepDown;

	private Vector<Integer> menu;

	private String toolTipText;

	public ModeToggleMenu(AppW appl, Vector<Integer> menu1, ToolBarW tb) {
		super();
		this.app = appl;
		this.toolbar = tb;
		this.menu = menu1;
		this.addStyleName("toolbar_item");
		
		buildGui();
		
	}
	
	public void buildGui(){
		tbutton = new FlowPanel();
		tbutton.addStyleName("toolbar_button");
		Image toolbarImg = new Image(((GGWToolBar)app.getToolbar()).getImageURL(menu.get(0).intValue()));
		toolbarImg.addStyleName("toolbar_icon");
		tbutton.add(toolbarImg);
		tbutton.getElement().setAttribute("mode",menu.get(0).intValue()+"");	
		addDomHandlers(tbutton);
		tbutton.addDomHandler(this, MouseOutEvent.getType());
		this.add(tbutton);
		addNativeToolTipHandler(tbutton.getElement(), this);
		setToolTipText(app.getToolTooltipHTML(menu.get(0).intValue()));
		
		//Adding submenus if needed.
		if (menu.size()>1){
			
			FlowPanel furtherToolsArrowPanel = new FlowPanel();
			furtherToolsArrowPanel.add(new Image(GuiResources.INSTANCE.toolbar_further_tools()));
			furtherToolsArrowPanel.setStyleName("furtherToolsTriangle");
			tbutton.add(furtherToolsArrowPanel);
			
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
						itemList.add(subLi);
					}
				}
			}
			this.submenu.add(itemList);
		
		
			hideMenu();
		}
	}

	public UnorderedList getItemList(){
		return itemList;
	}
	
	public void addDomHandlers(Widget w){
//		this.addDomHandler(this, MouseMoveEvent.getType());
//		this.addDomHandler(this, MouseOverEvent.getType());
//		this.addDomHandler(this, MouseOutEvent.getType());
		w.addDomHandler(this, MouseDownEvent.getType());
		w.addDomHandler(this, MouseUpEvent.getType());
		w.addDomHandler(this, TouchStartEvent.getType());
		w.addDomHandler(this, TouchEndEvent.getType());
//		this.addDomHandler(this, TouchMoveEvent.getType());
		w.addDomHandler(this, GestureEndEvent.getType());
		w.addDomHandler(this, LoseCaptureEvent.getType());
	}
	
//	private native void addNativeHandlers(Element element, ModeToggleMenu m)/*-{
//		element.addEventListener("touchstart",function(e) {
//			e.preventDefault();
//			m.@geogebra.web.gui.toolbar.ModeToggleMenu::onTouchStart(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
//		});
//		element.addEventListener("touchend",function() {
//			m.@geogebra.web.gui.toolbar.ModeToggleMenu::onTouchEnd(Lcom/google/gwt/dom/client/Element;)(element);
//		});
//		element.addEventListener("mousedown",function(e) {
//			m.@geogebra.web.gui.toolbar.ModeToggleMenu::onTouchStart(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
//		});
//		element.addEventListener("mouseup",function() {
//			m.@geogebra.web.gui.toolbar.ModeToggleMenu::onTouchEnd(Lcom/google/gwt/dom/client/Element;)(element);
//		});
//		element.addEventListener("click",function() {
//			m.@geogebra.web.gui.toolbar.ModeToggleMenu::onClick(Lcom/google/gwt/dom/client/Element;)(element);
//		});
//		element.addEventListener("touchleave",function(e) {
//			m.@geogebra.web.gui.toolbar.ModeToggleMenu::onTouchLeave(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
//		});
//		
//		element.addEventListener("touchcancel",function() {
//			m.@geogebra.web.gui.toolbar.ModeToggleMenu::onTouchLeave(Lcom/google/gwt/core/client/JavaScriptObject;)(e)
//		});
//		element.addEventListener("touchenter",function() {
//			m.@geogebra.web.gui.toolbar.ModeToggleMenu::onTouchLeave(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
//		});		
//	}-*/;
	
//	public void onTouchLeave(JavaScriptObject e){
//		Window.alert("TOUCHLEAVE");
//	}
//
//	public void onTouchCancel(Element element){
//		Window.alert("TOUCHcancel");
//	}
//
//	public void onTouchEnter(Element element){
//		Window.alert("TOUCHenter");
//	}
//	
//	public void onTouchStart(JavaScriptObject e){
//		App.debug("start" + e.toSource());
//	}
//	
//	public void onTouchEnd(Element element){
//		App.debug("end");
//		App.debug(element.toString());
//		app.setMode(Integer.parseInt(element.getAttribute("mode")));
//		itemList.getElement().getStyle().setProperty("visibility", "hidden");
//	}
//
//	public void onClick(Element element){
//		App.debug("click");
//	}
	
	public void showMenu(){
		ArrayList<ModeToggleMenu> modeToggleMenus = toolbar.getModeToggleMenus();
		for(int i=0; i< modeToggleMenus.size(); i++){
			if (modeToggleMenus.get(i).submenu != submenu){
				modeToggleMenus.get(i).hideMenu();
			} else if (submenu != null){
				submenu.addStyleName("visible");
			}
		}
	}
	
	public void hideMenu(){
		if (submenu == null) return;
		submenu.removeStyleName("visible");;
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
		
		FlowPanel furtherToolsArrowPanel = new FlowPanel();
		furtherToolsArrowPanel.add(new Image(GuiResources.INSTANCE.toolbar_further_tools()));
		furtherToolsArrowPanel.setStyleName("furtherToolsTriangle");
		tbutton.add(furtherToolsArrowPanel);
		
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
				mtm.tbutton.getElement().setAttribute("isSelected","false");
			}
		}
		tbutton.getElement().setAttribute("isSelected","true");
	}
	
	public void addSeparator(){
		//TODO
	}

	@Override
    public void onTouchEnd(TouchEndEvent event) {
		onEnd(event);
    }
	
	/**
	 * Check if the bottom half of the button has clicked.
	 */
	private boolean isBottomHalfClicked(DomEvent event){
		return (event.getNativeEvent().getClientY() - tbutton.getAbsoluteTop() > tbutton.getOffsetHeight() / 2);
	}
	
	public void onEnd(DomEvent event){
		if (event.getSource() == tbutton){
			if("true".equals(event.getRelativeElement().getAttribute("isSelected"))
					|| isBottomHalfClicked(event)){
				showMenu();
				keepDown = false;
				return;
			}
			
			// At one click close the other buttons' submenu, but don't close
			// own submenu, otherwise there would be problems, if the submenu opened
			// because of a long-press
			if (!isSubmenuOpen()) toolbar.closeAllSubmenu();
		}
		app.setMode(Integer.parseInt(event.getRelativeElement().getAttribute("mode")));
		if(event.getSource() != tbutton || keepDown) hideMenu();
		keepDown = false;
		
	}

	@Override
    public void onTouchStart(TouchStartEvent event) {
	    if (event.getSource() == tbutton){
	    	onStart(event);
	    }
	    
    }

	@Override
    public void onMouseUp(MouseUpEvent event) {
		onEnd(event);
    }

	@Override
    public void onMouseDown(MouseDownEvent event) {
	    if (event.getSource() == tbutton){
	    	onStart(event);
	    }    
    }
	
	/**
	 * Handles the touchstart and mousedown events on main tools.
	 * @param event
	 */
	public void onStart(DomEvent event){	
		event.preventDefault();
		final ModeToggleMenu tm = this;
		keepDown = true;
		//toolbar.closeAllSubmenu();
		
		Timer longPressTimer = new Timer(){
			@Override
            public void run() {
				if (keepDown){
					tm.showMenu();
					keepDown = false;
				}
            }
		};
		longPressTimer.schedule(2000); 		
	}

	@Override
    public void onGestureEnd(GestureEndEvent event) {
	    Window.alert("gesture end");
	    
    }

	@Override
    public void onLoseCapture(LoseCaptureEvent event) {
	    Window.alert("losecapturehandler");
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
	
	public boolean isSubmenuOpen(){
		if (submenu==null) return false;
		return (submenu.getElement().hasClassName("visible"));
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
    public void onMouseOut(MouseOutEvent event) {
		// Avoid opening submenu, if a user presses a button for a while,
		// then move on an another button without mouseup. 
		keepDown=false;
    }
}
