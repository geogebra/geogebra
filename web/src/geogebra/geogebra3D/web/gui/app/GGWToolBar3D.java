package geogebra.geogebra3D.web.gui.app;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.web.gui.app.GGWToolBar;

/**
 * 
 * @author mathieu
 *
 */
public class GGWToolBar3D extends GGWToolBar {
	
	
	
	@Override
    protected String getImageURLNotMacro(int mode) {
		
		switch (mode) {
		case EuclidianConstants.MODE_CIRCLE_AXIS_POINT:
			return myIconResourceBundle.mode_circleaxispoint_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS_DIRECTION:
			return myIconResourceBundle.mode_circlepointradiusdirection_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS:
			return myIconResourceBundle.mode_cone_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CONIFY:
			return myIconResourceBundle.mode_conify_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CUBE:
			return myIconResourceBundle.mode_cube_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS:
			return myIconResourceBundle.mode_cylinder_32().getSafeUri().asString();

		case EuclidianConstants.MODE_EXTRUSION:
			return myIconResourceBundle.mode_extrusion_32().getSafeUri().asString();

		case EuclidianConstants.MODE_MIRROR_AT_PLANE:
			return myIconResourceBundle.mode_mirroratplane_32().getSafeUri().asString();

		case EuclidianConstants.MODE_NET:
			return myIconResourceBundle.mode_net_32().getSafeUri().asString();

		case EuclidianConstants.MODE_ORTHOGONAL_PLANE:
			return myIconResourceBundle.mode_orthogonalplane_32().getSafeUri().asString();

		case EuclidianConstants.MODE_PARALLEL_PLANE:
			return myIconResourceBundle.mode_parallelplane_32().getSafeUri().asString();

		case EuclidianConstants.MODE_PLANE_THREE_POINTS:
			return myIconResourceBundle.mode_planethreepoint_32().getSafeUri().asString();

		case EuclidianConstants.MODE_PLANE:
			return myIconResourceBundle.mode_plane_32().getSafeUri().asString();

		case EuclidianConstants.MODE_PRISM:
			return myIconResourceBundle.mode_prism_32().getSafeUri().asString();

		case EuclidianConstants.MODE_PYRAMID:
			return myIconResourceBundle.mode_pyramid_32().getSafeUri().asString();
			
		case EuclidianConstants.MODE_ROTATE_AROUND_LINE:
			return myIconResourceBundle.mode_rotatearoundline_32().getSafeUri().asString();

		case EuclidianConstants.MODE_ROTATEVIEW:
			return myIconResourceBundle.mode_rotateview_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SPHERE_TWO_POINTS:
			return myIconResourceBundle.mode_sphere2_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SPHERE_POINT_RADIUS:
			return myIconResourceBundle.mode_spherepointradius_32().getSafeUri().asString();

		case EuclidianConstants.MODE_TETRAHEDRON:
			return myIconResourceBundle.mode_tetrahedron_32().getSafeUri().asString();

		case EuclidianConstants.MODE_VIEW_IN_FRONT_OF:
			return myIconResourceBundle.mode_viewinfrontof_32().getSafeUri().asString();

		case EuclidianConstants.MODE_VOLUME:
			return myIconResourceBundle.mode_volume_32().getSafeUri().asString();
			
		case EuclidianConstants.MODE_ORTHOGONAL_THREE_D:
			return myIconResourceBundle.mode_orthogonalthreed_32().getSafeUri().asString();
			
		}		
		
		return super.getImageURLNotMacro(mode);
		
	}

}
