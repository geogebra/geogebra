package org.geogebra.desktop.geogebra3D.euclidianForPlane;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.euclidian.EuclidianStyleBarD;
import org.geogebra.desktop.gui.util.MyToggleButtonD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * StyleBar for view for plane
 * 
 * @author Mathieu
 *
 */
public class EuclidianStyleBarForPlaneD extends EuclidianStyleBarD {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MyToggleButtonD btnCenterAndOrientation;

	/**
	 * Common constructor.
	 * 
	 * @param ev
	 *            view
	 */
	public EuclidianStyleBarForPlaneD(EuclidianViewForPlaneD ev) {
		super(ev);
	}

	@Override
	protected void addGraphicsDecorationsButtons() {
		add(btnShowAxes);
		add(btnShowGrid);
	}

	@Override
	protected void addBtnRotateView() {

		add(btnCenterAndOrientation);

	}

	@Override
	protected boolean isVisibleInThisView(GeoElement geo) {
		return geo.isVisibleInViewForPlane();
	}

	@Override
	protected void processSource(Object source,
			ArrayList<GeoElement> targetGeos) {

		if (source.equals(btnCenterAndOrientation)) {
			EuclidianViewForPlaneCompanion companion = (EuclidianViewForPlaneCompanion) ((EuclidianView) ev)
					.getCompanion();
			companion.updateCenterAndOrientationRegardingView();
			companion.updateScaleRegardingView();
		} else {
			super.processSource(source, targetGeos);
		}
	}

	@Override
	protected void createButtons() {

		super.createButtons();

		// ========================================
		// button
		btnCenterAndOrientation = new MyToggleButtonD(
				app.getScaledIcon(GuiResourcesD.STANDARD_VIEW), iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {
				// always show this button unless in pen mode
				this.setVisible(mode != EuclidianConstants.MODE_PEN);
			}
		};
		btnCenterAndOrientation.addActionListener(this);

	}

	@Override
	public void setLabels() {
		super.setLabels();
		btnCenterAndOrientation
				.setToolTipText(loc.getPlainTooltip("stylebar.ViewDefault"));

	}

	@Override
	public void updateGUI() {
		super.updateGUI();

		btnCenterAndOrientation.removeActionListener(this);
		btnCenterAndOrientation.setSelected(false);
		btnCenterAndOrientation.addActionListener(this);

	}

}
