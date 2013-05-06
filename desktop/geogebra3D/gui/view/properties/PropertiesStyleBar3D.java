package geogebra3D.gui.view.properties;

import geogebra.common.gui.view.properties.PropertiesView;
import geogebra.common.main.App;
import geogebra.common.main.OptionType;
import geogebra.gui.view.properties.PropertiesStyleBarD;
import geogebra.main.AppD;

import javax.swing.JMenuItem;


/**
 * Style bar for properties view (in 3D)
 * 
 * @author mathieu
 *
 */
public class PropertiesStyleBar3D extends PropertiesStyleBarD {

	/**
	 * constructor
	 * @param propertiesView properties view
	 * @param app application
	 */
	public PropertiesStyleBar3D(PropertiesView propertiesView, AppD app) {
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
		


	}

}
