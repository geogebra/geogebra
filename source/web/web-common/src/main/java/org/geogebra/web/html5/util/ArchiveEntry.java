package org.geogebra.web.html5.util;

import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.StringUtil;

import elemental2.core.JsArray;
import elemental2.core.Uint8Array;
import elemental2.dom.Blob;
import elemental2.dom.BlobPropertyBag;
import elemental2.dom.URL;

public class ArchiveEntry {
	public final String string;
	public final Uint8Array data;
	private final FileExtensions extension;
	private String fileName;

	private ArchiveEntry(String fileName, String string, Uint8Array blob) {
		this.string = string;
		this.data = blob;
		this.fileName = fileName;
		extension = StringUtil.getFileExtension(fileName);
	}

	/**
	 * @param blob binary content
	 */
	public ArchiveEntry(String fileName, Uint8Array blob) {
		this(fileName, null, blob);
	}

	/**
	 * @param string text content
	 */
	public ArchiveEntry(String fileName, String string) {
		this(fileName, string, null);
	}

	public static int dataUrlToBinarySizeKB(String data) {
		return data.length() * 3 / 4 / 1024;
	}

	public ArchiveEntry copy(String fileName) {
		return new ArchiveEntry(fileName, string, data);
	}

	/**
	 * @return URL for temporary use
	 */
	public String createUrl() {
		if (string != null) {
			return string;
		}
		BlobPropertyBag options = BlobPropertyBag.create();
		options.setType(extension.getMime());
		return URL.createObjectURL(new Blob(
				new JsArray<>(Blob.ConstructorBlobPartsArrayUnionType.of(data)),
				options));
	}

	public boolean isEmpty() {
		return StringUtil.empty(string) && data == null;
	}

	/**
	 * @return data URL
	 */
	public String export() {
		return "data:" + extension.getMime() + ";base64," + Base64.bytesToBase64(data);
	}

	public String getFileName() {
		return fileName;
	}

	public FileExtensions getExtension() {
		return extension;
	}

	public boolean isThumbnail() {
		return MyXMLio.XML_FILE_THUMBNAIL.equalsIgnoreCase(fileName);
	}

	/**
	 * @return size in kilobytes
	 */
	public int getSizeKB() {
		if (data != null) {
			return data.byteLength / 1024;
		} else {
			return dataUrlToBinarySizeKB(string);
		}
	}
}
