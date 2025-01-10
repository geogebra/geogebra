// Copyright 2001-2005 freehep
package org.freehep.graphicsio.pdf;

import java.io.IOException;

import org.freehep.graphics2d.font.CharTable;
import org.freehep.graphicsio.font.FontEmbedder;

public class PDFCharTableWriter implements PDFRedundanceTracker.Writer {

	private static PDFCharTableWriter ctw;

	public static PDFCharTableWriter getInstance() {
		if (ctw == null) {
			ctw = new PDFCharTableWriter();
		}
		return ctw;
	}

	@Override
	public void writeObject(Object object, PDFRef ref, PDFWriter pdf)
			throws IOException {

		CharTable charTable = (CharTable) object;

		PDFDictionary encoding = pdf.openDictionary(ref.getName());
		encoding.entry("Type", pdf.name("Encoding"));

		Object[] differences = new Object[257];
		differences[0] = Integer.valueOf(0);
		for (int i = 0; i < 256; i++) {
			String charName = charTable.toName(i);
			differences[i + 1] = (charName != null) ? pdf.name(charName)
					: pdf.name(FontEmbedder.NOTDEF);
		}
		encoding.entry("Differences", differences);

		pdf.close(encoding);
	}
}
