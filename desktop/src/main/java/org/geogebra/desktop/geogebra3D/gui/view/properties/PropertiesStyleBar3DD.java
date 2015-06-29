package org.geogebra.desktop.geogebra3D.gui.view.properties;

import javax.swing.JMenuItem;

import org.geogebra.common.gui.view.properties.PropertiesView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.desktop.gui.view.properties.PropertiesStyleBarD;
import org.geogebra.desktop.main.AppD;


/**
 * Style bar for properties view (in 3D)
 * 
 * @author mathieu
 *
 */
public class PropertiesStyleBar3DD extends PropertiesStyleBarD {

	/**
	 * constructor
	 * @param propertiesView properties view
	 * @param app application
	 */
	public PropertiesStyleBar3DD(PropertiesView propertiesView, AppD app) {
		super(propertiesView, app);
	}

	@Override
	protected PropertiesButton newPropertiesButton(OptionType type){

		return new PropertiesButton();

	}

	@Override
	protected JMenuItem newJMenuItem(OptionType type){
		
		return new JMenuItem();
		
	}
	
	
	@Override
	public void updateGUI() {
		
		super.updateGUI();
		
		buttonMap.get(OptionType.EUCLIDIAN3D).setVisible(
				app.getGuiManager()
						.showView(App.VIEW_EUCLIDIAN3D));
		
		buttonMap.get(OptionType.EUCLIDIAN_FOR_PLANE).setVisible(
				app.hasEuclidianViewForPlaneVisible());
		


	}

}
