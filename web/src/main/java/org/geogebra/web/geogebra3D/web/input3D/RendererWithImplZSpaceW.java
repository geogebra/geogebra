package org.geogebra.web.geogebra3D.web.input3D;

import org.geogebra.common.main.App;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.RendererImplShadersW;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.RendererWithImplW;
import org.geogebra.web.html5.util.DynamicScriptElement;
import org.geogebra.web.html5.util.ScriptLoadCallback;
import org.geogebra.web.web.gui.layout.DockPanelW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
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

	public RendererWithImplZSpaceW(final EuclidianViewInput3DW view) {
		super(view);

		DynamicScriptElement script = (DynamicScriptElement) Document.get()
				.createScriptElement();
		script.setSrc(GWT.getModuleBaseURL() + "js/gl-matrix-min.js");
		ScriptLoadCallback scriptCallback = new ScriptLoadCallback() {
			private boolean canceled = false;
			@Override
			public void onLoad() {
				if (canceled) {
					return;
				}
				DynamicScriptElement script = (DynamicScriptElement) Document.get()
						.createScriptElement();
				script.setSrc(GWT.getModuleBaseURL() + "js/zSpace.js");
				ScriptLoadCallback scriptCallback = new ScriptLoadCallback() {
					private boolean canceled = false;
					@Override
					public void onLoad() {
						if (canceled) {
							return;
						}
						// create zspace object
						zSpace = new ZSpaceGwt(glContext, webGLCanvas.getElement());
						((InputZSpace3DW) view.getInput3D()).setZSpace(zSpace);
					}

					@Override
					public void onError() {
						if (canceled) {
							return;
						}
					}

					public void cancel() {
						canceled = true;

					}

				};
				script.addLoadHandler(scriptCallback);
				Document.get().getBody().appendChild(script);
			}

			@Override
			public void onError() {
				if (canceled) {
					return;
				}
			}

			public void cancel() {
				canceled = true;

			}

		};
		script.addLoadHandler(scriptCallback);
		Document.get().getBody().appendChild(script);

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
		if (zSpace == null) {
			return;
		}
		zSpace.zspaceLeftView();
	}

	@Override
	final protected void setBufferRight() {
		if (zSpace == null) {
			return;
		}
		zSpace.zspaceRightView();
	}

}
