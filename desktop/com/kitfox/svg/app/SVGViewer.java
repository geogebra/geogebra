/*
 * SVG Salamander
 * Copyright (c) 2004, Mark McKay
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 *   - Redistributions of source code must retain the above 
 *     copyright notice, this list of conditions and the following
 *     disclaimer.
 *   - Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials 
 *     provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE. 
 * 
 * Mark McKay can be contacted at mark@kitfox.com.  Salamander and other
 * projects can be found at http://www.kitfox.com
 *
 * Created on April 3, 2004, 5:28 PM
 */

package com.kitfox.svg.app;

import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGConst;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGDisplayPanel;
import com.kitfox.svg.SVGElement;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class SVGViewer extends javax.swing.JFrame 
{
    public static final long serialVersionUID = 1;

    SVGDisplayPanel svgDisplayPanel = new SVGDisplayPanel();

    /** FileChooser for running in trusted environments */
    final JFileChooser fileChooser;
    {
//        fileChooser = new JFileChooser(new File("."));
        JFileChooser fc = null;
        try
        {
            fc = new JFileChooser();
            fc.setFileFilter(
                new javax.swing.filechooser.FileFilter() {
                    final Matcher matchLevelFile = Pattern.compile(".*\\.svg[z]?").matcher("");

                    public boolean accept(File file)
                    {
                        if (file.isDirectory()) return true;

                        matchLevelFile.reset(file.getName());
                        return matchLevelFile.matches();
                    }

                    public String getDescription() { return "SVG file (*.svg, *.svgz)"; }
                }
            );
        }
        catch (AccessControlException ex)
        {
            //Do not create file chooser if webstart refuses permissions
        }
        fileChooser = fc;
    }

    /** Backup file service for opening files in WebStart situations */
    /*
    final FileOpenService fileOpenService;
    {
        try 
        { 
            fileOpenService = (FileOpenService)ServiceManager.lookup("javax.jnlp.FileOpenService"); 
        } 
        catch (UnavailableServiceException e) 
        { 
            fileOpenService = null; 
        } 
    }
     */
    
    /** Creates new form SVGViewer */
    public SVGViewer() {
        initComponents();

        setSize(800, 600);

        svgDisplayPanel.setBgColor(Color.white);

        svgDisplayPanel.setPreferredSize(getSize());
        panel_svgArea.add(svgDisplayPanel, BorderLayout.CENTER);
//        scrollPane_svgArea.setViewportView(svgDisplayPanel);
    }

    private void loadURL(URL url)
    {
        boolean verbose = cmCheck_verbose.isSelected();
        
//                SVGUniverse universe = new SVGUniverse();
        SVGUniverse universe = SVGCache.getSVGUniverse();
        SVGDiagram diagram = null;
        URI uri;

        if (!CheckBoxMenuItem_anonInputStream.isSelected())
        {
            //Load from a disk with a valid URL
            uri = universe.loadSVG(url);

            if (verbose) System.err.println("Loading document " + uri.toString());

            diagram = universe.getDiagram(uri);
        }
        else
        {
            //Load from a stream with no particular valid URL
            try
            {
                InputStream is = url.openStream();
                uri = universe.loadSVG(is, "defaultName");

                if (verbose) System.err.println("Loading document " + uri.toString());
            }
            catch (Exception e)
            {
                Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, null, e);
                return;
            }
        }
/*
ByteArrayOutputStream bs = new ByteArrayOutputStream();
ObjectOutputStream os = new ObjectOutputStream(bs);
os.writeObject(universe);
os.close();

ByteArrayInputStream bin = new ByteArrayInputStream(bs.toByteArray());
ObjectInputStream is = new ObjectInputStream(bin);
universe = (SVGUniverse)is.readObject();
is.close();
*/

        diagram = universe.getDiagram(uri);

        svgDisplayPanel.setDiagram(diagram);
        repaint();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        scrollPane_svgArea = new javax.swing.JScrollPane();
        panel_svgArea = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        menu_file = new javax.swing.JMenu();
        cm_loadFile = new javax.swing.JMenuItem();
        cm_loadUrl = new javax.swing.JMenuItem();
        menu_window = new javax.swing.JMenu();
        cm_800x600 = new javax.swing.JMenuItem();
        CheckBoxMenuItem_anonInputStream = new javax.swing.JCheckBoxMenuItem();
        cmCheck_verbose = new javax.swing.JCheckBoxMenuItem();
        menu_help = new javax.swing.JMenu();
        cm_about = new javax.swing.JMenuItem();

        setTitle("SVG Viewer - Salamander Project");
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                exitForm(evt);
            }
        });

        panel_svgArea.setLayout(new java.awt.BorderLayout());

        panel_svgArea.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                panel_svgAreaMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                panel_svgAreaMouseReleased(evt);
            }
        });

        scrollPane_svgArea.setViewportView(panel_svgArea);

        getContentPane().add(scrollPane_svgArea, java.awt.BorderLayout.CENTER);

        menu_file.setMnemonic('f');
        menu_file.setText("File");
        cm_loadFile.setMnemonic('l');
        cm_loadFile.setText("Load File...");
        cm_loadFile.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cm_loadFileActionPerformed(evt);
            }
        });

        menu_file.add(cm_loadFile);

        cm_loadUrl.setText("Load URL...");
        cm_loadUrl.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cm_loadUrlActionPerformed(evt);
            }
        });

        menu_file.add(cm_loadUrl);

        jMenuBar1.add(menu_file);

        menu_window.setText("Window");
        cm_800x600.setText("800 x 600");
        cm_800x600.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cm_800x600ActionPerformed(evt);
            }
        });

        menu_window.add(cm_800x600);

        CheckBoxMenuItem_anonInputStream.setText("Anonymous Input Stream");
        menu_window.add(CheckBoxMenuItem_anonInputStream);

        cmCheck_verbose.setText("Verbose");
        cmCheck_verbose.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cmCheck_verboseActionPerformed(evt);
            }
        });

        menu_window.add(cmCheck_verbose);

        jMenuBar1.add(menu_window);

        menu_help.setText("Help");
        cm_about.setText("About...");
        cm_about.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cm_aboutActionPerformed(evt);
            }
        });

        menu_help.add(cm_about);

        jMenuBar1.add(menu_help);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cm_loadUrlActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cm_loadUrlActionPerformed
    {//GEN-HEADEREND:event_cm_loadUrlActionPerformed
        String urlStrn = JOptionPane.showInputDialog(this, "Enter URL of SVG file");
        if (urlStrn == null) return;
        
        try
        {
            URL url = new URL(URLEncoder.encode(urlStrn, "UTF-8"));
            loadURL(url);
        }
        catch (Exception e)
        {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, null, e);
        }

    }//GEN-LAST:event_cm_loadUrlActionPerformed

    private void panel_svgAreaMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_panel_svgAreaMouseReleased
    {//GEN-HEADEREND:event_panel_svgAreaMouseReleased
        SVGDiagram diagram = svgDisplayPanel.getDiagram();
        List pickedElements;
        try
        {
            pickedElements = diagram.pick(new Point(evt.getX(), evt.getY()), null);
        } 
        catch (SVGException e)
        {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, null, e);
            return;
        }
        
        System.out.println("Pick results:");
        for (Iterator it = pickedElements.iterator(); it.hasNext();)
        {
            ArrayList path = (ArrayList)it.next();
            
            System.out.print("  Path: ");
            
            for (Iterator it2 = path.iterator(); it2.hasNext();)
            {
                SVGElement ele = (SVGElement)it2.next();

                System.out.print("" + ele.getId() + "(" + ele.getClass().getName() + ") ");
            }
            System.out.println();
        }
    }//GEN-LAST:event_panel_svgAreaMouseReleased

    private void panel_svgAreaMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_panel_svgAreaMousePressed
    {//GEN-HEADEREND:event_panel_svgAreaMousePressed

    }//GEN-LAST:event_panel_svgAreaMousePressed

    private void cmCheck_verboseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cmCheck_verboseActionPerformed
    {//GEN-HEADEREND:event_cmCheck_verboseActionPerformed
        SVGCache.getSVGUniverse().setVerbose(cmCheck_verbose.isSelected());
    }//GEN-LAST:event_cmCheck_verboseActionPerformed

    private void cm_aboutActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cm_aboutActionPerformed
    {//GEN-HEADEREND:event_cm_aboutActionPerformed
        //JOptionPane.showMessageDialog(this, "Salamander SVG - Created by Mark McKay\nhttp://www.kitfox.com");
        VersionDialog dlg = new VersionDialog(this, true, cmCheck_verbose.isSelected());
        dlg.setVisible(true);
    }//GEN-LAST:event_cm_aboutActionPerformed

    private void cm_800x600ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cm_800x600ActionPerformed
        setSize(800, 600);
    }//GEN-LAST:event_cm_800x600ActionPerformed
    
    private void cm_loadFileActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cm_loadFileActionPerformed
    {//GEN-HEADEREND:event_cm_loadFileActionPerformed
        try
        {
            int retVal = fileChooser.showOpenDialog(this);
            if (retVal == JFileChooser.APPROVE_OPTION)
            {
                File chosenFile = fileChooser.getSelectedFile();

                URL url = chosenFile.toURI().toURL();

                loadURL(url);
            }
        }
        catch (Exception e)
        {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, null, e);
        }

    }//GEN-LAST:event_cm_loadFileActionPerformed

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
//        setVisible(false);
//        dispose();
        System.exit(0);
    }//GEN-LAST:event_exitForm

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new SVGViewer().setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBoxMenuItem CheckBoxMenuItem_anonInputStream;
    private javax.swing.JCheckBoxMenuItem cmCheck_verbose;
    private javax.swing.JMenuItem cm_800x600;
    private javax.swing.JMenuItem cm_about;
    private javax.swing.JMenuItem cm_loadFile;
    private javax.swing.JMenuItem cm_loadUrl;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu menu_file;
    private javax.swing.JMenu menu_help;
    private javax.swing.JMenu menu_window;
    private javax.swing.JPanel panel_svgArea;
    private javax.swing.JScrollPane scrollPane_svgArea;
    // End of variables declaration//GEN-END:variables

}
