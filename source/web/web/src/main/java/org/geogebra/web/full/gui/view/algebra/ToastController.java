package org.geogebra.web.full.gui.view.algebra;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.web.full.gui.components.ComponentToast;
import org.geogebra.web.html5.main.AppW;

import com.himamis.retex.editor.share.syntax.SyntaxHint;
import com.himamis.retex.editor.share.syntax.SyntaxTooltipUpdater;

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
