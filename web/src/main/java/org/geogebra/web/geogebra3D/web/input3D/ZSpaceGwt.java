package org.geogebra.web.geogebra3D.web.input3D;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.dom.client.Element;
import com.googlecode.gwtgl.binding.WebGLRenderingContext;

public class ZSpaceGwt {

	JavaScriptObject impl;

	public ZSpaceGwt(WebGLRenderingContext gl, Element canvas) {
		init(this, gl, canvas);
	}

	protected native void init(ZSpaceGwt zsGwt, WebGLRenderingContext gl,
			Element canvas) /*-{
		zsGwt.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl = new $wnd.ZSpace(
				gl, canvas, $wnd);
		var zs = zsGwt.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl;
		zs.zspaceInit();
		zs.setCanvasOffset(310, 0);
	}-*/;

	public native void zspaceUpdate(ZSpaceGwt zsGwt) /*-{
		zsGwt.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl
				.zspaceUpdate();
	}-*/;

	public native JsArrayNumber zspaceLeftView(ZSpaceGwt zsGwt) /*-{
		zsGwt.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl
				.zspaceLeftView();
		return zsGwt.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl.leftViewMatrix;
	}-*/;

	public native void zspaceRightView(ZSpaceGwt zsGwt) /*-{
		zsGwt.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl
				.zspaceRightView();
	}-*/;

	public native void zspaceFrameEnd(ZSpaceGwt zsGwt) /*-{
		zsGwt.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl
				.zspaceFrameEnd();
	}-*/;

}
