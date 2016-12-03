package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.gui.util.ClickStartHandler;

import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Algebra view marble to show or hide geos
 *
 */
public class Marble extends SimplePanel
{
	private RadioTreeItem gc;
	/** whether the last switch was done using touch (ignore onclick in that case) */
	boolean touchUsed;
	
	/**
	 * Toggle visibility of corresponding geo
	 */
	void toggleVisibility(){
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
	 * set background-images via HTML
	 * 
	 * @param text
	 *            URL of image as string
	 * 
	 *            STEFFI: OLD Marbles will be done by css now!
	 */
	/*
	 * public void setImage(String text) { //String html = "<img src=\"" + text
	 * + "\" style=\"height: 19px;margin-right: 5px;\">"; String html =
	 * "<img src=\"" + text + "\">"; this.getElement().setInnerHTML(html); }
	 */

	/**
	 * @param value
	 *            true tfor visible, false for invisible geo
	 */
	public void setChecked(boolean value) {
		if (value) {
			// Steffi: Marbles will be drawn by css now
			// setImage(showUrl.asString());
			this.removeStyleName("elemHidden");
			this.addStyleName("elemShown");
			updateMarble(true);
		} else {
			// setImage(hiddenUrl.asString());
			this.removeStyleName("elemShown");
			this.addStyleName("elemHidden");
			updateMarble(false);
		}
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
			// Filling color should be the same color but 30% opacity (77)
			GColor c = gc.getGeo().getAlgebraColor();
			GColor fillColor = c.deriveWithAlpha(77);
			this.getElement().getStyle()
					.setBorderColor(GColor.getColorString(c));
			this.getElement().getStyle().setBackgroundColor(GColor.getColorString(fillColor));
		}
		else {
			this.getElement().getStyle().setBackgroundColor(GColor.getColorString(GColor.WHITE));
		}
	}
	
	/**
	 * Enable or disable this control
	 * 
	 * @param euclidianShowable
	 *            whether the geo may be shown/hidden
	 */
	public void setEnabled(boolean show) {
		if (!show) {
			addStyleName("marbleHidden");
		} else {
			removeStyleName("marbleHidden");
		}
    }

}
