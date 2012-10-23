package geogebra3D.io.layout;

import geogebra.common.awt.GRectangle;
import geogebra.common.io.layout.DockPanelData;

/**
 * @author mathieu
 *
 * Data for plane that created the view
 */
public class DockPanelDataForPlane extends DockPanelData{
	
	private String plane;


	public DockPanelDataForPlane(int viewId, String toolbar, boolean isVisible, boolean openInFrame, boolean showStyleBar, GRectangle windowRect, String embeddedDef, int embeddedSize, String plane) {
		
		super(viewId, toolbar, isVisible, openInFrame,
				showStyleBar, windowRect,
				embeddedDef, embeddedSize);
		
		this.plane=plane;
	}
	
	@Override
	public String getXml() {
		
		StringBuilder sb = getStartXml();
		sb.append("\" plane=\"");
		sb.append(plane);
		sb.append("\" />\n");
		return sb.toString();
		
	}
	
}
