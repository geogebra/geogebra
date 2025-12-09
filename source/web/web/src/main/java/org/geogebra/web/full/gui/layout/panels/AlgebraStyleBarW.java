/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.layout.panels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.full.gui.util.StyleBarW2;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.main.AppW;

/**
 * StyleBar for AlgebraView
 */
public class AlgebraStyleBarW extends StyleBarW2 implements SettingListener {

	/** button to open the popup with the supported tree-modes */
	PopupMenuButtonW treeModeButton;
	/** button for description mode */
	PopupMenuButtonW descriptionButton;
	/** list of all supported {@link SortMode modes} */
	ArrayList<SortMode> supportedModes = new ArrayList<>();

	private GeoElement selectedEntry;
	/** localization */
	private final Localization loc;
	private final List<AlgebraStyle> algebraStyles;

	/**
	 * @param app
	 *            {@link AppW}
	 */
	public AlgebraStyleBarW(AppW app) {
		super(app, App.VIEW_ALGEBRA);
		app.getSettings().getAlgebra().addListener(this);
		this.loc = app.getLocalization();
		this.algebraStyles = AlgebraStyle.getAvailableValues(app);
		update(null);

		createColorBtn();
		createLineStyleBtn();
		createPointStyleBtn(-1);

		ClickStartHandler.init(this, new ClickStartHandler(false, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				// nothing to do here
			}
		});
		optionType = OptionType.ALGEBRA;
	}

	/**
	 * @param selectedItem
	 *            selected element
	 */
	public void update(GeoElement selectedItem) {

		this.selectedEntry = selectedItem;

		clear();

		if (selectedItem == null) {
			addTreeModeButton();
			addDescriptionButton();
			addMenuButton();
		} else {
			add(btnColor);
			btnColor.update(Collections.singletonList(selectedItem));
			add(btnLineStyle);
			btnLineStyle.update(Collections.singletonList(selectedItem));
			add(btnPointStyle);
			btnPointStyle.update(Collections.singletonList(selectedItem));
			addMenuButton();
		}
		// addViewButton is too expensive
		if (!app.isUnbundledOrWhiteboard()) {
			if (getViewButton() == null) {
				addViewButton();
			} else {
				add(getViewButton());
			}
		}
		setToolTips();
	}

	private void addTreeModeButton() {
		supportedModes.clear();
		supportedModes.add(SortMode.DEPENDENCY);
		supportedModes.add(SortMode.TYPE);
		supportedModes.add(SortMode.ORDER);
		supportedModes.add(SortMode.LAYER);

		ImageOrText[] strTreeMode = getTreeModeStr();
		if (treeModeButton == null) {
			treeModeButton = new PopupMenuButtonW(app, strTreeMode,
					strTreeMode.length, 1, SelectionTable.MODE_TEXT);
	
			ImageOrText icon = new ImageOrText(
					MaterialDesignResources.INSTANCE.sortObjects());
			treeModeButton.setFixedIcon(icon);
	
			treeModeButton.addFastClickHandler(event -> {
				SortMode selectedMode = app.getAlgebraView().getTreeMode();
				treeModeButton.setSelectedIndex(supportedModes
						.indexOf(selectedMode));
			});
	
			treeModeButton.addPopupHandler(index -> {
				// called if a object of the popup is clicked
				app.getSettings().getAlgebra()
						.setTreeMode(supportedModes.get(index));
				app.closePopups();
			});
		}
		add(treeModeButton);
	}

	private void addDescriptionButton() {
		ImageOrText[] strTreeMode = getDescriptionModes();
		if (descriptionButton == null) {
			descriptionButton = new PopupMenuButtonW(app, strTreeMode,
					strTreeMode.length, 1, SelectionTable.MODE_TEXT);

			ImageOrText icon = new ImageOrText(
					MaterialDesignResources.INSTANCE.description(), 24);
			descriptionButton.setFixedIcon(icon);

			descriptionButton.addFastClickHandler(event ->
					descriptionButton.setSelectedIndex(
							algebraStyles.indexOf(app.getAlgebraStyle())));

			descriptionButton.addPopupHandler(index -> {
				// called if a object of the popup is clicked
				app.getSettings().getAlgebra().setStyle(algebraStyles.get(index));
				if (app.getGuiManager().hasPropertiesView()) {
					app.getGuiManager().getPropertiesView().repaintView();
				}
				app.getKernel().updateConstruction(false);
				app.closePopups();
			});
		}
		add(descriptionButton);
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
			modes[i] = loc.getMenu(supportedModes.get(i).toString());
		}
		return ImageOrText.convert(modes);
    }

	private ImageOrText[] getDescriptionModes() {
		return ImageOrText.convert(AlgebraSettings.getDescriptionModes(app).toArray(new String[0]));
	}

	private void setToolTips() {
		treeModeButton.setTitle(app.getLocalization().getPlainTooltip("SortBy"));
		descriptionButton.setTitle(app.getLocalization().getMenu(
				"AlgebraDescriptions"));
	}

	@Override
	public void setOpen(boolean showStyleBar) {
		// TODO Auto-generated method stub
	}

	@Override
	public void settingsChanged(AbstractSettings settings) {
		//
	}

	@Override
	public void setLabels() {
		super.setLabels();
		this.treeModeButton.getMyTable().updateText(getTreeModeStr());
		this.descriptionButton.getMyTable().updateText(getDescriptionModes());
		setToolTips();
	}

	@Override
	protected ArrayList<GeoElement> getTargetGeos() {
		ArrayList<GeoElement> targetGeos = new ArrayList<>();
		if (selectedEntry != null) {
			targetGeos.add(selectedEntry);
		}
		return targetGeos;
	}
}
