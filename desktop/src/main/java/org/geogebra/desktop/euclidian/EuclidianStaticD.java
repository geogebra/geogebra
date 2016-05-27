package org.geogebra.desktop.euclidian;

import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.desktop.awt.GBasicStrokeD;

public class EuclidianStaticD extends EuclidianStatic {

	// This has to be made singleton or use prototype,
	// while its static methods be made non-static,
	// or implement by some other solution e.g. AbstractEuclidianStatic,
	// in order to be usable from Common. (like an adapter)



	static public java.awt.BasicStroke getDefaultStrokeAwt() {
		return GBasicStrokeD.getAwtStroke(EuclidianStatic.getDefaultStroke());
	}

}
