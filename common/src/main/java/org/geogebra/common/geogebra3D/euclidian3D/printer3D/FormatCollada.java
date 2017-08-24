package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

import java.util.ArrayList;

/**
 * OpenSCAD format
 */
public class FormatCollada implements Format {
	
	private ArrayList<String> labels;

	@Override
	public void getExtension(StringBuilder sb) {
		sb.append(".dae");
	}

	@Override
	public void getScriptStart(StringBuilder sb) {
		if (labels == null) {
			labels = new ArrayList<String>();
		} else {
			labels.clear();
		}
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		sb.append("\n<COLLADA xmlns=\"http://www.collada.org/2005/11/COLLADASchema\" version=\"1.4.1\">");
		sb.append("\n  <asset>");
		sb.append("\n    <contributor>");
		sb.append("\n      <authoring_tool>GeoGebra</authoring_tool>");
		sb.append("\n    </contributor>");
		sb.append("\n    <unit name=\"meter\" meter=\"1\"/>");
		sb.append("\n    <up_axis>Z_UP</up_axis>");
		sb.append("\n  </asset>");
		sb.append("\n  <library_lights>");
		sb.append("\n    <light id=\"Lamp-light\" name=\"Lamp\">");
		sb.append("\n      <technique_common>");
		sb.append("\n        <point>");
		sb.append("\n          <color sid=\"color\">1 1 1</color>");
		sb.append("\n          <constant_attenuation>1</constant_attenuation>");
		sb.append("\n          <linear_attenuation>0</linear_attenuation>");
		sb.append("\n          <quadratic_attenuation>0.00111109</quadratic_attenuation>");
		sb.append("\n        </point>");
		sb.append("\n      </technique_common>");
		sb.append("\n    </light>");
		sb.append("\n  </library_lights>");
		sb.append("\n  <library_geometries>");
	}

	@Override
	public void getScriptEnd(StringBuilder sb) {
		sb.append("\n  </library_geometries>");
		sb.append("\n  <library_controllers/>");
		sb.append("\n  <library_visual_scenes>");
		sb.append("\n    <visual_scene id=\"Scene\" name=\"Scene\">");
		// light
		sb.append("\n      <node id=\"Lamp\" name=\"Lamp\" type=\"NODE\">");
		sb.append("\n        <matrix sid=\"transform\">-0.2908646 -0.7711008 0.5663932 4.076245 0.9551712 -0.1998834 0.2183912 1.005454 -0.05518906 0.6045247 0.7946723 5.903862 0 0 0 1</matrix>");
		sb.append("\n        <instance_light url=\"#Lamp-light\"/>");
		sb.append("\n      </node>");
		// geometries
		for (String label: labels) {
			sb.append("\n      <node id=\"");
			sb.append(label);
			sb.append("\" name=\"");
			sb.append(label);
			sb.append("\" type=\"NODE\">");
			sb.append("\n        <instance_geometry url=\"#");
			sb.append(label);
			sb.append("-mesh\" name=\"");
			sb.append(label);
			sb.append("\"/>");
			sb.append("\n      </node>");
		}
		
		sb.append("\n    </visual_scene>");
		sb.append("\n  </library_visual_scenes>");
		sb.append("\n  <scene>");
		sb.append("\n    <instance_visual_scene url=\"#Scene\"/>");
		sb.append("\n  </scene>");
		sb.append("\n</COLLADA>");
	}
	
	private String currentLabel = "";

	@Override
	public void getObjectStart(StringBuilder sb, String type, String label) {
		currentLabel = label;
		labels.add(currentLabel);
	}

	@Override
	public void getPolyhedronStart(StringBuilder sb) {
		sb.append("\n    <geometry id=\"");
		sb.append(currentLabel);
		sb.append("-mesh\" name=\"");
		sb.append(currentLabel);
		sb.append("\">");
		sb.append("\n      <mesh>");
	}

	@Override
	public void getPolyhedronEnd(StringBuilder sb) {
		sb.append("\n      </mesh>");
		sb.append("\n    </geometry>");
	}
	
	private int currentCount = 0;
	
	private void getArrayStart(StringBuilder sb, int count, String type) {
		currentCount = count;
		sb.append("\n        <source id=\"");
		sb.append(currentLabel);
		sb.append("-mesh-");
		sb.append(type);
		sb.append("\">");
		
		sb.append("\n          <float_array id=\"");
		sb.append(currentLabel);
		sb.append("-mesh-");
		sb.append(type);
		sb.append("-array\" count=\"");
		sb.append(count * 3);
		sb.append("\">");
	}

	@Override
	public void getVerticesStart(StringBuilder sb, int count) {
		getArrayStart(sb, count, "positions");
	}

	@Override
	public void getVertices(StringBuilder sb, double x, double y, double z) {
		sb.append(x);
		sb.append(" ");
		sb.append(y);
		sb.append(" ");
		sb.append(z);
	}

	@Override
	public void getVerticesSeparator(StringBuilder sb) {
		sb.append(" ");
	}
	
	private void getArrayEnd(StringBuilder sb, String type) {
		sb.append("</float_array>");
		sb.append("\n          <technique_common>");
		sb.append("\n            <accessor source=\"#");
		sb.append(currentLabel);
		sb.append("-mesh-");
		sb.append(type);
		sb.append("-array\" count=\"");
		
		sb.append(currentCount);
		sb.append("\" stride=\"3\">");
		sb.append("\n              <param name=\"X\" type=\"float\"/>");
		sb.append("\n              <param name=\"Y\" type=\"float\"/>");
		sb.append("\n              <param name=\"Z\" type=\"float\"/>");
		sb.append("\n            </accessor>");
		sb.append("\n          </technique_common>");
		sb.append("\n        </source>");
		
	}

	@Override
	public void getVerticesEnd(StringBuilder sb) {
		getArrayEnd(sb, "positions");
	}

	@Override
	public void getFacesStart(StringBuilder sb, int count) {
		sb.append("\n        <vertices id=\"");
		sb.append(currentLabel);
		sb.append("-mesh-vertices\">");
		sb.append("\n          <input semantic=\"POSITION\" source=\"#");
		sb.append(currentLabel);
		sb.append("-mesh-positions\"/>");
		sb.append("\n        </vertices>");
		sb.append("\n        <polylist count=\"");
		sb.append(count);
		sb.append("\">");
		sb.append("\n          <input semantic=\"VERTEX\" source=\"#");
		sb.append(currentLabel);
		sb.append("-mesh-vertices\" offset=\"0\"/>");
		sb.append("\n          <input semantic=\"NORMAL\" source=\"#");
		sb.append(currentLabel);
		sb.append("-mesh-normals\" offset=\"1\"/>");
		sb.append("\n          <vcount>");
		for (int i = 0; i < count; i++) {
			sb.append("3 ");
		}
		sb.append("</vcount>");
		sb.append("\n          <p>");
	}

	@Override
	public void getFaces(StringBuilder sb, int v1, int v2, int v3, int normal) {
		appendIndex(sb, v1, normal);
		sb.append(" ");
		appendIndex(sb, v2, normal);
		sb.append(" ");
		appendIndex(sb, v3, normal);
	}

	private void appendIndex(StringBuilder sb, int index, int normal) {
		sb.append(index);
		sb.append(" ");
		if (normal == -1) {
			sb.append(index);
		} else {
			sb.append(normal);
		}
	}

	@Override
	public void getFacesSeparator(StringBuilder sb) {
		sb.append(" ");
	}

	@Override
	public void getFacesEnd(StringBuilder sb) {
		sb.append("</p>");
		sb.append("\n        </polylist>");
	}

	@Override
	public void getListType(StringBuilder sb, int type) {
		sb.append("\n  g ");
		sb.append(type);
	}

	@Override
	public void getNormalsStart(StringBuilder sb, int count) {
		getArrayStart(sb, count, "normals");
	}

	@Override
	public void getNormal(StringBuilder sb, double x, double y, double z) {
		sb.append(x);
		sb.append(" ");
		sb.append(y);
		sb.append(" ");
		sb.append(z);
	}

	@Override
	public void getNormalsSeparator(StringBuilder sb) {
		sb.append(" ");
	}

	@Override
	public void getNormalsEnd(StringBuilder sb) {
		getArrayEnd(sb, "normals");
	}

	@Override
	public boolean handlesSurfaces() {
		return true;
	}

	@Override
	public boolean needsClosedObjects() {
		return false;
	}

	@Override
	public boolean handlesNormals() {
		return true;
	}

}
