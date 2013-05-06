package geogebra3D.gui.view.properties;

import geogebra.common.main.OptionType;
import geogebra.gui.dialog.options.OptionPanelD;
import geogebra.gui.dialog.options.OptionsEuclidianD;
import geogebra.gui.view.properties.PropertiesStyleBarD;
import geogebra.gui.view.properties.PropertiesViewD;
import geogebra.main.AppD;
import geogebra3D.App3D;
import geogebra3D.gui.dialogs.options.OptionsEuclidian3D;

/**
 * Just adding 3D view for properties
 * @author mathieu
 *
 */
public class PropertiesView3D extends PropertiesViewD {
	
	private OptionsEuclidianD euclidianPanel3D;

	/**
	 * Constructor
	 * @param app application
	 */
	public PropertiesView3D(AppD app) {
		super(app);
	}
	
	
	
	@Override
	public OptionPanelD getOptionPanel(OptionType type) {
		if(type==OptionType.EUCLIDIAN3D){
			if (euclidianPanel3D == null) {
				euclidianPanel3D = new OptionsEuclidian3D((AppD) app,
						((App3D) app).getEuclidianView3D());
				euclidianPanel3D.setLabels();
			}

			return euclidianPanel3D;

		}
		
		return super.getOptionPanel(type);
	}
	
	
	@Override
	public void setLabels() {
		
		super.setLabels();
		
		if (euclidianPanel3D != null)
			euclidianPanel3D.setLabels();

	}
	
	@Override
	public void updateFonts() {
		
		if (isIniting) {
			return;
		}

		super.updateFonts();
		
		if (euclidianPanel3D != null)
			euclidianPanel3D.updateFont();
		
	}
	
	@Override
	protected PropertiesStyleBarD newPropertiesStyleBar() {
		return new PropertiesStyleBar3D(this, (AppD) app);
	}

}
