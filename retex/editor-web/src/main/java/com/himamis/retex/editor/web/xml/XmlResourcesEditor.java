package com.himamis.retex.editor.web.xml;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.himamis.retex.renderer.web.resources.xml.XmlResource;

public interface XmlResourcesEditor extends ClientBundle {
	

	public static final XmlResourcesEditor INSTANCE = GWT
			.create(XmlResourcesEditor.class);

	/*
	 * This interface was generated based on the available source xml at that
	 * time. Please regenerate this if you add/delete/rename xmls.
	 */
	@Source("com/himamis/retex/editor/web/meta/Octave.xml")
	public XmlResource octave();

}
