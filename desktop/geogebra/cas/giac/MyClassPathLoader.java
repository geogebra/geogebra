package geogebra.cas.giac;

import geogebra.main.AppD;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.jogamp.common.jvm.JNILibLoaderBase.LoaderAction;

/**
 * Adapted from http://www.jotschi.de/Uncategorized/2011/09/26/jogl2-jogamp-classpathloader-for-native-libraries.html
 *
 */
public class MyClassPathLoader implements LoaderAction {

	/**
	 * Loads the given library with the libname from the classpath root
	 */
	public boolean loadLibrary(String libname, boolean ignoreError) {
		
		String extension;
		if (AppD.WINDOWS) {
			extension = ".dll";
		} else if (AppD.MAC_OS) {
			extension = ".jnilib";
		} else {
			// assume Linux
			extension = ".so";
		}
		
		String filename = libname + extension;
		InputStream ins = ClassLoader.getSystemResourceAsStream(filename);
		
		try {
			File tmpFile = writeTmpFile(ins, filename);
			System.load(tmpFile.getAbsolutePath());
			tmpFile.delete();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Write the content of the inputstream into a tempfile with the given
	 * filename
	 * 
	 * @param ins
	 * @param filename
	 * @throws IOException
	 */
	private File writeTmpFile(InputStream ins, String filename)
			throws IOException {

		File tmpFile = new File(System.getProperty("java.io.tmpdir"), filename);
		tmpFile.delete();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(tmpFile);

			byte[] buffer = new byte[1024];
			int len;
			while ((len = ins.read(buffer)) != -1) {

				fos.write(buffer, 0, len);
			}
		} finally {
			if (ins != null) {
				ins.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return tmpFile;
	}

	public void loadLibrary(String libname, String[] preload,
			boolean preloadIgnoreError) {
		loadLibrary(libname, preloadIgnoreError);
	}

	public boolean loadLibrary(String arg0, boolean arg1, ClassLoader arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	public void loadLibrary(String arg0, String[] arg1, boolean arg2,
			ClassLoader arg3) {
		// TODO Auto-generated method stub
		
	}

}