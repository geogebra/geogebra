package geogebra.kernel.discrete.signalprocesser.shared;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.JFrame;

public class StatusDialog extends javax.swing.JDialog {
    
    public static void start(Window parent, String title, String caption, final Thread thread) {
        StatusDialog _dialog;
        if ( parent instanceof Frame ) {
            _dialog = new StatusDialog((Frame)parent,  title, caption);
        } else if ( parent instanceof Dialog ) {
            _dialog = new StatusDialog((Dialog)parent,  title, caption);
        } else {
            throw new RuntimeException("Unknown window type " + parent.getClass().getName());
        }
        final StatusDialog dialog = _dialog;
        Thread dialogthread = new Thread() {
            public void run() {
                dialog.setVisible(true);
            }
        };
        dialogthread.start();
        
        // Start thread to run job
        new Thread() {
            public void run() {
                // Start the thread
                thread.start();
                synchronized ( thread ) {
                    try {
                        thread.wait();
                    } catch ( InterruptedException e ) {
                        throw new RuntimeException(e);
                    }
                }
                
                // Close the dialog
                dialog.setVisible(false);
                dialog.dispose();
            }
        }.start();
    }
    
    public StatusDialog(Frame parent, String title, String caption) {
        super(parent, true);
        initComponents();
        this.setTitle(title);
        lblCaption.setText(caption);
    }
    
    public StatusDialog(Dialog parent, String title, String caption) {
        super(parent, true);
        initComponents();
        this.setTitle(title);
        lblCaption.setText(caption);
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        panelCenter = new javax.swing.JPanel();
        lblCaption = new javax.swing.JLabel();
        panelGap = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        panelCenter.setLayout(new java.awt.BorderLayout());

        panelCenter.add(lblCaption, java.awt.BorderLayout.NORTH);

        panelGap.setLayout(null);

        panelGap.setPreferredSize(new java.awt.Dimension(220, 5));
        panelCenter.add(panelGap, java.awt.BorderLayout.CENTER);

        progressBar.setIndeterminate(true);
        panelCenter.add(progressBar, java.awt.BorderLayout.SOUTH);

        getContentPane().add(panelCenter, new java.awt.GridBagConstraints());

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-319)/2, (screenSize.height-127)/2, 319, 127);
    }
    // </editor-fold>//GEN-END:initComponents
    
    public static void main(String args[]) {
        StatusDialog dialog = new StatusDialog(new JFrame(), "Title", "Caption");
        dialog.setVisible(true);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblCaption;
    private javax.swing.JPanel panelCenter;
    private javax.swing.JPanel panelGap;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables
    
}
