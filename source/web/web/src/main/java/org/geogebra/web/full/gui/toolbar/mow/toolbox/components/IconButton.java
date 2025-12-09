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

package org.geogebra.web.full.gui.toolbar.mow.toolbox.components;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ResourcePrototype;

public class IconButton extends StandardButton implements SetLabels {
	private static final int DEFAULT_BUTTON_WIDTH = 24;
	protected IconSpec image;
	private final String ariaLabelTransKey;
	private String dataTitleTransKey;
	private final Localization localization;

	protected String selectionColor;

	/**
	 * Press icon button with given aria-label.
	 * @param appW {@link AppW}
	 * @param icon {@link IconSpec} image
	 * @param ariaLabel aria-label
	 * @param onHandler on press handler
	 */
	public IconButton(AppW appW, IconSpec icon, String ariaLabel, Runnable onHandler) {
		this(appW, icon, ariaLabel);
		addFastClickHandler(event -> {
			if (!isDisabled() && onHandler != null) {
				onHandler.run();
				setActive(true);
			}
		});
	}

	protected IconButton(AppW appW, IconSpec icon, String ariaLabel) {
		super(icon, DEFAULT_BUTTON_WIDTH);
		addStyleName("iconButton");
		image = icon;
		selectionColor = getSelectionColor(appW);
		AriaHelper.setLabel(this, appW.getLocalization().getMenu(ariaLabel));
		ariaLabelTransKey = ariaLabel;
		localization = appW.getLocalization();
	}

	/**
	 * Toggle icon button with given aria-label, data-title.
	 * @param appW {@link AppW}
	 * @param icon {@link IconSpec} image
	 * @param ariaLabel aria-label
	 * @param dataTitle data-title
	 * @param onHandler switch on handler
	 * @param offHandler switch off handler
	 */
	public IconButton(AppW appW, IconSpec icon, String ariaLabel, String dataTitle,
			Runnable onHandler, Runnable offHandler) {
		this(appW, icon, ariaLabel);
		dataTitleTransKey = dataTitle;
		AriaHelper.setTitle(this, appW.getLocalization().getMenu(dataTitle));
		addFastClickHandler(event -> {
			if (!isDisabled()) {
				if (isActive() && offHandler != null) {
					offHandler.run();
				} else {
					onHandler.run();
				}
				setActive(!isActive());
			}
		});
	}

	/**
	 * Toggle icon button with given aria-label, data-title and data-test.
	 * @param appW {@link AppW}
	 * @param icon {@link IconSpec} image
	 * @param ariaLabel aria-label
	 * @param dataTitle data-title
	 * @param dataTest data-test
	 * @param onHandler switch on handler
	 * @param offHandler switch off handler
	 */
	public IconButton(AppW appW, IconSpec icon, String ariaLabel, String dataTitle,
			String dataTest, Runnable onHandler, Runnable offHandler) {
		this(appW, icon, ariaLabel, dataTitle, onHandler, offHandler);
		TestHarness.setAttr(this, dataTest);
	}

	/**
	 * Press button with given aria-label and data-title.
	 * @param appW {@link AppW}
	 * @param icon {@link IconSpec} image
	 * @param ariaLabel aria-label
	 * @param dataTitle data-title
	 * @param dataTest data-test
	 * @param onHandler on press handler
	 */
	public IconButton(AppW appW, IconSpec icon, String ariaLabel, String dataTitle,
			String dataTest, Runnable onHandler) {
		this(appW, icon, ariaLabel, onHandler);
		dataTitleTransKey = dataTitle;
		AriaHelper.setTitle(this, appW.getLocalization().getMenu(dataTitle));
		TestHarness.setAttr(this, dataTest);
	}

	/**
	 * Small press icon buttons, with identical aria-label and data-title.
	 * @param appW {@link AppW}
	 * @param icon {@link IconSpec} image
	 * @param ariaLabel aria-label (also used as data-title)
	 * @param clickHandler click handler
	 */
	public IconButton(AppW appW, Runnable clickHandler, IconSpec icon,
			String ariaLabel) {
		this(appW, icon, ariaLabel);
		if (ariaLabel != null && !ariaLabel.isBlank()) {
			dataTitleTransKey = ariaLabel;
			AriaHelper.setTitle(this, appW.getLocalization().getMenu(dataTitleTransKey));
		}
		addStyleName("small");
		selectionColor = getSelectionColor(appW);
		addFastClickHandler((event) -> {
			if (clickHandler != null) {
				clickHandler.run();
			}
		});
	}

	/**
	 * Small press icon buttons, only with aria label (without data-title).
	 * @param appW {@link AppW}
	 * @param ariaLabel aria-label
	 * @param icon {@link IconSpec} image
	 */
	public IconButton(AppW appW, String ariaLabel, IconSpec icon) {
		this(appW, icon, ariaLabel);
		addStyleName("small");
	}

	/**
	 * Disable button.
	 * @param isDisabled whether is disabled or not
	 */
	public void setDisabled(boolean isDisabled) {
		AriaHelper.setAriaDisabled(this, isDisabled);
		Dom.toggleClass(this, "disabled", isDisabled);
	}

	/**
	 * @param isActive whether is on or off
	 */
	public void setActive(boolean isActive) {
		AriaHelper.setPressedState(this, isActive);
		Dom.toggleClass(this, "active", isActive);
		setIcon(image.withFill(isActive ? selectionColor : GColor.BLACK.toString()));
	}

	/**
	 * Remove active state.
	 */
	public void deactivate() {
		AriaHelper.setPressedState(this, false);
		Dom.toggleClass(this, "active", false);
		setIcon(image.withFill(GColor.BLACK.toString()));
	}

	public boolean isActive() {
		return getElement().hasClassName("active");
	}

	private boolean isDisabled() {
		return getElement().hasClassName("disabled");
	}

	/**
	 * Updates the image and the aria attributes
	 * @param image image
	 * @param mode tool mode
	 * @param appW {@link AppW}
	 */
	public void updateImgAndTxt(IconSpec image, int mode, AppW appW) {
		this.image = isActive() ? image.withFill(selectionColor) : image;
		setIcon(image);
		setAltText(appW.getToolAriaLabel(mode));
		AriaHelper.setDataTitle(this, appW.getToolName(mode));
		TestHarness.setAttr(this, "selectModeButton" + mode);
	}

	/**
	 * Updates the aria label and the data title for this Icon Button (e.g. when language changes).
	 */
	@Override
	public void setLabels() {
		String ariaLabel = localization.getMenu(ariaLabelTransKey);
		String dataTitle = localization.getMenu(dataTitleTransKey);
		AriaHelper.setLabel(this, ariaLabel);
		AriaHelper.setDataTitle(this, dataTitle);
	}

	/**
	 * Checks if a mode is reachable from here, directly or via popup.
	 * @param mode app mode
	 * @return whether the mode can be activated with this button
	 */
	public boolean containsMode(int mode) {
		return false;
	}

	private String getSelectionColor(AppW appW) {
		return appW.getGeoGebraElement().getDarkColor(appW.getFrameElement());
	}

	@Override
	public void setIcon(ResourcePrototype icon) {
		SVGResource svgResource = isActive()
				? ((SVGResource) icon).withFill(selectionColor) : (SVGResource) icon;
		super.setIcon(svgResource);
		image = new ImageIconSpec(svgResource);
	}

}
