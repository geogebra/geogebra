package org.geogebra.web.geogebra3D.web.euclidian3D.openGL.shaders;

import com.googlecode.gwtgl.binding.WebGLRenderingContext;

/**
 * Some GPUs cannot translated shaders, this class should provide information
 * about the supported ones.
 */
public class GpuBlacklist {

	/*
	 * This renderer is used by older chromebooks eg Samsung Chromebook
	 * XE500C21-H02US and probably similar models. If needed this can be
	 * extended to use a list or using more sophisticated methods to blacklist
	 * some gpus
	 */
	private static final String blacklistedRenderer = "Gallium 0.4 on i915 (chipset: Pineview M)";

	/**
	 * @param glContext
	 *            gl context used
	 * @return True if the current gpu is blacklisted.
	 */
	public static boolean isCurrentGpuBlacklisted(
	        WebGLRenderingContext glContext) {
		String unmaskedRenderer = getUnmaskedRendererWebgl(glContext);
		return blacklistedRenderer.equals(unmaskedRenderer);
	}

	private static native String getUnmaskedRendererWebgl(
	        WebGLRenderingContext context) /*-{
		var dbgRenderInfo = context.getExtension("WEBGL_debug_renderer_info");
		if (dbgRenderInfo) {
			return context.getParameter(dbgRenderInfo.UNMASKED_RENDERER_WEBGL);
		}
		return '';
	}-*/;
}
