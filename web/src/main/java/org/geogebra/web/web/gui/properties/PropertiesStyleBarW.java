package org.geogebra.web.web.gui.properties;

import java.util.HashMap;

import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.ImageFactory;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.images.ImgResourceHelper;
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
			OptionType.OBJECTS, OptionType.EUCLIDIAN, OptionType.EUCLIDIAN2,
			OptionType.EUCLIDIAN_FOR_PLANE, OptionType.EUCLIDIAN3D,
			OptionType.SPREADSHEET, OptionType.CAS, OptionType.ALGEBRA
	};
	
	private PropertiesViewW propertiesView;
	protected App app;
	private FlowPanel wrappedPanel;
	//private PopupMenuButton btnOption;
	protected HashMap<OptionType, MenuItem> buttonMap;

	private MenuItem currentButton;

	public PropertiesStyleBarW(PropertiesViewW propertiesView, App app) {
		this.propertiesView = propertiesView;
		this.app = app;
		
		this.wrappedPanel = new FlowPanel();
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
		buttonMap.get(OptionType.CAS).setVisible(
				app.getGuiManager().showView(App.VIEW_CAS));
    }



	private void buildGUI() {
		
		MenuBar toolbar = new MenuBar(true);
		toolbar.setStyleName("menuProperties");
		
		buttonMap = new HashMap<OptionType, MenuItem>();
		
		for (final OptionType type : OptionTypesImpl) {
			if (typeAvailable(type)){
				final PropertiesButton btn = new PropertiesButton(
						getMenuHtml(type), new Command() {

					public void execute() {
						propertiesView.setOptionPanel(type, 0);
						selectButton(type);
					}
				});
				btn.setTitle(propertiesView.getTypeString(type));
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
		return type != OptionType.EUCLIDIAN3D
				&& type != OptionType.EUCLIDIAN_FOR_PLANE;
	}
	
	

	protected void selectButton(OptionType type) {
		if(currentButton != null){
			this.currentButton.removeStyleName("selected");
		}
		currentButton = buttonMap.get(type);
		currentButton.addStyleName("selected");
	    
    }




	private String getMenuHtml(OptionType type) {
		String typeString = "";//propertiesView.getTypeString(type);
	    return typeString != null ? MainMenu.getMenuBarHtml(getTypeIcon(type), typeString): null; 
    }
	
	protected void setIcon(OptionType type, PopupMenuButton btn) {
		PerspectiveResources pr = ((ImageFactory)GWT.create(ImageFactory.class)).getPerspectiveResources();
		switch (type) {
		
		case DEFAULTS:
			ImgResourceHelper.setIcon(AppResources.INSTANCE.options_defaults224(), btn) ;
		case SPREADSHEET:
			ImgResourceHelper.setIcon(pr.menu_icon_spreadsheet24(), btn);
		case EUCLIDIAN:
			ImgResourceHelper.setIcon(pr.menu_icon_graphics24(), btn);
		case EUCLIDIAN2:
			ImgResourceHelper.setIcon(pr.menu_icon_graphics224(), btn);
		case EUCLIDIAN_FOR_PLANE:
			ImgResourceHelper.setIcon(pr.menu_icon_graphics_extra24(),
					btn);
		case EUCLIDIAN3D:
			ImgResourceHelper.setIcon(pr.menu_icon_graphics3D24(), btn);
		case CAS:
			ImgResourceHelper.setIcon(pr.menu_icon_cas24(), btn);
		case ADVANCED:
			ImgResourceHelper.setIcon(AppResources.INSTANCE.options_advanced24(), btn);
		case ALGEBRA:
			ImgResourceHelper
					.setIcon(AppResources.INSTANCE.options_algebra24(), btn);
		case OBJECTS:
			//AppResourcesConverter.setIcon(AppResources.INSTANCE.options_objects24(), btn);
			ImgResourceHelper.setIcon(GuiResources.INSTANCE.properties_object(), btn);
		case LAYOUT:
			ImgResourceHelper.setIcon(AppResources.INSTANCE.options_layout24(), btn);
		}
	}

	protected String getTypeIcon(OptionType type) {
		PerspectiveResources pr = ((ImageFactory)GWT.create(ImageFactory.class)).getPerspectiveResources();
		switch (type) {
		case DEFAULTS:
			return AppResources.INSTANCE.options_defaults224().getSafeUri().asString();
		case SPREADSHEET:
			return ImgResourceHelper.safeURI(pr.menu_icon_spreadsheet24());
		case EUCLIDIAN:
			return ImgResourceHelper.safeURI(pr.menu_icon_graphics24());
			//return GuiResources.INSTANCE.properties_graphics().getSafeUri().asString();
		case EUCLIDIAN2:
			return ImgResourceHelper.safeURI(pr.menu_icon_graphics224());
			//return GuiResources.INSTANCE.properties_graphics2().getSafeUri().asString();
		case CAS:
			return ImgResourceHelper.safeURI(pr.menu_icon_cas24());
		case ADVANCED:
			return AppResources.INSTANCE.options_advanced24().getSafeUri().asString();
		case ALGEBRA:
			return AppResources.INSTANCE.options_algebra24().getSafeUri()
					.asString();
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
