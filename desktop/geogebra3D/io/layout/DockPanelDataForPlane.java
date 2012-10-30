package geogebra3D.io.layout;

import geogebra.common.awt.GRectangle;
import geogebra.common.io.layout.DockPanelData;
import geogebra.common.main.App;

/**
 * @author mathieu
 *
 * Data for plane that created the view
 */
public class DockPanelDataForPlane extends DockPanelData{
	


	public DockPanelDataForPlane(int viewId, String toolbar, boolean isVisible, boolean openInFrame, boolean showStyleBar, GRectangle windowRect, String embeddedDef, int embeddedSize, String plane) {
		
		super(viewId, toolbar, isVisible, openInFrame,
				showStyleBar, windowRect,
				embeddedDef, embeddedSize, plane);
		
	}
	
	@Override
	public String getXml() {
		
		StringBuilder sb = getStartXml();
		sb.append("\" plane=\"");
		sb.append(getPlane());
		sb.append("\" />\n");
		return sb.toString();
		
	}
	
	@Override
	protected int getViewIdForXML(){
		//plane will do the id
		return App.VIEW_EUCLIDIAN_FOR_PLANE_START;
	}
	
}
