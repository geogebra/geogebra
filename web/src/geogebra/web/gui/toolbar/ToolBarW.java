package geogebra.web.gui.toolbar;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.gui.layout.DockPanel;
import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.gui.toolbar.ToolbarItem;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.main.App;
import geogebra.html5.gui.util.UnorderedList;
import geogebra.web.main.AppW;

import java.util.ArrayList;
import java.util.Vector;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;



/**
 * @author gabor
 * 
 * Toolbar for GeoGebraWeb
 *
 */
public class ToolBarW extends FlowPanel implements MouseDownHandler, MouseUpHandler, 
MouseMoveHandler, MouseOutHandler, MouseOverHandler, TouchStartHandler, TouchEndHandler, 
TouchMoveHandler{
	
	private AppW app;
	private int mode;

	/**
	 * Dock panel associated to this toolbar or null if this is the general
	 * toolbar. Just a single toolbar might have no dock panel, otherwise the
	 * ToolbarContainer logic will not work properly.
	 */
	private DockPanel dockPanel;

	private ArrayList<ModeToggleMenu> modeToggleMenus = new ArrayList<ModeToggleMenu>();
	boolean keepDown;
	private UnorderedList menuList;

	/**
	 * Creates general toolbar.
	 * There is no app parameter here, because of UiBinder.
	 * After instantiate the ToolBar, call init(Application app) as well.
	 */
	public ToolBarW() {
		App.debug("ToolBarW");
		//this.setHeight("55px");  //toolbar's height
		this.addStyleName("GGWToolbar");
		this.getElement().setId("GGWToolBar");
	}

	/**
	 * Creates toolbar for a specific dock panel. Call buildGui() to actually
	 * create the GUI of this toolbar.
	 * 
	 * @param app application
	 * @param dockPanel dock panel
	 */
/*	public ToolBarW(AppW app, DockPanel dockPanel) {
		this();
		this.app = app;
		this.dockPanel = dockPanel;

		//setFloatable(false);
		//setBackground(getBackground());
	}
*/

	/**
	 * Initialization of the ToolBar object
	 * 
	 * @param app1 application
	 */
	public void init(AppW app1){
		App.debug("ToolBarW.init");
		this.app = app1;
	}

	/**
	 * @return The dock panel associated with this toolbar or null if this is
	 *         the general toolbar.
	 */
	public DockPanel getDockPanel() {
		App.debug("ToolBarW.getDockPanel");
		return dockPanel;
	}

	/**
	 * Creates a toolbar using the current strToolBarDefinition.
	 */
	public void buildGui() {
		App.debug("ToolBarW.buildGui");
		mode = -1;
	
		menuList = new UnorderedList();
		menuList.getElement().addClassName("toolbar_mainItem");
		addCustomModesToToolbar(menuList);
		
		this.clear();
		this.add(menuList);

		setMode(app.getMode());
		
	}
	
	//TODO: this function is just a temporary hack! Don't regenate the toolbar.
	public void update(){
		this.clear();
		UnorderedList newMenuList = new UnorderedList();
		int count = menuList.getWidgetCount();
		menuList.clear();
		for(int i=0; i<count; i++){
			menuList.add(modeToggleMenus.get(i));
		}
		this.add(menuList);
	}
	
//	public void addDomHandlers(Widget w){
////		this.addDomHandler(this, MouseMoveEvent.getType());
////		this.addDomHandler(this, MouseOverEvent.getType());
////		this.addDomHandler(this, MouseOutEvent.getType());
//		App.debug("addDomHandlers - " + w.toString());
//		w.addDomHandler(this, MouseDownEvent.getType());
//		w.addDomHandler(this, MouseUpEvent.getType());
//		w.addDomHandler(this, TouchStartEvent.getType());
//		w.addDomHandler(this, TouchEndEvent.getType());
////		this.addDomHandler(this, TouchMoveEvent.getType());
//	}
	
//	private native void addNativeHandlers(Element element, ToolBarW tb)/*-{
//		element.addEventListener("touchstart",function() {
//			tb.@geogebra.web.gui.toolbar.ToolBarW::onTouchStart(Lcom/google/gwt/dom/client/Element;)(element);
//		});
//		element.addEventListener("touchend",function() {
//			tb.@geogebra.web.gui.toolbar.ToolBarW::onTouchEnd(Lcom/google/gwt/dom/client/Element;)(element);
//		});
//		element.addEventListener("mousedown",function() {
//			tb.@geogebra.web.gui.toolbar.ToolBarW::onTouchStart(Lcom/google/gwt/dom/client/Element;)(element);
//		});
//		element.addEventListener("mouseup",function() {
//			tb.@geogebra.web.gui.toolbar.ToolBarW::onTouchEnd(Lcom/google/gwt/dom/client/Element;)(element);
//		});
//		element.addEventListener("click",function() {
//			tb.@geogebra.web.gui.toolbar.ToolBarW::onClick(Lcom/google/gwt/dom/client/Element;)(element);
//		});
//	}-*/;
//	
//	public void onTouchStart(final Element element){
//		App.debug("start" + element.getTagName());
//		keepDown  = true;
//		Timer longPressTimer = new Timer(){
//			@Override
//            public void run() {
//				App.debug("run");
//				App.debug(element.getElementsByTagName("ul").getItem(0)+"");
//				if (keepDown) element.getElementsByTagName("ul").getItem(0).getStyle().setProperty("visibility","visible");
//            }
//		};
//		longPressTimer.schedule(2000);
//	}
//
//	public void onTouchEnd(Element element){
//		App.debug("end");
//		keepDown = false;
//	}
//
//	public void onClick(Element element){
//		App.debug("click");
//	}
	
	public ArrayList<ModeToggleMenu> getModeToggleMenus(){
		return modeToggleMenus;
	}
	
	/**
	 * Sets toolbar mode. This will change the selected toolbar icon.
	 * @param newMode see EuclidianConstants for mode numbers
	 * 
	 * 
	 * @return actual mode number selected (might be different if it's not available)
	 */
	public int setMode(int newMode) {
		App.debug("ToolBarW.setMode: " +newMode);
		boolean success = false;
		int tmpMode = newMode;
		// there is no special icon/button for the selection listener mode, use
		// the
		// move mode button instead
		if (tmpMode == EuclidianConstants.MODE_SELECTION_LISTENER) {
			tmpMode = EuclidianConstants.MODE_MOVE;
		}

		if (modeToggleMenus != null) {
			for (int i = 0; i < modeToggleMenus.size(); i++) {
				ModeToggleMenu mtm = modeToggleMenus.get(i);
				if (mtm.selectMode(tmpMode)) {
					success = true;
					break;
				}
			}


			if (!success) {
					mode = setMode(getFirstMode());
				
			}
			
			this.mode = tmpMode;

		}

		return tmpMode;
	}

	/**
	 * @return currently selected mode
	 */
	public int getSelectedMode() {
		App.debug("ToolBarW.getSelectedMode");
		return mode;
	}
	
	/**
	 * @return first mode in this toolbar
	 */
	public int getFirstMode() {
		App.debug("ToolBarW.getFirstMode");
		if (modeToggleMenus == null || modeToggleMenus.size() == 0) {
			return -1;
		}
		ModeToggleMenu mtm = modeToggleMenus.get(0);
		return mtm.getFirstMode();
	}
	
	public UnorderedList getMenuList(){
		return menuList;
	}

	private Integer activeView = App.VIEW_EUCLIDIAN;
	/**
	 * Adds the given modes to a two-dimensional toolbar. The toolbar definition
	 * string looks like "0 , 1 2 | 3 4 5 || 7 8 9" where the int values are
	 * mode numbers, "," adds a separator within a menu, "|" starts a new menu
	 * and "||" adds a separator before starting a new menu.
	 * 
	 */
	//private void addCustomModesToToolbar(ModeToggleButtonGroup bg) {
	private void addCustomModesToToolbar(UnorderedList mainUl) {
		App.debug("ToolBarW.addCustomModesToToolbar");
		Vector<ToolbarItem> toolbarVec;
		
		try {
			if (dockPanel != null) {
				toolbarVec = ToolBar.parseToolbarString(dockPanel.getToolbarString());
			} else {
				toolbarVec = ToolBar.parseToolbarString(app.getGuiManager()
						.getToolbarDefinition());
			}
		} catch (Exception e) {
			if (dockPanel != null) {
				App.debug("invalid toolbar string: "
						+ dockPanel.getToolbarString());
			} else {
				App.debug("invalid toolbar string: "
						+ app.getGuiManager().getToolbarDefinition());
			}
			toolbarVec = ToolBar.parseToolbarString(getDefaultToolbarString());
		}
		
		// set toolbar
		for (int i = 0; i < toolbarVec.size(); i++) {
			ToolbarItem ob = toolbarVec.get(i);
			Vector<Integer> menu = ob.getMenu();
			
			ModeToggleMenu mtm = new ModeToggleMenu(app, menu, this);
			modeToggleMenus.add(mtm);
			//addDomHandlers(mtm);
			mainUl.add(mtm);
		}
    }

	/**
	 * @return The default definition of this toolbar with macros.
	 */
	public String getDefaultToolbarString() {
		App.debug("ToolBarW.getDefaultToolbarString");
		if (dockPanel != null) {
			return dockPanel.getDefaultToolbarString();
		}
		return ToolBarW.getAllTools(app);
	}

	

	/**
	 * @param app
	 * @return All tools as a toolbar definition string
	 */
	public static String getAllTools(AppW app) {
		App.debug("ToolBarW.getAllTools");
		StringBuilder sb = new StringBuilder();
	
		sb.append(geogebra.common.gui.toolbar.ToolBar.getAllToolsNoMacros(true, true));
	
		// macros
		Kernel kernel = app.getKernel();
		int macroNumber = kernel.getMacroNumber();
	
		// check if at least one macro is shown
		// to avoid strange GUI
		boolean at_least_one_shown = false;
		for (int i = 0; i < macroNumber; i++) {
			Macro macro = kernel.getMacro(i);
			if (macro.isShowInToolBar()) {
				at_least_one_shown = true;
				break;
			}
		}
	
		if (macroNumber > 0 && at_least_one_shown) {
			sb.append(" || ");
			for (int i = 0; i < macroNumber; i++) {
				Macro macro = kernel.getMacro(i);
				if (macro.isShowInToolBar()) {
					sb.append(i + EuclidianConstants.MACRO_MODE_ID_OFFSET);
					sb.append(" ");
				}
			}
		}
	
		return sb.toString();
	}

	public void setActiveView(Integer viewID) {
		App.debug("ToolBarW.setActiveView");
	    activeView = viewID;
    }

	public int getActiveView() {
		App.debug("ToolBarW.getActiveView");
		return activeView;
	}
	
	public boolean hasPopupOpen(){
		//return (getSelectedItem() != null);
		App.debug("ToolBarW.hasPopupOpen() changed, tooltip doesn't work because of this");
		return false;
	}

	@Override
    public void onMouseUp(MouseUpEvent event) {
		App.debug("onmouseup2");
		keepDown = false;
    }
	
	public void onEnd(){
		keepDown = false;
	}

	@Override
    public void onMouseDown(MouseDownEvent event) {
		App.debug("onmousedown");
	    onStart(event);
    }

	@Override
    public void onTouchEnd(TouchEndEvent event) {
		App.debug("keepdown<-false");
		keepDown = false;
	    
    }

	@Override
    public void onTouchStart(TouchStartEvent event) {
		onStart(event);
    }
	
	public void onStart(DomEvent event){
		//Window.alert("start!!!!");
		event.preventDefault();
		final Element element = event.getRelativeElement();
		keepDown = true;
		for(int i=0; i<modeToggleMenus.size(); i++){
			modeToggleMenus.get(i).getItemList().getElement().getStyle().setProperty("visibility","hidden");
		}
		
		Timer longPressTimer = new Timer(){
			@Override
            public void run() {
				App.debug("keepdown: " + keepDown);
				if (keepDown) element.getNextSiblingElement().getStyle().setProperty("visibility","visible");
            }
		};
		longPressTimer.schedule(2000); 		
	}

	@Override
    public void onMouseOver(MouseOverEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void onMouseOut(MouseOutEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void onMouseMove(MouseMoveEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void onTouchMove(TouchMoveEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	public void closeAllSubmenu() {
		for(int i=0; i<modeToggleMenus.size(); i++){
			modeToggleMenus.get(i).getItemList().getElement().getStyle().setProperty("visibility","hidden");
		}
    }
}
