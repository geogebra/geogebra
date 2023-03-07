package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.SimplePanel;

/**
 * Algebra view marble to show or hide geos
 *
 */
public class Marble extends SimplePanel {
	private RadioTreeItem gc;
	private Image imgText = null;

	/**
	 * Toggle visibility of corresponding geo
	 */
	void toggleVisibility() {
		GeoElement geo = gc.getGeo();

		geo.setEuclidianVisible(!geo.isSetEuclidianVisible());
		geo.updateVisualStyle(GProperty.VISIBLE);
		geo.getKernel().getApplication().storeUndoInfo();
		geo.getKernel().notifyRepaint();
		setChecked(geo.isEuclidianVisible());
	}
	
	/**
	 * @param gc object providing the GeoElement
	 */
	public Marble(final RadioTreeItem gc) {
		this.gc = gc;
		if (gc.getApplication().isUnbundledOrWhiteboard()
				&& !NavigatorUtil.isMobile()) {
			this.getElement().removeAttribute("title");
		}
		if (gc.isTextItem()) {
			imgText = new NoDragImage(
					MaterialDesignResources.INSTANCE.icon_quote_black(), 20);
			imgText.addStyleName("textOverMarble");
			add(imgText);
		}
		// stopPropagation activated (parameters for the constructor)
		ClickStartHandler.init(this, new ClickStartHandler(false, true) {
			@Override
			public boolean onClickStart(int x, int y, PointerEventType type,
					boolean right) {
				if (type == PointerEventType.TOUCH) {

					return false;
				}
				if (right) {
					return true;
				}
				toggleVisibility();
				return false;
			}

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				if (type == PointerEventType.TOUCH) {
						toggleVisibility();
				}
			}
		});
	}

	/**
	 * @param value
	 *            true tfor visible, false for invisible geo
	 */
	public void setChecked(boolean value) {
		updateMarble(value);
	}

	/**
	 * Steffi, 17/8/2015 Function to set the marble style for visible and
	 * unvisible geo (Background color changes, depending on visibility)
	 * 
	 * @param value
	 *            true for visible, false for invisible geo
	 */
	private void updateMarble(boolean value) {
		if (value) {
			// Filling color should be the same color but 40% opacity (102)
			GColor c = gc.getGeo().getAlgebraColor();
			GColor fillColor = this.gc.getGeo().getObjectColor()
					.deriveWithAlpha(102);
			this.getElement().getStyle()
					.setBorderColor(GColor.getColorString(c));
			this.getElement().getStyle()
					.setBackgroundColor(GColor
							.getColorString(fillColor));
		}
		else {
			this.getElement().getStyle().setBackgroundColor(GColor.getColorString(GColor.WHITE));
		}
		setAltText(value ? "visible" : "not visible");
	}
	
	/**
	 * Enable or disable this control
	 * 
	 * @param euclidianShowable
	 *            whether the geo may be shown/hidden
	 */
	public void setEnabled(boolean euclidianShowable) {
		if (!euclidianShowable) {
			addStyleName("marbleHidden");
		} else {
			removeStyleName("marbleHidden");
		}
	}

	/**
	 * @param alt
	 *            alternativve text for assistive technology
	 */
	public void setAltText(String alt) {
		getElement().setAttribute("alt", alt);
	}
}
