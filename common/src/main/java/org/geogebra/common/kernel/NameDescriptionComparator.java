package org.geogebra.common.kernel;

import java.util.Comparator;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Compares GeoElements by name and description alphabetically
 * 
 * @version 2010-06-14 Last change: generic Object replaced by GeoElement
 *          (Zbynek Konecny)
 */
public class NameDescriptionComparator implements Comparator<GeoElement> {
	public int compare(GeoElement geo1, GeoElement geo2) {
		if (geo1 == null)
			return -1;
		else if (geo2 == null)
			return 1;
		else
			return geo1.getNameDescription().compareTo(
					geo2.getNameDescription());
	}
}
