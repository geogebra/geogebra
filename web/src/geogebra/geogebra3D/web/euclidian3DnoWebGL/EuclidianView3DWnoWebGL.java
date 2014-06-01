package geogebra.geogebra3D.web.euclidian3DnoWebGL;

import geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.geogebra3D.web.euclidian3D.EuclidianView3DW;

/**
 * (dummy) 3D view for browsers that don't support webGL
 * @author mathieu
 *
 */
public class EuclidianView3DWnoWebGL extends EuclidianView3DW {

	/**
	 * constructor
	 * @param ec controller
	 * @param settings settings
	 */
	public EuclidianView3DWnoWebGL(EuclidianController3D ec,
            EuclidianSettings settings) {
	    super(ec, settings);
    }
	
	
	@Override
    protected Renderer createRenderer() {
	    return new RendererWnoWebGL(this);
    }

}
