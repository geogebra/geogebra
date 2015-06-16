package org.geogebra.desktop.geogebra3D.euclidian3D;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianStyleBarStatic3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoClippingCube3D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.euclidian.EuclidianStyleBarD;
import org.geogebra.desktop.gui.util.MyToggleButton;
import org.geogebra.desktop.gui.util.PopupMenuButton;
import org.geogebra.desktop.main.AppD;

/**
 * StyleBar for 3D euclidian view
 * 
 * @author matthieu
 *
 */
public class EuclidianStyleBar3D extends EuclidianStyleBarD {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PopupMenuButton btnRotateView, btnClipping;

	private MyToggleButton btnShowPlane;

	private PopupMenuButton btnViewProjection, btnViewDirection;

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

		EuclidianStyleBarStatic3D.addToDefaultMap(defaultGeoMap);

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
	protected void processSource(Object source, ArrayList<GeoElement> targetGeos) {

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
					getView().stopRotAnimation();
					btnRotateView.setSelected(false);
				} else {
					getView().setRotContinueAnimation(0,
							(btnRotateView.getSliderValue()) * 0.01);
					btnRotateView.setSelected(true);
				}
			}
		} else if (source.equals(btnClipping)) {
			if (btnClipping.getMySlider().isShowing()) {
				getView().getSettings().setClippingReduction(
						btnClipping.getSliderValue());
			} else {
				getView().toggleShowAndUseClippingCube();
			}
		} else if (source.equals(btnViewDirection)) {
			int si = btnViewDirection.getSelectedIndex();
			switch (si) {
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
			switch (si) {
			case EuclidianView3D.PROJECTION_ORTHOGRAPHIC:
				getView().getSettings().setProjection(
						EuclidianView3D.PROJECTION_ORTHOGRAPHIC);
				break;
			case EuclidianView3D.PROJECTION_PERSPECTIVE:
				getView().getSettings().setProjection(
						EuclidianView3D.PROJECTION_PERSPECTIVE);
				break;
			case EuclidianView3D.PROJECTION_GLASSES:
				getView().getSettings().setProjection(
						EuclidianView3D.PROJECTION_GLASSES);
				break;
			case EuclidianView3D.PROJECTION_OBLIQUE:
				getView().getSettings().setProjection(
						EuclidianView3D.PROJECTION_OBLIQUE);
				break;
			}
		} else
			super.processSource(source, targetGeos);
	}

	private class PopupMenuButtonForView3D extends PopupMenuButton {

		public PopupMenuButtonForView3D() {
			super(app, null, -1, -1, new Dimension(app.getScaledIconSize(),
					app.getScaledIconSize()),
					org.geogebra.common.gui.util.SelectionTable.MODE_ICON, false,
					true);

		}

		@Override
		public void update(Object[] geos) {
			this.setVisible(geos.length == 0
					&& mode != EuclidianConstants.MODE_PEN);
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
		btnShowPlane = new MyToggleButtonVisibleIfNoGeo(
				app.getScaledIcon("stylingbar_graphics3D_plane.png"),
				iconHeight);
		btnShowPlane.addActionListener(this);

		// ========================================
		// rotate view button
		btnRotateView = new PopupMenuButtonForView3D();
		btnRotateView.setIcon(app
				.getScaledIcon("stylingbar_graphics3D_rotateview_play.png"));
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
		btnClipping = new PopupMenuButtonForView3D();
		btnClipping.setIcon(app
				.getScaledIcon("stylingbar_graphics3D_clipping_medium.png"));
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
				.getScaledIcon("stylingbar_graphics3D_view_xy.png");
		directionIcons[1] = app
				.getScaledIcon("stylingbar_graphics3D_view_xz.png");
		directionIcons[2] = app
				.getScaledIcon("stylingbar_graphics3D_view_yz.png");
		directionIcons[3] = app
				.getScaledIcon("stylingbar_graphics3D_standardview_rotate.png");
		btnViewDirection = new ProjectionPopup(app, directionIcons);
		btnViewDirection.addActionListener(this);

		// ========================================
		// projection view button
		ImageIcon[] projectionIcons = new ImageIcon[4];
		projectionIcons[0] = app
				.getScaledIcon("stylingbar_graphics3D_view_orthographic.png");
		projectionIcons[1] = app
				.getScaledIcon("stylingbar_graphics3D_view_perspective.png");
		projectionIcons[2] = app
				.getScaledIcon("stylingbar_graphics3D_view_glasses.png");
		projectionIcons[3] = app
				.getScaledIcon("stylingbar_graphics3D_view_oblique.png");
		btnViewProjection = new ProjectionPopup(app, projectionIcons);
		btnViewProjection.addActionListener(this);

	}

	private class ProjectionPopup extends PopupMenuButton {// implements
															// ActionListener{
		private static final long serialVersionUID = 1L;

		public ProjectionPopup(AppD app, ImageIcon[] projectionIcons) {
			super(app, projectionIcons, 1, projectionIcons.length,
					new Dimension(app.getScaledIconSize(),
							app.getScaledIconSize()),
					org.geogebra.common.gui.util.SelectionTable.MODE_ICON, true,
					false);
			setIcon(projectionIcons[getView().getProjection()]);
		}

		@Override
		public void update(Object[] geos) {
			this.setVisible(geos.length == 0
					&& mode != EuclidianConstants.MODE_PEN);
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
		btnViewDirection.setToolTipText(loc
				.getPlainTooltip("stylebar.ViewDirection"));
		btnViewDirection.setToolTipArray(new String[] {
				loc.getPlainTooltip("stylebar.ViewXY"),
				loc.getPlainTooltip("stylebar.ViewXZ"),
				loc.getPlainTooltip("stylebar.ViewYZ"),
				loc.getPlainTooltip("stylebar.ViewDefaultRotate") });
		btnClipping.setToolTipText(loc.getPlainTooltip("stylebar.Clipping"));
		btnViewProjection.setToolTipText(loc
				.getPlainTooltip("stylebar.ViewProjection"));
		btnViewProjection.setToolTipArray(new String[] {
				loc.getPlainTooltip("stylebar.ParallelProjection"),
				loc.getPlainTooltip("stylebar.PerspectiveProjection"),
				loc.getPlainTooltip("stylebar.GlassesProjection"),
				loc.getPlainTooltip("stylebar.ObliqueProjection") });

		// btnViewProjection.setSelectedIndex(getView().getProjection());
	}

	@Override
	public void updateGUI() {

		if (isIniting)
			return;

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
	protected PopupMenuButton[] newPopupBtnList() {
		PopupMenuButton[] superList = super.newPopupBtnList();
		PopupMenuButton[] ret = new PopupMenuButton[superList.length + 4];
		for (int i = 0; i < superList.length; i++)
			ret[i] = superList[i];

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
	protected MyToggleButton[] newToggleBtnList() {
		MyToggleButton[] superList = super.newToggleBtnList();
		MyToggleButton[] ret = new MyToggleButton[superList.length + 1];
		for (int i = 0; i < superList.length; i++)
			ret[i] = superList[i];

		int index = superList.length;
		ret[index] = btnShowPlane;
		index++;
		return ret;
	}

	public EuclidianView3D getView() {
		return (EuclidianView3D) ev;
	}

	protected void selectPointStyle(int idx) {

	}
}
