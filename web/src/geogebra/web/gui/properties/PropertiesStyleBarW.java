package geogebra.web.gui.properties;

import geogebra.common.main.App;
import geogebra.common.main.OptionType;
import geogebra.html5.css.GuiResources;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.images.AppResourcesConverter;
import geogebra.web.gui.menubar.MainMenu;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.main.AppW;

import java.util.HashMap;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * @author gabor
 * Creates PropertiesStyleBar for Web
 *
 */
public class PropertiesStyleBarW extends
        geogebra.common.gui.view.properties.PropertiesStyleBar {

	private static OptionType OptionTypesImpl[] = {
		// Implemented types of the web
		OptionType.OBJECTS, OptionType.EUCLIDIAN, OptionType.EUCLIDIAN2
	};
	
	private PropertiesViewW propertiesView;
	private App app;
	private FlowPanel wrappedPanel;
	private PopupMenuButton btnOption;
	private MenuBar menu;
	private HashMap<OptionType, MenuItem> buttonMap;

	public PropertiesStyleBarW(PropertiesViewW propertiesView, App app) {
		this.propertiesView = propertiesView;
		this.app = app;
		
		this.wrappedPanel = new FlowPanel();
		this.btnOption = new PopupMenuButton((AppW) app);
		buildMenu();
		btnOption.setPopupMenu(menu);
		btnOption.setKeepVisible(true);
		btnOption.setStandardButton(true);
		wrappedPanel.setStyleName("propertiesStyleBar");
		/*AGbtnOption.setHorizontalTextPosition(SwingConstants.RIGHT);
		Dimension d = btnOption.getPreferredSize();
		d.width = menu.getPreferredSize().width;
		btnOption.setPreferredSize(d);*/
		
		buildGUI();
		updateGUI();
		
		
	}
	
	

	public void updateGUI() {
		OptionType seltype = propertiesView.getSelectedOptionType();
		setIcon(propertiesView
				.getSelectedOptionType(),btnOption);
		btnOption.setText(propertiesView.getTypeString(propertiesView.getSelectedOptionType())
				+ downTriangle);		
		
		//TODO: get it in css
		buttonMap.get(seltype).addStyleName("selected");
		
		buttonMap.get(OptionType.OBJECTS).setVisible(
				app.getSelectionManager().selectedGeosSize() > 0);
		
		buttonMap.get(OptionType.EUCLIDIAN).setVisible(
				app.getGuiManager()
						.showView(App.VIEW_EUCLIDIAN));
		
		buttonMap.get(OptionType.EUCLIDIAN2).setVisible(
				app.getGuiManager()
						.showView(App.VIEW_EUCLIDIAN2));
//	These are not implemented yet		
//		buttonMap.get(OptionType.SPREADSHEET).setVisible(
//				app.getGuiManager()
//						.showView(App.VIEW_SPREADSHEET));
//		
//		buttonMap.get(OptionType.CAS).setVisible(
//				app.getGuiManager()
//						.showView(App.VIEW_CAS));
    }



	private void buildGUI() {
		
		MenuBar toolbar = new MenuBar(true);
		toolbar.setStyleName("menuProperties");
		
		buttonMap = new HashMap<OptionType, MenuItem>();
		
		for (final OptionType type : OptionTypesImpl) {
			final PropertiesButton btn = new PropertiesButton(getMenuHtml(type));
			btn.setTitle(propertiesView.getTypeString(type));
			btn.setCommand(new Command() {
				
				public void execute() {
					propertiesView.setOptionPanel(type, 0);
				}
			});
			toolbar.addItem(btn);
			buttonMap.put(type, btn);
			
			if (type == OptionType.OBJECTS || type == OptionType.SPREADSHEET) {
				//toolbar.addSeparator();
			}
		}
		//if(!((AppW) app).getLAF().isSmart()){
			this.getWrappedPanel().add(toolbar);
	//	}
	    
    }

	private void buildMenu() {
	    if (menu == null) {
	    	menu = new MenuBar(true);
	    }
	    menu.clearItems();
	    
	    for (final OptionType type : OptionType.values()) {
	    	String typeHtml = getMenuHtml(type);
	    	if (typeHtml == null) {
	    		continue;
	    	}
	    	final MenuItem mi = new PropertiesButton(typeHtml);
	    	mi.setCommand( 
	    			new Command() {
						
						public void execute() {
							propertiesView.setOptionPanel(type, 0);
							buildMenu();
							setIcon(type, btnOption);
							btnOption.setText(mi.getText() + downTriangle);
						}
					});
	    	menu.addItem(mi);
	    	if (type == OptionType.OBJECTS || type == OptionType.SPREADSHEET) {
	    		menu.addSeparator();
	    	}
	    }
	    
    }

	private String getMenuHtml(OptionType type) {
		String typeString = "";//propertiesView.getTypeString(type);
	    return typeString != null ? MainMenu.getMenuBarHtml(getTypeIcon(type), typeString): null; 
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
			//AppResourcesConverter.setIcon(AppResources.INSTANCE.options_objects24(), btn);
			AppResourcesConverter.setIcon(GuiResources.INSTANCE.properties_object(), btn);
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
			//return AppResources.INSTANCE.view_graphics24().getSafeUri().asString();
			return GuiResources.INSTANCE.properties_graphics().getSafeUri().asString();
		case EUCLIDIAN2:
			//return AppResources.INSTANCE.view_graphics224().getSafeUri().asString();
			return GuiResources.INSTANCE.properties_graphics2().getSafeUri().asString();
		case CAS:
			return AppResources.INSTANCE.view_cas24().getSafeUri().asString();
		case ADVANCED:
			return AppResources.INSTANCE.options_advanced24().getSafeUri().asString();
		case OBJECTS:
			//return AppResources.INSTANCE.options_objects24().getSafeUri().asString();
			return GuiResources.INSTANCE.properties_object().getSafeUri().asString();
		case LAYOUT:
			return AppResources.INSTANCE.options_layout24().getSafeUri().asString();
		}
		return null;
    }



	public FlowPanel getWrappedPanel() {
	    return wrappedPanel;
    }


}
