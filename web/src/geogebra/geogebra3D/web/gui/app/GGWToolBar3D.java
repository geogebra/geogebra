package geogebra.geogebra3D.web.gui.app;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.geogebra3D.web.gui.toolbar.images.MyIconResourceBundle3D;
import geogebra.web.gui.app.GGWToolBar;

import com.google.gwt.core.client.GWT;

/**
 * 
 * @author mathieu
 *
 */
public class GGWToolBar3D extends GGWToolBar {
	
	
	static private MyIconResourceBundle3D myIconResourceBundle3D = GWT
	        .create(MyIconResourceBundle3D.class);

	
	@Override
    protected String getImageURLNotMacro(int mode) {
		
		switch (mode) {
		case EuclidianConstants.MODE_CIRCLE_AXIS_POINT:
			return myIconResourceBundle3D.mode_circleaxispoint_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS_DIRECTION:
			return myIconResourceBundle3D.mode_circlepointradiusdirection_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS:
			return myIconResourceBundle3D.mode_cone_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CONIFY:
			return myIconResourceBundle3D.mode_conify_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CUBE:
			return myIconResourceBundle3D.mode_cube_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS:
			return myIconResourceBundle3D.mode_cylinder_32().getSafeUri().asString();

		case EuclidianConstants.MODE_EXTRUSION:
			return myIconResourceBundle3D.mode_extrusion_32().getSafeUri().asString();

		case EuclidianConstants.MODE_MIRROR_AT_PLANE:
			return myIconResourceBundle3D.mode_mirroratplane_32().getSafeUri().asString();

		case EuclidianConstants.MODE_NET:
			return myIconResourceBundle3D.mode_net_32().getSafeUri().asString();

		case EuclidianConstants.MODE_ORTHOGONAL_PLANE:
			return myIconResourceBundle3D.mode_orthogonalplane_32().getSafeUri().asString();

		case EuclidianConstants.MODE_PARALLEL_PLANE:
			return myIconResourceBundle3D.mode_parallelplane_32().getSafeUri().asString();

		case EuclidianConstants.MODE_PLANE_THREE_POINTS:
			return myIconResourceBundle3D.mode_planethreepoint_32().getSafeUri().asString();

		case EuclidianConstants.MODE_PLANE:
			return myIconResourceBundle3D.mode_plane_32().getSafeUri().asString();

		case EuclidianConstants.MODE_PRISM:
			return myIconResourceBundle3D.mode_prism_32().getSafeUri().asString();

		case EuclidianConstants.MODE_PYRAMID:
			return myIconResourceBundle3D.mode_pyramid_32().getSafeUri().asString();
			
		case EuclidianConstants.MODE_ROTATE_AROUND_LINE:
			return myIconResourceBundle3D.mode_rotatearoundline_32().getSafeUri().asString();

		case EuclidianConstants.MODE_ROTATEVIEW:
			return myIconResourceBundle3D.mode_rotateview_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SPHERE_TWO_POINTS:
			return myIconResourceBundle3D.mode_sphere2_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SPHERE_POINT_RADIUS:
			return myIconResourceBundle3D.mode_spherepointradius_32().getSafeUri().asString();

		case EuclidianConstants.MODE_TETRAHEDRON:
			return myIconResourceBundle3D.mode_tetrahedron_32().getSafeUri().asString();

		case EuclidianConstants.MODE_VIEW_IN_FRONT_OF:
			return myIconResourceBundle3D.mode_viewinfrontof_32().getSafeUri().asString();

		case EuclidianConstants.MODE_VOLUME:
			return myIconResourceBundle3D.mode_volume_32().getSafeUri().asString();
			
		}		
		
		return super.getImageURLNotMacro(mode);
		
	}

}
