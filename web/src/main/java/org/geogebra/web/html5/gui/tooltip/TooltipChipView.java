package org.geogebra.web.html5.gui.tooltip;

import java.util.HashMap;

import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.timer.client.Timer;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Label;

public class TooltipChipView {
	private final HashMap<String, TooltipChip> tooltipMessages = new HashMap<>();

	public final static TooltipChipView INSTANCE = new TooltipChipView();

	/**
	 * @param tooltip tooltip content
	 * @param label label of a geo to be used as position anchor
	 * @param color CSS color code (e.g. "#00FF00")
	 * @param app app
	 */
	public void showMessage(String tooltip, String label, String color, AppW app) {
		GeoElement geo = app.getKernel().lookupLabel(label);
		DrawableND drawable = app.getActiveEuclidianView().getDrawableFor(geo);
		if (StringUtil.empty(tooltip)) {
			TooltipChip tooltipMsg = tooltipMessages.get(label);
			if (tooltipMsg != null) {
				tooltipMsg.hide();
			}
		} else if (drawable != null) {
			GRectangle2D bounds = drawable.getBoundsForStylebarPosition();
			double x = bounds.getMaxX() + getOffsetX(app);
			double y = bounds.getMinY() + getOffsetY(app);
			TooltipChip tooltipMsg = tooltipMessages.computeIfAbsent(label,
					l -> new TooltipChip());
			tooltipMsg.setText(tooltip);
			app.getAppletFrame().add(tooltipMsg);
			tooltipMsg.show(x, y, color);
		}
	}

	private double getOffsetY(AppW app) {
		return app.getActiveEuclidianView().getAbsoluteTop() - app.getAbsTop();
	}

	private double getOffsetX(AppW app) {
		return app.getActiveEuclidianView().getAbsoluteLeft() - app.getAbsLeft();
	}

	private static class TooltipChip extends Label {
		private final Timer hideTimer;

		public TooltipChip() {
			addStyleName("tooltipChip");
			hideTimer = new Timer() {
				@Override
				public void run() {
					hide();
				}
			};
		}

		private void hide() {
			getElement().addClassName("invisible");
		}

		private void show(double x, double y, String color) {
			getElement().removeClassName("invisible");
			Style style = getElement().getStyle();
			style.setBackgroundColor(color);
			style.setLeft(x, Style.Unit.PX);
			style.setTop(y, Style.Unit.PX);
			hideTimer.cancel();
			hideTimer.schedule(4000);
		}
	}
}
