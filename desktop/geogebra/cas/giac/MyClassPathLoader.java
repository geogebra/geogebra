package geogebra.cas.giac;

import geogebra.common.main.App;
import geogebra.main.AppD;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Adapted from http://www.jotschi.de/Uncategorized/2011/09/26/jogl2-jogamp-classpathloader-for-native-libraries.html
 *
 */
public class MyClassPathLoader {

	/**
	 * Loads the given library with the libname from the classpath root
	 * @param libname eg javagiac or javagiac64
	 * @return success
	 */
	public boolean loadLibrary(String libname) {
		
		String extension, prefix;
		if (AppD.WINDOWS) {
			prefix = "";
			extension = ".dll";
		} else if (AppD.MAC_OS) {
			prefix = "lib";
			extension = ".jnilib";
		} else {
			// assume Linux
			prefix = "lib";
			extension = ".so";
		}
		
		String filename = prefix + libname + extension;
		InputStream ins = ClassLoader.getSystemResourceAsStream(filename);
		
		if (ins == null) {
			App.error(filename + "not found");
			return false;
		}
			
		try {

			// Math.random() to avoid problems with 2 instances
			File tmpFile = writeTmpFile(ins, prefix + libname + Math.random()+ extension);
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
	private static File writeTmpFile(InputStream ins, String filename)
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

}