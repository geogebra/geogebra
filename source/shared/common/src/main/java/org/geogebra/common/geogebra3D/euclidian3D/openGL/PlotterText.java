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
