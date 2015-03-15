package geogebra.common.geogebra3D.euclidian3D.openGL;

/**
 * Class that manages text rendering
 * 
 * @author matthieu
 *
 */
public class PlotterText {

	/** geometry manager */
	private Manager manager;

	/**
	 * common constructor
	 * 
	 * @param manager
	 */
	public PlotterText(Manager manager) {

		this.manager = manager;

	}

	/**
	 * draws a rectangle
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param width
	 * @param height
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

	public void rectangleBounds(double x, double y, double z, double width,
			double height) {

		manager.startGeometry(Manager.Type.LINE_LOOP);
		manager.vertexInt(x, y, z);
		manager.vertexInt(x + width, y, z);
		manager.vertexInt(x + width, y + height, z);
		manager.vertexInt(x, y + height, z);
		manager.endGeometry();

	}

}
