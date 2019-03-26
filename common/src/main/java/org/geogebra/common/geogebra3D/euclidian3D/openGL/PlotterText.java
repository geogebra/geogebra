package org.geogebra.common.geogebra3D.euclidian3D.openGL;

/**
 * Class that manages text rendering
 * 
 * @author Mathieu
 *
 */
public class PlotterText {

	/** geometry manager */
	private Manager manager;

	/**
	 * common constructor
	 * 
	 * @param manager
	 *            openGL manager
	 */
	public PlotterText(Manager manager) {

		this.manager = manager;

	}

	/**
	 * draws a rectangle
	 * 
	 * @param x
	 *            vertex x-coord
	 * @param y
	 *            vertex y-coord
	 * @param z
	 *            vertex z-coord
	 * @param width
	 *            width
	 * @param height
	 *            height
	 */
	public void rectangle(double x, double y, double z, double width,
			double height) {

		manager.startGeometry(Manager.Type.TRIANGLES);

		manager.texture(0, 0);
		manager.vertexInt(x, y, z);
		manager.texture(1, 0);
		manager.vertexInt(x + width, y, z);
		manager.texture(1, 1);
		manager.vertexInt(x + width, y + height, z);

		manager.texture(0, 0);
		manager.vertexInt(x, y, z);
		manager.texture(1, 1);
		manager.vertexInt(x + width, y + height, z);
		manager.texture(0, 1);
		manager.vertexInt(x, y + height, z);

		manager.endGeometry();

	}

	/**
	 * Draw rectangle outline
	 * 
	 * @param x
	 *            vertex x-coord
	 * @param y
	 *            vertex y-coord
	 * @param z
	 *            vertex z-coord
	 * @param width
	 *            width
	 * @param height
	 *            height
	 * @param lineWidth
	 *            bounds line width
	 */
	public void rectangleBounds(double x, double y, double z, double width,
			double height, double lineWidth) {

		manager.startGeometry(Manager.Type.TRIANGLES);
		double w = lineWidth / 2;
		// bottom
		manager.texture(0, 0);
		manager.vertexInt(x - w, y - w, z);
		manager.texture(0, 0);
		manager.vertexInt(x + width - w, y - w, z);
		manager.texture(0, 0);
		manager.vertexInt(x - w, y + w, z);
		manager.texture(0, 0);
		manager.vertexInt(x - w, y + w, z);
		manager.texture(0, 0);
		manager.vertexInt(x + width - w, y - w, z);
		manager.texture(0, 0);
		manager.vertexInt(x + width - w, y + w, z);
		// top
		manager.texture(0, 0);
		manager.vertexInt(x + w, y + height - w, z);
		manager.texture(0, 0);
		manager.vertexInt(x + width + w, y + height - w, z);
		manager.texture(0, 0);
		manager.vertexInt(x + w, y + height + w, z);
		manager.texture(0, 0);
		manager.vertexInt(x + w, y + height + w, z);
		manager.texture(0, 0);
		manager.vertexInt(x + width + w, y + height - w, z);
		manager.texture(0, 0);
		manager.vertexInt(x + width + w, y + height + w, z);
		// left
		manager.texture(0, 0);
		manager.vertexInt(x - w, y + w, z);
		manager.texture(0, 0);
		manager.vertexInt(x + w, y + w, z);
		manager.texture(0, 0);
		manager.vertexInt(x - w, y + height + w, z);
		manager.texture(0, 0);
		manager.vertexInt(x + w, y + w, z);
		manager.texture(0, 0);
		manager.vertexInt(x + w, y + height + w, z);
		manager.texture(0, 0);
		manager.vertexInt(x - w, y + height + w, z);
		// right
		manager.texture(0, 0);
		manager.vertexInt(x + width - w, y - w, z);
		manager.texture(0, 0);
		manager.vertexInt(x + width + w, y - w, z);
		manager.texture(0, 0);
		manager.vertexInt(x + width - w, y + height - w, z);
		manager.texture(0, 0);
		manager.vertexInt(x + width + w, y - w, z);
		manager.texture(0, 0);
		manager.vertexInt(x + width + w, y + height - w, z);
		manager.texture(0, 0);
		manager.vertexInt(x + width - w, y + height - w, z);

		manager.endGeometry();

	}

}
