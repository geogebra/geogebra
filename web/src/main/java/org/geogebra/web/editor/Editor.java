package org.geogebra.web.editor;

import org.geogebra.web.keyboard.OnScreenKeyBoard;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.himamis.retex.editor.web.JlmEditorLib;
import com.himamis.retex.editor.web.MathFieldW;
import com.himamis.retex.editor.web.xml.XmlResourcesEditor;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.web.CreateLibrary;
import com.himamis.retex.renderer.web.FactoryProviderGWT;
import com.himamis.retex.renderer.web.font.opentype.Opentype;
import com.himamis.retex.renderer.web.resources.ResourceLoaderW;

public class Editor implements EntryPoint {
	private JlmEditorLib library;
	private Opentype opentype;
	public void onModuleLoad() {
		ResourceLoaderW.addResource(
				"/com/himamis/retex/editor/desktop/meta/Octave.xml",
				XmlResourcesEditor.INSTANCE.octave());
		FactoryProvider.INSTANCE = new FactoryProviderGWT();
		library = new JlmEditorLib();
		opentype = Opentype.INSTANCE;
		CreateLibrary.exportLibrary(library, opentype);
		addEditorFunction(this);

	}

	public void edit(Element el) {
		Canvas canvas = Canvas.createIfSupported();
		el.appendChild(canvas.getCanvasElement());
		MathFieldW fld = new MathFieldW(el, canvas.getContext2d());
		OnScreenKeyBoard kb = new OnScreenKeyBoard(new KeyboardContext(),
				false);
		el.appendChild(kb.getElement());
		fld.requestViewFocus();
	}
	private native void addEditorFunction(Editor library) /*-{
		$wnd.tryEditor = function(el) {
			library.@org.geogebra.web.editor.Editor::edit(Lcom/google/gwt/dom/client/Element;)(el);
		}

	}-*/;

}
