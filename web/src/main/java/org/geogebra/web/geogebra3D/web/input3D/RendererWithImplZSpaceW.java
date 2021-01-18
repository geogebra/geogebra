package org.geogebra.web.geogebra3D.web.input3D;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.RendererImplShadersW;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.RendererWithImplW;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.util.ScriptLoadCallback;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.user.client.Window;

import elemental2.webgl.WebGLRenderingContext;

/**
 * web renderer for zSpace
 * 
 *
 */
public class RendererWithImplZSpaceW extends RendererWithImplW {

	private ZSpaceGwt zSpace;

	/**
	 * @param view
	 *            3D view
	 */
	public RendererWithImplZSpaceW(final EuclidianViewInput3DW view) {
		super(view, Canvas.createIfSupported());

		ScriptElement matrixScript = Document.get().createScriptElement();
		matrixScript.setSrc(GWT.getModuleBaseURL() + "js/gl-matrix-min.js");
		ScriptLoadCallback scriptCallback = new ScriptLoadCallback() {
			private boolean canceled = false;
			@Override
			public void onLoad() {
				if (canceled) {
					return;
				}
				ScriptElement zSpaceScript = Document.get().createScriptElement();
				zSpaceScript.setSrc(GWT.getModuleBaseURL() + "js/zSpace.js");
				ScriptLoadCallback scriptCallbackZspace = new ScriptLoadCallback() {
					private boolean canceledZSpace = false;
					@Override
					public void onLoad() {
						if (canceledZSpace) {
							return;
						}
						initZspace(view);
					}

					@Override
					public void onError() {
						Log.warn("zSpace not loaded");
					}

					@Override
					public void cancel() {
						canceledZSpace = true;
					}

				};
				ResourcesInjector.loadJS(zSpaceScript,
						scriptCallbackZspace);
			}

			@Override
			public void onError() {
				Log.warn("gl matrix not loaded");
			}

			@Override
			public void cancel() {
				canceled = true;
			}

		};
		ResourcesInjector.loadJS(matrixScript, scriptCallback);
	}

	/**
	 * @param view
	 *            3D view
	 */
	protected void initZspace(EuclidianViewInput3DW view) {
		zSpace = new ZSpaceGwt(glContext, webGLCanvas.getElement());
		((InputZSpace3DW) view.getInput3D()).setZSpace(zSpace);
	}

	@Override
	protected void createGLContext(boolean preserveDrawingBuffer) {
		if (preserveDrawingBuffer) {
			glContext = getBufferedContext(webGLCanvas.getElement());

		} else {
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
		
		if (zSpace == null) {
			super.drawScene();
			return;
		}

		// give canvas position to zSpace -- don't set it since zSpace beta 5
//		DockPanelW panel = (DockPanelW) view3D.getApplication().getGuiManager()
//				.getLayout().getDockManager().getPanel(App.VIEW_EUCLIDIAN3D);
//		zSpace.setCanvasOffset(panel.getAbsoluteLeft(), panel.getAbsoluteTop());

		// update zSpace
		zSpace.zspaceUpdate();

		clearColorBuffer();
		getRendererImpl().clearDepthBuffer();

		super.drawScene();

		// end zSpace
		zSpace.zspaceFrameEnd();

	}

	@Override
	final public void setBufferLeft() {
		if (zSpace == null) {
			return;
		}
		zSpace.zspaceLeftView();
	}

	@Override
	final public void setBufferRight() {
		if (zSpace == null) {
			return;
		}
		zSpace.zspaceRightView();
		// this is needed to set correctly the right buffer after drawing the left one
		// TODO check why
		glContext.bindTexture(WebGLRenderingContext.TEXTURE_2D, null);
	}

}
