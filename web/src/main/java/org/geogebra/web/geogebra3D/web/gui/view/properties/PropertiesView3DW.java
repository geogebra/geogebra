package org.geogebra.web.geogebra3D.web.gui.view.properties;

import org.geogebra.common.main.OptionType;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;
import org.geogebra.web.geogebra3D.web.gui.dialog.options.OptionsEuclidian3DW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.dialog.options.OptionPanelW;
import org.geogebra.web.web.gui.dialog.options.OptionsEuclidianW;
import org.geogebra.web.web.gui.properties.PropertiesStyleBarW;
import org.geogebra.web.web.gui.properties.PropertiesViewW;

/**
 * Just adding 3D view for properties
 * 
 * @author mathieu
 *
 */
public class PropertiesView3DW extends PropertiesViewW {

	private OptionsEuclidianW euclidianPanel3D;

	/**
	 * Constructor
	 * 
	 * @param app
	 *            application
	 */
	public PropertiesView3DW(AppW app) {
		super(app);
	}

	@Override
	public OptionPanelW getOptionPanel(OptionType type, int subType) {
		if (type == OptionType.EUCLIDIAN3D) {
			if (euclidianPanel3D == null) {
				euclidianPanel3D = new OptionsEuclidian3DW((AppW) app,
				        app.getEuclidianView3D());
				euclidianPanel3D.setLabels();
				euclidianPanel3D.setView((EuclidianView3DW) app
				        .getEuclidianView3D());
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
