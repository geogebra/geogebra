package geogebra.web.gui.menubar;

import geogebra.html5.css.GuiResources;
import geogebra.web.gui.app.GGWFrameLayoutPanel;
import geogebra.web.main.AppW;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.StackPanel;

/**
 * Sidebar menu for SMART
 * 
 * 
 */

public class GeoGebraMenubarSMART extends FlowPanel implements GeoGebraMenuW, ResizeHandler {
	
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

	/**
	 * Constructs the menubar
	 * 
	 * @param app
	 *            application
	 */
	public GeoGebraMenubarSMART(AppW app) {
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
		
		this.menuPanel = new StackPanel(){
			@Override
            public void showStack(int index) {
		        super.showStack(index);
		        app.getGuiManager().setDraggingViews(index == 3 || index == 2);
		    }
		};
		this.menuPanel.addStyleName("menuPanel");
		
		this.menuPanel.add(fileMenu, setHTML(GuiResources.INSTANCE.menu_icon_file(), "File"), true);
		this.menuPanel.add(editMenu, setHTML(GuiResources.INSTANCE.menu_icon_edit(), "Edit"), true);
		this.menuPanel.add(perspectivesMenu, setHTML(GuiResources.INSTANCE.menu_icon_perspectives_algebra(), "Perspectives"), true);
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
		fileMenu = new FileMenuW(app, true, null);
	}

	private void createPerspectivesMenu() {
		perspectivesMenu = new PerspectivesMenuW(app);
	}

	private void createEditMenu() {
		editMenu = new EditMenuW(app);
	}
	
	private void createViewMenu() {

		viewMenu = (app.isApplet()) ? new ViewMenuW(app) : new ViewMenuApplicationW(app);
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
		if (!app.isApplet()) {
			((ViewMenuApplicationW) viewMenu).update();
		}
    }
	
	public void updateSelection() {
		if(this != null){
			getEditMenu().initActions();
		}
	}

	@Override
    public MenuItem getSignIn() {
		return null;
    }

	public void onResize(ResizeEvent e) {
		int menuHeight = (int) (app.getHeight() - GGWFrameLayoutPanel.COMMAND_LINE_HEIGHT - GGWFrameLayoutPanel.TOOLBAR_HEIGHT);
	    this.setHeight(menuHeight + "px");
    }

}
