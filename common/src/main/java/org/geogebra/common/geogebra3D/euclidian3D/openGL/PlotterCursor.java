package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Class that describes the geometry of the 3D cursor
 * 
 * @author ggb3D
 * 
 */
public class PlotterCursor {

	/** Cursor type */
	public enum Type {
		/** 2D cross preview for e.g. point on plane */
		CROSS2D(0, false, false),
		/** diamond preview for intersection point */
		DIAMOND(1, false, false),
		/** "cylinder" preview for e.g point on segment */
		CYLINDER(2, false, false),
		/** 3D cross preview for point in space */
		CROSS3D(3, false, false),
		/** horizontal arrowed cross when over an existing point */
		ALREADY_XY(4, true, true),
		/** vertical arrows when over an existing point */
		ALREADY_Z(5, true, true),
		/** 3 directions arrows when over an existing point */
		ALREADY_XYZ(6, true, true),
		/** cube displayed when moving drawing pad */
		CUBE(7, true, true),
		/** sphere for target */
		SPHERE(8, false, true),
		/** circle for target */
		TARGET_CIRCLE(9, false, true),
		/** for rotations */
		ROTATION(10, true, true);

		private int id;
		private boolean useLight;
		private boolean hasAllGLAttributes;

		private Type(int id, boolean useLight, boolean hasAllGLAttributes) {
			this.id = id;
			this.useLight = useLight;
			this.hasAllGLAttributes = hasAllGLAttributes;
		}

		/**
		 * 
		 * @return true if light is used for this cursor
		 */
		public boolean useLight() {
			return useLight;
		}

		/**
		 * 
		 * @return true if is a preview cursor
		 */
		public boolean isPreview() {
			return !hasAllGLAttributes();
		}

		/**
		 * 
		 * @return true if it as all GL attributes (position, color, normal,
		 *         texture)
		 */
		public boolean hasAllGLAttributes() {
			return hasAllGLAttributes;
		}

		/**
		 * 
		 * @return cursor id
		 */
		int getId() {
			return id;
		}
	}

	static private int TYPE_LENGTH = 11;

	static private float size = 12f;
	static private float thickness = 1.25f;
	static private float thickness2 = 1.25f;
	static private float depth = 1f;

	static private float size_start_move = 7f;
	static private float size_move = 40f;
	static private float thickness3 = 2 * thickness;

	static private float size_cube = size_start_move;

	static private float TARGET_DOT_ALPHA = 0.87f;

	static private float TARGET_CIRCLE_THICKNESS = EuclidianStyleConstants.DEFAULT_LINE_THICKNESS
			* PlotterBrush.LINE3D_THICKNESS / 2f;
	static private float TARGET_CIRCLE_RADIUS = 50f;
	static private float TARGET_CIRCLE_ALPHA = 0.38f;

	private int[] index;

	private Manager manager;

	private float r;
	private float g;
	private float b;
	private float a;

	private float nx;
	private float ny;
	private float nz;

	/**
	 * common constructor
	 * 
	 * @param manager
	 *            geometry manager
	 */
	public PlotterCursor(Manager manager) {

		this.manager = manager;

		manager.setScalerIdentity();

		index = new int[TYPE_LENGTH];

		// crosses
		for (int i = 0; i < 4; i++) {
			index[i] = manager.startNewList(-1, true);
			manager.startGeometry(Manager.Type.TRIANGLES);
			cursor(i);
			manager.endGeometry();
			manager.endList();
		}

		// moving cursors
		PlotterBrush brush = manager.getBrush();

		brush.setArrowType(PlotterBrush.ARROW_TYPE_SIMPLE);

		// sets the thickness for arrows
		brush.setThickness(1, 1f);

		brush.setAffineTexture(0.5f, 0.125f);

		// xy
		brush.start(-1);
		brush.setColor(GColor.GRAY);
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(size_start_move, 0, 0, 1),
				new Coords(size_move, 0, 0, 1));
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(-size_start_move, 0, 0, 1),
				new Coords(-size_move, 0, 0, 1));
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(0, size_start_move, 0, 1),
				new Coords(0, size_move, 0, 1));
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(0, -size_start_move, 0, 1),
				new Coords(0, -size_move, 0, 1));
		index[Type.ALREADY_XY.getId()] = brush.end();

		// z
		brush.start(-1);
		brush.setColor(GColor.GRAY);
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(0, 0, size_start_move, 1),
				new Coords(0, 0, size_move, 1));
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(0, 0, -size_start_move, 1),
				new Coords(0, 0, -size_move, 1));
		index[Type.ALREADY_Z.getId()] = brush.end();

		// xyz
		brush.start(-1);
		brush.setColor(GColor.GRAY);
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(size_start_move, 0, 0, 1),
				new Coords(size_move, 0, 0, 1));
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(-size_start_move, 0, 0, 1),
				new Coords(-size_move, 0, 0, 1));
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(0, size_start_move, 0, 1),
				new Coords(0, size_move, 0, 1));
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(0, -size_start_move, 0, 1),
				new Coords(0, -size_move, 0, 1));
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(0, 0, size_start_move, 1),
				new Coords(0, 0, size_move, 1));
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(0, 0, -size_start_move, 1),
				new Coords(0, 0, -size_move, 1));
		index[Type.ALREADY_XYZ.getId()] = brush.end();

		brush.setArrowType(PlotterBrush.ARROW_TYPE_NONE);

		// cube
		index[Type.CUBE.getId()] = manager.startNewList(-1, true);
		manager.startGeometry(Manager.Type.TRIANGLES);
		color(0.5f, 0.5f, 0.5f);
		// up
		normal(0, 0, 1);
		quadTV(size_cube, size_cube, size_cube, -size_cube, size_cube,
				size_cube,
				-size_cube, -size_cube, size_cube, size_cube, -size_cube,
				size_cube);
		// down
		normal(0, 0, -1);
		quadTV(size_cube, size_cube, -size_cube, size_cube, -size_cube,
				-size_cube, -size_cube, -size_cube, -size_cube, -size_cube,
				size_cube, -size_cube);
		// right
		normal(1, 0, 0);
		quadTV(size_cube, size_cube, size_cube, size_cube, -size_cube,
				size_cube,
				size_cube, -size_cube, -size_cube, size_cube, size_cube,
				-size_cube);
		// left
		normal(-1, 0, 0);
		quadTV(-size_cube, size_cube, size_cube, -size_cube, size_cube,
				-size_cube, -size_cube, -size_cube, -size_cube, -size_cube,
				-size_cube, size_cube);
		// back
		normal(0, 1, 0);
		quadTV(size_cube, size_cube, size_cube, size_cube, size_cube,
				-size_cube,
				-size_cube, size_cube, -size_cube, -size_cube, size_cube,
				size_cube);
		// front
		normal(0, -1, 0);
		quadTV(size_cube, -size_cube, size_cube, -size_cube, -size_cube,
				size_cube, -size_cube, -size_cube, -size_cube, size_cube,
				-size_cube, -size_cube);

		manager.endGeometry();
		manager.endList();

		// sphere
		index[Type.SPHERE.getId()] = manager.startNewList(-1, true);
		manager.startGeometry(Manager.Type.TRIANGLES);
		cursorSphere(1f, TARGET_DOT_ALPHA);
		manager.endGeometry();
		manager.endList();

		// circle for target
		brush.start(-1);
		brush.setColor(GColor.WHITE, TARGET_CIRCLE_ALPHA);
		brush.setThickness(TARGET_CIRCLE_THICKNESS);
		brush.circle(Coords.O, Coords.VX, Coords.VY, TARGET_CIRCLE_RADIUS, 64);
		index[Type.TARGET_CIRCLE.getId()] = brush.end();

		// rotation
		brush.start(-1);
		brush.setColor(GColor.GRAY);
		brush.setThickness(thickness3); // re sets the thickness
		brush.arcExtendedWithArrows(new Coords(0, 0, 0, 1),
				new Coords(1, 0, 0, 0), new Coords(0, 1, 0, 0), size_move / 2,
				-Math.PI * 0.6, Math.PI * 1.2, 64);
		index[Type.ROTATION.getId()] = brush.end();

		manager.setScalerView();
	}

	private void color(float red, float green, float blue, float alpha) {
		this.r = red;
		this.g = green;
		this.b = blue;
		this.a = alpha;
	}

	private void color(float red, float green, float blue) {
		color(red, green, blue, 1f);
	}

	private void normal(float nx1, float ny1, float nz1) {
		nx = nx1;
		ny = ny1;
		nz = nz1;
	}

	private void vertex(float x, float y, float z) {
		manager.color(r, g, b, a);
		manager.vertex(x, y, z);
	}

	private void tv(float x, float y, float z) {
		tnv(x, y, z, nx, ny, nz);
	}

	private void tnv(float x, float y, float z) {
		tnv(x, y, z, x, y, z);
	}

	private void tnv(float x, float y, float z, float nx1, float ny1,
			float nz1) {
		manager.texture(0, 0);
		manager.normal(nx1, ny1, nz1);
		vertex(x, y, z);
	}

	private void quad(float x1, float y1, float z1, float x2, float y2,
			float z2, float x3, float y3, float z3, float x4, float y4,
			float z4) {

		vertex(x1, y1, z1);
		vertex(x2, y2, z2);
		vertex(x3, y3, z3);

		vertex(x1, y1, z1);
		vertex(x3, y3, z3);
		vertex(x4, y4, z4);
	}

	private void quadTNV(float x1, float y1, float z1, float x2, float y2,
			float z2, float x3, float y3, float z3, float x4, float y4,
			float z4) {

		tnv(x1, y1, z1);
		tnv(x2, y2, z2);
		tnv(x3, y3, z3);

		tnv(x1, y1, z1);
		tnv(x3, y3, z3);
		tnv(x4, y4, z4);
	}

	private void quadTV(float x1, float y1, float z1, float x2, float y2,
			float z2, float x3, float y3, float z3, float x4, float y4,
			float z4) {

		tv(x1, y1, z1);
		tv(x2, y2, z2);
		tv(x3, y3, z3);

		tv(x1, y1, z1);
		tv(x3, y3, z3);
		tv(x4, y4, z4);
	}

	// ////////////////////////////////
	// INDEX
	// ////////////////////////////////

	/**
	 * return geometry index for each type of cursor
	 * 
	 * @param type
	 *            type
	 * @return geometry index for each type of cursor
	 */
	public int getIndex(Type type) {
		return index[type.getId()];
	}

	// ////////////////////////////////
	// GEOMETRIES
	// ////////////////////////////////

	private void cursor(int i) {

		switch (i) {
		default:
		case 0:
			cursorCross2D();
			break;
		case 1:
			cursorDiamond();
			break;
		case 2:
			cursorCylinder();
			break;
		case 3:
			cursorCross3D();
			break;
		}
	}

	private void cursorCross2D() {

		// white parts
		color(1, 1, 1);

		// up
		quad(thickness, size, depth, -thickness, size, depth, -thickness, -size,
				depth, thickness, -size, depth);

		quad(size, thickness, depth, thickness, thickness, depth, thickness,
				-thickness, depth, size, -thickness, depth);

		quad(-size, thickness, depth, -size, -thickness, depth, -thickness,
				-thickness, depth, -thickness, thickness, depth);

		// down
		quad(thickness, size, -depth, thickness, -size, -depth, -thickness,
				-size, -depth, -thickness, size, -depth);

		quad(size, thickness, -depth, size, -thickness, -depth, thickness,
				-thickness, -depth, thickness, thickness, -depth);

		quad(-size, thickness, -depth, -thickness, thickness, -depth,
				-thickness, -thickness, -depth, -size, -thickness, -depth);

		// black parts
		color(0, 0, 0);

		// up and down
		quadSymxOyRotOz90SymOz(thickness, thickness, depth,
				thickness + thickness2, thickness + thickness2, depth,
				thickness + thickness2, size + thickness2, depth, thickness,
				size, depth);

		quadSymxOyRotOz90SymOz(thickness, -thickness, depth, thickness, -size,
				depth, thickness + thickness2, -size - thickness2, depth,
				thickness + thickness2, -thickness - thickness2, depth);

		quadSymxOyRotOz90SymOz(size, thickness, depth, size, -thickness, depth,
				size + thickness2, -thickness - thickness2, depth,
				size + thickness2, thickness + thickness2, depth);

		// edges
		quadSymxOyRotOz90SymOz(thickness + thickness2, thickness + thickness2,
				-depth, thickness + thickness2, size + thickness2, -depth,
				thickness + thickness2, size + thickness2, depth,
				thickness + thickness2, thickness + thickness2, depth);

		quadSymxOyRotOz90SymOz(thickness + thickness2, -thickness - thickness2,
				-depth, thickness + thickness2, -thickness - thickness2, depth,
				thickness + thickness2, -size - thickness2, depth,
				thickness + thickness2, -size - thickness2, -depth);

		quadRotOz90SymOz(size + thickness2, thickness + thickness2, -depth,
				size + thickness2, thickness + thickness2, depth,
				size + thickness2, -thickness - thickness2, depth,
				size + thickness2, -thickness - thickness2, -depth);

	}

	private void cursorCross3D() {

		float t = (float) (thickness / Math.tan(Math.PI / 8));

		float size2 = size + thickness2;

		// white parts
		color(1, 1, 1);

		quadSymxOyRotOz90SymOz(thickness, t, t, -thickness, t, t, -thickness, t,
				size2, thickness, t, size2);

		quadSymxOyRotOz90SymOz(thickness, t, t, thickness, size2, t, -thickness,
				size2, t, -thickness, t, t);

		quadRotOz90SymOz(t, t, thickness, t, t, -thickness, t, size2,
				-thickness, t, size2, thickness);

		quadRotOz90SymOz(-t, t, thickness, -t, size2, thickness, -t, size2,
				-thickness, -t, t, -thickness);

		quadRotOz90SymOz(thickness, size2 + t - thickness, -thickness,
				-thickness, size2 + t - thickness, -thickness, -thickness,
				size2 + t - thickness, thickness, thickness,
				size2 + t - thickness, thickness);

		quadSymxOyRotOz90SymOz(thickness, -thickness, size2 + t - thickness,
				thickness, thickness, size2 + t - thickness, -thickness,
				thickness, size2 + t - thickness, -thickness, -thickness,
				size2 + t - thickness);

		// black parts
		color(0, 0, 0);

		quadSymxOyRotOz90SymOz(t, t, t, t, t, size2, t, thickness, size2, t,
				thickness, t);

		quadSymxOyRotOz90SymOz(thickness, t, t, thickness, t, size2, t, t,
				size2, t, t, t);

		quadSymxOyRotOz90SymOz(t, t, t, t, t, thickness, t, size2, thickness, t,
				size2, t);

		quadSymxOyRotOz90SymOz(thickness, t, t, t, t, t, t, size2, t, thickness,
				size2, t);

		quadSymxOyRotOz90SymOz(-t, t, t, -t, size2, t, -t, size2, thickness, -t,
				t, thickness);

		quadSymxOyRotOz90SymOz(-thickness, t, t, -thickness, size2, t, -t,
				size2, t, -t, t, t);

		quadSymxOyRotOz90SymOz(t, size2, t, t, size2 + t - thickness, t, -t,
				size2 + t - thickness, t, -t, size2, t);

		quadSymxOyRotOz90SymOz(t, size2 + t - thickness, t, t,
				size2 + t - thickness, thickness, -t, size2 + t - thickness,
				thickness, -t, size2 + t - thickness, t);

		quadRotOz90SymOz(t, size2, t, t, size2, -t, t, size2 + t - thickness,
				-t, t, size2 + t - thickness, t);

		quadRotOz90SymOz(t, size2 + t - thickness, thickness, t,
				size2 + t - thickness, -thickness, thickness,
				size2 + t - thickness, -thickness, thickness,
				size2 + t - thickness, thickness);

		quadRotOz90SymOz(-t, size2, t, -t, size2 + t - thickness, t, -t,
				size2 + t - thickness, -t, -t, size2, -t);

		quadRotOz90SymOz(-t, size2 + t - thickness, thickness, -thickness,
				size2 + t - thickness, thickness, -thickness,
				size2 + t - thickness, -thickness, -t, size2 + t - thickness,
				-thickness);

		quadSymxOyRotOz90SymOz(t, t, size2, t, t, size2 + t - thickness, t, -t,
				size2 + t - thickness, t, -t, size2);

		quadSymxOyRotOz90SymOz(t, t, size2 + t - thickness, thickness,
				thickness, size2 + t - thickness, thickness, -thickness,
				size2 + t - thickness, t, -t, size2 + t - thickness);

	}

	private void cursorDiamond() {

		float t1 = 0.15f;
		float t2 = 1f - 2 * t1;

		// black parts
		color(0, 0, 0);

		quadSymxOyRotOz90SymOz(1f, 0f, 0f, t2, t1, t1, t1, t1, t2, 0f, 0f, 1f);

		quadSymxOyRotOz90SymOz(0f, 0f, 1f, t1, t1, t2, t1, t2, t1, 0f, 1f, 0f);

		quadSymxOyRotOz90SymOz(0f, 1f, 0f, t1, t2, t1, t2, t1, t1, 1f, 0f, 0f);

		// white parts
		color(1, 1, 1);

		quadSymxOyRotOz90SymOz(t2, t1, t1, t2, t1, t1, t1, t2, t1, t1, t1, t2);

	}

	private void cursorCylinder() {

		int latitude = 8;
		float x1 = 4f;
		float r1 = PlotterBrush.LINE3D_THICKNESS / 3f;
		float r2 = (float) (r1 * Math.sqrt(2));
		float x2 = x1 / 3;

		float da = (float) (Math.PI / latitude);

		float y1;
		float z1;
		float y0, z0;

		// white parts
		color(1, 1, 1);

		// ring
		y1 = 2 * r2 * (float) Math.sin(da);
		z1 = 2 * r2 * (float) Math.cos(da);

		for (int i = 1; i <= latitude; i++) {
			y0 = y1;
			z0 = z1;
			y1 = 2 * r2 * (float) Math.sin((2 * i + 1) * da);
			z1 = 2 * r2 * (float) Math.cos((2 * i + 1) * da);

			quad(-x2, y0, z0, x2, y0, z0, x2, y1, z1, -x2, y1, z1);

		}

		// caps
		y1 = 2 * r1 * (float) Math.sin(da);
		z1 = 2 * r1 * (float) Math.cos(da);

		for (int i = 1; i < latitude / 2; i++) {
			y0 = y1;
			z0 = z1;
			y1 = 2 * r1 * (float) Math.sin((2 * i + 1) * da);
			z1 = 2 * r1 * (float) Math.cos((2 * i + 1) * da);

			quadSymOz(x1, y0, z0, x1, -y0, z0, x1, -y1, z1, x1, y1, z1);

		}

		// black parts
		color(0, 0, 0);

		// ring
		y1 = 2 * (float) Math.sin(da);
		z1 = 2 * (float) Math.cos(da);

		for (int i = 1; i <= latitude; i++) {
			y0 = y1;
			z0 = z1;
			y1 = 2 * (float) Math.sin((2 * i + 1) * da);
			z1 = 2 * (float) Math.cos((2 * i + 1) * da);

			quadSymOz(x2, y0 * r2, z0 * r2, x1, y0 * r1, z0 * r1, x1, y1 * r1,
					z1 * r1, x2, y1 * r2, z1 * r2);

		}

	}

	private void cursorSphere(float gray, float alpha) {

		manager.setDummyTexture();

		color(gray, gray, gray, alpha);

		int latitude = 8;

		float d = (float) (0.5 * Math.PI / latitude);

		float rcjp = 1f;
		float z3 = 0;

		for (int j = 0; j < latitude; j++) {

			float rcj = rcjp;
			float x2 = rcj;
			float y2 = 0f;
			float z1 = z3;

			rcjp = (float) (Math.cos((j + 1) * d));
			float x3 = rcjp;
			float y3 = 0f;
			z3 = (float) (Math.sin((j + 1) * d));

			for (int i = 0; i < 4 * latitude; i++) {

				float x1 = x2;
				float y1 = y2;

				float ci = (float) (Math.cos((i + 1) * d));
				float si = (float) (Math.sin((i + 1) * d));

				x2 = ci * rcj;
				y2 = si * rcj;

				float x4 = x3;
				float y4 = y3;

				x3 = ci * rcjp;
				y3 = si * rcjp;

				quadTNV(x1, y1, z1, x2, y2, z1, x3, y3, z3, x4, y4, z3);

				quadTNV(x1, y1, -z1, x4, y4, -z3, x3, y3, -z3, x2, y2, -z1);

			}
		}

	}

	private void quadSymxOyRotOz90SymOz(float x1, float y1, float z1, float x2,
			float y2, float z2, float x3, float y3, float z3, float x4,
			float y4, float z4) {

		quadRotOz90SymOz(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4);

		quadRotOz90SymOz(x1, y1, -z1, x4, y4, -z4, x3, y3, -z3, x2, y2, -z2);

	}

	private void quadRotOz90SymOz(float x1, float y1, float z1, float x2,
			float y2, float z2, float x3, float y3, float z3, float x4,
			float y4, float z4) {

		quadSymOz(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4);

		quadSymOz(-y1, x1, z1, -y2, x2, z2, -y3, x3, z3, -y4, x4, z4);

	}

	private void quadSymOz(float x1, float y1, float z1, float x2, float y2,
			float z2, float x3, float y3, float z3, float x4, float y4,
			float z4) {

		quad(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4);

		quad(-x1, -y1, z1, -x2, -y2, z2, -x3, -y3, z3, -x4, -y4, z4);

		/*
		 * vertex(-x1,y1,z1); vertex(-x4,y4,z4); vertex(-x3,y3,z3);
		 * vertex(-x2,y2,z2);
		 */

	}

}
