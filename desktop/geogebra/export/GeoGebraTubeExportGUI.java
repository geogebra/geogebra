package geogebra.export;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.main.AbstractApplication;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * Export GeoGebra worksheet to GeoGebraTube.
 * 
 * @author Florian Sonner
 */
public class GeoGebraTubeExportGUI  extends GeoGebraTubeExport {
	
	public GeoGebraTubeExportGUI(AbstractApplication app) {
		super(app);
	}

	/**
	 * Progress bar dialog.
	 */
	private JDialog progressDialog;
	
	/**
	 * Progress bar.
	 */
	private JProgressBar progressBar;
	
	/**
	 * Status label.
	 */
	private JLabel statusLabel;
	
	/** 
	 * Abort button.
	 */
	private JButton abortButton;
	
	protected void setMaximum(int i) {
		progressBar.setMaximum(i);
	}

	protected void setMinimum(int i) {
		progressBar.setMinimum(i);
		
	}

	protected void setIndeterminate(boolean b) {
		progressBar.setIndeterminate(b);
		
	}

	protected void setValue(int end) {
		progressBar.setValue(end);
		
	}

	protected void setEnabled(boolean b) {
		progressBar.setEnabled(b);
		
	}
	
	/**
	 * Shows a small dialog with a progress bar. 
	 */
	protected void showDialog() {
		// initialize components
		progressBar = new JProgressBar();
		statusLabel = new JLabel(app.getPlain("UploadPrepare") + " ...");
		
		// setup buttons
		abortButton = new JButton(app.getMenu("Close"));
		abortButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				hideDialog();
			}
		});
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		buttonPanel.add(abortButton);
		
		// main panel
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.add(statusLabel, BorderLayout.NORTH);
		panel.add(progressBar, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		// dialog options
		progressDialog = new JDialog();
		progressDialog.setTitle(app.getMenu("UploadGeoGebraTube"));
		progressDialog.setResizable(false);
		progressDialog.add(panel);
		
		progressDialog.pack();
		progressDialog.setVisible(true);
		progressDialog.setLocationRelativeTo(null); // center
	}
	
	protected void pack() {
		progressDialog.pack();
	}

	/**
	 * Hides progress dialog.
	 */
	public void hideDialog() {
		progressDialog.setVisible(false);
	}
	
	protected void statusLabelSetText(String plain) {
		statusLabel.setText(plain);
		}



}
