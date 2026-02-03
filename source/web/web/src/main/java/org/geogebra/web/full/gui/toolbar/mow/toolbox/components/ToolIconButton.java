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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ERASER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_HIGHLIGHTER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MASK;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PEN;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_CIRCLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_CURVE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_ELLIPSE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_FREEFORM;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_LINE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_PARALLELOGRAM;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_PENTAGON;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_RECTANGLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_SQUARE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_STADIUM;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHAPE_TRIANGLE;

import java.util.function.Consumer;

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
		getIconFromMode(mode, appW.getToolboxIconResource(), icon -> {
			image = icon;
			setActive(getElement().hasClassName("active"));
		});
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
	 * @param  callback called when icon loaded
	 */
	public void getIconFromMode(Integer mode, ToolboxIconResource toolboxIconResource,
			Consumer<IconSpec> callback) {
		IconSpec immediate = switch (mode) {
			case MODE_PEN -> toolboxIconResource.getImageResource(ToolboxIcon.PEN);
			case MODE_HIGHLIGHTER -> toolboxIconResource.getImageResource(ToolboxIcon.HIGHLIGHTER);
			case MODE_ERASER -> toolboxIconResource.getImageResource(ToolboxIcon.ERASER);
			case MODE_SHAPE_SQUARE -> toolboxIconResource.getImageResource(ToolboxIcon.SQUARE);
			case MODE_SHAPE_RECTANGLE ->
					toolboxIconResource.getImageResource(ToolboxIcon.RECTANGLE);
			case MODE_SHAPE_TRIANGLE -> toolboxIconResource.getImageResource(ToolboxIcon.TRIANGLE);
			case MODE_SHAPE_CIRCLE -> toolboxIconResource.getImageResource(ToolboxIcon.CIRCLE);
			case MODE_SHAPE_PENTAGON -> toolboxIconResource.getImageResource(ToolboxIcon.PENTAGON);
			case MODE_SHAPE_LINE -> toolboxIconResource.getImageResource(ToolboxIcon.LINE);
			case MODE_MASK -> toolboxIconResource.getImageResource(ToolboxIcon.MASK);
			case MODE_SHAPE_ELLIPSE -> toolboxIconResource.getImageResource(ToolboxIcon.ELLIPSE);
			case MODE_SHAPE_FREEFORM -> toolboxIconResource.getImageResource(ToolboxIcon.FREEFORM);
			case MODE_SHAPE_PARALLELOGRAM ->
					toolboxIconResource.getImageResource(ToolboxIcon.PARALLELOGRAM);
			case MODE_SHAPE_STADIUM -> toolboxIconResource.getImageResource(ToolboxIcon.STADIUM);
			case MODE_SHAPE_CURVE -> toolboxIconResource.getImageResource(ToolboxIcon.CURVE);
			default -> {
				GGWToolBar.getImageResource(mode, appW, toolImg -> {
					callback.accept(new ImageIconSpec((SVGResource) toolImg));
				});
				yield null;
			}
		};
		if (immediate != null) {
			callback.accept(immediate);
		}
	}
}
