package geogebra.web.gui.menubar;

import geogebra.common.kernel.Kernel;
import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.images.AppResources;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * The "Options" menu.
 */
public class OptionsMenu extends MenuBar {
	
	private AbstractApplication app;
	Kernel kernel;
	
	private LanguageMenu languageMenu;
	
	/**
	 * Constructs the "Option" menu
	 * @param app Application instance
	 */
	public OptionsMenu(AbstractApplication app) {

		super(true);
	    this.app = app;
	    kernel = app.getKernel();
	    addStyleName("GeoGebraMenuBar");
	    initItems();
	}
	
	private void initItems(){
		//"Algebra Descriptions" menu
		addAlgebraDescriptionMenu();
		
		//language menu
		addLanguageMenu();
	}
	
	private void addLanguageMenu() {
		languageMenu = new LanguageMenu(app);
		addItem(app.getMenu("Language"), languageMenu);
	}
	
	private void addAlgebraDescriptionMenu(){
		RadioButtonMenuBar submenu = new RadioButtonMenuBar();
		
		submenu.addItem(app.getPlain("Value"), new RadioButtonCommand(submenu, 0) {
			

			@Override
			public void exec() {
				Window.alert("algebra style: 0");
				kernel.setAlgebraStyle(0);
				kernel.updateConstruction();
			}
		});
		submenu.addItem(app.getPlain("Definition"), new RadioButtonCommand(submenu, 1) {
			@Override
			public void exec() {
				Window.alert("algebra style: 1");
				kernel.setAlgebraStyle(1);
				kernel.updateConstruction();
			}
		});
		submenu.addItem(app.getPlain("Command"), new RadioButtonCommand(submenu, 2) {
			@Override
			public void exec() {
				Window.alert("algebra style: 2");
				kernel.setAlgebraStyle(2);
				kernel.updateConstruction();
			}
		});
		
		submenu.setSelected(0);
		
		MenuItem algebraDescription = new MenuItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE
		        .empty().getSafeUri().asString(), app.getMenu("AlgebraDescriptions")),
		        true, submenu);		
		
		addItem(algebraDescription);		
	}

}
