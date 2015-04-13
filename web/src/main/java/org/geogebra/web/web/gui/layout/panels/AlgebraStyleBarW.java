package org.geogebra.web.web.gui.layout.panels;

import java.util.ArrayList;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.color.ColorPopupMenuButton;
import org.geogebra.web.web.gui.images.StyleBarResources;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.MyToggleButton2;
import org.geogebra.web.web.gui.util.PopupMenuButton;
import org.geogebra.web.web.gui.util.PopupMenuHandler;
import org.geogebra.web.web.gui.util.StyleBarW2;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

/**
 * StyleBar for AlgebraView
 */
public class AlgebraStyleBarW extends StyleBarW2 implements
		ValueChangeHandler<Boolean>, SettingListener {

	/** button to show hide auxiliary objects */
	private MyToggleButton2 auxiliary;
	/** button to open the popup with the supported tree-modes */
	PopupMenuButton treeModeButton;
	PopupMenuButton newObjectButton;
	/** list of all supported {@link SortMode modes} */
	ArrayList<SortMode> supportedModes = new ArrayList<SortMode>();

	private GeoElement selectedEntry;

	/**
	 * @param app
	 *            {@link AppW}
	 */
	public AlgebraStyleBarW(AppW app){
		super(app, App.VIEW_ALGEBRA);
		app.getSettings().getAlgebra().addListener(this);

		if (app.isPrerelease()) {
			update(null);
		} else {
			addAuxiliaryButton();
			addTreeModeButton();
			addViewButton();
			setLabels();
		}

		createColorBtn();
		btnColor.setChangeEventHandler(this);
		createLineStyleBtn(-1);
		createPointStyleBtn(-1);
		btnPointStyle.setChangeEventHandler(this);

		ClickStartHandler.init(this, new ClickStartHandler(false, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				// nothing to do here
			}
		});
	}

	private void createColorBtn() {

		final GDimensionW colorIconSize = new GDimensionW(20, ICON_HEIGHT);
		btnColor = new ColorPopupMenuButton(app, colorIconSize,
				ColorPopupMenuButton.COLORSET_DEFAULT, true) {

			@Override
			public void update(Object[] geos) {

				boolean geosOK = geos.length > 0;
				for (int i = 0; i < geos.length; i++) {
					GeoElement geo = ((GeoElement) geos[i])
							.getGeoElementForPropertiesDialog();
					if (geo instanceof GeoImage || geo instanceof GeoText
							|| geo instanceof GeoButton) {
						geosOK = false;
						break;
					}
				}

				setVisible(geosOK);

				if (geosOK) {
					// get color from first geo
					org.geogebra.common.awt.GColor geoColor;
					geoColor = ((GeoElement) geos[0]).getObjectColor();

					// check if selection contains a fillable geo
					// if true, then set slider to first fillable's alpha
					// value
					float alpha = 1.0f;
					boolean hasFillable = false;
					for (int i = 0; i < geos.length; i++) {
						if (((GeoElement) geos[i]).isFillable()) {
							hasFillable = true;
							alpha = ((GeoElement) geos[i]).getAlphaValue();
							break;
						}
					}

					if (hasFillable)
						setTitle(app.getPlain("stylebar.ColorTransparency"));
					else
						setTitle(app.getPlain("stylebar.Color"));
					setSliderVisible(hasFillable);

					setSliderValue(Math.round(alpha * 100));

					updateColorTable();

					// find the geoColor in the table and select it
					int index = this.getColorIndex(geoColor);
					setSelectedIndex(index);
					setDefaultColor(alpha, geoColor);

					this.setKeepVisible(false);
				}
			}
		};
		btnColor.addPopupHandler(this);
	}

	public void update(GeoElement selectedItem) {

		if (!app.isPrerelease()) {
			return;
		}

		this.selectedEntry = selectedItem;

		clear();

		if (selectedItem == null) {
			addAuxiliaryButton();
			addTreeModeButton();

			// to test ticket #4905, this shall be uncommented
			// addNewObjectButton();
		} else {
			add(btnColor);
			btnColor.update(new Object[] { selectedItem });
			add(btnLineStyle);
			btnLineStyle.update(new Object[] { selectedItem });
			add(btnPointStyle);
			btnPointStyle.update(new Object[] { selectedItem });
			addMenuButton();
		}
		//addViewButton is too expensive
		if(getViewButton() == null){
			addViewButton();
		}else{
			add(getViewButton());
		}
		setToolTips();
	}

	/**
	 * @param app
	 */
	private void addAuxiliaryButton() {
		auxiliary = new MyToggleButton2(StyleBarResources.INSTANCE.auxiliary());
		auxiliary.setDown(app.showAuxiliaryObjects());
		auxiliary.addValueChangeHandler(this);
		add(auxiliary);
	}

	private void addNewObjectButton() {

		String[] modes = new String[3];
		modes[0] = app.getPlain("Piecewise function");
		modes[1] = app.getPlain("Matrix");
		modes[2] = app.getPlain("Parametric curve");
		ImageOrText[] strNewObjectClickable = ImageOrText.convert(modes);

		newObjectButton = new PopupMenuButton(app, strNewObjectClickable,
		        strNewObjectClickable.length, 1,
		        org.geogebra.common.gui.util.SelectionTable.MODE_TEXT);

		ImageOrText icon = new ImageOrText();
		icon.setUrl(StyleBarResources.INSTANCE.point_cross().getSafeUri().asString());
		newObjectButton.setFixedIcon(icon);

		newObjectButton.addPopupHandler(new PopupMenuHandler() {
			@Override
			public void fireActionPerformed(PopupMenuButton actionButton) {
				// called if a object of the popup is clicked
				App.debug("newObjectButton clicked");

				switch( newObjectButton.getSelectedIndex() ) {
					// TODO
					
				}
			}
		});
		add(newObjectButton);
	}

	private void addTreeModeButton() {
		supportedModes.clear();
		supportedModes.add(SortMode.DEPENDENCY);
		supportedModes.add(SortMode.TYPE);
		supportedModes.add(SortMode.ORDER);

		ImageOrText[] strTreeMode = getTreeModeStr();
		if(treeModeButton == null){
			treeModeButton = new PopupMenuButton(app, strTreeMode,
			        strTreeMode.length, 1,
			        org.geogebra.common.gui.util.SelectionTable.MODE_TEXT);
	
			ImageOrText icon = new ImageOrText();
			icon.setUrl(StyleBarResources.INSTANCE.sortObjects().getSafeUri()
			        .asString());
			treeModeButton.setFixedIcon(icon);
	
			treeModeButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					SortMode selectedMode = app.getAlgebraView().getTreeMode();
					treeModeButton.setSelectedIndex(supportedModes
					        .indexOf(selectedMode));
				}
			});
	
			treeModeButton.addPopupHandler(new PopupMenuHandler() {
				@Override
				public void fireActionPerformed(PopupMenuButton actionButton) {
					// called if a object of the popup is clicked
					int i = treeModeButton.getSelectedIndex();
					app.getAlgebraView().setTreeMode(supportedModes.get(i));
				}
			});
		}
		add(treeModeButton);
	}

	/**
	 * creates an array from all available supported modes and converts it to an
	 * array of {@link ImageOrText} elements
	 * 
	 * @return {@link ImageOrText ImageOrText[]}
	 */
    private ImageOrText[] getTreeModeStr() {
		String[] modes = new String[supportedModes.size()];
		for (int i = 0; i < supportedModes.size(); i++) {
			modes[i] = app.getPlain(supportedModes.get(i).toString());
		}
		return ImageOrText.convert(modes);
    }

	private void setToolTips() {
		auxiliary.setToolTipText(app.getLocalization().getPlain(
		        "AuxiliaryObjects"));
		treeModeButton.setToolTipText(app.getLocalization().getPlainTooltip(
		        "SortBy"));
	}

	@Override
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		if (event.getSource() == auxiliary) {
			app.setShowAuxiliaryObjects(auxiliary.isDown());
		}
	}

	@Override
	public void setOpen(boolean showStyleBar) {
		// TODO Auto-generated method stub
	}

	@Override
	public void settingsChanged(AbstractSettings settings) {
		AlgebraSettings as = (AlgebraSettings) settings;
		auxiliary.setDown(as.getShowAuxiliaryObjects());
	}

	@Override
	public void setLabels() {
		super.setLabels();
		this.treeModeButton.getMyTable().updateText(getTreeModeStr());
		setToolTips();
	}


	@Override
	protected void handleEventHandlers(Object source) {
		needUndo = false;

		ArrayList<GeoElement> targetGeos = new ArrayList<GeoElement>();

		if (selectedEntry != null) {
			targetGeos.add(selectedEntry);
		}

		processSource(source, targetGeos);

		if (needUndo) {
			app.storeUndoInfo();
			needUndo = false;
		}
	}
}
