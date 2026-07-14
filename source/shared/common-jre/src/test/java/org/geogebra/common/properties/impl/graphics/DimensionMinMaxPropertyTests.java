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

package org.geogebra.common.properties.impl.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel.MinMaxType;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class DimensionMinMaxPropertyTests extends BaseAppTestSetup {
	private static final double DELTA = 1e-8;

	@Test
	public void setValueShouldUpdate2DMinMaxObjects() {
		setupApp(SuiteSubApp.GRAPHING);
		setup2DBounds();
		DimensionMinMaxProperty xMinProperty = new DimensionMinMaxProperty(getApp(),
				getLocalization(), "xmin", getEuclidianView(), MinMaxType.minX);
		DimensionMinMaxProperty xMaxProperty = new DimensionMinMaxProperty(getApp(),
				getLocalization(), "xmax", getEuclidianView(), MinMaxType.maxX);
		DimensionMinMaxProperty yMinProperty = new DimensionMinMaxProperty(getApp(),
				getLocalization(), "ymin", getEuclidianView(), MinMaxType.minY);
		DimensionMinMaxProperty yMaxProperty = new DimensionMinMaxProperty(getApp(),
				getLocalization(), "ymax", getEuclidianView(), MinMaxType.maxY);

		xMinProperty.setValue("-7");
		xMaxProperty.setValue("8");
		yMinProperty.setValue("-9");
		yMaxProperty.setValue("10");

		assertEquals("-7", xMinProperty.getValue());
		assertEquals("8", xMaxProperty.getValue());
		assertEquals("-9", yMinProperty.getValue());
		assertEquals("10", yMaxProperty.getValue());
		assertEquals(-7, getEuclidianView().getXminObject().getDouble(), DELTA);
		assertEquals(8, getEuclidianView().getXmaxObject().getDouble(), DELTA);
		assertEquals(-9, getEuclidianView().getYminObject().getDouble(), DELTA);
		assertEquals(10, getEuclidianView().getYmaxObject().getDouble(), DELTA);
		assertEquals(-7, getEuclidianView().getSettings().getXminObject().getDouble(), DELTA);
		assertEquals(8, getEuclidianView().getSettings().getXmaxObject().getDouble(), DELTA);
		assertEquals(-9, getEuclidianView().getSettings().getYminObject().getDouble(), DELTA);
		assertEquals(10, getEuclidianView().getSettings().getYmaxObject().getDouble(), DELTA);
	}

	@Test
	public void getValueShouldReadCurrentBoundObjects() {
		setupApp(SuiteSubApp.GRAPHING);
		setup2DBounds();
		DimensionMinMaxProperty xMinProperty = new DimensionMinMaxProperty(getApp(),
				getLocalization(), "xmin", getEuclidianView(), MinMaxType.minX);
		DimensionMinMaxProperty xMaxProperty = new DimensionMinMaxProperty(getApp(),
				getLocalization(), "xmax", getEuclidianView(), MinMaxType.maxX);
		DimensionMinMaxProperty yMinProperty = new DimensionMinMaxProperty(getApp(),
				getLocalization(), "ymin", getEuclidianView(), MinMaxType.minY);
		DimensionMinMaxProperty yMaxProperty = new DimensionMinMaxProperty(getApp(),
				getLocalization(), "ymax", getEuclidianView(), MinMaxType.maxY);

		assertEquals("-10", xMinProperty.getValue());
		assertEquals("10", xMaxProperty.getValue());
		assertEquals("-10", yMinProperty.getValue());
		assertEquals("10", yMaxProperty.getValue());
	}

	@Test
	public void invalidValueShouldNotReplaceCurrentObject() {
		setupApp(SuiteSubApp.GRAPHING);
		setup2DBounds();
		DimensionMinMaxProperty xMinProperty = new DimensionMinMaxProperty(getApp(),
				getLocalization(), "xmin", getEuclidianView(), MinMaxType.minX);

		xMinProperty.setValue("-7");
		xMinProperty.setValue("1/");

		assertEquals("-7", xMinProperty.getValue());
		assertEquals(-7, getEuclidianView().getXminObject().getDouble(), DELTA);
		assertEquals(-7, getEuclidianView().getSettings().getXminObject().getDouble(), DELTA);
		assertNotNull(xMinProperty.validateValue("1/"));
		assertNull(xMinProperty.validateValue("12"));
	}

	@Test
	public void setValueShouldUpdate3DZObjects() {
		setupApp(SuiteSubApp.G3D);
		setup3DBounds();
		DimensionMinMaxProperty zMinProperty = new DimensionMinMaxProperty(getApp(),
				getLocalization(), "zmin", getEuclidianView3D(), MinMaxType.minZ);
		DimensionMinMaxProperty zMaxProperty = new DimensionMinMaxProperty(getApp(),
				getLocalization(), "zmax", getEuclidianView3D(), MinMaxType.maxZ);

		zMinProperty.setValue("-15");
		zMaxProperty.setValue("16");

		assertEquals("-15", zMinProperty.getValue());
		assertEquals("16", zMaxProperty.getValue());
		assertEquals(-15, getEuclidianView3D().getZminObject().getDouble(), DELTA);
		assertEquals(16, getEuclidianView3D().getZmaxObject().getDouble(), DELTA);
		assertEquals(-15, getEuclidianView3D().getSettings().getZminObject().getDouble(), DELTA);
		assertEquals(16, getEuclidianView3D().getSettings().getZmaxObject().getDouble(), DELTA);
		assertEquals(-15, getEuclidianView3D().getZmin(), DELTA);
		assertEquals(16, getEuclidianView3D().getZmax(), DELTA);
	}

	private EuclidianView getEuclidianView() {
		return getApp().getEuclidianView1();
	}

	private EuclidianView3D getEuclidianView3D() {
		return (EuclidianView3D) getApp().getEuclidianView3D();
	}

	private void setup2DBounds() {
		EuclidianView view = getEuclidianView();
		view.setXminObject(new GeoNumeric(getKernel().getConstruction(), -10));
		view.setXmaxObject(new GeoNumeric(getKernel().getConstruction(), 10));
		view.setYminObject(new GeoNumeric(getKernel().getConstruction(), -10));
		view.setYmaxObject(new GeoNumeric(getKernel().getConstruction(), 10));
		view.getSettings().setXminObject(view.getXminObject(), false);
		view.getSettings().setXmaxObject(view.getXmaxObject(), false);
		view.getSettings().setYminObject(view.getYminObject(), false);
		view.getSettings().setYmaxObject(view.getYmaxObject(), false);
	}

	private void setup3DBounds() {
		EuclidianView3D view = getEuclidianView3D();
		view.setXminObject(new GeoNumeric(getKernel().getConstruction(), -10));
		view.setXmaxObject(new GeoNumeric(getKernel().getConstruction(), 10));
		view.setYminObject(new GeoNumeric(getKernel().getConstruction(), -10));
		view.setYmaxObject(new GeoNumeric(getKernel().getConstruction(), 10));
		view.setZminObject(new GeoNumeric(getKernel().getConstruction(), -10));
		view.setZmaxObject(new GeoNumeric(getKernel().getConstruction(), 10));
		view.getSettings().setXminObject(view.getXminObject(), false);
		view.getSettings().setXmaxObject(view.getXmaxObject(), false);
		view.getSettings().setYminObject(view.getYminObject(), false);
		view.getSettings().setYmaxObject(view.getYmaxObject(), false);
		view.getSettings().setZminObject(view.getZminObject(), false);
		view.getSettings().setZmaxObject(view.getZmaxObject(), false);
	}
}
