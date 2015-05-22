package org.geogebra.common.main.settings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.SensorLogger.Types;

/**
 * Settings for the data collection view.
 */
public class DataCollectionSettings extends AbstractSettings {

	// public class Defaults {
	// // timeout in ms
	// public static final int TIMEOUT = 100;
	// }
	//
	// private int timeout = Defaults.TIMEOUT;

	private GDimension preferredSize;

	public DataCollectionSettings(LinkedList<SettingListener> listeners) {
		super(listeners);
		preferredSize = AwtFactory.prototype.newDimension(0, 0);
	}

	public DataCollectionSettings() {
		super();
		preferredSize = AwtFactory.prototype.newDimension(0, 0);
	}

	// Object can be String or GeoElement
	// Strings needed in case before construction loaded
	// GeoElements needed later in case geo is renamed
	private HashMap<Types, Object> mapper = new HashMap<Types, Object>();

	/**
	 * called from loading a saved material
	 * 
	 * @param type
	 *            eg Types.ACCELEROMETER_X
	 * @param label
	 *            GeoElement's label
	 */
	public void mapSensorToGeo(Types type, String label) {
		mapper.put(type, label);
		settingChanged();
	}

	public void mapSensorToGeo(Types type, GeoElement geo) {
		mapper.put(type, geo);
	}

	/**
	 * @param type
	 *            eg Types.ACCELEROMETER_X
	 * @param cons
	 *            cons
	 * @return GeoElement (can be null)
	 */
	public GeoElement getGeoMappedToSensor(Types type, Construction cons) {
		Object geoObj =  mapper.get(type);
		
		GeoElement geo = null;
		
		if (geoObj instanceof String) {
			geo = cons.lookupLabel((String)geoObj);
			// faster lookup next time
			if (geo != null) {
				mapper.put(type, geo);
			}
		} else if (geoObj instanceof GeoElement) {
			geo = (GeoElement) geoObj;
		}
		
		return geo;
	}
	// ============================================
	// PreferredSize Settings
	// ============================================
	/**
	 * @return the preferredSize
	 */
	public GDimension preferredSize() {
		return preferredSize;
	}

	/**
	 * @param preferredSize
	 *            the preferredSize to set
	 */
	public void setPreferredSize(GDimension preferredSize) {
		if (this.preferredSize == null
				|| !this.preferredSize.equals(preferredSize)) {
			this.preferredSize = preferredSize;
			settingChanged();
		}
	}

	// ============================================
	// XML
	// ============================================

	/**
	 * returns settings in XML format
	 */
	public void getXML(StringBuilder sb, boolean asPreference, Construction cons) {

		sb.append("<dataCollectionView>\n");

		GDimension size = preferredSize();
		int width = size.getWidth();
		int height = size.getHeight();


		// sb.append("\t<size ");
		// if (width != 0) {
		// sb.append(" width=\"");
		// sb.append(width);
		// sb.append("\"");
		// }
		//
		// if (height != 0) {
		// sb.append(" height=\"");
		// sb.append(height);
		// sb.append("\"");
		// }
		// sb.append("/>\n");


		// if (timeout != Defaults.TIMEOUT) {
		// sb.append("\t<timeout ");
		// sb.append(" value=\"");
		// sb.append(timeout);
		// sb.append("\"");
		// sb.append("/>\n");
		// }

		// for testing only, REMOVE
		// mapper.put(Types.ACCELEROMETER_X, "testGeoLabel");

		Set<Types> types = mapper.keySet();
		Iterator<Types> it = types.iterator();
		while (it.hasNext()) {
			Types type = it.next();
			if (type.storeInXML()) {
				GeoElement geo = getGeoMappedToSensor(type, cons);
				if (geo != null) {
					sb.append("\t<");
					sb.append(type.toXMLString());
					sb.append(" geo=\"");
					sb.append(geo.getLabelSimple());
					sb.append("\"");
					sb.append("/>\n");
				}

			}
		}

		sb.append("</dataCollectionView>\n");

	}

	public void removeMappedGeo(Types type) {
		this.mapper.remove(type);
	}
}
