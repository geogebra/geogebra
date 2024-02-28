package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;

public class EuclidianStyleBarSelection {
	private final App app;
	private final EuclidianController ec;
	private final HashMap<Integer, Integer> defaultGeoMap;
	private ArrayList<GeoElement> defaultGeos;
	private GeoElement oldDefaultGeo;
	private Integer oldDefaultMode;

	/**
	 * @param app application
	 * @param ec controller
	 */
	public EuclidianStyleBarSelection(App app, EuclidianController ec) {
		defaultGeos = new ArrayList<>();
		defaultGeoMap = EuclidianStyleBarStatic.createDefaultMap();
		this.app = app;
		this.ec = ec;
	}

	/**
	 * Reset stored default geo from construction defaults
	 */
	public void restoreDefaultGeoFromConstruction() {
		if (oldDefaultGeo != null) {
			oldDefaultGeo = getDefaults()
					.getDefaultGeo(oldDefaultMode);
		}
	}

	/**
	 * Update construction defaults using previously stored geo
	 */
	public void restoreConstructionDefaults() {
		if (oldDefaultGeo != null) {
			// add oldDefaultGeo to the default map so that the old default
			// is restored
			getDefaults().addDefaultGeo(oldDefaultMode,
					oldDefaultGeo);
			oldDefaultGeo = null;
			oldDefaultMode = null;
		}
	}

	/**
	 * Store default geo for current mode
	 * @param mode app mode
	 */
	public void storeConstructionDefaults(int mode) {
		Integer type = getDefaultType(mode);
		if (defaultGeos.size() == 0) {
			oldDefaultGeo = null;
			oldDefaultMode = -1;
		} else {
			oldDefaultGeo = defaultGeos.get(0);
			oldDefaultMode = type;
		}
	}

	/**
	 * @return default geos for current mode (can be multiple for points)
	 */
	public ArrayList<GeoElement> getDefaultGeos() {
		return this.defaultGeos;
	}

	/**
	 * @return map mode -> default geo type
	 */
	public HashMap<Integer, Integer> getDefaultMap() {
		return defaultGeoMap;
	}

	/**
	 * Rebuild default geos for given mode
	 * @param mode app mode
	 */
	public void updateDefaultsForMode(int mode) {
		defaultGeos = new ArrayList<>();
		if (EuclidianView.isPenMode(mode)) {
			GeoElement geo = ec.getPen().getDefaultPenLine();
			if (geo != null) {
				defaultGeos.add(geo);
			}
			return;
		}
		Integer type = getDefaultType(mode);
		if (type.equals(
				ConstructionDefaults.DEFAULT_POINT_ALL_BUT_COMPLEX)) {
			// add all non-complex default points
			defaultGeos.add(getDefaults().getDefaultGeo(
					ConstructionDefaults.DEFAULT_POINT_FREE));
			defaultGeos.add(getDefaults().getDefaultGeo(
					ConstructionDefaults.DEFAULT_POINT_ON_PATH));
			defaultGeos.add(getDefaults().getDefaultGeo(
					ConstructionDefaults.DEFAULT_POINT_IN_REGION));
			defaultGeos.add(getDefaults().getDefaultGeo(
					ConstructionDefaults.DEFAULT_POINT_DEPENDENT));
		} else {
			GeoElement geo = getDefaults().getDefaultGeo(type);
			if (geo != null) {
				defaultGeos.add(geo);
			}
		}
	}

	/**
	 * @return list of target geos for stylebar (selected / default / just created)
	 */
	public ArrayList<GeoElement> getGeos() {
		ArrayList<GeoElement> targetGeos = new ArrayList<>(ec.getJustCreatedGeos());
		if (!EuclidianConstants.isMoveOrSelectionMode(ec.getMode())) {
			targetGeos.addAll(defaultGeos);
			Previewable p = ec.getView().getPreviewDrawable();
			if (p != null) {
				GeoElement geo = p.getGeoElement();
				if (geo != null) {
					targetGeos.add(geo);
				}
			}
		} else if (app.getSelectionManager().getFocusedGroupElement() != null) {
			targetGeos.add(app.getSelectionManager().getFocusedGroupElement());
		} else {
			targetGeos.addAll(app.getSelectionManager().getSelectedGeos());
		}
		return targetGeos;
	}

	private ConstructionDefaults getDefaults() {
		return app.getKernel().getConstruction().getConstructionDefaults();
	}

	private Integer getDefaultType(int mode) {
		ArrayList<GeoElement> justCreatedGeos = ec.getJustCreatedGeos();
		Integer type = defaultGeoMap.get(mode);
		if (Objects.equals(type, ConstructionDefaults.DEFAULT_POINT_ALL_BUT_COMPLEX)
				&& justCreatedGeos.size() == 1) {
			GeoElement justCreated = justCreatedGeos.get(0);
			if (justCreated.isGeoPoint()) {
				// get default type regarding what type of point has been
				// created
				if (((GeoPointND) justCreated).isPointOnPath()) {
					type = ConstructionDefaults.DEFAULT_POINT_ON_PATH;
				} else if (((GeoPointND) justCreated).hasRegion()) {
					type = ConstructionDefaults.DEFAULT_POINT_IN_REGION;
				} else if (!((GeoPointND) justCreated).isIndependent()) {
					type = ConstructionDefaults.DEFAULT_POINT_DEPENDENT;
				} else {
					type = ConstructionDefaults.DEFAULT_POINT_FREE;
				}
			}
		}
		return type;
	}
}