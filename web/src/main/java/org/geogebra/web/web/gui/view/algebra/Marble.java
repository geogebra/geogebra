package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.algebra.GeoContainer;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Algebra view marble to show or hide geos
 *
 */
public class Marble extends SimplePanel
{
	private SafeUri showUrl, hiddenUrl;
	private GeoContainer gc;
	/** whether the last switch was done using touch (ignore onclick in that case) */
	boolean touchUsed;
	
	/**
	 * Toggle visibility of corresponding geo
	 */
	void toggleVisibility(){
		gc.getGeo().setEuclidianVisible(!gc.getGeo().isSetEuclidianVisible());
		gc.getGeo().updateVisualStyle();
		gc.getGeo().getKernel().getApplication().storeUndoInfo();
		gc.getGeo().getKernel().notifyRepaint();
		setChecked(gc.getGeo().isEuclidianVisible());
	}
	
	/**
	 * @param showUrl url of image for shown geos
	 * @param hiddenUrl url of image for hidden geos
	 * @param gc object providing the GeoElement
	 */
	public Marble(SafeUri showUrl,SafeUri hiddenUrl,final GeoContainer gc){
		this.showUrl = showUrl;
		this.hiddenUrl = hiddenUrl;
		this.gc = gc;

		// stopPropagation activated (parameters for the constructor)
		ClickStartHandler.init(this, new ClickStartHandler(false, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				toggleVisibility();
			}
		});
	}

	/**
	 * set background-images via HTML
	 * @param text URL of image as string
	 */
	public void setImage(String text)
	{
		//String html = "<img src=\"" + text + "\" style=\"height: 19px;margin-right: 5px;\">";
		String html = "<img src=\"" + text + "\">";
		this.getElement().setInnerHTML(html);
	}

	/**
	 * @param value true tfor visible, false for invisible geo
	 */
	public void setChecked(boolean value)
	{
		if (value)
		{
			setImage(showUrl.asString());
		}
		else
		{
			setImage(hiddenUrl.asString());
		}
	}
	
	/**
	 * Enable or disable this control, NOT IMPLEMENTED
	 * @param euclidianShowable whether the geo may be shown/hidden
	 */
	public void setEnabled(boolean euclidianShowable) {
	    // TODO Auto-generated method stub
	    
    }

}
