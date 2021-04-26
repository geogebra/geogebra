package org.geogebra.web.geogebra3D.web.euclidian3D;

import java.util.List;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.resources.SVGResource;

/**
 * Stylebar popup for 3D rotation
 */
class RotateViewPopup extends PopupMenuButtonW {

	private final EuclidianStyleBar3DW euclidianStyleBar3DW;
	private ImageOrText pauseIcon;
	private ImageOrText playIcon;

	/**
	 * @param euclidianStyleBar3DW
	 *            stylebar
	 * @param playIcon
	 *            play icon
	 * @param pauseIcon
	 *            pause icon
	 */
	public RotateViewPopup(EuclidianStyleBar3DW euclidianStyleBar3DW,
			SVGResource playIcon, SVGResource pauseIcon) {
		super(euclidianStyleBar3DW.app, null, -1, -1, SelectionTable.MODE_ICON,
				false, true, null);
		this.euclidianStyleBar3DW = euclidianStyleBar3DW;

		this.playIcon = new ImageOrText(playIcon, 24);
		this.pauseIcon = new ImageOrText(pauseIcon, 24);

		setIcon(this.playIcon);

		getMySlider().setMinimum(-10);
		getMySlider().setMaximum(10);
		getMySlider().setTickSpacing(1);
		setSliderValue(5);
	}

	@Override
	protected void fireActionPerformed() {

		this.euclidianStyleBar3DW.getView().setRotContinueAnimation(0, getSliderValue() * 0.01);
		if (getSliderValue() == 0) {
			setIcon(playIcon);
		} else {
			setIcon(pauseIcon);
		}
	}

	@Override
	protected void onClickAction() {
		if (this.euclidianStyleBar3DW.getView().isRotAnimatedContinue()) {
			this.euclidianStyleBar3DW.getView().stopAnimation();
			setIcon(playIcon);
		} else {
			this.euclidianStyleBar3DW.getView().setRotContinueAnimation(0, getSliderValue() * 0.01);
			setIcon(pauseIcon);
		}
	}

	@Override
	public void update(List<GeoElement> geos) {
		this.setVisible(
				geos.size() == 0 && !EuclidianView.isPenMode(app.getMode())
						&& app.getMode() != EuclidianConstants.MODE_DELETE);
	}
}
