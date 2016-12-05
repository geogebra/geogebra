package org.geogebra.web.geogebra3D.web.input3D;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.input3D.Input3D;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;

public class EuclidianViewInput3DW extends EuclidianView3DW {

	private Input3D input3D;

	public EuclidianViewInput3DW(EuclidianController3D ec,
			EuclidianSettings settings) {
		super(ec, settings);
	}

	@Override
	protected void start() {
		input3D = ((EuclidianControllerInput3DW) euclidianController).input3D;
		input3D.init(this);
		super.start();
	}

	public Input3D getInput3D() {
		return input3D;
	}

	@Override
	protected Renderer createRenderer() {
		return new RendererWithImplZSpaceW(this);

	}

	@Override
	public void setProjection(int projection) {
		setProjectionGlasses();
	}

	@Override
	public boolean isStereoBuffered() {
		return true;
	}

	@Override
	public boolean wantsStereo() {
		return true;
	}

	@Override
	public boolean isAnimated() {
		return true;
	}

}
