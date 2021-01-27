package org.geogebra.web.geogebra3D.web.input3D;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.dom.client.Element;

import elemental2.webgl.WebGLRenderingContext;

/**
 * GWT connector for zSpace
 *
 */
public class ZSpaceGwt {

	/** native object */
	JavaScriptObject impl;

	/**
	 * @param gl
	 *            context
	 * @param canvas
	 *            canvas
	 */
	public ZSpaceGwt(WebGLRenderingContext gl, Element canvas) {
		init(gl, canvas);
	}

	/**
	 * @return zSpace avialable?
	 */
	public static native boolean zspaceIsAvailable() /*-{
		//		@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("navigator.userAgent = "+ navigator.userAgent);
		var index = navigator.userAgent.indexOf("zSpace Beta");
		//		@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("indexOf = "+ index);
		return index !== -1;
	}-*/;

	/**
	 * @param gl
	 *            context
	 * @param canvas
	 *            canvas
	 */
	protected native void init(WebGLRenderingContext gl,
			Element canvas) /*-{
		this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl = new $wnd.ZSpace(
				gl, canvas);
		var zs = this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl;
		zs.zspaceInit();
	}-*/;

	/**
	 * Update
	 */
	public native void zspaceUpdate() /*-{
		this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl
				.zspaceUpdate();
	}-*/;

	/**
	 * Left view
	 */
	public native void zspaceLeftView() /*-{
		this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl
				.zspaceLeftView();
	}-*/;

	/**
	 * Right view
	 */
	public native void zspaceRightView() /*-{
		this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl
				.zspaceRightView();
	}-*/;

	/**
	 * @return left view matrix
	 */
	public native JsArrayNumber getLeftViewMatrix() /*-{
		return this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl.leftViewMatrix;
	}-*/;

	/**
	 * @return right view matrix
	 */
	public native JsArrayNumber getRightViewMatrix() /*-{
		return this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl.rightViewMatrix;
	}-*/;

	/**
	 * Frame end
	 */
	public native void zspaceFrameEnd() /*-{
		this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl
				.zspaceFrameEnd();
	}-*/;

	/**
	 * @return head position in viewport space
	 */
	public native JsArrayNumber getViewportSpaceHeadPose() /*-{
		return this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl.viewportSpaceHeadPose;
	}-*/;

	/**
	 * @param x
	 *            x offset
	 * @param y
	 *            y offset
	 */
	public native void setCanvasOffset(int x, int y) /*-{
		this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl
				.setBrowserViewportOffset(x, y);
	}-*/;

	/**
	 * @return stylus pose in viewport
	 */
	public native JsArrayNumber getViewportSpaceStylusPose() /*-{
		return this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl.viewportSpaceStylusPose;
	}-*/;

	/**
	 * @return pressed button IDs
	 */
	public native JsArrayNumber getButtonPressed() /*-{
		return this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl.buttonPressed;
	}-*/;

}
