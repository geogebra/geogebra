package org.geogebra.web.web.gui.properties;

import java.util.HashMap;

import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.ImageFactory;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.images.AppResourcesConverter;
import org.geogebra.web.web.gui.images.PerspectiveResources;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.gui.util.PopupMenuButton;

import com.google.gwt.core.shared.GWT;
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
        org.geogebra.common.gui.view.properties.PropertiesStyleBar {

	private static OptionType OptionTypesImpl[] = {
		// Implemented types of the web
		OptionType.OBJECTS, OptionType.EUCLIDIAN, OptionType.EUCLIDIAN2, OptionType.EUCLIDIAN3D, OptionType.SPREADSHEET
	};
	
	private PropertiesViewW propertiesView;
	protected App app;
	private FlowPanel wrappedPanel;
	//private PopupMenuButton btnOption;
	private MenuBar menu;
	protected HashMap<OptionType, MenuItem> buttonMap;

	private MenuItem currentButton;

	public PropertiesStyleBarW(PropertiesViewW propertiesView, App app) {
		this.propertiesView = propertiesView;
		this.app = app;
		
		this.wrappedPanel = new FlowPanel();
		buildMenu();
		wrappedPanel.setStyleName("propertiesStyleBar");
		/*AGbtnOption.setHorizontalTextPosition(SwingConstants.RIGHT);
		Dimension d = btnOption.getPreferredSize();
		d.width = menu.getPreferredSize().width;
		btnOption.setPreferredSize(d);*/
		
		buildGUI();
		updateGUI();
		
		
	}
	
	

	public void updateGUI() {
		

		//selectButton(seltype);
		
		buttonMap.get(OptionType.OBJECTS).setVisible(
				app.getSelectionManager().selectedGeosSize() > 0);
		
		buttonMap.get(OptionType.EUCLIDIAN).setVisible(
				app.getGuiManager()
						.showView(App.VIEW_EUCLIDIAN));
		
		buttonMap.get(OptionType.EUCLIDIAN2).setVisible(
				app.getGuiManager()
						.showView(App.VIEW_EUCLIDIAN2));
//	These are not implemented yet		
		buttonMap.get(OptionType.SPREADSHEET).setVisible(
				app.getGuiManager().showView(App.VIEW_SPREADSHEET));
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
			if (typeAvailable(type)){
				final PropertiesButton btn = new PropertiesButton(getMenuHtml(type));
				btn.setTitle(propertiesView.getTypeString(type));
				btn.setCommand(new Command() {

					public void execute() {
						propertiesView.setOptionPanel(type, 0);
						selectButton(type);
					}
				});
				toolbar.addItem(btn);
				buttonMap.put(type, btn);

				if (type == OptionType.OBJECTS || type == OptionType.SPREADSHEET) {
					//toolbar.addSeparator();
				}
			}
		}
		//if(!((AppW) app).getLAF().isSmart()){
			this.getWrappedPanel().add(toolbar);
	//	}
	    
    }
	
	/**
	 * @param type type
	 * @return true if the type is really available
	 */
	protected boolean typeAvailable(OptionType type){
		return type != OptionType.EUCLIDIAN3D;
	}
	
	

	protected void selectButton(OptionType type) {
		if(currentButton != null){
			this.currentButton.removeStyleName("selected");
		}
		currentButton = buttonMap.get(type);
		currentButton.addStyleName("selected");
	    
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
	
	protected void setIcon(OptionType type, PopupMenuButton btn) {
		PerspectiveResources pr = ((ImageFactory)GWT.create(ImageFactory.class)).getPerspectiveResources();
		switch (type) {
		
		case DEFAULTS:
			AppResourcesConverter.setIcon(AppResources.INSTANCE.options_defaults224(), btn) ;
		case SPREADSHEET:
			AppResourcesConverter.setIcon(pr.menu_icon_spreadsheet(), btn);
		case EUCLIDIAN:
			AppResourcesConverter.setIcon(pr.menu_icon_graphics(), btn);
		case EUCLIDIAN2:
			AppResourcesConverter.setIcon(pr.menu_icon_graphics2(), btn);
		case EUCLIDIAN3D:
			AppResourcesConverter.setIcon(pr.menu_icon_graphics3D(), btn);
		case CAS:
			AppResourcesConverter.setIcon(pr.menu_icon_cas(), btn);
		case ADVANCED:
			AppResourcesConverter.setIcon(AppResources.INSTANCE.options_advanced24(), btn);
		case OBJECTS:
			//AppResourcesConverter.setIcon(AppResources.INSTANCE.options_objects24(), btn);
			AppResourcesConverter.setIcon(GuiResources.INSTANCE.properties_object(), btn);
		case LAYOUT:
			AppResourcesConverter.setIcon(AppResources.INSTANCE.options_layout24(), btn);
		}
	}

	protected String getTypeIcon(OptionType type) {
		PerspectiveResources pr = ((ImageFactory)GWT.create(ImageFactory.class)).getPerspectiveResources();
		switch (type) {
		case DEFAULTS:
			return AppResources.INSTANCE.options_defaults224().getSafeUri().asString();
		case SPREADSHEET:
			return GGWToolBar.safeURI(pr.menu_icon_spreadsheet());
		case EUCLIDIAN:
			return GGWToolBar.safeURI(pr.menu_icon_graphics());
			//return GuiResources.INSTANCE.properties_graphics().getSafeUri().asString();
		case EUCLIDIAN2:
			return GGWToolBar.safeURI(pr.menu_icon_graphics2());
			//return GuiResources.INSTANCE.properties_graphics2().getSafeUri().asString();
		case CAS:
			return GGWToolBar.safeURI(pr.menu_icon_cas());
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
