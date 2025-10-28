package org.geogebra.web.full.gui.toolbar.mow.toolbox.components;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ERASER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_HIGHLIGHTER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PEN;

import org.geogebra.common.awt.GColor;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.toolbox.ToolboxIcon;
import org.geogebra.web.html5.main.toolbox.ToolboxIconResource;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.resources.SVGResourcePrototype;

public class ToolIconButton extends IconButton {
	private int mode = -1;
	private final AppW appW;

	/**
	 * Press icon button with given tool mode.
	 * @param mode tool mode
	 * @param appW {@link AppW}
	 */
	public ToolIconButton(int mode, AppW appW) {
		super(appW, new ImageIconSpec(SVGResourcePrototype.EMPTY),
				appW.getToolAriaLabel(mode));
		this.mode = mode;
		this.appW = appW;
		AriaHelper.setDataTitle(this, appW.getToolName(mode));
		image = getIconFromMode(mode, appW.getToolboxIconResource());
		setActive(getElement().hasClassName("active"));
		addStyleName("iconButton");
	}

	/** Press icon button with given tool mode.
	 * @param mode tool mode
	 * @param appW {@link AppW}
	 * @param icon {@link IconSpec} image
	 * @param onHandler switch on handler
	 */
	public ToolIconButton(int mode, AppW appW, IconSpec icon, Runnable onHandler) {
		super(appW, icon, appW.getToolAriaLabel(mode), appW.getToolAriaLabel(mode),
				appW.getToolAriaLabel(mode), onHandler);
		this.appW = appW;
		this.mode = mode;
	}

	protected ToolIconButton(AppW appW, IconSpec icon, String ariaLabel, String ariaLabel1,
			Runnable onHandler, Runnable offHandler) {
		super(appW, icon, ariaLabel, ariaLabel1, onHandler, offHandler);
		this.appW = appW;
	}

	protected ToolIconButton(AppW appW, IconSpec imageResource, String ariaLabel, String ariaLabel1,
			String s, Runnable offHandler) {
		super(appW, imageResource, ariaLabel, ariaLabel1, s, offHandler);
		this.appW = appW;
	}

	@Override
	public void setLabels() {
		if (mode > -1) {
			String ariaLabel = appW.getToolAriaLabel(mode);
			String dataTitle = appW.getToolName(mode);
			AriaHelper.setLabel(this, ariaLabel);
			AriaHelper.setDataTitle(this, dataTitle);
		} else {
			super.setLabels();
		}

	}

	public int getMode() {
		return mode;
	}

	@Override
	public boolean containsMode(int mode) {
		return mode == this.mode;
	}

	/**
	 * @param mode tool mode
	 * @param toolboxIconResource icon resource
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
				setIcon(image.withFill(isActive() ? selectionColor : GColor.BLACK.toString()));
			});
			return image;
		}
	}
}
