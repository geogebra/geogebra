package org.geogebra.web.full.gui.toolbar.mow.toolbox.components;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ERASER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_HIGHLIGHTER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PEN;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.toolbox.ToolboxIcon;
import org.geogebra.web.html5.main.toolbox.ToolboxIconResource;
import org.geogebra.web.html5.util.TestHarness;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.resources.SVGResourcePrototype;
import org.gwtproject.resources.client.ResourcePrototype;

public class IconButton extends StandardButton implements SetLabels {
	private static final int DEFAULT_BUTTON_WIDTH = 24;
	private IconSpec image;
	private final String ariaLabelTransKey;
	private String dataTitleTransKey;
	private int mode = -1;
	private final Localization localization;
	private AppW appW;
	private String selectionColor;

	/**
	 * Constructor
	 * @param mode - tool mode
	 * @param appW - application
	 */
	public IconButton(int mode, AppW appW) {
		this(appW.getLocalization(), new ImageIconSpec(SVGResourcePrototype.EMPTY),
				appW.getToolAriaLabel(mode));
		this.mode = mode;
		this.appW = appW;
		selectionColor = getSelectionColor(appW);
		AriaHelper.setDataTitle(this, appW.getToolName(mode));
		image = getIconFromMode(mode, appW.getToolboxIconResource());
		setActive(getElement().hasClassName("active"));
		addStyleName("iconButton");
	}

	/** Constructor
	 * @param mode - tool mode
	 * @param appW - application
	 * @param icon - image
	 * @param onHandler - switch on handler
	 */
	public IconButton(int mode, AppW appW, IconSpec icon, Runnable onHandler) {
		this(appW, icon, appW.getToolAriaLabel(mode), appW.getToolAriaLabel(mode),
				appW.getToolAriaLabel(mode), onHandler);
		this.appW = appW;
		this.mode = mode;
	}

	/**
	 * Constructor press icon button
	 * @param loc - localization
	 * @param icon - image
	 * @param ariaLabel - label
	 * @param onHandler - on press handler
	 */
	public IconButton(Localization loc, IconSpec icon, String ariaLabel, Runnable onHandler) {
		this(loc, icon, ariaLabel);
		addFastClickHandler(event -> {
			if (!isDisabled() && onHandler != null) {
				onHandler.run();
				setActive(true);
			}
		});
	}

	private IconButton(Localization loc, IconSpec icon, String ariaLabel) {
		super(icon, DEFAULT_BUTTON_WIDTH);
		addStyleName("iconButton");
		image = icon;
		AriaHelper.setLabel(this, loc.getMenu(ariaLabel));
		ariaLabelTransKey = ariaLabel;
		localization = loc;
	}

	/**
	 * Constructor toggle icon button
	 * @param appW - application
	 * @param icon - image
	 * @param ariaLabel - label
	 * @param dataTitle - tooltip
	 * @param onHandler - switch on handler
	 * @param offHandler - switch off handler
	 */
	public IconButton(AppW appW, IconSpec icon, String ariaLabel, String dataTitle,
			Runnable onHandler, Runnable offHandler) {
		this(appW.getLocalization(), icon, ariaLabel);
		dataTitleTransKey = dataTitle;
		selectionColor = getSelectionColor(appW);
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
	 * Constructor toggle icon button with dataTest
	 * @param appW - application
	 * @param icon - image
	 * @param ariaLabel - label
	 * @param dataTitle - tooltip
	 * @param dataTest - id for ui test
	 * @param onHandler - switch on handler
	 * @param offHandler - switch off handler
	 */
	public IconButton(AppW appW, IconSpec icon, String ariaLabel, String dataTitle,
			String dataTest, Runnable onHandler, Runnable offHandler) {
		this(appW, icon, ariaLabel, dataTitle, onHandler, offHandler);
		TestHarness.setAttr(this, dataTest);
	}

	/**
	 * Constructor
	 * @param appW - application
	 * @param icon - image
	 * @param ariaLabel - label
	 * @param dataTitle - title
	 * @param dataTest - test
	 * @param onHandler - on press handler
	 */
	public IconButton(AppW appW, IconSpec icon, String ariaLabel, String dataTitle,
			String dataTest, Runnable onHandler) {
		this(appW.getLocalization(), icon, ariaLabel, onHandler);
		dataTitleTransKey = dataTitle;
		AriaHelper.setTitle(this, appW.getLocalization().getMenu(dataTitle));
		TestHarness.setAttr(this, dataTest);
		selectionColor = getSelectionColor(appW);
	}

	/**
	 * Small press icon buttons, used in notes top bar
	 * @param appW - application
	 * @param image - svg
	 * @param ariaLabel - aria label
	 * @param clickHandler - click handler
	 */
	public IconButton(AppW appW, Runnable clickHandler, IconSpec image,
			String ariaLabel) {
		this(appW.getLocalization(), image, ariaLabel);
		if (ariaLabel != null) {
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
	 * Disable button
	 * @param isDisabled - whether is disabled or not
	 */
	public void setDisabled(boolean isDisabled) {
		AriaHelper.setAriaDisabled(this, isDisabled);
		Dom.toggleClass(this, "disabled", isDisabled);
	}

	/**
	 * @param isActive - whether is on or off
	 */
	public void setActive(boolean isActive) {
		AriaHelper.setPressedState(this, isActive);
		Dom.toggleClass(this, "active", isActive);
		setIcon(image.withFill(isActive ? selectionColor : GColor.BLACK.toString()));
	}

	/**
	 * Remove active state
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
	 * @param image - image
	 * @param mode - tool mode
	 * @param appW - application
	 */
	public void updateImgAndTxt(IconSpec image, int mode, AppW appW) {
		this.image = isActive() ? image.withFill(selectionColor) : image;
		setIcon(image);
		setAltText(appW.getToolAriaLabel(mode));
		AriaHelper.setDataTitle(this, appW.getToolName(mode));
		TestHarness.setAttr(this, "selectModeButton" + mode);
	}

	/**
	 * Updates the aria label and the data title for this Icon Button (e.g. when language changes)
	 */
	@Override
	public void setLabels() {
		String ariaLabel;
		String dataTitle;
		if (mode > -1) {
			ariaLabel = appW.getToolAriaLabel(mode);
			dataTitle = appW.getToolName(mode);
		} else {
			ariaLabel = localization.getMenu(ariaLabelTransKey);
			dataTitle = localization.getMenu(dataTitleTransKey);
		}
		AriaHelper.setLabel(this, ariaLabel);
		AriaHelper.setDataTitle(this, dataTitle);
	}

	public int getMode() {
		return mode;
	}

	public boolean containsMode(int mode) {
		return getMode() == mode;
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

	/**
	 * @param mode - tool mode
	 * @param toolboxIconResource - icon resource
	 * @return icon
	 */
	public IconSpec getIconFromMode(Integer mode, ToolboxIconResource toolboxIconResource) {
		switch (mode) {
		case MODE_PEN:
			return toolboxIconResource.getImageResource(ToolboxIcon.PEN);
		case MODE_HIGHLIGHTER:
			return toolboxIconResource.getImageResource(ToolboxIcon.HIGHLIGHTER);
		case MODE_ERASER:
			return toolboxIconResource.getImageResource(ToolboxIcon.ERASER);
		default:
			GGWToolBar.getImageResource(mode, appW, toolImg -> {
				image = new ImageIconSpec((SVGResource) toolImg);
				setIcon(image);
			});
			return image;
		}
	}
}
