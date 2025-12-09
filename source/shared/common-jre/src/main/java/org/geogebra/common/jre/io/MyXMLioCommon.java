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

package org.geogebra.common.jre.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;

import org.geogebra.common.jre.gui.MyImageJre;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;

public final class MyXMLioCommon extends MyXMLioJre {
	public MyXMLioCommon(Kernel kernel, Construction cons) {
		super(kernel, cons);
	}

	@Override
	protected void loadSVG(String svg, String name) {
		// not supported yet
	}

	@Override
	protected void loadBitmap(ZipInputStream zip, String name) {
		// not supported yet
	}

	@Override
	protected MyImageJre getExportImage(double width, double height) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected MyImageJre getExternalImage(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void writeImage(MyImageJre img, String ext,
			OutputStream os) throws IOException {
		// TODO Auto-generated method stub

	}
}