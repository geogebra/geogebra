package fr.grenoble.ujf.giac;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * CAS resource bundle
 */
public interface CASResources extends ClientBundle {

	/**
	 * maybe it's better if INSTANCE is created later?
	 */
	CASResources INSTANCE = GWT.create(CASResources.class);

	/** @return giac.js */
	@Source("fr/grenoble/ujf/giac/giac.js")
	TextResource giacJs();

	/** @return giac.wasm */
	@Source("fr/grenoble/ujf/giac/giac.wasm.js")
	TextResource giacWasm();

}
