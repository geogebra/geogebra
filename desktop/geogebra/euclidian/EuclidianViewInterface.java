package geogebra.euclidian;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.Previewable;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;


/**
 * 
 * Interface between EuclidianView (2D or 3D) and EuclidianController (2D or 3D)
 * 
 * (TODO) see EuclidianView for detail of methods
 * 
 */

public interface EuclidianViewInterface extends EuclidianViewInterfaceCommon {

	
	//public GeoDirectionND getDirection();
}
