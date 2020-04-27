package org.geogebra.common.jre.main;

import java.util.ArrayList;
import java.util.Collection;

import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

public class TemplateHelper {
	private final Kernel kernel;
	private App app;

	/**
	 * @param app target app
	 */
	public TemplateHelper(App app) {
		this.app = app;
		this.kernel = app.getKernel();
	}

	/**
	 * Copy settings and defaults from another app
	 * @param templateApp other app
	 */
	public void applyTemplate(App templateApp) {
		app.setLabelingStyle(templateApp.getLabelingStyle());
		setConstructionDefaults(templateApp.getKernel());
		setAllGeoStylesFromDefaults();
		kernel.updateConstruction(false);
	}

	/**
	 * Use construction defaults to style current geos
	 */
	private void setAllGeoStylesFromDefaults() {
		ArrayList<GeoElement> selected = app.getSelectionManager()
				.getSelectedGeos();

		Collection<GeoElement> target;
		if (selected.isEmpty()) {
			target = kernel.getConstruction().getGeoSetWithCasCellsConstructionOrder();
		} else {
			target = selected;
		}
		ConstructionDefaults objectDefaults = kernel.getConstruction()
				.getConstructionDefaults();

		for (GeoElement actual: target) {
			boolean oldLabelVisible = actual.isLabelVisible();
			objectDefaults.setDefaultVisualStyles(actual, false, false, false);
			// label visibility is tricky because of labeling options: safer to keep old value
			actual.setLabelVisible(oldLabelVisible);
		}
	}

	private void setConstructionDefaults(Kernel otherKernel) {
		kernel.getConstruction().getConstructionDefaults().setConstructionDefaults(
				otherKernel.getConstruction().getConstructionDefaults());
	}
}
