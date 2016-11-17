package org.geogebra.web.geogebra3D.web.input3D;

import org.geogebra.common.euclidian3D.Input3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianController3DW;

public class EuclidianControllerInput3DW extends EuclidianController3DW {


	protected Input3D input3D;

	public EuclidianControllerInput3DW(Kernel kernel, Input3D input3D) {
		super(kernel);

		this.input3D = input3D;
	}


	@Override
	public void updateInput3D() {
		Log.debug("update input 3D");
	}

}
