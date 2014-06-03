package geogebra.geogebra3D.web.euclidian3D.openGL;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.kernel.Matrix.Coords;

/**
 * Specific manager for Internet Explorer : no TRIANGLE_FAN geometry
 * @author mathieu
 *
 */
public class ManagerShadersIE extends ManagerShaders {

	/**
	 * constructor
	 * @param renderer GL renderer
	 * @param view3d 3D view
	 */
	public ManagerShadersIE(Renderer renderer, EuclidianView3D view3d) {
	    super(renderer, view3d);
    }
	
	private Coords triangleFanApex;


	@Override
    protected void triangleFanApex(Coords v){
		triangleFanApex = v.copyVector();
	}


	@Override
    protected void triangleFanVertex(Coords v){
		vertex(triangleFanApex);
		vertex(v);
	}

}
