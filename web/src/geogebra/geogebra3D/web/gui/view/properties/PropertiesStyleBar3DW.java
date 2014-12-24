package geogebra.geogebra3D.web.gui.view.properties;

import geogebra.common.main.App;
import geogebra.common.main.OptionType;
import geogebra.html5.main.AppW;
import geogebra.web.gui.ImageFactory;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.images.AppResourcesConverter;
import geogebra.web.gui.images.PerspectiveResources;
import geogebra.web.gui.properties.PropertiesStyleBarW;
import geogebra.web.gui.properties.PropertiesViewW;
import geogebra.web.gui.util.PopupMenuButton;

import com.google.gwt.core.shared.GWT;

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
	protected void setIcon(OptionType type, PopupMenuButton btn) {
		if (type == OptionType.EUCLIDIAN3D) {
			PerspectiveResources pr = ((ImageFactory) GWT
			        .create(ImageFactory.class)).getPerspectiveResources();
			AppResourcesConverter.setIcon(pr.menu_icon_graphics3D(), btn);
		} else {
			super.setIcon(type, btn);
		}
	}

	@Override
	protected String getTypeIcon(OptionType type) {
		if (type == OptionType.EUCLIDIAN3D) {
			PerspectiveResources pr = ((ImageFactory) GWT
			        .create(ImageFactory.class)).getPerspectiveResources();
			return GGWToolBar.safeURI(pr.menu_icon_graphics3D());
		}
		return super.getTypeIcon(type);
	}

	@Override
	public void updateGUI() {

		super.updateGUI();

		buttonMap.get(OptionType.EUCLIDIAN3D).setVisible(
		        app.getGuiManager().showView(App.VIEW_EUCLIDIAN3D));

	}

	@Override
	protected boolean typeAvailable(OptionType type) {
		return true;
	}

}
