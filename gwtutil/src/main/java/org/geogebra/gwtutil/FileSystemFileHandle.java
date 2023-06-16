package org.geogebra.gwtutil;

import elemental2.dom.Blob;
import elemental2.dom.File;
import elemental2.promise.Promise;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class FileSystemFileHandle {
	public native Promise<FileSystemWritableFileStream> createWritable();

	public native Promise<File> getFile();

	@JsType(isNative = true, namespace = JsPackage.GLOBAL)
	public static class FileSystemWritableFileStream {
		public native void write(Blob blob);

		public native void close();
	}
}
