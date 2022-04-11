package org.geogebra.common.jre.headless;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.jre.plugin.GgbAPIJre;
import org.geogebra.common.main.App;

public class GgbAPIHeadless extends GgbAPIJre {

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
		// stub
	}

	@Override
	public boolean writePNGtoFile(String filename, double exportScale,
			boolean transparent, double DPI, boolean greyscale) {
		// stub
		return false;
	}

	@Override
	public void handleSlideAction(String eventType, String pageIdx, String appStat) {
		// stub
	}

	@Override
	public void selectSlide(String pageIdx) {
		// stub
	}

	@Override
	public void previewRefresh() {
		// stub
	}

	@Override
	public void setAlgebraOptions(Object options) {
		// stub
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
	protected String base64encodePNG(boolean transparent, double DPI,
			double exportScale, EuclidianView ev) {
		// stub
		return "";
	}
}
