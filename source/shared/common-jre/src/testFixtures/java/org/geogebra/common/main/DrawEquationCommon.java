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

package org.geogebra.common.main;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.io.FactoryProviderCommon;
import org.geogebra.common.kernel.kernelND.GeoElementND;

import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Image;

public class DrawEquationCommon extends DrawEquation {

	@Override
	public GDimension drawEquation(App app, GeoElementND geo, GGraphics2D g2,
			int x, int y, String text, GFont font, boolean serif,
			GColor fgColor, GColor bgColor, boolean useCache,
			boolean updateAgain, Runnable callback) {
		return AwtFactory.getPrototype().newDimension(0, 0);
	}

	@Override
	public Image getCachedDimensions(String text, GeoElementND geo,
			Color fgColor, GFont font, int style, int[] ret) {
		return null;
	}

	@Override
	public void checkFirstCall() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
		checkFirstCallStatic();
	}

	@Override
	public Color convertColor(GColor color) {
		return null;
	}

	@Override
	public GDimension measureEquation(App app, String text,
			GFont font, boolean serif) {
		return AwtFactory.getPrototype().newDimension(0, 0);
	}

}
