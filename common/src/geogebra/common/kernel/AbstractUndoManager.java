package geogebra.common.kernel;

public abstract class AbstractUndoManager {

	public abstract void processXML(String string) throws Exception;

	public abstract void redo();
	
	public abstract void undo();
	
	public abstract void storeUndoInfo();
	
	public abstract void storeUndoInfo(boolean b);
	
	public abstract void restoreCurrentUndoInfo();
	
	public abstract void initUndoInfo();
	
	public abstract boolean redoPossible() ;
	
	public abstract boolean undoPossible() ;

	public abstract Object getCurrentUndoInfo();

	public abstract void storeUndoInfoAfterPasteOrAdd();
}
