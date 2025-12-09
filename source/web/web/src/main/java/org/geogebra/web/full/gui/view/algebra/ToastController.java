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

package org.geogebra.web.full.gui.view.algebra;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.editor.share.syntax.SyntaxHint;
import org.geogebra.editor.share.syntax.SyntaxTooltipUpdater;
import org.geogebra.web.full.gui.components.ComponentToast;
import org.geogebra.web.html5.main.AppW;

public class ToastController implements SyntaxTooltipUpdater {

	private final Supplier<Rectangle> boundsSupplier;
	private final AppW app;
	private ComponentToast toast;

	/**
	 * @param app application
	 * @param boundsSupplier provides bounds (relative to app) for showing the toast
	 */
	public ToastController(AppW app, Supplier<Rectangle> boundsSupplier) {
		this.app = app;
		this.boundsSupplier = boundsSupplier;
	}

	@Override
	public void updateSyntaxTooltip(@Nonnull SyntaxHint sh) {
		if (!sh.isEmpty()) {
			Rectangle bounds = boundsSupplier.get();

			String hintHtml = sh.getPrefix() + "<strong>"
					+ sh.getActivePlaceholder() + "</strong>" + sh.getSuffix();

			if (toast == null) {
				toast = new ComponentToast(app, hintHtml);
			} else {
				toast.updateContent(hintHtml);
			}
			if (!toast.isShowing()) {
				toast.show((int) bounds.getMinX(), (int) bounds.getMinY(), (int) bounds.getMaxY(),
						(int) bounds.getWidth());
			}
		} else {
			hide();
		}
	}

	/**
	 * Hide the toast
	 */
	public void hide() {
		if (toast != null) {
			toast.hide();
		}
	}
}
