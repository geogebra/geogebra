/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.util;

import java.util.ArrayList;
import java.util.List;

import org.codemirror.editor.CodeMirrorResources;
import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.JavaScriptInjector;
import org.geogebra.web.html5.util.StringConsumer;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.ui.FlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

import elemental2.dom.Element;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Plain CodeMirror 6 editor host for the scripting editor prototype.
 */
public class CodeMirrorEditorWidget extends FlowPanel {
	private boolean loading = false;
	private static final List<Runnable> callbacks = new ArrayList<>();
	private final FlowPanel editorHost = new FlowPanel();
	private CodeMirrorEditor codeMirrorEditor;
	private String text = "";
	private boolean focusOnCodeMirrorLoad = false;

	/**
	 * Creates a code mirror editor widget.
	 */
	public CodeMirrorEditorWidget() {
		editorHost.addStyleName("codeMirrorEditorHost");
		add(editorHost);
	}

	/**
	 * @param text editor text
	 */
	public void setText(String text) {
		this.text = text == null ? "" : text;
		if (codeMirrorEditor != null && !codeMirrorEditor.getValue().equals(text)) {
			codeMirrorEditor.setValue(this.text);
		}
	}

	/**
	 * @return editor text
	 */
	public String getText() {
		return codeMirrorEditor == null ? text : codeMirrorEditor.getValue();
	}

	/**
	 * Refresh code mirror editor.
	 */
	public void refreshEditor() {
		if (codeMirrorEditor != null) {
			codeMirrorEditor.refresh();
		}
	}

	/**
	 * Focus code mirror editor.
	 */
	public void focusEditor() {
		if (codeMirrorEditor != null) {
			codeMirrorEditor.focus();
		} else {
			focusOnCodeMirrorLoad = true;
		}
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		initCodeMirror();
	}

	@Override
	protected void onDetach() {
		if (codeMirrorEditor != null) {
			codeMirrorEditor.destroy();
			codeMirrorEditor = null;
		}
		super.onDetach();
	}

	private void initCodeMirror() {
		if (codeMirrorEditor != null) {
			return;
		}

		onCodeMirrorLoaded(() -> Scheduler.get().scheduleDeferred(() -> {
			if (!isAttached() || codeMirrorEditor != null) {
				return;
			}

			CodeMirrorOptions options = CodeMirrorOptions.create();
			options.setValue(text);
			options.setOnChange(value -> text = value);
			codeMirrorEditor = CodeMirror.createEditor(
					Js.uncheckedCast(editorHost.getElement()), options);
			refreshEditor();
			if (focusOnCodeMirrorLoad) {
				codeMirrorEditor.focus();
			}
		}));
	}

	private void onCodeMirrorLoaded(Runnable callback) {
		if (Window.getCodeMirror() != null) {
			callback.run();
			return;
		}

		callbacks.add(callback);
		if (loading) {
			return;
		}

		loading = true;
		GWT.runAsync(CodeMirrorEditorWidget.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable throwable) {
				loading = false;
				callbacks.clear();
				Log.error("CodeMirror editor failed to load");
			}

			@Override
			public void onSuccess() {
				JavaScriptInjector.inject(CodeMirrorResources.INSTANCE.codemirror());
				loading = false;
				runCallbacks();
			}
		});
	}

	private static void runCallbacks() {
		List<Runnable> pendingCallbacks = new ArrayList<>(callbacks);
		callbacks.clear();
		for (Runnable callback : pendingCallbacks) {
			callback.run();
		}
	}

	/**
	 * Access to global window properties.
	 */
	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
	private final static class Window {
		@JsProperty(name = "GeoGebraCodeMirror")
		private static native Object getCodeMirror();
	}

	/**
	 * Minimal JsInterop surface for CodeMirror 6.
	 */
	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "GeoGebraCodeMirror")
	private final static class CodeMirror {
		private static native CodeMirrorEditor createEditor(Element element,
				CodeMirrorOptions options);
	}

	/**
	 * CodeMirror editor options.
	 */
	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
	public final static class CodeMirrorOptions {
		@JsProperty
		public native void setValue(String value);

		@JsProperty
		public native void setOnChange(StringConsumer onChange);

		/**
		 * @return plain JS object
		 */
		@JsOverlay
		private static CodeMirrorOptions create() {
			return Js.uncheckedCast(JsPropertyMap.of());
		}
	}

	/**
	 * CodeMirror editor instance.
	 */
	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
	private final static class CodeMirrorEditor {
		private native void destroy();

		private native void focus();

		private native String getValue();

		private native void setValue(String text);

		private native void refresh();
	}
}
