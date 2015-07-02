package org.geogebra.web.geogebra3D.web.euclidian3D;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianStyleBarStatic3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.geogebra3D.web.gui.images.StyleBar3DResources;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.euclidian.EuclidianStyleBarW;
import org.geogebra.web.web.gui.images.StyleBarResources;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.MyToggleButton2;
import org.geogebra.web.web.gui.util.PopupMenuButton;

import com.google.gwt.resources.client.ImageResource;

/**
 * style bar for 3D view
 * 
 * @author mathieu
 *
 */
public class EuclidianStyleBar3DW extends EuclidianStyleBarW {

	private RotateViewPopup btnRotateView;

	// private ClippingPopup btnClipping;

	private MyToggleButton2 btnShowGrid3D;

	private AxesAndPlanePopup btnShowAxesAndPlane;

	private PopupMenuButton btnChangeView;

	// private PopupMenuButton btnViewProjection;

	/**
	 * constructor
	 * 
	 * @param ev
	 *            euclidian view
	 */
	public EuclidianStyleBar3DW(EuclidianView ev) {
		super(ev, App.VIEW_EUCLIDIAN3D);
	}

	@Override
	protected void setOptionType() {
		optionType = OptionType.EUCLIDIAN3D;
	}

	@Override
	public EuclidianView3D getView() {
		return (EuclidianView3D) ev;
	}

	@Override
	protected void createDefaultMap() {

		super.createDefaultMap();

		EuclidianStyleBarStatic3D.addToDefaultMap(defaultGeoMap);

	}


	@Override
	protected void createButtons() {

		super.createButtons();

		getBtnPointStyle().setEuclidian3D(true);

		// ========================================
		// rotate view button
		btnRotateView = new RotateViewPopup(
		        StyleBar3DResources.INSTANCE.rotateViewPlay(),
		        StyleBar3DResources.INSTANCE.rotateViewPause());
		btnRotateView.addPopupHandler(this);
		// btnRotateView.addActionListener(this);

		// // ========================================
		// // clipping button
		// ImageOrText[] clippingIcons = new ImageOrText[4];
		// for (int i = 0; i < 4; i++) {
		// clippingIcons[i] = new ImageOrText();
		// }
		// clippingIcons[0].setUrl(StyleBarResources.INSTANCE.stylingbar_empty()
		// .getSafeUri().asString());
		// clippingIcons[1].setUrl(StyleBar3DResources.INSTANCE.clippingSmall()
		// .getSafeUri().asString());
		// clippingIcons[2].setUrl(StyleBar3DResources.INSTANCE.clippingMedium()
		// .getSafeUri().asString());
		// clippingIcons[3].setUrl(StyleBar3DResources.INSTANCE.clippingBig()
		// .getSafeUri().asString());
		// btnClipping = new ClippingPopup(app, clippingIcons, 1, 4,
		// org.geogebra.common.gui.util.SelectionTable.MODE_ICON, getView());
		// btnClipping.addPopupHandler(this);

		// ========================================
		/*
		 * rotate x text field textRotateX = new JTextField(3);
		 * textRotateX.addActionListener(this);
		 */

		// ========================================
		// view direction button

		ImageOrText[] directionIcons = ImageOrText.convert(new ImageResource[] {
				StyleBarResources.INSTANCE.standard_view(),
				StyleBarResources.INSTANCE.view_all_objects(),
				StyleBar3DResources.INSTANCE.standardViewRotate(),
				StyleBar3DResources.INSTANCE.viewXY(),
				StyleBar3DResources.INSTANCE.viewXZ(),
				StyleBar3DResources.INSTANCE.viewYZ() }, 24);

		btnChangeView = new ProjectionPopup(app, directionIcons);
		btnChangeView.addPopupHandler(this);

		// // ========================================
		// // projection view button
		// ImageOrText[] projectionIcons = new ImageOrText[4];
		// for (int i = 0; i < 4; i++) {
		// projectionIcons[i] = new ImageOrText();
		// }
		// projectionIcons[0].setUrl(StyleBar3DResources.INSTANCE
		// .viewOrthographic().getSafeUri().asString());
		// projectionIcons[1].setUrl(StyleBar3DResources.INSTANCE.viewPerspective()
		// .getSafeUri().asString());
		// projectionIcons[2].setUrl(StyleBar3DResources.INSTANCE.viewGlasses()
		// .getSafeUri().asString());
		// projectionIcons[3].setUrl(StyleBar3DResources.INSTANCE.viewOblique()
		// .getSafeUri().asString());
		// btnViewProjection = new ProjectionPopup(app, projectionIcons);
		// btnViewProjection.addPopupHandler(this);

	}

	private class ProjectionPopup extends PopupMenuButton {
		private static final long serialVersionUID = 1L;

		public ProjectionPopup(AppW app, ImageOrText[] projectionIcons) {
			super(app, projectionIcons, 1, projectionIcons.length,
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

	private class RotateViewPopup extends PopupMenuButtonForView3D {

		public RotateViewPopup(ImageResource playIcon, ImageResource pauseIcon) {
			super();

			this.playIcon = new ImageOrText(playIcon);
			this.pauseIcon = new ImageOrText(pauseIcon);

			setIcon(this.playIcon);

			getMySlider().setMinimum(-10);
			getMySlider().setMaximum(10);
			getMySlider().setMajorTickSpacing(10);
			getMySlider().setMinorTickSpacing(1);
			getMySlider().setPaintTicks(true);
			// getMySlider().setPaintTrack(false);
			// getMySlider().setSnapToTicks(true);
			setSliderValue(5);

		}

		private ImageOrText pauseIcon, playIcon;

		@Override
		protected void fireActionPerformed() {
			getView().setRotContinueAnimation(0, getSliderValue() * 0.01);
			if (getSliderValue() == 0) {
				setIcon(playIcon);
			} else {
				setIcon(pauseIcon);
			}
		}

		@Override
		protected void onClickAction() {
			if (getView().isRotAnimatedContinue()) {
				getView().stopRotAnimation();
				setIcon(playIcon);
			} else {
				getView().setRotContinueAnimation(0, getSliderValue() * 0.01);
				setIcon(pauseIcon);
			}
		}
	}

	private class PopupMenuButtonForView3D extends PopupMenuButton /*
																	 * implements
																	 * ClickHandler
																	 */{

		public PopupMenuButtonForView3D() {
			super(EuclidianStyleBar3DW.this.app, null, -1, -1,
			        org.geogebra.common.gui.util.SelectionTable.MODE_ICON, false,
			        true);
		}

		@Override
		public void update(Object[] geos) {
			this.setVisible(geos.length == 0
			        && mode != EuclidianConstants.MODE_PEN);
		}

		/*
		 * public void onClick(ClickEvent event) { App.debug("onClick : "+this);
		 * }
		 */

		/*
		 * @Override public Point getToolTipLocation(MouseEvent e) { return new
		 * Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y); }
		 */

	}

	@Override
	protected boolean processSourceForAxesAndGrid(Object source) {
		if (source.equals(btnShowAxesAndPlane)) {
			btnShowAxesAndPlane.setEVFromIndex();
			return true;
		}

		return false;

	}

	@Override
	protected boolean processSource(Object source,
			ArrayList<GeoElement> targetGeos) {


		// if (source.equals(btnClipping)) {
		// int index = btnClipping.getSelectedIndex();
		// if (index == 0) {
		// getView().setUseClippingCube(false);
		// getView().setShowClippingCube(false);
		// } else {
		// getView().setUseClippingCube(true);
		// getView().setShowClippingCube(true);
		// getView().getSettings().setClippingReduction(index - 1);
		// }
		// getView().repaintView();
		//
		// } else
		if (source.equals(btnChangeView)) {
			int si = btnChangeView.getSelectedIndex();
			switch (si) {
			case 0: // standard view
				getView().setStandardView(true);
				break;
			case 1: // show all objects
				getView().setViewShowAllObjects(true);
				break;
			case 2: // standard view orientation
				getView().setRotAnimation(EuclidianView3D.ANGLE_ROT_OZ,
						EuclidianView3D.ANGLE_ROT_XOY, false);
				break;

			// views parallel to axes
			case 3:
				getView().setRotAnimation(-90, 90, true);
				break;
			case 4:
				getView().setRotAnimation(-90, 0, true);
				break;
			case 5:
				getView().setRotAnimation(0, 0, true);
				break;
			}
			// } else if (source.equals(btnViewProjection)) {
			// int si = btnViewProjection.getSelectedIndex();
			// switch (si) {
			// case EuclidianView3D.PROJECTION_ORTHOGRAPHIC:
			// getView().getSettings().setProjection(
			// EuclidianView3D.PROJECTION_ORTHOGRAPHIC);
			// break;
			// case EuclidianView3D.PROJECTION_PERSPECTIVE:
			// getView().getSettings().setProjection(
			// EuclidianView3D.PROJECTION_PERSPECTIVE);
			// break;
			// case EuclidianView3D.PROJECTION_GLASSES:
			// getView().getSettings().setProjection(
			// EuclidianView3D.PROJECTION_GLASSES);
			// break;
			// case EuclidianView3D.PROJECTION_OBLIQUE:
			// getView().getSettings().setProjection(
			// EuclidianView3D.PROJECTION_OBLIQUE);
			// break;
			// }
			// getView().repaintView();
		} else {
			return super.processSource(source, targetGeos);
		}

		// the only way to get here is not to enter the else-branch (-> any
		// button was processed)
		return true;
	}

	@Override
	protected void createAxesAndGridButtons() {

		// ========================================
		// show axes button
		ImageOrText[] axesAndPlaneIcons = ImageOrText.convert(
				new ImageResource[] {
						StyleBarResources.INSTANCE.stylingbar_empty(),
						StyleBarResources.INSTANCE.axes(),
						StyleBar3DResources.INSTANCE.plane(),
						StyleBar3DResources.INSTANCE.axes_plane() }, 24);

		btnShowAxesAndPlane = new AxesAndPlanePopup(app, axesAndPlaneIcons, -1,
		        4, org.geogebra.common.gui.util.SelectionTable.MODE_ICON, getView());
		btnShowAxesAndPlane.addPopupHandler(this);

		// ========================================
		// show grid button
		btnShowGrid3D = new MyToggleButtonForEV(StyleBarResources.INSTANCE.grid());
		btnShowGrid3D.setSelected(ev.getShowGrid());
		btnShowGrid3D.addValueChangeHandler(this);
	}

	@Override
	protected MyToggleButton2 getAxesOrGridToggleButton() {
		return btnShowGrid3D;
	}

	@Override
	protected PopupMenuButton getAxesOrGridPopupMenuButton() {
		return btnShowAxesAndPlane;
	}

	@Override
	protected void addAxesAndGridButtons() {
		add(btnShowAxesAndPlane);
		add(btnShowGrid3D);
	}

	@Override
	protected void addChangeViewButtons() {

		add(btnChangeView);

	}

	@Override
	protected void addBtnRotateView() {

		add(btnRotateView);

	}

	@Override
	protected boolean isVisibleInThisView(GeoElement geo) {
		return geo.isVisibleInView3D();
	}

	@Override
	protected void setToolTips() {

		super.setToolTips();

		Localization loc = app.getLocalization();
		btnRotateView
		        .setToolTipText(loc.getPlainTooltip("stylebar.RotateView"));
		// btnClipping.setToolTipText(loc.getPlainTooltip("stylebar.Clipping"));
		// btnViewProjection.setToolTipText(loc
		// .getPlainTooltip("stylebar.ViewProjection"));
		// btnChangeView.setToolTipText(loc
		// .getPlainTooltip("stylebar.ViewDirection"));
	}

	// @Override
	// public void updateGUI() {
	//
	// super.updateGUI();
	//
	// btnClipping.updateGUI();
	//
	// }

	@Override
	protected PopupMenuButton[] newPopupBtnList() {
		PopupMenuButton[] superList = super.newPopupBtnList();
		PopupMenuButton[] ret = new PopupMenuButton[superList.length + 4];
		for (int i = 0; i < superList.length; i++)
			ret[i] = superList[i];

		int index = superList.length;
		ret[index] = btnRotateView;
		index++;
		ret[index] = btnChangeView;
		// index++;
		// ret[index] = btnClipping;
		// index++;
		// ret[index] = btnViewProjection;
		return ret;
	}

	@Override
	protected void setActionCommands() {
		setActionCommand(btnShowGrid3D, "showGrid");
		setActionCommand(btnStandardView, "standardView");
		setActionCommand(btnPointCapture, "pointCapture");
	}

	@Override
	protected void setAxesAndGridToolTips(Localization loc) {
		btnShowGrid3D.setToolTipText(loc.getPlainTooltip("stylebar.Grid"));
		btnShowAxesAndPlane
		        .setToolTipText(loc.getPlainTooltip("stylebar.Axes"));
	}

	@Override
	protected void updateAxesAndGridGUI() {
		btnShowGrid3D.removeValueChangeHandler();
		btnShowGrid3D.setSelected(ev.getShowGrid());
		btnShowGrid3D.addValueChangeHandler(this);

		btnShowAxesAndPlane.removeActionListener(this);
		btnShowAxesAndPlane.setIndexFromEV();
	}
}
