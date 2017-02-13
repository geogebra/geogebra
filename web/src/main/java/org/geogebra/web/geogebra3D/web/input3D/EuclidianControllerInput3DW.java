package org.geogebra.web.geogebra3D.web.input3D;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianControllerCompanion;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.input3D.EuclidianControllerInput3DCompanion;
import org.geogebra.common.geogebra3D.input3D.Input3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianController3DW;

public class EuclidianControllerInput3DW extends EuclidianController3DW {


	protected Input3D input3D;

	public EuclidianControllerInput3DW(Kernel kernel, Input3D input3D) {
		super(kernel);

		this.input3D = input3D;

		((EuclidianControllerInput3DCompanion) companion).setInput3D(input3D);

	}


	@Override
	public void updateInput3D() {
		input3D.update();
	}

	@Override
	protected EuclidianControllerCompanion newCompanion() {
		return new EuclidianControllerInput3DCompanion(this);
	}

	@Override
	public boolean hasInput3D() {
		return true;
	}

	@Override
	public GPoint getMouseLoc() {
		if (input3D.currentlyUseMouse2D()) {
			return super.getMouseLoc();
		}

		return input3D.getMouseLoc();
	}

	@Override
	public void wrapMouseReleased(AbstractEvent e) {
		if (!input3D.wasRightReleased() && !input3D.useQuaternionsForRotate()) {
			processRightRelease();
			return;
		}

		super.wrapMouseReleased(e);

	}

	private void processRightRelease() {
		((EuclidianView3D) getView()).setRotContinueAnimation(
				app.getMillisecondTime() - timeOld, animatedRotSpeed);
	}

}
