package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.html5.css.GuiResources;
import geogebra.html5.gui.laf.GLookAndFeel;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.StackPanel;

/**
 * Sidebar menu for SMART
 * 
 * 
 */

public class MainMenu extends FlowPanel implements ResizeHandler {
	
	/**
	 * Appw app
	 */
	/*private MenuItem signIn;
	private SignedInMenuW signedIn;
	private MenuItem signedInMenu;*/
	
	private AppW app;
	
	private StackPanel menuPanel;
	private ViewMenuW viewMenu;
	private FileMenuW fileMenu;
	private HelpMenuW helpMenu;
	private OptionsMenuW optionsMenu;
	private EditMenuW editMenu;
	private PerspectivesMenuW perspectivesMenu;

	private GMenuBar[] menus;

	/**
	 * Constructs the menubar
	 * 
	 * @param app
	 *            application
	 */
	public MainMenu(AppW app) {
		this.addStyleName("menubarSMART");
		this.app = app;
		init();
	}

	private void init() {
		this.createFileMenu();
		this.createPerspectivesMenu();
		this.createEditMenu();
		this.createViewMenu();
		this.createOptionsMenu();
		this.createHelpMenu();
		this.menus = new GMenuBar[]{fileMenu,editMenu,perspectivesMenu,viewMenu, optionsMenu, helpMenu};
		for(int i=0; i<menus.length; i++){
			final int next = (i+1)%menus.length;
			final int previous = (i-1+menus.length)%menus.length;
			final int index = i;
		this.menus[i].addDomHandler(new KeyDownHandler(){
			
			@Override
            public void onKeyDown(KeyDownEvent event) {
				int keyCode = event.getNativeKeyCode();
				//First / last below are not intuitive -- note that default handler of
				//down skipped already from last to first
				if(keyCode == KeyCodes.KEY_DOWN){
					if(menus[index].isFirstItemSelected()){
						menuPanel.showStack(next);
						menus[next].focus();
					}
					
				}
				if(keyCode == KeyCodes.KEY_UP){
					if(menus[index].isLastItemSelected()){
						menuPanel.showStack(previous);
						menus[previous].focus();
					}
				}
				if(keyCode == KeyCodes.KEY_ESCAPE){
					app.toggleMenu();
					app.getGuiManager().getToolbarPanel().selectMenuButton(-1);
				}
	            
            }}, KeyDownEvent.getType());
		}
		this.menuPanel = new StackPanel(){
			@Override
            public void showStack(int index) {
		        super.showStack(index);
		        app.getGuiManager().setDraggingViews(index == 3 || index == 2, false);
		    }
		};
		this.menuPanel.addStyleName("menuPanel");
		
		this.menuPanel.add(fileMenu, setHTML(GuiResources.INSTANCE.menu_icon_file(), "File"), true);
		this.menuPanel.add(editMenu, setHTML(GuiResources.INSTANCE.menu_icon_edit(), "Edit"), true);
		this.menuPanel.add(perspectivesMenu, setHTML(GuiResources.INSTANCE.menu_icon_perspectives(), "Perspectives"), true);
		this.menuPanel.add(viewMenu, setHTML(GuiResources.INSTANCE.menu_icon_view(), "View"), true);
		this.menuPanel.add(optionsMenu, setHTML(GuiResources.INSTANCE.menu_icon_options(), "Options"), true);
		this.menuPanel.add(helpMenu, setHTML(GuiResources.INSTANCE.menu_icon_help(), "Help"), true);
		
	    this.add(menuPanel);
	    
	    onResize(null);
	}
	
	private String setHTML(ImageResource img, String s){
		//return  "<img src=\""+img.getSafeUri().asString()+"\" /><span style= \"font-size:80% \"  >" + s + "</span>";
		return  "<img src=\""+img.getSafeUri().asString()+"\" /><span>" + app.getMenu(s) + "</span>";
	}
	
	
	
	private void createFileMenu() {
		fileMenu = new FileMenuW(app, null);
	}

	private void createPerspectivesMenu() {
		perspectivesMenu = new PerspectivesMenuW(app);
	}

	private void createEditMenu() {
		editMenu = new EditMenuW(app);
	}
	
	private void createViewMenu() {

		viewMenu = new ViewMenuW(app);
	}
	
	private void createHelpMenu() {
		helpMenu = new HelpMenuW(app);
	}

	private void createOptionsMenu() {
		optionsMenu = new OptionsMenuW(app);
	}

	private EditMenuW getEditMenu() {
	    return editMenu;
    }

	public void updateMenubar() {
		app.getOptionsMenu().update();
		
		viewMenu.update();
		
    }
	
	public void updateSelection() {
		if(this != null){
			getEditMenu().initActions();
		}
	}

    public MenuItem getSignIn() {
		return null;
    }

	public void onResize(ResizeEvent e) {
		boolean input = app.getArticleElement().getDataParamShowAlgebraInput() || App.isFullAppGui();
		int menuHeight = (int) (app.getHeight() - (input ? GLookAndFeel.COMMAND_LINE_HEIGHT : 0) - GLookAndFeel.TOOLBAR_HEIGHT);
	    this.setHeight(menuHeight + "px");
    }
	
	public void focus(){
		int index= Math.max(menuPanel.getSelectedIndex(),0);
		if(this.menus[index]!=null){
			this.menus[index].focus();
		}
	}
	
	public static void addSubmenuArrow(AppW app,MenuBar w) {
		
			w.addStyleName("subMenuLeftSide");
			FlowPanel arrowSubmenu = new FlowPanel();
			arrowSubmenu.addStyleName("arrowSubmenu");
			Image arrow = new Image(GuiResources.INSTANCE.arrow_submenu_right());
			arrowSubmenu.add(arrow);
		    w.getElement().appendChild(arrowSubmenu.getElement());
		
    }

	public static String getMenuBarHtml(String url, String str, boolean enabled) {
		String text2 = str.replace("\"", "'");
		String text3 = (enabled) ? text2 :  "<span style=\"color:gray;\">"+text2+"</span>";
		return  "<img class=\"GeoGebraMenuImage\" alt=\""+text2+"\" src=\""+url+"\" />"+" "+ text3;
    }

	public static String getMenuBarHtml(String url, String str) {
		String text = str.replace("\"", "'");
		return "<img width=\"16\" height=\"16\" alt=\""+text+"\" src=\""+url+"\" />"+" "+text;
    }

	public static void setMenuSelected(MenuItem m, boolean visible) {
		if (visible) {
			m.addStyleName("checked");
		} else {
			m.removeStyleName("checked");
		}
	}

}
