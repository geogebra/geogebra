package org.geogebra.desktop.geogebra3D.euclidian3D;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianStyleBarStatic3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoClippingCube3D;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.euclidian.EuclidianStyleBarD;
import org.geogebra.desktop.geogebra3D.gui.GuiResources3D;
import org.geogebra.desktop.gui.util.MyToggleButtonD;
import org.geogebra.desktop.gui.util.PopupMenuButtonD;
import org.geogebra.desktop.main.AppD;

/**
 * StyleBar for 3D euclidian view
 * 
 * @author Mathieu
 *
 */
public class EuclidianStyleBar3D extends EuclidianStyleBarD {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PopupMenuButtonD btnRotateView, btnClipping;

	private MyToggleButtonD btnShowPlane;

	private PopupMenuButtonD btnViewProjection, btnViewDirection;

	/**
	 * Common constructor.
	 * 
	 * @param ev
	 */
	public EuclidianStyleBar3D(EuclidianView3D ev) {
		super(ev);
	}

	@Override
	protected void createDefaultMap() {

		super.createDefaultMap();

		EuclidianStyleBarStatic3D.addToDefaultMap(defaultGeoMap,
				ev.getApplication());

	}

	@Override
	protected void addBtnShowPlane() {
		add(btnShowPlane);
	}

	@Override
	protected void addBtnRotateView() {

		add(btnRotateView);
		add(btnViewDirection);

		add(btnClipping);
		add(btnViewProjection);
	}

	@Override
	protected boolean isVisibleInThisView(GeoElement geo) {
		return geo.isVisibleInView3D();
	}

	@Override
	protected void processSource(Object source,
			ArrayList<GeoElement> targetGeos) {

		if (source.equals(btnShowPlane)) {
			getView().getSettings().togglePlane();
		} else if (source.equals(btnRotateView)) {
			if (btnRotateView.getMySlider().isShowing()) {// if slider is
															// showing, start
															// rotation
				getView().setRotContinueAnimation(0,
						(btnRotateView.getSliderValue()) * 0.01);
			} else {// if button has been clicked, toggle rotation
				if (getView().isRotAnimatedContinue()) {
					getView().stopAnimation();
					btnRotateView.setSelected(false);
				} else {
					getView().setRotContinueAnimation(0,
							(btnRotateView.getSliderValue()) * 0.01);
					btnRotateView.setSelected(true);
				}
			}
		} else if (source.equals(btnClipping)) {
			if (btnClipping.getMySlider().isShowing()) {
				getView().getSettings()
						.setClippingReduction(btnClipping.getSliderValue());
			} else {
				getView().toggleShowAndUseClippingCube();
			}
		} else if (source.equals(btnViewDirection)) {
			int si = btnViewDirection.getSelectedIndex();
			switch (si) {
			default:
			case 0:
				getView().setRotAnimation(-90, 90, true);
				break;
			case 1:
				getView().setRotAnimation(-90, 0, true);
				break;
			case 2:
				getView().setRotAnimation(0, 0, true);
				break;
			case 3:
				getView().setDefaultRotAnimation();
				break;
			}
		} else if (source.equals(btnViewProjection)) {
			int si = btnViewProjection.getSelectedIndex();
			getView().getSettings().setProjection(si);
		} else {
			super.processSource(source, targetGeos);
		}
	}

	@SuppressWarnings("serial")
	private class PopupMenuButtonForView3D extends PopupMenuButtonD {

		public PopupMenuButtonForView3D(AppD app1) {
			super(app1, null, -1, -1,
					new Dimension(app1.getScaledIconSize(),
							app1.getScaledIconSize()),
					SelectionTable.MODE_ICON, false, true);

		}

		@Override
		public void update(Object[] geos) {
			this.setVisible(
					geos.length == 0 && mode != EuclidianConstants.MODE_PEN);
		}

		/*
		 * @Override public Point getToolTipLocation(MouseEvent e) { return new
		 * Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y); }
		 */

	}

	@Override
	protected void createButtons() {

		super.createButtons();
		getBtnPointStyle().getMyTable().setVisible(false);
		// ========================================
		// show grid button
		btnShowPlane = new MyToggleButtonDforEV(
				app.getScaledIcon(GuiResources3D.STYLINGBAR_GRAPHICS3D_PLANE),
				iconHeight);
		btnShowPlane.addActionListener(this);

		// ========================================
		// rotate view button
		btnRotateView = new PopupMenuButtonForView3D(app);
		btnRotateView.setIcon(app.getScaledIcon(
				GuiResources3D.STYLINGBAR_GRAPHICS3D_ROTATEVIEW_PLAY));
		btnRotateView.getMySlider().setMinimum(-10);
		btnRotateView.getMySlider().setMaximum(10);
		btnRotateView.getMySlider().setMajorTickSpacing(10);
		btnRotateView.getMySlider().setMinorTickSpacing(1);
		btnRotateView.getMySlider().setPaintTicks(true);
		// btnRotateView.getMySlider().setPaintLabels(true);
		btnRotateView.getMySlider().setPaintTrack(false);
		btnRotateView.getMySlider().setSnapToTicks(true);
		btnRotateView.setSliderValue(5);
		btnRotateView.addActionListener(this);

		// ========================================
		// clipping button
		btnClipping = new PopupMenuButtonForView3D(app);
		btnClipping.setIcon(app.getScaledIcon(
				GuiResources3D.STYLINGBAR_GRAPHICS3D_CLIPPING_MEDIUM));
		btnClipping.getMySlider().setMinimum(GeoClippingCube3D.REDUCTION_MIN);
		btnClipping.getMySlider().setMaximum(GeoClippingCube3D.REDUCTION_MAX);
		btnClipping.getMySlider().setMajorTickSpacing(1);
		btnClipping.getMySlider().setMinorTickSpacing(1);
		btnClipping.getMySlider().setPaintTicks(true);
		// btnRotateView.getMySlider().setPaintLabels(true);
		btnClipping.getMySlider().setPaintTrack(true);
		btnClipping.getMySlider().setSnapToTicks(true);
		btnClipping.setSliderValue(getView().getClippingReduction());
		btnClipping.addActionListener(this);

		// ========================================
		// view yz direction
		ImageIcon[] directionIcons = new ImageIcon[4];
		directionIcons[0] = app
				.getScaledIcon(GuiResources3D.STYLINGBAR_GRAPHICS3D_VIEW_XY);
		directionIcons[1] = app
				.getScaledIcon(GuiResources3D.STYLINGBAR_GRAPHICS3D_VIEW_XZ);
		directionIcons[2] = app
				.getScaledIcon(GuiResources3D.STYLINGBAR_GRAPHICS3D_VIEW_YZ);
		directionIcons[3] = app.getScaledIcon(
				GuiResources3D.STYLINGBAR_GRAPHICS3D_STANDARDVIEW_ROTATE);
		btnViewDirection = new ProjectionPopup(app, directionIcons);
		btnViewDirection.addActionListener(this);

		// ========================================
		// projection view button
		ImageIcon[] projectionIcons = new ImageIcon[4];
		projectionIcons[0] = app
				.getScaledIcon(GuiResources3D.PROJECTION_ORTOGRAPHIC);
		projectionIcons[1] = app
				.getScaledIcon(GuiResources3D.PROJECTION_PERSPECTIVE);
		projectionIcons[2] = app
				.getScaledIcon(GuiResources3D.PROJECTION_GLASSES);
		projectionIcons[3] = app
				.getScaledIcon(GuiResources3D.PROJECTION_OBLIQUE);
		btnViewProjection = new ProjectionPopup(app, projectionIcons);
		btnViewProjection.addActionListener(this);

	}

	private class ProjectionPopup extends PopupMenuButtonD {// implements
															// ActionListener{
		private static final long serialVersionUID = 1L;

		public ProjectionPopup(AppD app, ImageIcon[] projectionIcons) {
			super(app, projectionIcons, 1, projectionIcons.length,
					new Dimension(app.getScaledIconSize(),
							app.getScaledIconSize()),
					SelectionTable.MODE_ICON, true, false);
			setIcon(projectionIcons[getView().getProjection()]);
		}

		@Override
		public void update(Object[] geos) {
			this.setVisible(
					geos.length == 0 && mode != EuclidianConstants.MODE_PEN);
		}

		/*
		 * @Override public Point getToolTipLocation(MouseEvent e) { return new
		 * Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y); }
		 */

	}

	@Override
	public void setLabels() {
		super.setLabels();
		btnShowPlane.setToolTipText(loc.getPlainTooltip("stylebar.xOyPlane"));
		btnRotateView
				.setToolTipText(loc.getPlainTooltip("stylebar.RotateView"));
		btnViewDirection
				.setToolTipText(loc.getPlainTooltip("stylebar.ViewDirection"));
		btnViewDirection.setToolTipArray(
				new String[] { loc.getPlainTooltip("stylebar.ViewXY"),
						loc.getPlainTooltip("stylebar.ViewXZ"),
						loc.getPlainTooltip("stylebar.ViewYZ"),
						loc.getPlainTooltip("stylebar.ViewDefaultRotate") });
		btnClipping.setToolTipText(loc.getPlainTooltip("stylebar.Clipping"));
		btnViewProjection
				.setToolTipText(loc.getPlainTooltip("stylebar.ViewProjection"));
		btnViewProjection.setToolTipArray(new String[] {
				loc.getPlainTooltip("stylebar.ParallelProjection"),
				loc.getPlainTooltip("stylebar.PerspectiveProjection"),
				loc.getPlainTooltip("stylebar.GlassesProjection"),
				loc.getPlainTooltip("stylebar.ObliqueProjection") });

		// btnViewProjection.setSelectedIndex(getView().getProjection());
	}

	@Override
	public void updateGUI() {

		if (isIniting) {
			return;
		}

		super.updateGUI();

		btnShowPlane.removeActionListener(this);
		btnShowPlane.setSelected(getView().getShowPlane());
		btnShowPlane.addActionListener(this);

		btnRotateView.removeActionListener(this);
		btnRotateView.setSelected(false);
		btnRotateView.addActionListener(this);

		/*
		 * btnViewDirection.removeActionListener(this);
		 * btnViewDirection.setSelectedIndex(0);
		 * btnViewDirection.addActionListener(this);
		 */

		btnClipping.removeActionListener(this);
		btnClipping.setSelected(getView().showClippingCube());
		btnClipping.setSliderValue(getView().getClippingReduction());
		btnClipping.addActionListener(this);

		btnViewProjection.removeActionListener(this);
		btnViewProjection.setSelectedIndex(getView().getProjection());
		btnViewProjection.addActionListener(this);

	}

	@Override
	protected PopupMenuButtonD[] newPopupBtnList() {
		PopupMenuButtonD[] superList = super.newPopupBtnList();
		PopupMenuButtonD[] ret = new PopupMenuButtonD[superList.length + 4];
		for (int i = 0; i < superList.length; i++) {
			ret[i] = superList[i];
		}

		int index = superList.length;
		ret[index] = btnRotateView;
		index++;
		ret[index] = btnViewDirection;
		index++;
		ret[index] = btnClipping;
		index++;
		ret[index] = btnViewProjection;
		return ret;
	}

	@Override
	protected MyToggleButtonD[] newToggleBtnList() {
		MyToggleButtonD[] superList = super.newToggleBtnList();
		MyToggleButtonD[] ret = new MyToggleButtonD[superList.length + 1];
		for (int i = 0; i < superList.length; i++) {
			ret[i] = superList[i];
		}

		int index = superList.length;
		ret[index] = btnShowPlane;
		index++;
		return ret;
	}

	public EuclidianView3D getView() {
		return (EuclidianView3D) ev;
	}

	@Override
	protected void selectPointStyle(int idx) {
		// no point style in 3D
	}
}
