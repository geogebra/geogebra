package org.geogebra.web.geogebra3D.web.input3D;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.dom.client.Element;
import com.googlecode.gwtgl.binding.WebGLRenderingContext;

public class ZSpaceGwt {

	JavaScriptObject impl;

	public ZSpaceGwt(WebGLRenderingContext gl, Element canvas) {
		init(gl, canvas);
	}
	
	public static native boolean zspaceIsAvailable() /*-{
	    if (navigator.getVRDisplays) {
	      @org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("navigator.getVRDisplays");
	      navigator.getVRDisplays().then(function (displays) {
	        if (displays.length > 0) {
	          var i;
	          var flag = false;
	          for (i = 0; i < displays.length; i++) {
	          	@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("displays[i].displayName = "+displays[i].displayName);
	            if (displays[i].displayName == "ZSpace Display") {
	              flag = true;
	            }
	          }
	          if (flag) {
	          	$wnd.localStorage.setItem('zSpace', 'true');
	          } else {
	          	$wnd.localStorage.setItem('zSpace', 'false');
	          }
	        }
	      });
	      var stored = $wnd.localStorage.getItem('zSpace');
	      @org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("localStorage.getItem = "+stored);
	      if (stored == "true") {
	      	return true;
	      }
	    } 
	    return false;
	}-*/;

	protected native void init(WebGLRenderingContext gl,
			Element canvas) /*-{
		this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl = new $wnd.ZSpace(
				gl, canvas, $wnd);
		var zs = this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl;
		zs.zspaceInit();
		zs.setCanvasOffset(0, 0);
	}-*/;

	public native void zspaceUpdate() /*-{
		this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl
				.zspaceUpdate();
	}-*/;

	public native void zspaceLeftView() /*-{
		this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl
				.zspaceLeftView();
	}-*/;

	public native void zspaceRightView() /*-{
		this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl
				.zspaceRightView();
	}-*/;

	public native JsArrayNumber getLeftViewMatrix() /*-{
		return this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl.leftViewMatrix;
	}-*/;

	public native JsArrayNumber getRightViewMatrix() /*-{
		return this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl.rightViewMatrix;
	}-*/;

	public native void zspaceFrameEnd() /*-{
		this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl
				.zspaceFrameEnd();
	}-*/;

	public native JsArrayNumber getViewportSpaceHeadPose() /*-{
		return this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl.viewportSpaceHeadPose;
	}-*/;

	public native void setCanvasOffset(int x, int y) /*-{
		return this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl
				.setCanvasOffset(x, y);
	}-*/;

	public native JsArrayNumber getViewportSpaceStylusPose() /*-{
		return this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl.viewportSpaceStylusPose;
	}-*/;

	public native JsArrayNumber getButtonPressed() /*-{
		return this.@org.geogebra.web.geogebra3D.web.input3D.ZSpaceGwt::impl.buttonPressed;
	}-*/;

}
