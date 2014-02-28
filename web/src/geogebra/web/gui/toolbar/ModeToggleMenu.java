package geogebra.web.gui.toolbar;

import geogebra.common.main.App;
import geogebra.html5.gui.util.ListItem;
import geogebra.html5.gui.util.UnorderedList;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.main.AppW;

import java.util.ArrayList;
import java.util.Vector;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.GestureEndEvent;
import com.google.gwt.event.dom.client.GestureEndHandler;
import com.google.gwt.event.dom.client.LoseCaptureEvent;
import com.google.gwt.event.dom.client.LoseCaptureHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
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
TouchStartHandler, TouchEndHandler, GestureEndHandler, LoseCaptureHandler{

	private static final long serialVersionUID = 1L;

	FlowPanel tbutton;
	FlowPanel submenu;
	FlowPanel submenuArrow;
	UnorderedList itemList;

	private AppW app;

	private ToolBarW toolbar;

	boolean keepDown;

	private Vector<Integer> menu;
	
	
//	public ModeToggleMenu(AppW app, ToolBarW toolbar,
//			ModeToggleButtonGroup bg) {
//		
//	}

	public ModeToggleMenu(AppW appl, Vector<Integer> menu1, ToolBarW handler) {
		super();
		this.app = appl;
		this.toolbar = handler;
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
		this.add(tbutton);
		
		submenu = new FlowPanel();
		submenu.setStyleName("toolbar_submenu");
		
		submenuArrow = new FlowPanel();
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

		this.add(submenu);	
	}

//	public Image getButton(){
//		return tbutton;
//	}

	public FlowPanel getItemList(){
		//Changes by Steffi: return FlowPanel submenu instead of list
		return submenu;
	}

	
	public void addDomHandlers(Widget w){
//		this.addDomHandler(this, MouseMoveEvent.getType());
//		this.addDomHandler(this, MouseOverEvent.getType());
//		this.addDomHandler(this, MouseOutEvent.getType());
		App.debug("addDomHandlers - " + w.toString());
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
	
	public void onTouchLeave(JavaScriptObject e){
		Window.alert("TOUCHLEAVE");
	}

	public void onTouchCancel(Element element){
		Window.alert("TOUCHcancel");
	}

	public void onTouchEnter(Element element){
		Window.alert("TOUCHenter");
	}
	
	public void onTouchStart(JavaScriptObject e){
		App.debug("start" + e.toSource());
	}
	
	public void onTouchEnd(Element element){
		App.debug("end");
		App.debug(element.toString());
		app.setMode(Integer.parseInt(element.getAttribute("mode")));
		itemList.getElement().getStyle().setProperty("visibility", "hidden");
	}

	public void onClick(Element element){
		App.debug("click");
	}
	
	public void openMenu(){
		itemList.getElement().getStyle().setProperty("visibility", "visible");
	}
	
	public boolean selectMode(int mode) {
		App.debug("select mode --- " +mode);
		String modeText = mode + "";

		for (int i = 0; i < this.getItemList().getWidgetCount(); i++) {
			Widget mi = this.getItemList().getWidget(i);
			// found item for mode?
			if (mi.getElement().getAttribute("mode").equals(modeText)) {
				selectItem(mi);
				return true;
			}
		}
		tbutton.getElement().setAttribute("isSelected", "false");
		App.debug("tbutton selected <-false: " + tbutton.toString());
		return false;
	}

	
	public int getFirstMode() {
		if (itemList.getWidgetCount() == 0){
			return -1;
		}
		
		int firstmode = Integer.parseInt(itemList.getWidget(0).getElement().getAttribute("mode"));
		App.debug("firstmode: " + firstmode);
		return firstmode;
	}
	
	void selectItem(Widget mi) {
		
		App.debug("selectItem!!!");
		
		final String miMode = mi.getElement().getAttribute("mode");
		// check if the menu item is already selected
		if (tbutton.getElement().getAttribute("isSelected").equals(true)
				&& tbutton.getElement().getAttribute("mode").equals(miMode)) {
			return;
		}
		
		App.debug("tbutton new mode: " + miMode);
		tbutton.getElement().setAttribute("mode",miMode);
		tbutton.clear();
		Image buttonImage = new Image(((GGWToolBar)app.getToolbar()).getImageURL(Integer.parseInt(miMode)));
		buttonImage.addStyleName("toolbar_icon");
		tbutton.add(buttonImage);
//		tbutton.getElement().setInnerHTML(new Image(((GGWToolBar)app.getToolbar()).getImageURL(Integer.parseInt(miMode)))+"");
		toolbar.update();
		
		ArrayList<ModeToggleMenu> modeToggleMenus = toolbar.getModeToggleMenus();
		for (int i = 0; i < modeToggleMenus.size(); i++) {
			ModeToggleMenu mtm = modeToggleMenus.get(i);
			if (mtm != this) {
				App.debug("tbutton selected <-false: " + tbutton.toString());
				mtm.tbutton.getElement().setAttribute("isSelected","false");
			}
		}
		tbutton.getElement().setAttribute("isSelected","true");
		App.debug("tbutton selected <-true: " + tbutton.toString());
		//toolbar.update(); //TODO remove later
		//tbutton.setToolTipText(app.getToolTooltipHTML(Integer.parseInt(miMode)));
		setToolTipText(app.getToolTooltipHTML(Integer.parseInt(miMode)));
	}
	
	public void setToolTipText(String string){
		App.debug("TODO setTooltiptext");
	}
	
	public void addSeparator(){
		//TODO
	}
	
	public void addItem(){
		
	}

	@Override
    public void onTouchEnd(TouchEndEvent event) {
		onEnd(event);
    }
	
	public void onEnd(DomEvent event){
		keepDown = false;
		if (event.getSource() == tbutton && "true".equals(event.getRelativeElement().getAttribute("isSelected"))){
			itemList.getElement().getStyle().setProperty("visibility", "visible");
			return;
		}
		app.setMode(Integer.parseInt(event.getRelativeElement().getAttribute("mode")));
		itemList.getElement().getStyle().setProperty("visibility", "hidden");
		
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
		final Element element = event.getRelativeElement();
		keepDown = true;
		toolbar.closeAllSubmenu();
		
		Timer longPressTimer = new Timer(){
			@Override
            public void run() {
				App.debug("keepdown: " + keepDown);
				if (keepDown){
					element.getNextSiblingElement().getStyle().setProperty("visibility","visible");
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
	
}
