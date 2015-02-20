package geogebra.geogebra3D.web.euclidian3D;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.geogebra3D.euclidian3D.EuclidianStyleBarStatic3D;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.geogebra3D.web.gui.images.StyleBar3DResources;
import geogebra.html5.main.AppW;
import geogebra.web.euclidian.EuclidianStyleBarW;
import geogebra.web.gui.images.AppResourcesConverter;
import geogebra.web.gui.images.StyleBarResources;
import geogebra.web.gui.util.ImageOrText;
import geogebra.web.gui.util.MyToggleButton2;
import geogebra.web.gui.util.PopupMenuButton;

import java.util.ArrayList;

import com.google.gwt.resources.client.ImageResource;

/**
 * style bar for 3D view
 * 
 * @author mathieu
 *
 */
public class EuclidianStyleBar3DW extends EuclidianStyleBarW {

	private RotateViewPopup btnRotateView;

	private ClippingPopup btnClipping;

	private MyToggleButton2 btnShowGrid3D;

	private AxesAndPlanePopup btnShowAxesAndPlane;

	private PopupMenuButton btnViewDirection;

	private PopupMenuButton btnViewProjection;

	/**
	 * constructor
	 * 
	 * @param ev
	 *            euclidian view
	 */
	public EuclidianStyleBar3DW(EuclidianView ev) {
		super(ev, App.VIEW_EUCLIDIAN3D);
		optionType = optionType.EUCLIDIAN3D;
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

	private void setIcon(PopupMenuButton button, ImageResource icon) {
		AppResourcesConverter.setIcon(icon, button);
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

		// ========================================
		// clipping button
		ImageOrText[] clippingIcons = new ImageOrText[4];
		for (int i = 0; i < 4; i++) {
			clippingIcons[i] = new ImageOrText();
		}
		clippingIcons[0].setUrl(StyleBarResources.INSTANCE.stylingbar_empty()
		        .getSafeUri().asString());
		clippingIcons[1].setUrl(StyleBar3DResources.INSTANCE.clippingSmall()
		        .getSafeUri().asString());
		clippingIcons[2].setUrl(StyleBar3DResources.INSTANCE.clippingMedium()
		        .getSafeUri().asString());
		clippingIcons[3].setUrl(StyleBar3DResources.INSTANCE.clippingBig()
		        .getSafeUri().asString());
		btnClipping = new ClippingPopup(app, clippingIcons, 1, 4,
		        geogebra.common.gui.util.SelectionTable.MODE_ICON, getView());
		// btnClipping = new PopupMenuButtonForView3D(){
		// @Override
		// protected void fireActionPerformed() {
		// getView().setClippingReduction(btnClipping.getSliderValue());
		// }
		//
		// @Override
		// protected void onClickAction(){
		// getView().toggleShowAndUseClippingCube();
		// }
		// };
		// setIcon(btnClipping, StyleBar3DResources.INSTANCE.clippingBig());
		// btnClipping.getMySlider().setMinimum(GeoClippingCube3D.REDUCTION_MIN);
		// btnClipping.getMySlider().setMaximum(GeoClippingCube3D.REDUCTION_MAX);
		// btnClipping.getMySlider().setMajorTickSpacing(1);
		// btnClipping.getMySlider().setMinorTickSpacing(1);
		// btnClipping.getMySlider().setPaintTicks(true);
		// //btnClipping.getMySlider().setPaintTrack(true);
		// //btnClipping.getMySlider().setSnapToTicks(true);
		// btnClipping.setSliderValue(getView().getClippingReduction());
		btnClipping.addPopupHandler(this);

		// ========================================
		/*
		 * rotate x text field textRotateX = new JTextField(3);
		 * textRotateX.addActionListener(this);
		 */

		// ========================================
		// view direction button

		ImageOrText[] directionIcons = new ImageOrText[4];
		for (int i = 0; i < 4; i++) {
			directionIcons[i] = new ImageOrText();
		}
		directionIcons[0].setUrl(StyleBar3DResources.INSTANCE.viewXY()
		        .getSafeUri().asString());
		directionIcons[1].setUrl(StyleBar3DResources.INSTANCE.viewXZ()
		        .getSafeUri().asString());
		directionIcons[2].setUrl(StyleBar3DResources.INSTANCE.viewYZ()
		        .getSafeUri().asString());
		directionIcons[3].setUrl(StyleBar3DResources.INSTANCE
		        .standardViewRotate().getSafeUri().asString());
		btnViewDirection = new ProjectionPopup(app, directionIcons);
		btnViewDirection.addPopupHandler(this);
		// btnViewDefault = new
		// MyToggleButtonForEV(StyleBar3DResources.INSTANCE.standardViewRotate(),
		// iconHeight);
		//
		// btnViewDefault.addValueChangeHandler(this);
		//
		//
		// //========================================
		// // view xy button
		// btnViewXY = new
		// MyToggleButtonForEV(StyleBar3DResources.INSTANCE.viewXY(),
		// iconHeight);
		//
		// btnViewXY.addValueChangeHandler(this);
		//
		// //========================================
		// // view xz button
		// btnViewXZ = new
		// MyToggleButtonForEV(StyleBar3DResources.INSTANCE.viewXZ(),
		// iconHeight);
		//
		// btnViewXZ.addValueChangeHandler(this);
		//
		// //========================================
		// // view yz button
		// btnViewYZ = new
		// MyToggleButtonForEV(StyleBar3DResources.INSTANCE.viewYZ(),
		// iconHeight);
		//
		// btnViewYZ.addValueChangeHandler(this);

		// ========================================
		// projection view button
		ImageOrText[] projectionIcons = new ImageOrText[4];
		for (int i = 0; i < 4; i++) {
			projectionIcons[i] = new ImageOrText();
		}
		projectionIcons[0].setUrl(StyleBar3DResources.INSTANCE
		        .viewOrthographic().getSafeUri().asString());
		projectionIcons[1].setUrl(StyleBar3DResources.INSTANCE.viewPerspective()
		        .getSafeUri().asString());
		projectionIcons[2].setUrl(StyleBar3DResources.INSTANCE.viewGlasses()
		        .getSafeUri().asString());
		projectionIcons[3].setUrl(StyleBar3DResources.INSTANCE.viewOblique()
		        .getSafeUri().asString());
		btnViewProjection = new ProjectionPopup(app, projectionIcons);
		btnViewProjection.addPopupHandler(this);

	}

	private class ProjectionPopup extends PopupMenuButton {
		private static final long serialVersionUID = 1L;

		public ProjectionPopup(AppW app, ImageOrText[] projectionIcons) {
			super(app, projectionIcons, 1, projectionIcons.length,
			        geogebra.common.gui.util.SelectionTable.MODE_ICON, true,
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

			this.playIcon = new ImageOrText();
			this.playIcon.setUrl(playIcon.getSafeUri().asString());
			this.pauseIcon = new ImageOrText();
			this.pauseIcon.setUrl(pauseIcon.getSafeUri().asString());

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
			        geogebra.common.gui.util.SelectionTable.MODE_ICON, false,
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
	protected void processSource(Object source, ArrayList<GeoElement> targetGeos) {

		// App.debug("source = "+source);

		/*
		 * if (source.equals(btnShowPlane)) { getView().togglePlane();
		 * getView().repaintView();
		 */

		if (source.equals(btnClipping)) {
			int index = btnClipping.getSelectedIndex();
			if (index == 0) {
				getView().setUseClippingCube(false);
				getView().setShowClippingCube(false);
			} else {
				getView().setUseClippingCube(true);
				getView().setShowClippingCube(true);
				getView().getSettings().setClippingReduction(index - 1);
			}
			getView().repaintView();

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
				getView().setRotAnimation(EuclidianView3D.ANGLE_ROT_OZ,
				        EuclidianView3D.ANGLE_ROT_XOY, false);
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
			getView().repaintView();
		} else
			super.processSource(source, targetGeos);
	}

	@Override
	protected void createAxesAndGridButtons() {

		// ========================================
		// show axes button
		ImageOrText[] axesAndPlaneIcons = new ImageOrText[4];
		for (int i = 0; i < 4; i++) {
			axesAndPlaneIcons[i] = new ImageOrText();
		}
		axesAndPlaneIcons[0].setUrl(StyleBarResources.INSTANCE
		        .stylingbar_empty().getSafeUri().asString());
		axesAndPlaneIcons[1].setUrl(StyleBarResources.INSTANCE.axes()
		        .getSafeUri().asString());
		axesAndPlaneIcons[2].setUrl(StyleBar3DResources.INSTANCE.plane()
		        .getSafeUri().asString());
		axesAndPlaneIcons[3].setUrl(StyleBar3DResources.INSTANCE.axes_plane()
		        .getSafeUri().asString());

		btnShowAxesAndPlane = new AxesAndPlanePopup(app, axesAndPlaneIcons, -1,
		        4, geogebra.common.gui.util.SelectionTable.MODE_ICON, getView());
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
	protected void setToolTips() {

		super.setToolTips();

		Localization loc = app.getLocalization();
		btnRotateView
		        .setToolTipText(loc.getPlainTooltip("stylebar.RotateView"));
		btnClipping.setToolTipText(loc.getPlainTooltip("stylebar.Clipping"));
		btnViewProjection.setToolTipText(loc
		        .getPlainTooltip("stylebar.ViewProjection"));
		btnViewDirection.setToolTipText(loc
		        .getPlainTooltip("stylebar.ViewDirection"));
	}

	@Override
	public void updateGUI() {

		super.updateGUI();

		btnClipping.updateGUI();

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
