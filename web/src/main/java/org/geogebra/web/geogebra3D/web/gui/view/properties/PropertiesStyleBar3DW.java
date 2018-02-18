package org.geogebra.web.geogebra3D.web.gui.view.properties;

import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.ImageFactory;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.gui.properties.PropertiesStyleBarW;
import org.geogebra.web.full.gui.properties.PropertiesViewW;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.html5.main.AppW;

/**
 * Style bar for properties view (in 3D)
 * 
 * @author mathieu
 *
 */
public class PropertiesStyleBar3DW extends PropertiesStyleBarW {

	/**
	 * constructor
	 * 
	 * @param propertiesView
	 *            properties view
	 * @param app
	 *            application
	 */
	public PropertiesStyleBar3DW(PropertiesViewW propertiesView, AppW app) {
		super(propertiesView, app);
	}

	@Override
	protected String getTypeIcon(OptionType type) {
		switch (type) {
		case EUCLIDIAN3D:
			return ImgResourceHelper
					.safeURI(MaterialDesignResources.INSTANCE.graphing3D());
		case EUCLIDIAN_FOR_PLANE:
			SvgPerspectiveResources pr = ImageFactory
					.getPerspectiveResources();
			return ImgResourceHelper.safeURI(pr.menu_icon_graphics_extra());
		default:
			return super.getTypeIcon(type);
		}
	}

	@Override
	public void updateGUI() {

		super.updateGUI();

		setButtonVisible(OptionType.EUCLIDIAN3D,
		        app.getGuiManager().showView(App.VIEW_EUCLIDIAN3D));

		setButtonVisible(OptionType.EUCLIDIAN_FOR_PLANE,
				app.hasEuclidianViewForPlaneVisible());

	}

	@Override
	protected boolean typeAvailable(OptionType type) {
		return true;
	}

}
