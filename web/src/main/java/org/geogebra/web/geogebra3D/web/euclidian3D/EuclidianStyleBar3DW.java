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
import org.geogebra.web.full.euclidian.ToggleButtonWforEV;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.full.gui.util.ToggleButton;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.resources.SVGResource;

/**
 * style bar for 3D view
 */
public class EuclidianStyleBar3DW extends EuclidianStyleBarW {

	private RotateViewPopup btnRotateView;
	private ToggleButton btnShowGrid3D;
	private AxesAndPlanePopup btnShowAxesAndPlane;
	private PopupMenuButtonW btnViewProjection;

	/**
	 * constructor
	 * @param ev - euclidian view
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

		btnRotateView = new RotateViewPopup(
		        this, MaterialDesignResources.INSTANCE.rotateViewPlay(),
				MaterialDesignResources.INSTANCE.rotateViewPause());
		btnRotateView.addPopupHandler(this);

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
		btnChangeView.setFixedIcon(new ImageOrText(MaterialDesignResources.INSTANCE.home_black(),
				24));
		btnChangeView.addPopupHandler(this);
	}

	@Override
	protected boolean isBackground() {
		return btnShowGrid3D != null && btnShowGrid3D.isVisible();
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
		case 0:
			getView().setStandardView(true);
			break;
		case 1:
			getView().setViewShowAllObjects(true, false);
			break;
		case 2:
			getView().setRotAnimation(EuclidianView3DInterface.ANGLE_ROT_OZ,
					EuclidianView3DInterface.ANGLE_ROT_XOY, false);
			break;
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
		ImageOrText[] axesAndPlaneIcons = ImageOrText.convert(
				new SVGResource[] {
						MaterialDesignResources.INSTANCE.stylebar_empty(),
						MaterialDesignResources.INSTANCE.axes_black(),
						MaterialDesignResources.INSTANCE.plane(),
						MaterialDesignResources.INSTANCE.axes_plane() }, 24);

		btnShowAxesAndPlane = new AxesAndPlanePopup(app, axesAndPlaneIcons,
				getView());
		btnShowAxesAndPlane.addPopupHandler(this);

		btnShowGrid3D = new ToggleButtonWforEV(
				MaterialDesignResources.INSTANCE.grid_black(), this);
		btnShowGrid3D.setSelected(ev.getShowGrid());
		btnShowGrid3D.addFastClickHandler(this);
	}

	@Override
	protected ToggleButton getAxesOrGridToggleButton() {
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
		        .setTitle(loc.getPlainTooltip("stylebar.RotateView"));
		btnViewProjection.setTitle(loc
				.getPlainTooltip("stylebar.ViewProjection"));
	}

	@Override
	protected ToggleButton[] newToggleBtnList() {
		ToggleButton[] superList = super.newToggleBtnList();
		
		if (!app.isUnbundledOrWhiteboard()) {
			return superList;
		}

		ToggleButton[] ret = new ToggleButton[superList.length + 1];
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
		setActionCommand(btnPointCapture, "pointCapture");
	}

	@Override
	protected void setAxesAndGridToolTips(Localization loc) {
		btnShowGrid3D.setTitle(loc.getPlainTooltip("stylebar.Grid"));
		btnShowAxesAndPlane
		        .setTitle(loc.getPlainTooltip("stylebar.Axes"));
	}

	@Override
	protected void updateAxesAndGridGUI() {
		btnShowGrid3D.setSelected(ev.getShowGrid());
		btnShowGrid3D.addFastClickHandler(this);

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
