package org.geogebra.web.full.gui.util;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.SyntaxAdapterImpl;
import org.geogebra.web.html5.util.CopyPasteW;

public class SyntaxAdapterImplWithPaste extends SyntaxAdapterImpl {

	public SyntaxAdapterImplWithPaste(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected boolean checkClipboardFormat(String text) {
		return CopyPasteW.pasteIfEncoded(getKernel().getApplication(), text);
	}
}
