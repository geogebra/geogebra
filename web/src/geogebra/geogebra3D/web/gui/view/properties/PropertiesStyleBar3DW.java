package geogebra.geogebra3D.web.gui.view.properties;

import geogebra.common.main.App;
import geogebra.common.main.OptionType;
import geogebra.geogebra3D.html5.css.Gui3DResources;
import geogebra.geogebra3D.web.gui.images.App3DResources;
import geogebra.web.gui.images.AppResourcesConverter;
import geogebra.web.gui.properties.PropertiesStyleBarW;
import geogebra.web.gui.properties.PropertiesViewW;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.main.AppW;


/**
 * Style bar for properties view (in 3D)
 * 
 * @author mathieu
 *
 */
public class PropertiesStyleBar3DW extends PropertiesStyleBarW {

	/**
	 * constructor
	 * @param propertiesView properties view
	 * @param app application
	 */
	public PropertiesStyleBar3DW(PropertiesViewW propertiesView, AppW app) {
		super(propertiesView, app);
	}

	
	@Override
    protected void setIcon(OptionType type, PopupMenuButton btn) {
		if (type == OptionType.EUCLIDIAN3D){
			AppResourcesConverter.setIcon(App3DResources.INSTANCE.view_graphics3D(), btn);
		}else{
			super.setIcon(type, btn);
		}
	}
	
	@Override
    protected String getTypeIcon(OptionType type) {
		if (type == OptionType.EUCLIDIAN3D){
			return Gui3DResources.INSTANCE.properties_graphics3d().getSafeUri().asString();
		}
		return super.getTypeIcon(type);
	}
	
	
	
	@Override
	public void updateGUI() {
		
		super.updateGUI();
		
		buttonMap.get(OptionType.EUCLIDIAN3D).setVisible(
				app.getGuiManager()
						.showView(App.VIEW_EUCLIDIAN3D));
		


	}
	
	@Override
    protected boolean typeAvailable(OptionType type){
		return true;
	}

}
