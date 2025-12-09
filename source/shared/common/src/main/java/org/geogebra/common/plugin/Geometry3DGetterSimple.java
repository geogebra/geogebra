/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.plugin;

import java.util.HashMap;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Simple geometry getter with 4 lines for each geometry type:
 * 
 * ## name (if filtered to one geo export)
 * 
 * # type
 * 
 * v x y z x y z ... (vertices)
 * 
 * n x y z x y z ... (normals)
 * 
 * c r g b a r g b a ... (colors)
 * 
 * t i1 i2 i3 ... (triangles indices)
 *
 */
public class Geometry3DGetterSimple implements Geometry3DGetter {

	private HashMap<GeometryType, GeometryStringBuilders> geometryStringsMap;
	private GeometryStringBuilders currentGeometryStrings;
	private String exportName;
	private boolean filterGeoNames;

	private static class GeometryStringBuilders {
		public StringBuilder vsb;
		public StringBuilder nsb;
		public StringBuilder csb;
		public StringBuilder tsb;
		public GeometryType type;
		public int index;
		public int nextShift;

		public GeometryStringBuilders(GeometryType type) {
			this.type = type;
			index = 0;
			nextShift = 0;
			vsb = new StringBuilder("v");
			nsb = new StringBuilder("n");
			csb = new StringBuilder("c");
			tsb = new StringBuilder("t");
		}
	}

	/**
	 * constructor
	 * 
	 * @param name
	 *            geo name to filter; set it to "" if no filtering
	 */
	public Geometry3DGetterSimple(String name) {
		geometryStringsMap = new HashMap<>();
		exportName = name;
		if (exportName != null && !"".equals(exportName)) {
			filterGeoNames = true;
		} else {
			filterGeoNames = false;
		}
	}

	@Override
	public boolean handles(GeoElement geo, GeometryType type) {
		if (filterGeoNames) {
			boolean ret = exportName.equals(geo.getLabelSimple());
			return ret;
		}
		return true;
	}

	@Override
	public void startGeometry(GeometryType type) {
		currentGeometryStrings = geometryStringsMap.get(type);
		if (currentGeometryStrings == null) {
			currentGeometryStrings = new GeometryStringBuilders(type);
			geometryStringsMap.put(type, currentGeometryStrings);
		} else {
			currentGeometryStrings.index += currentGeometryStrings.nextShift;
			currentGeometryStrings.nextShift = 0;
		}
	}

	@Override
	public void addVertexNormalColor(double x, double y, double z, double nx,
			double ny, double nz, double r, double g, double b, double a) {
		currentGeometryStrings.vsb.append(" ");
		currentGeometryStrings.vsb.append(x);
		currentGeometryStrings.vsb.append(" ");
		currentGeometryStrings.vsb.append(y);
		currentGeometryStrings.vsb.append(" ");
		currentGeometryStrings.vsb.append(z);
	
		currentGeometryStrings.nsb.append(" ");
		currentGeometryStrings.nsb.append(nx);
		currentGeometryStrings.nsb.append(" ");
		currentGeometryStrings.nsb.append(ny);
		currentGeometryStrings.nsb.append(" ");
		currentGeometryStrings.nsb.append(nz);
		
		currentGeometryStrings.csb.append(" ");
		currentGeometryStrings.csb.append(r);
		currentGeometryStrings.csb.append(" ");
		currentGeometryStrings.csb.append(g);
		currentGeometryStrings.csb.append(" ");
		currentGeometryStrings.csb.append(b);
		currentGeometryStrings.csb.append(" ");
		currentGeometryStrings.csb.append(a);

		currentGeometryStrings.nextShift++;
	}

	@Override
	public void addTriangle(int v1, int v2, int v3) {
		currentGeometryStrings.tsb.append(" ");
		appendIndex(currentGeometryStrings.tsb, v1);
		currentGeometryStrings.tsb.append(" ");
		appendIndex(currentGeometryStrings.tsb, v2);
		currentGeometryStrings.tsb.append(" ");
		appendIndex(currentGeometryStrings.tsb, v3);
	}

	private void appendIndex(StringBuilder sb, int i) {
		sb.append(i + currentGeometryStrings.index);
	}

	/**
	 * 
	 * @return result
	 */
	public StringBuilder get() {
		StringBuilder sb = new StringBuilder();
		if (filterGeoNames) {
			sb.append("## ");
			sb.append(exportName);
			sb.append("\n");
		}
		boolean notFirst = false;
		for (GeometryStringBuilders geometryStrings : geometryStringsMap
				.values()) {
			if (notFirst) {
				sb.append("\n");
			} else {
				notFirst = true;
			}
			sb.append("# ");
			sb.append(geometryStrings.type.name);
			sb.append("\n");
			sb.append(geometryStrings.vsb);
			sb.append("\n");
			sb.append(geometryStrings.nsb);
			sb.append("\n");
			sb.append(geometryStrings.csb);
			sb.append("\n");
			sb.append(geometryStrings.tsb);
		}
		return sb;
	}

}
