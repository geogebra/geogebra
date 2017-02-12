package org.geogebra.web.geogebra3D.web.input3D;

import org.geogebra.common.main.App;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.RendererImplShadersW;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.RendererWithImplW;
import org.geogebra.web.web.gui.layout.DockPanelW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.googlecode.gwtgl.binding.WebGLRenderingContext;

/**
 * web renderer for zSpace
 * 
 *
 */
public class RendererWithImplZSpaceW extends RendererWithImplW {

	private ZSpaceGwt zSpace;

	public RendererWithImplZSpaceW(EuclidianViewInput3DW view) {
		super(view);
		zSpace = new ZSpaceGwt(glContext, webGLCanvas.getElement());
		((InputZSpace3DW) view.getInput3D()).setZSpace(zSpace);

	}


	@Override
	protected void createGLContext(boolean preserveDrawingBuffer) {
		if (preserveDrawingBuffer) {
			glContext = getBufferedContext(webGLCanvas.getElement());

		} else {
			// glContext = (WebGLRenderingContext) webGLCanvas
			// .getContext("webgl");
			glContext = getBufferedContextNoPDB(webGLCanvas.getElement());
			((RendererImplShadersW) getRendererImpl()).setGL(glContext);
		}
		if (glContext == null) {
			Window.alert("Sorry, Your Browser doesn't support WebGL!");
		}

	}

	private static native WebGLRenderingContext getBufferedContext(
			Element element) /*-{
		return element.getContext("webgl", {
			preserveDrawingBuffer : true,
			alpha : false,
			antialias : true
		});
	}-*/;

	private static native WebGLRenderingContext getBufferedContextNoPDB(
			Element element) /*-{
		return element.getContext("webgl", {
			alpha : false,
			antialias : true
		});
	}-*/;

	@Override
	public void drawScene() {

		// give canvas position to zSpace
		DockPanelW panel = (DockPanelW) view3D.getApplication().getGuiManager()
				.getLayout().getDockManager().getPanel(App.VIEW_EUCLIDIAN3D);
		zSpace.setCanvasOffset(panel.getAbsoluteLeft(), panel.getAbsoluteTop());

		// update zSpace
		zSpace.zspaceUpdate();

		clearColorBuffer();
		clearDepthBuffer();

		super.drawScene();

		// end zSpace
		zSpace.zspaceFrameEnd();

	}

	@Override
	final protected void setBufferLeft() {
		zSpace.zspaceLeftView();
	}

	@Override
	final protected void setBufferRight() {
		zSpace.zspaceRightView();
	}

	public ZSpaceGwt getZSpace() {
		return zSpace;
	}

}
