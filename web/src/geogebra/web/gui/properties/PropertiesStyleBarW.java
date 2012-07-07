package geogebra.web.gui.properties;

import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

import geogebra.common.gui.view.properties.PropertiesView.OptionType;
import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.images.AppResourcesConverter;
import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.main.AppW;

public class PropertiesStyleBarW extends
        geogebra.common.gui.view.properties.PropertiesStyleBar {

	private PropertiesViewW propertiesView;
	private App app;
	private PopupPanel wrappedPanel;
	private PopupMenuButton btnOption;
	private MenuBar menu;

	public PropertiesStyleBarW(PropertiesViewW propertiesView, App app) {
		this.propertiesView = propertiesView;
		this.app = app;
		
		this.wrappedPanel = new PopupPanel();
		this.btnOption = new PopupMenuButton((AppW) app);
		buildMenu();
		
		
	}

	private void buildMenu() {
	    if (menu == null) {
	    	menu = new MenuBar(true);
	    }
	    menu.clearItems();
	    
	    for (final OptionType type : OptionType.values()) {
	    	final MenuItem mi = new MenuItem(getMenuHtml(type), true, 
	    			new Command() {
						
						public void execute() {
							propertiesView.setOptionPanel(type);
							buildMenu();
							setIcon(type, btnOption);
							//AG tmp ?????btnOption.setText(mi.getText() + downTriangle);
						}
					});
	    }
	    
    }

	private String getMenuHtml(OptionType type) {
	    return GeoGebraMenubarW.getMenuBarHtml(getTypeIcon(type), propertiesView.getTypeString(type));
    }
	
	private void setIcon(OptionType type, PopupMenuButton btn) {
		switch (type) {
		case DEFAULTS:
			AppResourcesConverter.setIcon(AppResources.INSTANCE.options_defaults224(), btn) ;
		case SPREADSHEET:
			AppResourcesConverter.setIcon(AppResources.INSTANCE.view_spreadsheet24(), btn);
		case EUCLIDIAN:
			AppResourcesConverter.setIcon(AppResources.INSTANCE.view_graphics24(), btn);
		case EUCLIDIAN2:
			AppResourcesConverter.setIcon(AppResources.INSTANCE.view_graphics224(), btn);
		case CAS:
			AppResourcesConverter.setIcon(AppResources.INSTANCE.view_cas24(), btn);
		case ADVANCED:
			AppResourcesConverter.setIcon(AppResources.INSTANCE.options_advanced24(), btn);
		case OBJECTS:
			AppResourcesConverter.setIcon(AppResources.INSTANCE.options_objects24(), btn);
		case LAYOUT:
			AppResourcesConverter.setIcon(AppResources.INSTANCE.options_layout24(), btn);
		}
	}

	private String getTypeIcon(OptionType type) {
		switch (type) {
		case DEFAULTS:
			return AppResources.INSTANCE.options_defaults224().getSafeUri().asString();
		case SPREADSHEET:
			return AppResources.INSTANCE.view_spreadsheet24().getSafeUri().asString();
		case EUCLIDIAN:
			return AppResources.INSTANCE.view_graphics24().getSafeUri().asString();
		case EUCLIDIAN2:
			return AppResources.INSTANCE.view_graphics224().getSafeUri().asString();
		case CAS:
			return AppResources.INSTANCE.view_cas24().getSafeUri().asString();
		case ADVANCED:
			return AppResources.INSTANCE.options_advanced24().getSafeUri().asString();
		case OBJECTS:
			return AppResources.INSTANCE.options_objects24().getSafeUri().asString();
		case LAYOUT:
			return AppResources.INSTANCE.options_layout24().getSafeUri().asString();
		}
		return null;
    }
}
