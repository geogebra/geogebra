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

package org.geogebra.common.jre.headless;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.jre.plugin.GgbAPIJre;
import org.geogebra.common.main.App;

public class GgbAPIHeadless extends GgbAPIJre {

	private ApiDelegate delegate;

	public GgbAPIHeadless(App app) {
		super(app);
	}

	@Override
	public byte[] getGGBfile() {
		// stub
		return null;
	}

	@Override
	public void setErrorDialogsActive(boolean flag) {
		// stub
	}

	@Override
	public void refreshViews() {
		// stub
	}

	@Override
	public void openFile(String strURL) {
		if (delegate != null) {
			delegate.openFile(strURL);
		}
	}

	@Override
	protected void exportPNGClipboard(boolean transparent, int DPI,
			double exportScale, EuclidianView ev) {
		// stub

	}

	@Override
	protected void exportPNGClipboardDPIisNaN(boolean transparent,
			double exportScale, EuclidianView ev) {
		// stub

	}

	@Override
	protected String base64encodePNG(boolean transparent, double dpi,
			double exportScale, EuclidianView ev) {
		if (delegate != null) {
			return delegate.base64encodePNG(transparent, dpi, exportScale, ev);
		}
		return "";
	}

	public void setImageExporter(ApiDelegate exporter) {
		this.delegate = exporter;
	}
}
