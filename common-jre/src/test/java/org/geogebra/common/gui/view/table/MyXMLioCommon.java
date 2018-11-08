package org.geogebra.common.gui.view.table;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;

import org.geogebra.common.jre.gui.MyImageJre;
import org.geogebra.common.jre.io.MyXMLioJre;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;

final class MyXMLioCommon extends MyXMLioJre {
	MyXMLioCommon(Kernel kernel, Construction cons) {
		super(kernel, cons);
	}

	@Override
	protected void readZip(ZipInputStream zip, boolean isGGTfile)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected MyImageJre getExportImage(double width, double height) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected MyImageJre getExternalImage(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void writeImage(MyImageJre img, String ext,
			OutputStream os) throws IOException {
		// TODO Auto-generated method stub

	}
}