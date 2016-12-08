package org.geogebra.web.geogebra3D.web.input3D;

import org.geogebra.common.euclidian.EuclidianViewCompanion;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.input3D.EuclidianViewInput3DCompanion;
import org.geogebra.common.geogebra3D.input3D.Input3D;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;

public class EuclidianViewInput3DW extends EuclidianView3DW {

	private Input3D input3D;

	public EuclidianViewInput3DW(EuclidianController3D ec,
			EuclidianSettings settings) {
		super(ec, settings);
	}

	private EuclidianViewInput3DCompanion companionInput3D;

	@Override
	protected EuclidianViewCompanion newEuclidianViewCompanion() {
		companionInput3D = new EuclidianViewInput3DCompanion(this);
		return companionInput3D;
	}

	@Override
	public EuclidianViewInput3DCompanion getCompanion() {
		return companionInput3D;
	}

	@Override
	protected void start(){
		input3D = ((EuclidianControllerInput3DW) euclidianController).input3D;
		input3D.init(this);
		getCompanion().setInput3D(input3D);
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
	public boolean isAnimated() {
		return true;
	}

}
