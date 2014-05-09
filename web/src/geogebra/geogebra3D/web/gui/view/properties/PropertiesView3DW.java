package geogebra.geogebra3D.web.gui.view.properties;

import geogebra.common.main.OptionType;
import geogebra.web.gui.dialog.options.OptionPanelW;
import geogebra.web.gui.dialog.options.OptionsEuclidianW;
import geogebra.web.gui.properties.PropertiesStyleBarW;
import geogebra.web.gui.properties.PropertiesViewW;
import geogebra.web.main.AppW;

/**
 * Just adding 3D view for properties
 * @author mathieu
 *
 */
public class PropertiesView3DW extends PropertiesViewW {
	
	private OptionsEuclidianW euclidianPanel3D;

	/**
	 * Constructor
	 * @param app application
	 */
	public PropertiesView3DW(AppW app) {
		super(app);
	}
	
	
	
	@Override
	public OptionPanelW getOptionPanel(OptionType type, int subType) {
		if(type==OptionType.EUCLIDIAN3D){
			if (euclidianPanel3D == null) {
				
				/*
				euclidianPanel3D = new OptionsEuclidian3D((AppD) app,
						((App3D) app).getEuclidianView3D());
				euclidianPanel3D.setLabels();
				*/
				
				euclidianPanel3D = new OptionsEuclidianW((AppW) app,
						((AppW) app).getActiveEuclidianView());
				euclidianPanel3D.setLabels();
				euclidianPanel3D.setView(((AppW)app).getEuclidianView1());
				euclidianPanel3D.showCbView(false);
			}

			return euclidianPanel3D;

		}
		
		return super.getOptionPanel(type, subType);
	}
	
	
	
	
	@Override
	protected PropertiesStyleBarW newPropertiesStyleBar() {
		return new PropertiesStyleBar3DW(this, (AppW) app);
	}
	

}
