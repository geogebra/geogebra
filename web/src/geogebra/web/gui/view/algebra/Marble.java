package geogebra.web.gui.view.algebra;

import geogebra.common.kernel.geos.GeoElement;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.SimplePanel;

public class Marble extends SimplePanel
{
	private SafeUri showUrl, hiddenUrl;
	private GeoContainer gc;
	
	public interface GeoContainer {
		public GeoElement getGeo();
		
	}
	
	public Marble(SafeUri showUrl,SafeUri hiddenUrl,final GeoContainer gc){
		this.showUrl = showUrl;
		this.hiddenUrl = hiddenUrl;
		this.gc = gc;
		addDomHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				gc.getGeo().setEuclidianVisible(!gc.getGeo().isSetEuclidianVisible());
				gc.getGeo().updateVisualStyle();
				gc.getGeo().getKernel().getApplication().storeUndoInfo();
				gc.getGeo().getKernel().notifyRepaint();

				setChecked(gc.getGeo().isEuclidianVisible());
			}
		}, ClickEvent.getType());
	}

	/**
	 * set background-images via HTML
	 */
	public void setImage(String text)
	{
		String html = "<img src=\"" + text + "\" style=\"height: 19px;margin-right: 5px;\">";
		this.getElement().setInnerHTML(html);
	}

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

	public void setEnabled(boolean euclidianShowable) {
	    // TODO Auto-generated method stub
	    
    }

}
