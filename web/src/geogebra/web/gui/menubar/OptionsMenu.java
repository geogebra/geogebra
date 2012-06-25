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
		
		//addPointCapturingMenu();
	}
	
	private void addLanguageMenu() {
		languageMenu = new LanguageMenu(app);
		addItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE
		        .empty().getSafeUri().asString(), app.getMenu("Language")), true, languageMenu);
	}
	
	private void addAlgebraDescriptionMenu(){
		RadioButtonMenuBar submenu = new RadioButtonMenuBar();
		
		submenu.addItem(app.getMenu("Value"), new RadioButtonCommand(submenu, 0) {
			

			@Override
			public void exec() {
				kernel.setAlgebraStyle(0);
				kernel.updateConstruction();
			}
		});
		submenu.addItem(app.getMenu("Definition"), new RadioButtonCommand(submenu, 1) {
			@Override
			public void exec() {
				kernel.setAlgebraStyle(1);
				kernel.updateConstruction();
			}
		});
		submenu.addItem(app.getMenu("Command"), new RadioButtonCommand(submenu, 2) {
			@Override
			public void exec() {
				kernel.setAlgebraStyle(2);
				kernel.updateConstruction();
			}
		});
				
		MenuItem algebraDescription = new MenuItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE
		        .empty().getSafeUri().asString(), app.getMenu("AlgebraDescriptions")),
		        true, submenu);		
		
		addItem(algebraDescription);		
	}

	private void addPointCapturingMenu(){
		RadioButtonMenuBar submenu = new RadioButtonMenuBar();
		
		submenu.addItem(app.getMenu("Labeling.automatic"), new RadioButtonCommand(submenu, 0) {
			@Override
			public void exec() {
				setPointCapturing(3);
			}
		});
		submenu.addItem(app.getMenu("SnapToGrid"), new RadioButtonCommand(submenu, 1) {
			@Override
			public void exec() {
				setPointCapturing(0);
			}
		});
		submenu.addItem(app.getMenu("FixedToGrid"), new RadioButtonCommand(submenu, 2) {
			@Override
			public void exec() {
				setPointCapturing(1);
			}
		});
	
		submenu.addItem(app.getMenu("off"), new RadioButtonCommand(submenu, 3) {
			@Override
			public void exec() {
				setPointCapturing(2);
			}
		});
		
		MenuItem algebraDescription = new MenuItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE
		        .magnet2().getSafeUri().asString(), app.getMenu("PointCapturing")),
		        true, submenu);		
		
		addItem(algebraDescription);		
	}
	
	void setPointCapturing(int mode){
		app.getEuclidianView1().setPointCapturing(mode);
		if (app.hasEuclidianView2EitherShowingOrNot()) {
			app.getEuclidianView2().setPointCapturing(mode);
		}
		app.setUnsaved();		
	}

}
