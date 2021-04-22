package org.geogebra.web.geogebra3D.web.euclidian3D;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianStyleBarStatic3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.euclidian.EuclidianStyleBarW;
import org.geogebra.web.full.euclidian.MyToggleButtonWforEV;
import org.geogebra.web.full.gui.util.MyToggleButtonW;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.resources.SVGResource;

/**
 * style bar for 3D view
 * 
 * @author mathieu
 *
 */
public class EuclidianStyleBar3DW extends EuclidianStyleBarW {

	private RotateViewPopup btnRotateView;

	// private ClippingPopup btnClipping;

	private MyToggleButtonW btnShowGrid3D;

	private AxesAndPlanePopup btnShowAxesAndPlane;

	private PopupMenuButtonW btnViewProjection;

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
		EuclidianStyleBarStatic3D.addToDefaultMap(selection.getDefaultMap());
	}

	@Override
	protected void createButtons() {

		super.createButtons();

		getBtnPointStyle().setEuclidian3D(true);

		// ========================================
		// rotate view button
		btnRotateView = new RotateViewPopup(
		        this, MaterialDesignResources.INSTANCE.rotateViewPlay(),
				MaterialDesignResources.INSTANCE.rotateViewPause());
		btnRotateView.addPopupHandler(this);

		// // ========================================
		// // projection view button
		ImageOrText[] projectionIcons = ImageOrText.convert(
				new SVGResource[] {
						MaterialDesignResources.INSTANCE.projection_orthographic(),
						MaterialDesignResources.INSTANCE.projection_perspective(),
						MaterialDesignResources.INSTANCE.projection_glasses(),
						MaterialDesignResources.INSTANCE.projection_oblique() }, 24);
		btnViewProjection = new ProjectionPopup(app, projectionIcons);
		btnViewProjection.setSelectedIndex(((EuclidianSettings3D) ev
				.getSettings()).getProjection());
		btnViewProjection.addPopupHandler(this);
	}

	@Override
	protected void createChangeViewButtons() {
		ImageOrText[] directionIcons = ImageOrText.convert(new SVGResource[] {
				MaterialDesignResources.INSTANCE.home_black(),
				MaterialDesignResources.INSTANCE.show_all_objects_black(),
				MaterialDesignResources.INSTANCE.standardViewRotate(),
				MaterialDesignResources.INSTANCE.viewXY(),
				MaterialDesignResources.INSTANCE.viewXZ(),
				MaterialDesignResources.INSTANCE.viewYZ() }, 24);

		btnChangeView = new ProjectionPopup(app, directionIcons);
		btnChangeView.setIcon(directionIcons[getView().getProjection()]);
		btnChangeView.addPopupHandler(this);
	}

	@Override
	protected boolean isBackground() {
		return (btnShowGrid3D != null && btnShowGrid3D.isVisible());
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
	protected void setDirection(int si) {
		switch (si) {
		default:
		case 0: // standard view
			getView().setStandardView(true);
			break;
		case 1: // show all objects
			getView().setViewShowAllObjects(true, false);
			break;
		case 2: // standard view orientation
			getView().setRotAnimation(EuclidianView3DInterface.ANGLE_ROT_OZ,
					EuclidianView3DInterface.ANGLE_ROT_XOY, false);
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
	}

	@Override
	protected void createAxesAndGridButtons() {

		// ========================================
		// show axes button
		ImageOrText[] axesAndPlaneIcons = ImageOrText.convert(
				new SVGResource[] {
						MaterialDesignResources.INSTANCE.stylebar_empty(),
						MaterialDesignResources.INSTANCE.axes_black(),
						MaterialDesignResources.INSTANCE.plane(),
						MaterialDesignResources.INSTANCE.axes_plane() }, 24);

		btnShowAxesAndPlane = new AxesAndPlanePopup(app, axesAndPlaneIcons,
				getView());
		btnShowAxesAndPlane.addPopupHandler(this);

		// ========================================
		// show grid button
		btnShowGrid3D = new MyToggleButtonWforEV(
				MaterialDesignResources.INSTANCE.grid_black(), this);
		btnShowGrid3D.setSelected(ev.getShowGrid());
		btnShowGrid3D.addValueChangeHandler(this);
	}

	@Override
	protected MyToggleButtonW getAxesOrGridToggleButton() {
		return btnShowGrid3D;
	}

	@Override
	protected PopupMenuButtonW getAxesOrGridPopupMenuButton() {
		return btnShowAxesAndPlane;
	}

	@Override
	protected void addAxesAndGridButtons() {
		add(btnShowAxesAndPlane);
		add(btnShowGrid3D);
	}

	@Override
	protected void addBtnRotateView() {
		add(btnViewProjection);
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
		btnViewProjection.setToolTipText(loc
				.getPlainTooltip("stylebar.ViewProjection"));
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
	protected MyToggleButtonW[] newToggleBtnList() {
		MyToggleButtonW[] superList = super.newToggleBtnList();
		
		if (!app.isUnbundledOrWhiteboard()) {
			return superList;
		}

		MyToggleButtonW[] ret = new MyToggleButtonW[superList.length + 1];
		for (int i = 0; i < superList.length; i++) {
			ret[i] = superList[i];
		}
		ret[superList.length] = btnShowGrid3D;
		return ret;
	}

	@Override
	protected PopupMenuButtonW[] newPopupBtnList() {
		PopupMenuButtonW[] superList = super.newPopupBtnList();
		PopupMenuButtonW[] ret = new PopupMenuButtonW[superList.length + 4];

		// -1: skip change view popup and insert it after rotate
		for (int i = 0; i < superList.length - 1; i++) {
			ret[i] = superList[i];
		}

		int index = superList.length;
		ret[index] = btnRotateView;
		index++;
		ret[index] = btnChangeView;
		// index++;
		// ret[index] = btnClipping;
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

		btnShowAxesAndPlane.setIndexFromEV();
	}

	@Override
	protected boolean processSource(Object source,
			ArrayList<GeoElement> targetGeos) {
		if (source.equals(btnViewProjection)) {
			int si = btnViewProjection.getSelectedIndex();
			getView().getSettings().setProjection(si);
			getView().repaint();
			return true;
		}
		return super.processSource(source, targetGeos);
	}
}
