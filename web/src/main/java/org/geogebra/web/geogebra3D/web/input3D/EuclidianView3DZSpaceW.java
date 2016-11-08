package org.geogebra.web.geogebra3D.web.input3D;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;

public class EuclidianView3DZSpaceW extends EuclidianView3DW {

	public EuclidianView3DZSpaceW(EuclidianController3D ec,
			EuclidianSettings settings) {
		super(ec, settings);
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

}
