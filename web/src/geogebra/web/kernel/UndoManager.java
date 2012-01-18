package geogebra.web.kernel;

import geogebra.common.kernel.AbstractUndoManager;
import geogebra.common.kernel.Construction;
import geogebra.web.io.MyXMLio;

public class UndoManager extends AbstractUndoManager {

	private MyXMLio xmlio;

	public UndoManager(Construction cons) {
	    super(cons);
		xmlio = new MyXMLio(cons.getKernel(), cons);
		cons.setXMLio(xmlio);
    }

	@Override
	public void processXML(String xml) throws Exception {
		xmlio.processXmlString(xml, true, false);

	}

	@Override
	public void storeUndoInfoAfterPasteOrAdd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeUndoInfo(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void loadUndoInfo(AppState state) {
		// TODO Auto-generated method stub

	}
}
