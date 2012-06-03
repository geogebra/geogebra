package geogebra.gui.layout;

/**
 * interface for DockSplitPane and DockPanel
 * @author mathieu
 *
 */
public interface DockComponent {
	
	/**
	 * 
	 * @param prefix prefix to add
	 * @return string description with prefix
	 */
	public String toString(String prefix);
	
	/**
	 * Update resize weight
	 * @return true if it contains a panel that takes new space (currently if contains an euclidian view)
	 */
	public boolean updateResizeWeight();

}
