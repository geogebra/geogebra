package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

import java.util.ArrayList;
import java.util.HashMap;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * OpenSCAD format
 */
public class FormatCollada implements Format {
	
	private static float AMBIENT = Renderer.AMBIENT_0;
	private ArrayList<IdColor> idColors;

	private HashMap<Integer, GColor> materials;

	private HashMap<String, Integer> labels;
	private String currentLabel = "";
	private int currentCount = 0;
	private GColor currentColor;

	private StringBuilder sb2 = new StringBuilder();

	private static class IdColor {
		public String id;
		public GColor color;

		public IdColor(String id, GColor color) {
			this.id = id;
			this.color = color;
		}
	}

	@Override
	public String getExtension() {
		return "dae";
	}

	@Override
	public void getScriptStart(StringBuilder sb) {
		if (idColors == null) {
			idColors = new ArrayList<>();
		}
		if (materials == null) {
			materials = new HashMap<>();
		} 
		if (labels == null) {
			labels = new HashMap<>();
		}
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		sb.append("\n<COLLADA xmlns=\"http://www.collada.org/2005/11/COLLADASchema\" version=\"1.5\">");
		sb.append("\n  <asset>");
		sb.append("\n    <contributor>");
		sb.append("\n      <authoring_tool>GeoGebra</authoring_tool>");
		sb.append("\n    </contributor>");
		sb.append("\n    <unit name=\"centimeter\" meter=\"0.01\"/>");
		sb.append("\n    <up_axis>Z_UP</up_axis>");
		sb.append("\n  </asset>");
		sb.append("\n  <library_lights>");
		sb.append("\n    <light id=\"L_dir\" name=\"Directional\">");
		sb.append("\n      <technique_common>");
		sb.append("\n        <directional>");
		sb.append("\n          <color sid=\"color\">1 1 1</color>");
		sb.append("\n        </directional>");
		sb.append("\n      </technique_common>");
		sb.append("\n    </light>");
		sb.append("\n    <light id=\"L_amb\" name=\"Ambient\">");
		sb.append("\n      <technique_common>");
		sb.append("\n        <ambient>");
		sb.append("\n          <color sid=\"color\">");
		sb.append(AMBIENT);
		sb.append(" ");
		sb.append(AMBIENT);
		sb.append(" ");
		sb.append(AMBIENT);
		sb.append("</color>");
		sb.append("\n        </ambient>");
		sb.append("\n      </technique_common>");
		sb.append("\n    </light>");
		sb.append("\n  </library_lights>");
		sb.append("\n  <library_geometries>");
	}

	@Override
	public void getScriptEnd(StringBuilder sb) {
		sb.append("\n  </library_geometries>");
		// materials
		sb.append("\n  <library_effects>");
		for (GColor color : materials.values()) {
			sb.append("\n    <effect id=\"");
			getMaterial(sb, color);
			sb.append("-effect\">");
			sb.append("\n      <profile_COMMON>");
			sb.append("\n        <technique sid=\"common\">");
			sb.append("\n          <phong>");
			sb.append("\n            <ambient>");
			sb.append("\n              <color sid=\"ambient\">0 0 0 1.0</color>");
			sb.append("\n            </ambient>");
			sb.append("\n            <diffuse>");
			sb.append("\n              <color sid=\"diffuse\">");
			sb.append(color.getRed() / 255.0);
			sb.append(" ");
			sb.append(color.getGreen() / 255.0);
			sb.append(" ");
			sb.append(color.getBlue() / 255.0);
			sb.append(" ");
			sb.append(color.getAlpha() / 255.0);
			sb.append("</color>");
			sb.append("\n            </diffuse>");
			sb.append("\n            <specular>");
			sb.append("\n              <color sid=\"specular\">0.5 0.5 0.5 1</color>");
			sb.append("\n            </specular>");
			sb.append("\n            <shininess>");
			sb.append("\n              <float sid=\"shininess\">50</float>");
			sb.append("\n            </shininess>");
			sb.append("\n            <index_of_refraction>");
			sb.append("\n             <float sid=\"index_of_refraction\">1</float>");
			sb.append("\n            </index_of_refraction>");
			sb.append("\n          </phong>");
			sb.append("\n        </technique>");
			sb.append("\n      </profile_COMMON>");
			sb.append("\n    </effect>");
		}
		sb.append("\n  </library_effects>");
		sb.append("\n  <library_materials>");
		for (GColor color : materials.values()) {
			sb2.setLength(0);
			getMaterial(sb2, color);
			sb.append("\n    <material id=\"");
			sb.append(sb2);
			sb.append("-material\" name=\"");
			sb.append(sb2);
			sb.append("\">");
			sb.append("\n      <instance_effect url=\"#");
			sb.append(sb2);
			sb.append("-effect\"/>");
			sb.append("\n    </material>");
		}
		sb.append("\n  </library_materials>");
		// scene
		sb.append("\n  <library_controllers/>");
		sb.append("\n  <library_visual_scenes>");
		sb.append("\n    <visual_scene id=\"Scene\" name=\"Scene\">");
		// light
		sb.append("\n      <node id=\"L_Dir\" name=\"Directional\" type=\"NODE\">");
		sb.append("\n        <matrix sid=\"transform\">");
		sb.append("-0.70711 0 0.70711 0  0 1 0 0  0.70711 0 0.70711 0  0 0 0 1</matrix>");
		sb.append("\n        <instance_light url=\"#L_dir\"/>");
		sb.append("\n      </node>");
		sb.append("\n      <node id=\"L_Amb\" name=\"Ambient\" type=\"NODE\">");
		sb.append("\n        <instance_light url=\"#L_amb\"/>");
		sb.append("\n      </node>");
		// geometries
		for (IdColor idColor : idColors) {
			String label = idColor.id;
			sb.append("\n      <node id=\"");
			sb.append(label);
			sb.append("\" name=\"");
			sb.append(label);
			sb.append("\" type=\"NODE\">");
			sb.append("\n        <instance_geometry url=\"#");
			sb.append(label);
			sb.append("-mesh\" name=\"");
			sb.append(label);
			sb.append("\">");
			sb.append("\n          <bind_material>");
			sb.append("\n            <technique_common>");
			sb2.setLength(0);
			getMaterial(sb2, idColor.color);
			sb.append("\n              <instance_material symbol=\"");
			sb.append(sb2);
			sb.append("-material\" target=\"#");
			sb.append(sb2);
			sb.append("-material\"/>");
			sb.append("\n            </technique_common>");
			sb.append("\n          </bind_material>");
			sb.append("\n        </instance_geometry>");
			sb.append("\n      </node>");
		}

		sb.append("\n    </visual_scene>");
		sb.append("\n  </library_visual_scenes>");
		sb.append("\n  <scene>");
		sb.append("\n    <instance_visual_scene url=\"#Scene\"/>");
		sb.append("\n  </scene>");
		sb.append("\n</COLLADA>");
		
		idColors.clear();
		materials.clear();
		labels.clear();
	}
		
	static private void getMaterial(StringBuilder sb, GColor color) {
		sb.append("M_");
		sb.append(color.getRed());
		sb.append("_");
		sb.append(color.getGreen());
		sb.append("_");
		sb.append(color.getBlue());
		sb.append("_");
		sb.append(color.getAlpha());
	}

	@Override
	public void getObjectStart(StringBuilder sb, String type, GeoElement geo, boolean transparency,
			GColor color, double alpha) {
		currentLabel = geo.getLabelSimple();
		Integer n = labels.get(currentLabel);
		if (n != null) {
			// we need a new label
			labels.put(currentLabel, n + 1);
			currentLabel = currentLabel + "_" + n;
		} else {
			// if needed, second time we'll use label_2 instead of label
			labels.put(currentLabel, 2);
		}
		GColor c = color == null ? geo.getObjectColor() : color;
		if (transparency) {
			currentColor = c.deriveWithAlpha((int) (alpha * 255));
		} else {
			currentColor = c;
		}
		materials.put(currentColor.hashCode(), currentColor);
		idColors.add(new IdColor(currentLabel, currentColor));
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
	public void getFacesStart(StringBuilder sb, int count, boolean hasSpecificNormals) {
		sb.append("\n        <vertices id=\"");
		sb.append(currentLabel);
		sb.append("-mesh-vertices\">");
		sb.append("\n          <input semantic=\"POSITION\" source=\"#");
		sb.append(currentLabel);
		sb.append("-mesh-positions\"/>");
		sb.append("\n        </vertices>");
		sb.append("\n        <triangles material=\"");
		getMaterial(sb, currentColor);
		sb.append("-material\" count=\"");
		sb.append(count);
		sb.append("\">");
		sb.append("\n          <input semantic=\"VERTEX\" source=\"#");
		sb.append(currentLabel);
		sb.append("-mesh-vertices\" offset=\"0\"/>");
		sb.append("\n          <input semantic=\"NORMAL\" source=\"#");
		sb.append(currentLabel);
		if (hasSpecificNormals) {
			sb.append("-mesh-normals\" offset=\"1\"/>");
		} else {
			sb.append("-mesh-normals\" offset=\"0\"/>");
		}
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

	private static void appendIndex(StringBuilder sb, int index, int normal) {
		sb.append(index);
		if (normal != -1) {
			sb.append(" ");
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
		sb.append("\n        </triangles>");
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
