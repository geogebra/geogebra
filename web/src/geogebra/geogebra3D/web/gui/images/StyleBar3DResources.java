package geogebra.geogebra3D.web.gui.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface StyleBar3DResources extends ClientBundle {
	StyleBar3DResources INSTANCE = GWT.create(StyleBar3DResources.class);
	
	//EUCLIDIAN 3D STYLEBAR:
	
	@Source("old_images/plane.gif")
	ImageResource plane();
	
	
	@Source("old_images/stylebar_rotateview.gif")
	ImageResource rotateView();

	
	
	@Source("old_images/stylebar_clipping.gif")
	ImageResource clipping();
		
	@Source("old_images/standard_view_rotate.gif")
	ImageResource standardViewRotate();
	

	@Source("old_images/view_xy.gif")
	ImageResource viewXY();
	
	@Source("old_images/view_xz.gif")
	ImageResource viewXZ();
	
	@Source("old_images/view_yz.gif")
	ImageResource viewYZ();
	
	
	
	@Source("old_images/stylebar_vieworthographic.gif")
	ImageResource viewOrthographic();
	
	@Source("old_images/stylebar_viewperspective.gif")
	ImageResource viewPerspective();
	
	@Source("old_images/stylebar_viewglasses.gif")
	ImageResource viewGlasses();
	
	@Source("old_images/stylebar_viewoblique.gif")
	ImageResource viewOblique();
	

	
	
	
}
