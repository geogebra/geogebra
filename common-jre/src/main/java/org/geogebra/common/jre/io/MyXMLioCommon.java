package org.geogebra.common.jre.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;

import org.geogebra.common.jre.gui.MyImageJre;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;

public final class MyXMLioCommon extends MyXMLioJre {
	public MyXMLioCommon(Kernel kernel, Construction cons) {
		super(kernel, cons);
	}

	@Override
	protected void loadSVG(String svg, String name) {
		// not supported yet
	}

	@Override
	protected void loadBitmap(ZipInputStream zip, String name) {
		// not supported yet
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