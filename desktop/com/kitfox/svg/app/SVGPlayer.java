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


import com.kitfox.svg.SVGConst;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGDisplayPanel;
import com.kitfox.svg.SVGElement;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
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
public class SVGPlayer extends javax.swing.JFrame
{
    public static final long serialVersionUID = 1;

    SVGDisplayPanel svgDisplayPanel = new SVGDisplayPanel();

    final PlayerDialog playerDialog;
    
    SVGUniverse universe;
    
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
    public SVGPlayer() {
        initComponents();

        setSize(800, 600);

        svgDisplayPanel.setBgColor(Color.white);
        svgDisplayPanel.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent evt)
            {
                SVGDiagram diagram = svgDisplayPanel.getDiagram();
                if (diagram == null) return;
                
                System.out.println("Picking at cursor (" + evt.getX() + ", " + evt.getY() + ")");
                try
                {
                    List paths = diagram.pick(new Point2D.Float(evt.getX(), evt.getY()), null);
                    for (int i = 0; i < paths.size(); i++)
                    {
                        ArrayList path = (ArrayList)paths.get(i);
                        System.out.println(pathToString(path));
                    }
                }
                catch (SVGException ex)
                {
                    Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, 
                        "Could not pick", ex);
                }
            }
        }
        );
        
        svgDisplayPanel.setPreferredSize(getSize());
        scrollPane_svgArea.setViewportView(svgDisplayPanel);
        
        playerDialog = new PlayerDialog(this);
    }
    
    private String pathToString(List path)
    {
        if (path.size() == 0) return "";
        
        StringBuffer sb = new StringBuffer();
        sb.append(path.get(0));
        for (int i = 1; i < path.size(); i++)
        {
            sb.append("/");
            sb.append(((SVGElement)path.get(i)).getId());
        }
        return sb.toString();
    }
    
    public void updateTime(double curTime)
    {
        try
        {
            if (universe != null)
            {
                universe.setCurTime(curTime);
                universe.updateTime();
    //            svgDisplayPanel.updateTime(curTime);
                repaint();
            }
        }
        catch (Exception e)
        {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, null, e);
        }
    }

    private void loadURL(URL url)
    {
        boolean verbose = cmCheck_verbose.isSelected();

        universe = new SVGUniverse();
        universe.setVerbose(verbose);
        SVGDiagram diagram = null;

        if (!CheckBoxMenuItem_anonInputStream.isSelected())
        {
            //Load from a disk with a valid URL
            URI uri = universe.loadSVG(url);

            if (verbose) System.err.println(uri.toString());

            diagram = universe.getDiagram(uri);
        }
        else
        {
            //Load from a stream with no particular valid URL
            try
            {
                InputStream is = url.openStream();
                URI uri = universe.loadSVG(is, "defaultName");

                if (verbose) System.err.println(uri.toString());

                diagram = universe.getDiagram(uri);
            }
            catch (Exception e)
            {
                Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, null, e);
            }
        }

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
        jMenuBar1 = new javax.swing.JMenuBar();
        menu_file = new javax.swing.JMenu();
        cm_loadFile = new javax.swing.JMenuItem();
        cm_loadUrl = new javax.swing.JMenuItem();
        menu_window = new javax.swing.JMenu();
        cm_player = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        cm_800x600 = new javax.swing.JMenuItem();
        CheckBoxMenuItem_anonInputStream = new javax.swing.JCheckBoxMenuItem();
        cmCheck_verbose = new javax.swing.JCheckBoxMenuItem();
        menu_help = new javax.swing.JMenu();
        cm_about = new javax.swing.JMenuItem();

        setTitle("SVG Player - Salamander Project");
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                exitForm(evt);
            }
        });

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
        cm_player.setText("Player");
        cm_player.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cm_playerActionPerformed(evt);
            }
        });

        menu_window.add(cm_player);

        menu_window.add(jSeparator2);

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

    private void cmCheck_verboseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cmCheck_verboseActionPerformed
    {//GEN-HEADEREND:event_cmCheck_verboseActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_cmCheck_verboseActionPerformed

    private void cm_playerActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cm_playerActionPerformed
    {//GEN-HEADEREND:event_cm_playerActionPerformed
        playerDialog.setVisible(true);
    }//GEN-LAST:event_cm_playerActionPerformed

    private void cm_aboutActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cm_aboutActionPerformed
    {//GEN-HEADEREND:event_cm_aboutActionPerformed
        VersionDialog dia = new VersionDialog(this, true, cmCheck_verbose.isSelected());
        dia.setVisible(true);
//        JOptionPane.showMessageDialog(this, "Salamander SVG - Created by Mark McKay\nhttp://www.kitfox.com");
    }//GEN-LAST:event_cm_aboutActionPerformed

    private void cm_800x600ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cm_800x600ActionPerformed
        setSize(800, 600);
    }//GEN-LAST:event_cm_800x600ActionPerformed
    
    private void cm_loadFileActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cm_loadFileActionPerformed
    {//GEN-HEADEREND:event_cm_loadFileActionPerformed
        boolean verbose = cmCheck_verbose.isSelected();
        
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
        System.exit(0);
    }//GEN-LAST:event_exitForm

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new SVGPlayer().setVisible(true);
    }

    public void updateTime(double curTime, double timeStep, int playState)
    {
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBoxMenuItem CheckBoxMenuItem_anonInputStream;
    private javax.swing.JCheckBoxMenuItem cmCheck_verbose;
    private javax.swing.JMenuItem cm_800x600;
    private javax.swing.JMenuItem cm_about;
    private javax.swing.JMenuItem cm_loadFile;
    private javax.swing.JMenuItem cm_loadUrl;
    private javax.swing.JMenuItem cm_player;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JMenu menu_file;
    private javax.swing.JMenu menu_help;
    private javax.swing.JMenu menu_window;
    private javax.swing.JScrollPane scrollPane_svgArea;
    // End of variables declaration//GEN-END:variables

}
