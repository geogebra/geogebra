package org.geogebra.desktop.geogebra3D.gui.view.properties;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.desktop.geogebra3D.App3D;
import org.geogebra.desktop.geogebra3D.gui.dialogs.options.OptionsEuclidian3DD;
import org.geogebra.desktop.gui.dialog.options.OptionPanelD;
import org.geogebra.desktop.gui.dialog.options.OptionsEuclidianD;
import org.geogebra.desktop.gui.dialog.options.OptionsEuclidianForPlaneD;
import org.geogebra.desktop.gui.view.properties.PropertiesStyleBarD;
import org.geogebra.desktop.gui.view.properties.PropertiesViewD;
import org.geogebra.desktop.main.AppD;

/**
 * Just adding 3D view for properties
 * @author mathieu
 *
 */
public class PropertiesView3DD extends PropertiesViewD {
	
	private OptionsEuclidianD euclidianPanel3D;
	private OptionsEuclidianForPlaneD euclidianForPlanePanel;

	/**
	 * Constructor
	 * @param app application
	 */
	public PropertiesView3DD(AppD app) {
		super(app);
	}
	
	
	
	@Override
	public OptionPanelD getOptionPanel(OptionType type) {

		switch (type) {
		case EUCLIDIAN3D:
			if (euclidianPanel3D == null) {
				euclidianPanel3D = new OptionsEuclidian3DD((AppD) app,
						((App3D) app).getEuclidianView3D());
				euclidianPanel3D.setLabels();
			}

			return euclidianPanel3D;

		case EUCLIDIAN_FOR_PLANE:
			EuclidianView view = (EuclidianView) app.getActiveEuclidianView();
			if (!view.isViewForPlane()) {
				view = app.getViewForPlaneVisible();
			}
			if (euclidianForPlanePanel == null) {
				euclidianForPlanePanel = new OptionsEuclidianForPlaneD(
						(AppD) app, view);
				euclidianForPlanePanel.setLabels();
			} else {
				euclidianForPlanePanel.updateView(view);
				euclidianForPlanePanel.setLabels();
			}

			return euclidianForPlanePanel;
		}
		
		return super.getOptionPanel(type);
	}
	
	
	@Override
	public void setLabels() {
		
		super.setLabels();
		
		if (euclidianPanel3D != null) {
			euclidianPanel3D.setLabels();
		}

		if (euclidianForPlanePanel != null) {
			euclidianForPlanePanel.setLabels();
		}

	}
	
	@Override
	public void updateFonts() {
		
		if (isIniting) {
			return;
		}

		super.updateFonts();
		
		if (euclidianPanel3D != null) {
			euclidianPanel3D.updateFont();
		}

		if (euclidianForPlanePanel != null) {
			euclidianForPlanePanel.updateFont();
		}
		
	}
	
	@Override
	protected PropertiesStyleBarD newPropertiesStyleBar() {
		return new PropertiesStyleBar3DD(this, (AppD) app);
	}

	@Override
	public void updatePanelGUI(int id) {
		if (id == App.VIEW_EUCLIDIAN3D && euclidianPanel3D != null) {
			euclidianPanel3D.updateGUI();
		} else {
			super.updatePanelGUI(id);
		}
	}

}
