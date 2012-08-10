package geogebra.common.javax.swing;

public interface GOptionPane {

    //
    // Option types - the same as in JOptionPane
    //

    /** 
     * Type meaning Look and Feel should not supply any options -- only
     * use the options from the <code>GOptionPane</code>.
     */
    public static final int         DEFAULT_OPTION = -1;
//    /** Type used for <code>showConfirmDialog</code>. */
//    public static final int         YES_NO_OPTION = 0;
//    /** Type used for <code>showConfirmDialog</code>. */
//    public static final int         YES_NO_CANCEL_OPTION = 1;
//    /** Type used for <code>showConfirmDialog</code>. */
//    public static final int         OK_CANCEL_OPTION = 2;

	
	public abstract int showConfirmDialog(Object parentComponent,
			String message, String title, int optionType, int messageType);

}
