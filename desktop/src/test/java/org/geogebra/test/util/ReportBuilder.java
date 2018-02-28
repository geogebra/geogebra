package org.geogebra.test.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.geogebra.common.util.AsyncOperation;

public class ReportBuilder extends AsyncOperation<String> {
	OutputStreamWriter isw = null;

	public ReportBuilder(String filename) {
		final String path = "build" + File.separator + "reports";
		File dir = new File(path);
		dir.mkdirs();
		File f = new File(path + File.separator + filename);

		try {
			isw = new OutputStreamWriter(new FileOutputStream(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("file:///" + f.getAbsolutePath());
	}

	@Override
	public void callback(String content) {
		try {
			isw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		if (isw != null) {
			try {
				isw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
