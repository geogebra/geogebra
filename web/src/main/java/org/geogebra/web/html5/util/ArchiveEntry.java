package org.geogebra.web.html5.util;

import org.geogebra.common.util.StringUtil;

import elemental2.core.JsArray;
import elemental2.core.Uint8Array;
import elemental2.dom.Blob;
import elemental2.dom.URL;

public class ArchiveEntry {
	public final String string;
	public final Uint8Array data;

	private ArchiveEntry(String string, Uint8Array blob) {
		this.string = string;
		this.data = blob;
	}

	/**
	 * @param blob binary content
	 */
	public ArchiveEntry(Uint8Array blob) {
		this(null, blob);
	}

	/**
	 * @param string text content
	 */
	public ArchiveEntry(String string) {
		this(string, null);
	}

	public ArchiveEntry duplicate() {
		return new ArchiveEntry(string, data);
	}

	/**
	 * @return URL for temporary use
	 */
	public String createUrl() {
		return string == null ? URL.createObjectURL(new Blob(
				new JsArray<>(Blob.ConstructorBlobPartsArrayUnionType.of(data)))) : string;
	}

	public boolean isEmpty() {
		return StringUtil.empty(string) && data == null;
	}

	/**
	 * @param fileName filename
	 * @return data URL
	 */
	public String export(String fileName) {
		String ext = StringUtil.getFileExtensionStr(fileName);
		return "data:image/" + ext + ";base64," + Base64.bytesToBase64(data);
	}
}
