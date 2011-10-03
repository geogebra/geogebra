package geogebra.kernel.discrete.signalprocesser.voronoi;
import geogebra.kernel.discrete.signalprocesser.shared.JCollapsiblePanel;
import geogebra.kernel.discrete.signalprocesser.shared.StatusDialog;
import geogebra.kernel.discrete.signalprocesser.voronoi.representation.AbstractRepresentation;
import geogebra.kernel.discrete.signalprocesser.voronoi.representation.RepresentationFactory;
import geogebra.kernel.discrete.signalprocesser.voronoi.representation.RepresentationInterface;
import geogebra.kernel.discrete.signalprocesser.voronoi.representation.boundaryproblem.BoundaryProblemRepresentation;
import geogebra.kernel.discrete.signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation;
import geogebra.kernel.discrete.signalprocesser.voronoi.shapegeneration.ShapeGeneration;
import geogebra.kernel.discrete.signalprocesser.voronoi.shapegeneration.ShapeGenerationException;
import geogebra.kernel.discrete.signalprocesser.voronoi.statusstructure.VLinkedNode;
import geogebra.kernel.discrete.signalprocesser.voronoi.statusstructure.binarysearchtreeimpl.debug.DebugTree;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class VoronoiTest extends javax.swing.JFrame {
    
    javax.swing.ButtonGroup groupMatt = new javax.swing.ButtonGroup();

	
	// Frame height/width
    public static final int FRAME_WIDTH  = 800;
    public static final int FRAME_HEIGHT =  400;
    
    // Margin around the generated shapes
    public static final int SHAPEMARGIN_TOPBOTTOM =  60;
    public static final int SHAPEMARGIN_LEFTRIGHT = 120;
    
    public static final int POINT_SIZE = 10;
    //public static final String SAVE_FILE = "voronoipoints.txt";
    
    public static DebugTree treedialog = null;
    
    private boolean SHOW_POINTS                     = false;
    private boolean SHOW_POINT_COORDINATES          = false;
    private boolean SHOW_MOUSE_LOCATION             = false;
    private boolean SHOW_INTERACTIVE_SWEEPLINE      = false;
    private boolean SHOW_CIRCLEEVENTS               = false;
    private boolean SHOW_EXPECTED_BORDER            = false;
    private boolean SHOW_INTERSECTION_WITH_EXPECTED = false;
    private boolean SHOW_ALPHA_SHAPE = false;
    
    private int backupboundaryenhancedvalue = -1;
    
    private double expectedarea = -1;
    
    private String lastdirectoryopened = null;
    //private CountryListModel countrylistmodel;
    private Shape alphashape; 
    
    private SignalPanel panel;
    private ArrayList<VPoint> points = new ArrayList<VPoint>();
    private ArrayList<VPoint> borderpoints = null;
    private TestRepresentationWrapper representationwrapper = new TestRepresentationWrapper();
    
    private AbstractRepresentation representation;
    
    public static void main(String args[]) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            displayError(null, e);
        }
        
        // Load the application
        VoronoiTest frame = new VoronoiTest();
        frame.setVisible(true);
    }
    
    /** Creates new form MainFrame */
    public VoronoiTest() {
        // Inital components
        initComponents();
        initMattComponents();
        
        // Sync the state with what is being visually shown
        sliderAreaCutOffToUseStateChanged(null);
        chkShowPointsActionPerformed(null);
        chkShowPointCoordinatesActionPerformed(null);
        chkShowCircleEventsActionPerformed(null);
        chkShowTreeStructureActionPerformed(null);
        chkShowMouseLocationActionPerformed(null);
        chkShowSweeplineActionPerformed(null);
        chkShowEdgeLengthsActionPerformed(null);
        chkShowInternalTrianglesActionPerformed(null);
        chkShowMinimumSpanningTreeActionPerformed(null);
        chkShowDebugInfoActionPerformed(null);
        chkMaxEdgesToRemoveActionPerformed(null);
        chkShowExpectedBorderActionPerformed(null);
        chkShowIntersectionActionPerformed(null);
        
        // Set the default generation to use
        optLetterGenerationActionPerformed(null);
        //optCountryGenerationActionPerformed(null);
        
        // Set the representation
        //optNone.setSelected(true);
        //optNoneActionPerformed(null);
        optEdgeRemoval.setSelected(true);
        optEdgeRemovalActionPerformed(null);
        //optClustering.setSelected(true);
        //optClusteringActionPerformed(null);
        
        // Load any saved points
        //try {
        //    loadPoints();
        //} catch ( IOException e ) {
        //    displayError(e);
       // }
        
        // Set countries to country list
        //try {
            //cboCountries.setModel( countrylistmodel = new CountryListModel(cboCountries,CountryData.getCountryList()) );
       // } catch ( IOException e ) {
        //    displayError(e);
       // }
        
        // Add panel
        panel = new SignalPanel();
        getContentPane().add(panel, java.awt.BorderLayout.CENTER);
        
        // Center the entire frame
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        super.setBounds(
                (screensize.width-FRAME_WIDTH)/2,
                (screensize.height-FRAME_HEIGHT)/2,
                FRAME_WIDTH, FRAME_HEIGHT);
    }
    
    public class SignalPanel extends JPanel implements MouseListener, MouseMotionListener {
        private int mouse_x = -1;
        private int mouse_y = -1;
        private int attentiontopoint = -1;
        private VPoint attentiontovpoint = null;
        private VPoint attentiontovpoint_onclick = null;
        
        public SignalPanel() {
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
        }
        
        public void mousePressed(MouseEvent e) {
            // If mouse button 1
            if ( e.getButton()==MouseEvent.BUTTON1 ) {
                if ( attentiontovpoint!=null ) {
                    attentiontovpoint_onclick = attentiontovpoint;
                } else {
                    
                    // Add to vector
                    if ( representation==null ) {
                        points.add( new VPoint(e.getX(), e.getY()) );
                    } else {
                        points.add( representation.createPoint(e.getX(), e.getY()) );
                    }
                    
                    // Save the points
                    //try {
                     //   savePoints();
                    //} catch ( IOException e2 ) {
                    //    displayError(e2);
                   // }
                    
                    // Determine mouse over
                    int mouseoverindex = getPointOverIndex(e.getX(), e.getY());
                    if ( mouseoverindex!=attentiontopoint ) {
                        attentiontopoint = mouseoverindex;
                    }
                    
                }
            } else if ( attentiontovpoint!=null ) { // If other mouse button
                points.remove(attentiontovpoint);
                attentiontopoint = -1;
                attentiontovpoint = null;
                attentiontovpoint_onclick = null;
                
                // Save the points
                //try {
                //    savePoints();
                //} catch ( IOException e2 ) {
                //    displayError(e2);
               // }
                
                // Determine mouse over
                int mouseoverindex = getPointOverIndex(e.getX(), e.getY());
                if ( mouseoverindex!=attentiontopoint ) {
                    attentiontopoint = mouseoverindex;
                }
                
            }

            // Repaint
            this.getParent().repaint();

            
            // Update controls (post repaint)
            updateControls();            
        }
        
        public void paintComponent(Graphics _g) {
            Graphics2D g = (Graphics2D) _g;
            
            // Get dimensions
            double width = this.getWidth();
            double height = this.getHeight();
            
            // Set background color
            g.setColor(Color.white);
            g.fillRect(0,0,(int)width,(int)height);
            
            // Draw mouse coord
            g.setColor(Color.black);
            if ( SHOW_MOUSE_LOCATION || SHOW_INTERACTIVE_SWEEPLINE ) {
                g.drawString("(" + mouse_x + ", " + mouse_y + ")", 5 , (int)height-20);
            }
            
            // Run algorithm
            // ( being very careful to catch errors and show them to the user )
            try {
                g.setColor( Color.red );
                representationwrapper.innerrepresentation = representation;
                if ( SHOW_INTERACTIVE_SWEEPLINE==false ) {
                    if ( points!=null ) {
                        VoronoiAlgorithm.generateVoronoi(representationwrapper, points);
                    }
                } else {
                    if ( attentiontovpoint!=null ) {
                        if ( points!=null ) {
                            VoronoiAlgorithm.generateVoronoi(representationwrapper, points, g, attentiontovpoint, mouse_y);
                        }
                    } else {
                        if ( points!=null ) {
                            VoronoiAlgorithm.generateVoronoi(representationwrapper, points, g, attentiontovpoint_onclick, mouse_y);
                        }
                    }
                }
            } catch ( Error e ) {
                points.clear();
                displayError(e); throw e;
            } catch ( RuntimeException e ) {
                points.clear();
                displayError(e); throw e;
            }
            
            // Show Intersection with expected border
            if ( SHOW_INTERSECTION_WITH_EXPECTED && borderpoints!=null && representation instanceof TriangulationRepresentation) {
                // Get the representation
                TriangulationRepresentation triangularrep = (TriangulationRepresentation) representation;
                
                // Check we're in edge removal mode
                if ( triangularrep.getMode()==TriangulationRepresentation.MODE_REDUCE_OUTER_BOUNDARIES ) {
                    // Create the initial area required
                    Area area = ShapeGeneration.createArea( borderpoints );
                    
                    // Calculate the exclusive or with second area
                    ArrayList outterpoints = triangularrep.getPointsFormingOutterBoundary();
                    area.exclusiveOr( ShapeGeneration.createArea( outterpoints ) );
                    
                    // Draw the resulting shape
                    g.setPaint(Color.yellow);
                    g.fill( ShapeGeneration.createShape(area) );
                    
                    // Set captions appropriately
                    try {
                        Shape shape = ShapeGeneration.createShape(area);
                        double l2norm = VoronoiShared.calculateAreaOfShape(shape);
                        double error = l2norm / expectedarea * 100;
                        txtL2Norm.setText( String.format("%.1f",l2norm) + " pixels^2");
                        txtErrorFromExpectedArea.setText( String.format("%.2f",error) + "%");
                    } catch ( Exception e ) {
                        e.printStackTrace();
                        txtL2Norm.setText( "Error" );
                        txtErrorFromExpectedArea.setText( "Error" );
                    }
                    try {
                        txtActualArea.setText( String.format("%.1f",VoronoiShared.calculateAreaOfShape(outterpoints)) + " pixels^2");
                    } catch ( Exception e ) {
                        e.printStackTrace();
                        txtActualArea.setText( "Error" );
                    }
                    try {
                        txtActualPerimeter.setText( String.format("%.1f",VoronoiShared.calculatePerimeterOfShape(outterpoints)) + " pixels");
                    } catch ( Exception e ) {
                        e.printStackTrace();
                        txtActualPerimeter.setText( "Error" );
                    }
                }
            }
            
            // Draw the expected border (if we even have one)
            if ( SHOW_EXPECTED_BORDER && borderpoints!=null ) {
                VPoint prev = null;
                Stroke originalstroke = g.getStroke();
                g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.setColor(Color.red);
                for ( VPoint point : borderpoints ) {
                    if ( prev==null ) {
                        prev = point;
                        continue;
                    }
                    
                    // Paint line
                    g.drawLine((int)prev.x, (int)prev.y, (int)point.x, (int)point.y);
                    
                    // Set the new previous point
                    prev = point;
                }
                g.setStroke(originalstroke);
            }
            
            // Drawing points
            if ( points!=null && SHOW_POINTS ) {
                g.setColor(Color.blue);
                for ( VPoint point : points ) {
                    g.fillOval((int)(point.x-POINT_SIZE/2), (int)(point.y-POINT_SIZE/2), POINT_SIZE, POINT_SIZE);
                    if ( SHOW_POINT_COORDINATES ) {
                        g.drawString("(" + point.x + "," + point.y + ")", (int)(point.x+POINT_SIZE/2+1), (int)(point.y));
                    }
                }
            }
            
            // Show circle events
            if ( SHOW_CIRCLEEVENTS ) {
                g.setColor(Color.red);
                for ( VPoint point : representationwrapper.circleevents ) {
                    g.fillOval((int)(point.x-POINT_SIZE/2), (int)(point.y-POINT_SIZE/2), POINT_SIZE, POINT_SIZE);
                    if ( SHOW_POINT_COORDINATES ) {
                        g.drawString("(" + point.x + "," + point.y + ")", (int)(point.x+POINT_SIZE/2+1), (int)(point.y));
                    }
                }
            }

            // Show alpha shapes
            if(SHOW_ALPHA_SHAPE){
                // Draw the resulting shape
            	
                g.setPaint(Color.green);
                g.draw(alphashape);
                g.setPaint(new Color(0,255,0,25));
                g.fill(alphashape);
            }
            
            // Paint the representation
            if ( representation!=null ) {
                g.setColor(Color.magenta);
                
                // Be very careful to catch errors and show them to the user
                try {
                    representation.paint(g);
                } catch ( Error e ) {
                    displayError(e); throw e;
                } catch ( RuntimeException e ) {
                    displayError(e); throw e;
                }
            }
        }
        
        public void mouseMoved(MouseEvent e) {
            if ( mouse_x==e.getX() && mouse_y==e.getY() ) return;
            int mouseoverindex = getPointOverIndex(mouse_x = e.getX(), mouse_y = e.getY());
            if ( mouseoverindex!=attentiontopoint ) {
                attentiontopoint = mouseoverindex;
            }
            
            if ( SHOW_MOUSE_LOCATION || SHOW_INTERACTIVE_SWEEPLINE ) {
                this.repaint();
            }
        }
        
        private int getPointOverIndex(int mouse_x, int mouse_y) {
            for ( int x=0 ; x<points.size() ; x++ ) {
                VPoint point = points.get(x);
                if ( mouse_x>=point.x-POINT_SIZE/2 && mouse_x<=point.x+POINT_SIZE/2
                        && mouse_y>=point.y-POINT_SIZE/2 && mouse_y<=point.y+POINT_SIZE/2 ) {
                    attentiontovpoint = point;
                    return x;
                }
            }
            attentiontovpoint = null;
            return -1;
        }
        
        // Ignored
        public void mouseDragged(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mouseClicked(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
    }
    
    private void savePoints() throws IOException {
        //savePoints(SAVE_FILE);
    }
    private void savePoints(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        for ( VPoint point : points ) {
            writer.write(point.x + "," + point.y + "\n");
        }
        writer.close();
    }
    private void exportPoints(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        for ( VPoint point : points ) {
            writer.write(point.x + " " + point.y + "\n");
        }
        writer.close();
    }
    
    private void loadPoints() throws IOException {
        //loadPoints(SAVE_FILE);
    }
    private void loadPoints(String filename) throws IOException {
        // Clear the points array
        points.clear();
        borderpoints = null;
        
        // Load the points
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            
            String line;
            while ( (line=reader.readLine())!=null ) {
                if ( line.trim().length()<=0 ) continue;
                
                // Split components of line
                String[] values;
                if ( line.indexOf(',')>0 ) {
                    values = line.split(",", 2);
                } else if ( line.indexOf(' ')>0 ) {
                    values = line.split(" ", 2);
                } else {
                    throw new IOException("Expected value line to be comma or space seperated - except found neither");
                }
                
                // Get values
                int x = Integer.parseInt(values[0]);
                int y = Integer.parseInt(values[1]);
                
                // Add to points array
                if ( representation==null ) {
                    points.add( new VPoint(x, y) );
                } else {
                    points.add( representation.createPoint(x, y) );
                }
            }
            reader.close();
        } catch ( FileNotFoundException e ) {
            // ignore this exception
        }
    }
    
    private void initMattComponents(){
    	JCollapsiblePanel mattPanel = new JCollapsiblePanel();
        mattPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Matt's custom controls"));
    	JButton hi = new JButton("Read in alpha shape");
    	mattPanel.add(hi);
        //panelTop.add(mattPanel);    	
        hi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hiActionPerformed(evt);
            }
        });
    }
    
    private void hiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optMaxEdgeOfSmallestTEdgeActionPerformed
    	
    	javax.swing.JFileChooser jfc = new javax.swing.JFileChooser();
    	//jfc.setCurrentDirectory(new File("d:/Research/Live/Nonconvex"));
    	jfc.showOpenDialog(this);
    	
    	String filename = jfc.getSelectedFile().getAbsolutePath();  	
    	// Read in alphashape
		int [] points = this.getAlphaShapeFromFile(filename + "-alf");
		//for(int i=0; i<points.length; i++){
		//	System.out.println(points[i]);
		//}
		
		// Read in point locations from file
		int no_coords = 0; 
    	try{
    		java.io.FileReader fr = new java.io.FileReader(filename);
    		BufferedReader br = new BufferedReader(fr);
    		while(br.readLine()!=null){
    			no_coords++;
    		}
    		br.close();
    		fr.close();
    	}catch(IOException e){e.printStackTrace();}
    	
    	// Store data about point locations
    	int [][] coords = new int[no_coords][2];     	
    	try{
    		java.io.FileReader fr = new java.io.FileReader(filename);
    		BufferedReader br = new BufferedReader(fr);
    		String line = br.readLine();
    		int count = 0; 
    		while(line!=null){
    			StringTokenizer st = new StringTokenizer(line);
    			try{
    				int x = Integer.parseInt(st.nextToken());
    				int y = Integer.parseInt(st.nextToken());
    				//System.out.println(x + ", " + y  + ", " + count);
    				coords[count][0] = x;
    				coords[count][1] = y;
    				count++; 
    			}catch(Exception e){System.out.println("Ignored");}
    			line = br.readLine();
    		}
    	}catch(Exception e){e.printStackTrace(System.out);}

    	// Construct geometry from alphashape
    	java.awt.geom.GeneralPath gp = new java.awt.geom.GeneralPath();
    	boolean move = true; 
    	for(int i=0; i<points.length; i++){
    		if(move){
    			gp.moveTo(coords[points[i]][0], coords[points[i]][1]);
    			move=false;
    		} else { 
    			
    			//System.out.println(i + ", " + points[i]);
    			//System.out.println(coords[points[i]][0]);
    			gp.lineTo(coords[Math.abs(points[i])][0], coords[Math.abs(points[i])][1]);
    			if(points[i]<0){
    				move=true;
    				gp.closePath();
    			}
    		}
    	}
		gp.closePath();
		alphashape = gp;    		
		SHOW_ALPHA_SHAPE = true;
		this.repaint();        
    }

    
   private int[] getAlphaShapeFromFile(String filename){

	   // Find number of lines in alf output from alpha shape code. 
	   // Note first line is always a comment
	   int count=0; 
	   try{
		   java.io.FileReader fr = new java.io.FileReader(filename);
		   BufferedReader br = new BufferedReader(fr);     		
		   String line = br.readLine();
		   line=br.readLine();
		   while(line!=null){
			   count++;
			   line=br.readLine();
		   }
		   br.close();
		   fr.close();
	   }catch(IOException e){e.printStackTrace();}
		
	   // Read in all data
	   System.out.println(count);
	   int [][] data = new int[count][2];
	   try{
		   java.io.FileReader fr = new java.io.FileReader(filename);
		   BufferedReader br = new BufferedReader(fr);     		
		   String line = br.readLine();
		   line=br.readLine(); // Ignore first comment line 
		   int lineno = 0;
		   while(line!=null){
			   StringTokenizer st = new StringTokenizer(line);
			   try{
				   data[lineno][0] = Integer.parseInt(st.nextToken());
				   data[lineno][1]= Integer.parseInt(st.nextToken());
				   
			   }catch(Exception e){e.printStackTrace(System.out);}
			   line=br.readLine();			
			   lineno++; 
		}
	   }catch(IOException e){e.printStackTrace(System.out);}
	   
	   // Process data into correct order
	   boolean more = true;
	   boolean change = true;
	   int lineno=0;
	   int lastnumber=-1;
	   int [] points = new int[count+1];
	   while(more){
	   while(change){
		   change=false;
		   for(int i=0; i<count; i++){
			   if(data[i][0]>=0){
			   //System.out.println(lineno);
			   // first two numbers to read in
			   if(lastnumber<0){
				   points[lineno]=data[i][0];
				   points[lineno+1]=data[i][1];
				   lastnumber=data[i][1];
				   data[i][0]=-1;
				   data[i][1]=-1;
				   lineno+=2;
				   change=true;
			   }
			   else{
				   if(data[i][0]==lastnumber){
					   points[lineno]=data[i][1];
					   lineno+=1; 
					   lastnumber = data[i][1];
					   data[i][0]=-1;
					   data[i][1]=-1;
					   change=true;
				   }
				   else if (data[i][1]==lastnumber){
					   points[lineno]=data[i][0];
					   lineno+=1; 
					   lastnumber = data[i][0];					   
					   data[i][0]=-1;
					   data[i][1]=-1;
					   change=true;
				   }
			   }
		   }
		   }
	   }
	   System.out.println("Lineno=" + lineno + ", points=" + points.length);
	   for(int i=0; i<points.length; i++){
		   System.out.println(points[i]);		   
	   }
	   if(lineno>=points.length-1)more=false; 
	   lastnumber=-1;
	   lineno=lineno-1; 
	   change=true;
	   // Place negative number at end of segment
	   points[lineno-1]=-points[lineno-1];
	   }
	   return points; 
   }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        groupRepresentations = new javax.swing.ButtonGroup();
        groupEdgeRemoval = new javax.swing.ButtonGroup();
        optBoundary = new javax.swing.JRadioButton();
        optBoundaryUsingAngle20 = new javax.swing.JRadioButton();
        optBoundaryUsingAngle30 = new javax.swing.JRadioButton();
        optBoundaryEnhanced = new javax.swing.JRadioButton();
        panelBoundaryEnhanced = new javax.swing.JPanel();
        sliderAreaCutOffToUse = new javax.swing.JSlider();
        groupGenerationType = new javax.swing.ButtonGroup();
        scrollRight = new javax.swing.JScrollPane();
        panelRight = new javax.swing.JPanel();
        panelTop = new javax.swing.JPanel();
        panelActions = new JPanel();
        panelActionsInner = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        JButton btnExport = new javax.swing.JButton();
        btnLoad = new javax.swing.JButton();
        btnExportToSVG = new javax.swing.JButton();
        btnTestSuiteForm = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        panelStatistics = new JPanel();
        panelStatCaptions = new javax.swing.JPanel();
        lblL2Norm = new javax.swing.JLabel();
        lblErrorFromExpectedArea = new javax.swing.JLabel();
        lblExpectedArea = new javax.swing.JLabel();
        lblActualArea = new javax.swing.JLabel();
        lblExpectedPerimeter = new javax.swing.JLabel();
        lblActualPerimeter = new javax.swing.JLabel();
        panelStatLabels = new javax.swing.JPanel();
        txtL2Norm = new javax.swing.JLabel();
        txtErrorFromExpectedArea = new javax.swing.JLabel();
        txtExpectedArea = new javax.swing.JLabel();
        txtActualArea = new javax.swing.JLabel();
        txtExpectedPerimeter = new javax.swing.JLabel();
        txtActualPerimeter = new javax.swing.JLabel();
        panelPoints = new JPanel();
        panelPointsInner = new javax.swing.JPanel();
        panelGenerate = new javax.swing.JPanel();
        panelGenerationSelection = new javax.swing.JPanel();
        txtLetter = new javax.swing.JTextField();
        cboCountries = new javax.swing.JComboBox();
        btnGenerate = new javax.swing.JButton();
        panelGap1 = new javax.swing.JPanel();
        panelPointOptions = new javax.swing.JPanel();
        panelLeft = new javax.swing.JPanel();
        lblGenerationType = new javax.swing.JLabel();
        lblFont = new javax.swing.JLabel();
        lblShapePoints = new javax.swing.JLabel();
        lblInternalPoints = new javax.swing.JLabel();
        lblShapePointMinDensity = new javax.swing.JLabel();
        lblInternalMinDensity = new javax.swing.JLabel();
        panelCenter = new javax.swing.JPanel();
        panelGenerationType = new javax.swing.JPanel();
        optLetterGeneration = new javax.swing.JRadioButton();
        optCountryGeneration = new javax.swing.JRadioButton();
        cboFont = new javax.swing.JComboBox();
        cboShapePoints = new javax.swing.JComboBox();
        cboInternalPoints = new javax.swing.JComboBox();
        cboShapePointMinDensity = new javax.swing.JComboBox();
        cboInternalMinDensity = new javax.swing.JComboBox();
        panelGap = new javax.swing.JPanel();
        panelClearPoints = new javax.swing.JPanel();
        btnClearPoints = new javax.swing.JButton();
        panelCheckBoxes = new javax.swing.JPanel();
        chkShowPoints = new javax.swing.JCheckBox();
        chkShowExpectedBorder = new javax.swing.JCheckBox();
        chkShowIntersection = new javax.swing.JCheckBox();
        chkAddShapePointsToSplitLongLines = new javax.swing.JCheckBox();
        panelRepresentations = new JPanel();
        optNone = new javax.swing.JRadioButton();
        optVoronoiCells = new javax.swing.JRadioButton();
        optSimpleTriangulation = new javax.swing.JRadioButton();
        optEdgeRemoval = new javax.swing.JRadioButton();
        optClustering = new javax.swing.JRadioButton();
        panelEdgeRemoval = new JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        optNoLengthRestriction = new javax.swing.JRadioButton();
        optUserLengthRestriction = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        panelGapWest = new javax.swing.JPanel();
        sliderLengthRestriction = new javax.swing.JSlider();
        panelGapSouth = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        optNormalisedLengthRestriction = new javax.swing.JRadioButton();
        panelGapWest2 = new javax.swing.JPanel();
        sliderNormalisedLengthRestriction = new javax.swing.JSlider();
        panelGapSouth1 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        optMaxEdgeOfMinSpanningTree = new javax.swing.JRadioButton();
        optMaxEdgeOfSmallestTEdge = new javax.swing.JRadioButton();
        optApplyAboveMSTAndSmallestTEdgeInProportion = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        panelGapWest1 = new javax.swing.JPanel();
        sliderApplyInProportion = new javax.swing.JSlider();
        panelEdgeRemovalOptions = new JPanel();
        jPanel8 = new javax.swing.JPanel();
        chkShowEdgeLengths = new javax.swing.JCheckBox();
        chkShowInternalTriangles = new javax.swing.JCheckBox();
        chkShowMinimumSpanningTree = new javax.swing.JCheckBox();
        chkShowDebugInfo = new javax.swing.JCheckBox();
        chkMaxEdgesToRemove = new javax.swing.JCheckBox();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        sliderMaxEdgesToRemove = new javax.swing.JSlider();
        panelOptions = new JPanel();
        chkShowMouseLocation = new javax.swing.JCheckBox();
        chkShowPointCoordinates = new javax.swing.JCheckBox();
        chkShowSweepline = new javax.swing.JCheckBox();
        chkShowTreeStructure = new javax.swing.JCheckBox();
        chkShowCircleEvents = new javax.swing.JCheckBox();

        groupRepresentations.add(optBoundary);
        optBoundary.setText("Boundary");
        optBoundary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optBoundaryActionPerformed(evt);
            }
        });

        groupRepresentations.add(optBoundaryUsingAngle20);
        optBoundaryUsingAngle20.setText("Boundary (20\u02da)");
        optBoundaryUsingAngle20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optBoundaryUsingAngle20ActionPerformed(evt);
            }
        });

        groupRepresentations.add(optBoundaryUsingAngle30);
        optBoundaryUsingAngle30.setText("Boundary (30\u02da)");
        optBoundaryUsingAngle30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optBoundaryUsingAngle30ActionPerformed(evt);
            }
        });

        groupRepresentations.add(optBoundaryEnhanced);
        optBoundaryEnhanced.setText("Boundary Enhanced");
        optBoundaryEnhanced.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optBoundaryEnhancedActionPerformed(evt);
            }
        });

        panelBoundaryEnhanced.setLayout(new java.awt.GridLayout(0, 1));

        panelBoundaryEnhanced.setBorder(javax.swing.BorderFactory.createTitledBorder("Boundary Enhanced"));
        sliderAreaCutOffToUse.setMajorTickSpacing(8000);
        sliderAreaCutOffToUse.setMaximum(25000);
        sliderAreaCutOffToUse.setPaintLabels(true);
        sliderAreaCutOffToUse.setPaintTicks(true);
        sliderAreaCutOffToUse.setValue(8000);
        sliderAreaCutOffToUse.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderAreaCutOffToUseStateChanged(evt);
            }
        });

        panelBoundaryEnhanced.add(sliderAreaCutOffToUse);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Non-convex Hull Test Program");
        scrollRight.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollRight.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelRight.setLayout(new java.awt.BorderLayout());

        panelRight.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panelTop.setLayout(new javax.swing.BoxLayout(panelTop, javax.swing.BoxLayout.Y_AXIS));

        panelActions.setLayout(new java.awt.BorderLayout());

        panelActions.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));
        panelActionsInner.setLayout(new java.awt.GridLayout(0, 1, 0, 4));

        jPanel6.setLayout(new java.awt.GridLayout(1, 0, 2, 0));

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        jPanel6.add(btnSave);

        btnExport.setText("Export");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        jPanel6.add(btnExport);

        btnLoad.setText("Load");
        btnLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadActionPerformed(evt);
            }
        });

        jPanel6.add(btnLoad);

        panelActionsInner.add(jPanel6);

        btnExportToSVG.setText("Export to SVG");
        btnExportToSVG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportToSVGActionPerformed(evt);
            }
        });

        panelActionsInner.add(btnExportToSVG);

        btnTestSuiteForm.setText("Test Suite Dialog");
        btnTestSuiteForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTestSuiteFormActionPerformed(evt);
            }
        });

        //panelActionsInner.add(btnTestSuiteForm);

        btnExit.setText("Exit");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        panelActionsInner.add(btnExit);

        panelActions.add(panelActionsInner, java.awt.BorderLayout.CENTER);

        panelStatistics.setLayout(new java.awt.BorderLayout(6, 0));

        panelStatistics.setBorder(javax.swing.BorderFactory.createTitledBorder("Statistics"));
        panelStatCaptions.setLayout(new java.awt.GridLayout(0, 1, 0, 4));

        lblL2Norm.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblL2Norm.setText("L2-Norm:");
        panelStatCaptions.add(lblL2Norm);

        lblErrorFromExpectedArea.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblErrorFromExpectedArea.setText("Error From Area:");
        panelStatCaptions.add(lblErrorFromExpectedArea);

        lblExpectedArea.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblExpectedArea.setText("Expected Area:");
        panelStatCaptions.add(lblExpectedArea);

        lblActualArea.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblActualArea.setText("Actual Area:");
        panelStatCaptions.add(lblActualArea);

        lblExpectedPerimeter.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblExpectedPerimeter.setText("Expected Perimeter:");
        panelStatCaptions.add(lblExpectedPerimeter);

        lblActualPerimeter.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblActualPerimeter.setText("Actual Perimeter:");
        panelStatCaptions.add(lblActualPerimeter);

        panelStatistics.add(panelStatCaptions, java.awt.BorderLayout.WEST);

        panelStatLabels.setLayout(new java.awt.GridLayout(0, 1, 0, 4));

        txtL2Norm.setText("n/a");
        panelStatLabels.add(txtL2Norm);

        txtErrorFromExpectedArea.setText("n/a");
        panelStatLabels.add(txtErrorFromExpectedArea);

        txtExpectedArea.setText("n/a");
        panelStatLabels.add(txtExpectedArea);

        txtActualArea.setText("n/a");
        panelStatLabels.add(txtActualArea);

        txtExpectedPerimeter.setText("n/a");
        panelStatLabels.add(txtExpectedPerimeter);

        txtActualPerimeter.setText("n/a");
        panelStatLabels.add(txtActualPerimeter);

        panelStatistics.add(panelStatLabels, java.awt.BorderLayout.CENTER);



        panelPoints.setLayout(new java.awt.BorderLayout());

        panelPoints.setBorder(javax.swing.BorderFactory.createTitledBorder("Points / Shape Generation"));
        panelPointsInner.setLayout(new javax.swing.BoxLayout(panelPointsInner, javax.swing.BoxLayout.Y_AXIS));

        panelGenerate.setLayout(new java.awt.BorderLayout(3, 0));

        panelGenerationSelection.setLayout(new java.awt.GridLayout(0, 1));

        txtLetter.setFont(new java.awt.Font("Tahoma", 1, 12));
        txtLetter.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtLetter.setText("S");
        txtLetter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtLetterKeyTyped(evt);
            }
        });

        panelGenerationSelection.add(txtLetter);

        cboCountries.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCountriesActionPerformed(evt);
            }
        });

        panelGenerationSelection.add(cboCountries);

        panelGenerate.add(panelGenerationSelection, java.awt.BorderLayout.CENTER);

        btnGenerate.setText("Generate");
        btnGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateActionPerformed(evt);
            }
        });

        panelGenerate.add(btnGenerate, java.awt.BorderLayout.EAST);

        panelPointsInner.add(panelGenerate);

        panelGap1.setLayout(null);

        panelGap1.setPreferredSize(new java.awt.Dimension(4, 4));
        panelPointsInner.add(panelGap1);

        panelPointOptions.setLayout(new java.awt.BorderLayout(2, 0));

        panelLeft.setLayout(new java.awt.GridLayout(0, 1, 0, 2));

        lblGenerationType.setText("Generation Type:");
        panelLeft.add(lblGenerationType);

        lblFont.setText("Font:");
        panelLeft.add(lblFont);

        lblShapePoints.setText("Shape Points:");
        panelLeft.add(lblShapePoints);

        lblInternalPoints.setText("Internal Points:");
        panelLeft.add(lblInternalPoints);

        lblShapePointMinDensity.setText("Shape Point Min Density:");
        panelLeft.add(lblShapePointMinDensity);

        lblInternalMinDensity.setText("Internal Min Density:");
        panelLeft.add(lblInternalMinDensity);

        panelPointOptions.add(panelLeft, java.awt.BorderLayout.WEST);

        panelCenter.setLayout(new java.awt.GridLayout(0, 1, 0, 2));

        panelGenerationType.setLayout(new java.awt.BorderLayout());

        groupGenerationType.add(optLetterGeneration);
        optLetterGeneration.setSelected(true);
        optLetterGeneration.setText("Letter");
        optLetterGeneration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optLetterGenerationActionPerformed(evt);
            }
        });

        panelGenerationType.add(optLetterGeneration, java.awt.BorderLayout.WEST);

        groupGenerationType.add(optCountryGeneration);
        optCountryGeneration.setText("Country");
        optCountryGeneration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optCountryGenerationActionPerformed(evt);
            }
        });

        panelGenerationType.add(optCountryGeneration, java.awt.BorderLayout.CENTER);

        panelCenter.add(panelGenerationType);

        cboFont.setEditable(true);
        cboFont.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Arial", "Courier New", "Garamond", "Times New Roman", "Lucida Console" }));
        cboFont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboFontActionPerformed(evt);
            }
        });

        panelCenter.add(cboFont);

        cboShapePoints.setEditable(true);
        cboShapePoints.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No Points", "10", "25", "50", "100", "250", "Maximum Possible" }));
        cboShapePoints.setSelectedIndex(6);
        cboShapePoints.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboShapePointsActionPerformed(evt);
            }
        });

        panelCenter.add(cboShapePoints);

        cboInternalPoints.setEditable(true);
        cboInternalPoints.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No Points", "10", "25", "50", "100", "250", "Maximum Possible" }));
        cboInternalPoints.setSelectedIndex(6);
        cboInternalPoints.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboInternalPointsActionPerformed(evt);
            }
        });

        panelCenter.add(cboInternalPoints);

        cboShapePointMinDensity.setEditable(true);
        cboShapePointMinDensity.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "8", "10", "12", "15", "20", "25", "30", "40", "50", "100", "250" }));
        cboShapePointMinDensity.setSelectedIndex(9);
        cboShapePointMinDensity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboShapePointMinDensityActionPerformed(evt);
            }
        });

        panelCenter.add(cboShapePointMinDensity);

        cboInternalMinDensity.setEditable(true);
        cboInternalMinDensity.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "8", "10", "12", "15", "20", "25", "30", "40", "50", "100", "250" }));
        cboInternalMinDensity.setSelectedIndex(9);
        cboInternalMinDensity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboInternalMinDensityActionPerformed(evt);
            }
        });

        panelCenter.add(cboInternalMinDensity);

        panelPointOptions.add(panelCenter, java.awt.BorderLayout.CENTER);

        panelPointsInner.add(panelPointOptions);

        panelGap.setLayout(null);

        panelGap.setPreferredSize(new java.awt.Dimension(4, 4));
        panelPointsInner.add(panelGap);

        panelClearPoints.setLayout(new java.awt.BorderLayout());

        btnClearPoints.setText("Clear Points");
        btnClearPoints.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearPointsActionPerformed(evt);
            }
        });

        panelClearPoints.add(btnClearPoints, java.awt.BorderLayout.SOUTH);

        panelPointsInner.add(panelClearPoints);

        panelCheckBoxes.setLayout(new java.awt.GridLayout(0, 1));

        chkShowPoints.setSelected(true);
        chkShowPoints.setText("Show Points");
        chkShowPoints.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowPointsActionPerformed(evt);
            }
        });

        panelCheckBoxes.add(chkShowPoints);

        chkShowExpectedBorder.setSelected(true);
        chkShowExpectedBorder.setText("Show Expected Border");
        chkShowExpectedBorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowExpectedBorderActionPerformed(evt);
            }
        });

        panelCheckBoxes.add(chkShowExpectedBorder);

        chkShowIntersection.setSelected(true);
        chkShowIntersection.setText("Show Intersection (L2 Norm)");
        chkShowIntersection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowIntersectionActionPerformed(evt);
            }
        });

        panelCheckBoxes.add(chkShowIntersection);

        chkAddShapePointsToSplitLongLines.setSelected(true);
        chkAddShapePointsToSplitLongLines.setText("Add Shape Points to Split Long Lines");
        chkAddShapePointsToSplitLongLines.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAddShapePointsToSplitLongLinesActionPerformed(evt);
            }
        });

        panelCheckBoxes.add(chkAddShapePointsToSplitLongLines);

        panelPointsInner.add(panelCheckBoxes);

        panelPoints.add(panelPointsInner, java.awt.BorderLayout.CENTER);


        panelRepresentations.setLayout(new java.awt.GridLayout(0, 1));

        panelRepresentations.setBorder(javax.swing.BorderFactory.createTitledBorder("Representations"));
        groupRepresentations.add(optNone);
        optNone.setSelected(true);
        optNone.setText("None");
        optNone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optNoneActionPerformed(evt);
            }
        });

        panelRepresentations.add(optNone);

        groupRepresentations.add(optVoronoiCells);
        optVoronoiCells.setText("Voronoi");
        optVoronoiCells.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optVoronoiCellsActionPerformed(evt);
            }
        });

        panelRepresentations.add(optVoronoiCells);

        groupRepresentations.add(optSimpleTriangulation);
        optSimpleTriangulation.setText("Triangulation");
        optSimpleTriangulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optSimpleTriangulationActionPerformed(evt);
            }
        });

        panelRepresentations.add(optSimpleTriangulation);

        groupRepresentations.add(optEdgeRemoval);
        optEdgeRemoval.setText("Edge Removal");
        optEdgeRemoval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optEdgeRemovalActionPerformed(evt);
            }
        });

        panelRepresentations.add(optEdgeRemoval);

        groupRepresentations.add(optClustering);
        optClustering.setText("Clustering");
        optClustering.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optClusteringActionPerformed(evt);
            }
        });

        panelRepresentations.add(optClustering);


        panelEdgeRemoval.setLayout(new java.awt.BorderLayout());

        panelEdgeRemoval.setBorder(javax.swing.BorderFactory.createTitledBorder("Edge Removal"));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        groupEdgeRemoval.add(optNoLengthRestriction);
        //optNoLengthRestriction.setSelected(true);
        optNoLengthRestriction.setText("No Length Cut-off");
        optNoLengthRestriction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optNoLengthRestrictionActionPerformed(evt);
            }
        });

        //jPanel1.add(optNoLengthRestriction);

        groupEdgeRemoval.add(optUserLengthRestriction);
        optUserLengthRestriction.setText("User Length Cut-off");
        optUserLengthRestriction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optUserLengthRestrictionActionPerformed(evt);
            }
        });

        //jPanel1.add(optUserLengthRestriction);

        jPanel4.add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        panelGapWest.setLayout(null);

        panelGapWest.setPreferredSize(new java.awt.Dimension(16, 5));
        jPanel2.add(panelGapWest, java.awt.BorderLayout.WEST);

        sliderLengthRestriction.setMajorTickSpacing(100);
        sliderLengthRestriction.setPaintLabels(true);
        sliderLengthRestriction.setPaintTicks(true);
        sliderLengthRestriction.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderLengthRestrictionStateChanged(evt);
            }
        });

        //jPanel2.add(sliderLengthRestriction, java.awt.BorderLayout.CENTER);

        panelGapSouth.setLayout(null);

        panelGapSouth.setPreferredSize(new java.awt.Dimension(2, 2));
        jPanel2.add(panelGapSouth, java.awt.BorderLayout.SOUTH);

        jPanel4.add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.BorderLayout());

        groupEdgeRemoval.add(optNormalisedLengthRestriction);
        optNormalisedLengthRestriction.setText("Normalised Length");
        optNormalisedLengthRestriction.setSelected(true);
        optNormalisedLengthRestriction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optNormalisedLengthRestrictionActionPerformed(evt);
            }
        });

        jPanel3.add(optNormalisedLengthRestriction, java.awt.BorderLayout.NORTH);

        panelGapWest2.setLayout(null);

        panelGapWest2.setPreferredSize(new java.awt.Dimension(16, 5));
        jPanel3.add(panelGapWest2, java.awt.BorderLayout.WEST);

        sliderNormalisedLengthRestriction.setMajorTickSpacing(25);
        sliderNormalisedLengthRestriction.setMinorTickSpacing(5);
        sliderNormalisedLengthRestriction.setPaintLabels(true);
        sliderNormalisedLengthRestriction.setPaintTicks(true);
        sliderNormalisedLengthRestriction.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderNormalisedLengthRestrictionStateChanged(evt);
            }
        });

        jPanel3.add(sliderNormalisedLengthRestriction, java.awt.BorderLayout.CENTER);

        panelGapSouth1.setLayout(null);

        panelGapSouth1.setPreferredSize(new java.awt.Dimension(2, 2));
        jPanel3.add(panelGapSouth1, java.awt.BorderLayout.SOUTH);

        jPanel4.add(jPanel3, java.awt.BorderLayout.SOUTH);

        panelEdgeRemoval.add(jPanel4, java.awt.BorderLayout.NORTH);

        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.Y_AXIS));

        groupEdgeRemoval.add(optMaxEdgeOfMinSpanningTree);
        optMaxEdgeOfMinSpanningTree.setText("Max Edge of Minimum Spanning Tree");
        optMaxEdgeOfMinSpanningTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optMaxEdgeOfMinSpanningTreeActionPerformed(evt);
            }
        });

        jPanel7.add(optMaxEdgeOfMinSpanningTree);

        groupEdgeRemoval.add(optMaxEdgeOfSmallestTEdge);
        optMaxEdgeOfSmallestTEdge.setText("Max of Smallest Triangle Edge");
        optMaxEdgeOfSmallestTEdge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optMaxEdgeOfSmallestTEdgeActionPerformed(evt);
            }
        });

        jPanel7.add(optMaxEdgeOfSmallestTEdge);

        //groupEdgeRemoval.add(optApplyAboveMSTAndSmallestTEdgeInProportion);
        optApplyAboveMSTAndSmallestTEdgeInProportion.setText("Apply above two in following proportion");
        optApplyAboveMSTAndSmallestTEdgeInProportion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optApplyAboveMSTAndSmallestTEdgeInProportionActionPerformed(evt);
            }
        });

        //jPanel7.add(optApplyAboveMSTAndSmallestTEdgeInProportion);

        panelEdgeRemoval.add(jPanel7, java.awt.BorderLayout.CENTER);

        jPanel5.setLayout(new java.awt.BorderLayout());

        panelGapWest1.setLayout(null);

        panelGapWest1.setPreferredSize(new java.awt.Dimension(16, 5));
        jPanel5.add(panelGapWest1, java.awt.BorderLayout.WEST);

        sliderApplyInProportion.setMajorTickSpacing(10);
        sliderApplyInProportion.setMinorTickSpacing(1);
        sliderApplyInProportion.setPaintLabels(true);
        sliderApplyInProportion.setPaintTicks(true);
        sliderApplyInProportion.setValue(15);
        sliderApplyInProportion.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderApplyInProportionStateChanged(evt);
            }
        });

        //jPanel5.add(sliderApplyInProportion, java.awt.BorderLayout.CENTER);

        panelEdgeRemoval.add(jPanel5, java.awt.BorderLayout.SOUTH);


        panelEdgeRemovalOptions.setLayout(new javax.swing.BoxLayout(panelEdgeRemovalOptions, javax.swing.BoxLayout.Y_AXIS));

        panelEdgeRemovalOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Edge Removal Options"));
        jPanel8.setLayout(new java.awt.GridLayout(0, 1));

        chkShowEdgeLengths.setText("Show Edge Lengths");
        chkShowEdgeLengths.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowEdgeLengthsActionPerformed(evt);
            }
        });

        jPanel8.add(chkShowEdgeLengths);

        chkShowInternalTriangles.setText("Show Internal Triangles");
        chkShowInternalTriangles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowInternalTrianglesActionPerformed(evt);
            }
        });

        jPanel8.add(chkShowInternalTriangles);

        chkShowMinimumSpanningTree.setText("Show Minimum Spanning Tree");
        chkShowMinimumSpanningTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowMinimumSpanningTreeActionPerformed(evt);
            }
        });

        jPanel8.add(chkShowMinimumSpanningTree);

        chkShowDebugInfo.setText("Show Debug Information");
        chkShowDebugInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowDebugInfoActionPerformed(evt);
            }
        });

        jPanel8.add(chkShowDebugInfo);

        chkMaxEdgesToRemove.setText("Max Edges to Remove");
        chkMaxEdgesToRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMaxEdgesToRemoveActionPerformed(evt);
            }
        });

        jPanel8.add(chkMaxEdgesToRemove);

        panelEdgeRemovalOptions.add(jPanel8);

        jPanel9.setLayout(new java.awt.BorderLayout());

        jPanel10.setLayout(null);

        jPanel10.setPreferredSize(new java.awt.Dimension(16, 5));
        jPanel9.add(jPanel10, java.awt.BorderLayout.WEST);

        sliderMaxEdgesToRemove.setMajorTickSpacing(100);
        sliderMaxEdgesToRemove.setMaximum(500);
        sliderMaxEdgesToRemove.setMinorTickSpacing(1);
        sliderMaxEdgesToRemove.setPaintLabels(true);
        sliderMaxEdgesToRemove.setPaintTicks(true);
        sliderMaxEdgesToRemove.setValue(0);
        sliderMaxEdgesToRemove.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderMaxEdgesToRemoveStateChanged(evt);
            }
        });

        jPanel9.add(sliderMaxEdgesToRemove, java.awt.BorderLayout.CENTER);

        panelEdgeRemovalOptions.add(jPanel9);

        panelOptions.setLayout(new java.awt.GridLayout(0, 1));

        panelOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Debug Options"));
        chkShowMouseLocation.setText("Show Mouse Location");
        chkShowMouseLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowMouseLocationActionPerformed(evt);
            }
        });

        panelOptions.add(chkShowMouseLocation);

        chkShowPointCoordinates.setText("Show Coordinates");
        chkShowPointCoordinates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowPointCoordinatesActionPerformed(evt);
            }
        });

        panelOptions.add(chkShowPointCoordinates);

        chkShowSweepline.setText("Show Sweepline");
        chkShowSweepline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowSweeplineActionPerformed(evt);
            }
        });

        panelOptions.add(chkShowSweepline);

        chkShowTreeStructure.setText("Show Tree Structure");
        chkShowTreeStructure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowTreeStructureActionPerformed(evt);
            }
        });

        panelOptions.add(chkShowTreeStructure);

        chkShowCircleEvents.setText("Show Circle Events");
        chkShowCircleEvents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowCircleEventsActionPerformed(evt);
            }
        });

        panelOptions.add(chkShowCircleEvents);

        //((JCollapsiblePanel)panelStatistics).setCollapsed(true);
        //((JCollapsiblePanel)panelPoints).setCollapsed(true);
        //((JCollapsiblePanel)panelOptions).setCollapsed(true);
        //((JCollapsiblePanel)panelRepresentations).setCollapsed(true);
        //((JCollapsiblePanel)panelEdgeRemovalOptions).setCollapsed(true);
        //((JCollapsiblePanel)panelActions).setCollapsed(true);

        panelTop.add(panelEdgeRemoval);
        panelTop.add(panelStatistics);
        panelTop.add(panelPoints);
        panelTop.add(panelOptions);
        panelTop.add(panelRepresentations);
        panelTop.add(panelEdgeRemovalOptions);
        //panelTop.add(panelActions);

        panelRight.add(panelTop, java.awt.BorderLayout.NORTH);

        scrollRight.setViewportView(panelRight);

        getContentPane().add(scrollRight, java.awt.BorderLayout.EAST);

    }// </editor-fold>//GEN-END:initComponents
    
    private void chkShowMouseLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowMouseLocationActionPerformed
        SHOW_MOUSE_LOCATION = chkShowMouseLocation.isSelected();
        this.repaint();
    }//GEN-LAST:event_chkShowMouseLocationActionPerformed
    
    private void chkShowPointsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowPointsActionPerformed
        SHOW_POINTS = chkShowPoints.isSelected();
        this.repaint();
    }//GEN-LAST:event_chkShowPointsActionPerformed
    
    private void optNormalisedLengthRestrictionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optNormalisedLengthRestrictionActionPerformed
        updateControls();
    }//GEN-LAST:event_optNormalisedLengthRestrictionActionPerformed
    
    private void sliderNormalisedLengthRestrictionStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderNormalisedLengthRestrictionStateChanged
        // No need to updateControls() - just repaint
        this.repaint();
    }//GEN-LAST:event_sliderNormalisedLengthRestrictionStateChanged
    
    private void btnExportToSVGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportToSVGActionPerformed
    }//GEN-LAST:event_btnExportToSVGActionPerformed
    
    private void cboInternalMinDensityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboInternalMinDensityActionPerformed
        btnGenerate.doClick();
    }//GEN-LAST:event_cboInternalMinDensityActionPerformed
    
    private void cboShapePointsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboShapePointsActionPerformed
        btnGenerate.doClick();
    }//GEN-LAST:event_cboShapePointsActionPerformed
    
    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnExitActionPerformed
    
    private void chkShowIntersectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowIntersectionActionPerformed
        SHOW_INTERSECTION_WITH_EXPECTED = chkShowIntersection.isSelected();
        this.repaint();
    }//GEN-LAST:event_chkShowIntersectionActionPerformed
    
    private void chkAddShapePointsToSplitLongLinesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAddShapePointsToSplitLongLinesActionPerformed
        btnGenerate.doClick();
    }//GEN-LAST:event_chkAddShapePointsToSplitLongLinesActionPerformed
    
    private void cboCountriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCountriesActionPerformed
        btnGenerate.doClick();
    }//GEN-LAST:event_cboCountriesActionPerformed
    
    private void optCountryGenerationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optCountryGenerationActionPerformed
        // By default, don't split long lines for countries
        chkAddShapePointsToSplitLongLines.setSelected(false);
        
        // Setup panel combo box
        panelGenerationSelection.removeAll();
        panelGenerationSelection.add( cboCountries );
        panelGenerationSelection.validate();
        panelGenerationSelection.repaint();
    }//GEN-LAST:event_optCountryGenerationActionPerformed
    
    private void optLetterGenerationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optLetterGenerationActionPerformed
        // By default, split long lines for letters
        chkAddShapePointsToSplitLongLines.setSelected(true);
        
        // Setup letter text field
        panelGenerationSelection.removeAll();
        panelGenerationSelection.add( txtLetter );
        panelGenerationSelection.validate();
        panelGenerationSelection.repaint();
    }//GEN-LAST:event_optLetterGenerationActionPerformed
    
    private void btnLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setDialogTitle("Choose Points File to Load");
        String directory = ( lastdirectoryopened!=null ? lastdirectoryopened : chooser.getCurrentDirectory().getAbsolutePath() );
        chooser.setSelectedFile( new File( directory + File.separator + "pointsfile.txt" ) );
        
        int returnval = chooser.showOpenDialog(this);
        if( returnval==JFileChooser.APPROVE_OPTION ) {
            File file = chooser.getSelectedFile();
            
            // Set last directory
            lastdirectoryopened = file.getPath();
            
            // Load the points file
            boolean loadsuccessful;
            try {
                loadPoints(file.getAbsolutePath());
                loadsuccessful = true;
            } catch ( IOException e ) {
                displayError(e);
                loadsuccessful = false;
            }
            
            // Refresh the screen
            panel.repaint();
            
            // Save the points file back to our default save file
            if ( loadsuccessful ) {
                try {
                    savePoints();
                } catch ( IOException e ) {
                    displayError(e);
                }
            }
        }
    }//GEN-LAST:event_btnLoadActionPerformed
    
    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setDialogTitle("Choose location to Save Points File");
        String directory = ( lastdirectoryopened!=null ? lastdirectoryopened : chooser.getCurrentDirectory().getAbsolutePath() );
        chooser.setSelectedFile( new File( directory + File.separator + "pointsfile.txt" ) );
        
        int returnval = chooser.showSaveDialog(this);
        if( returnval==JFileChooser.APPROVE_OPTION ) {
            File file = chooser.getSelectedFile();
            
            // Set last directory
            lastdirectoryopened = file.getPath();
            
            // Write the file
            try {
                savePoints(file.getAbsolutePath());
            } catch ( IOException e ) {
                displayError(e);
            }
        }
    }//GEN-LAST:event_btnSaveActionPerformed
    
    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setDialogTitle("Choose location to Save Points File");
        String directory = ( lastdirectoryopened!=null ? lastdirectoryopened : chooser.getCurrentDirectory().getAbsolutePath() );
        chooser.setSelectedFile( new File( directory + File.separator + "pointsfile.txt" ) );
        
        int returnval = chooser.showSaveDialog(this);
        if( returnval==JFileChooser.APPROVE_OPTION ) {
            File file = chooser.getSelectedFile();
            
            // Set last directory
            lastdirectoryopened = file.getPath();
            
            // Write the file
            try {
                exportPoints(file.getAbsolutePath());
            } catch ( IOException e ) {
                displayError(e);
            }
        }
    }//GEN-LAST:event_btnSaveActionPerformed
    
    private void cboFontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboFontActionPerformed
        if ( optLetterGeneration.isSelected() ) {
            btnGenerate.doClick();
        }
    }//GEN-LAST:event_cboFontActionPerformed
    
    private void chkShowExpectedBorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowExpectedBorderActionPerformed
        SHOW_EXPECTED_BORDER = chkShowExpectedBorder.isSelected();
        this.repaint();
    }//GEN-LAST:event_chkShowExpectedBorderActionPerformed
    
    private void cboShapePointMinDensityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboShapePointMinDensityActionPerformed
        btnGenerate.doClick();
    }//GEN-LAST:event_cboShapePointMinDensityActionPerformed
    
    private void cboInternalPointsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboInternalPointsActionPerformed
        btnGenerate.doClick();
    }//GEN-LAST:event_cboInternalPointsActionPerformed
    
    private void btnGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateActionPerformed
        StatusDialog.start(this,"Generating Shape", "Please wait while shape is generated", new Thread() {
            public void run() {
                // Print text layout
                Rectangle shapebounds = new Rectangle(SHAPEMARGIN_LEFTRIGHT, SHAPEMARGIN_TOPBOTTOM, (int)panel.getWidth()-2*SHAPEMARGIN_LEFTRIGHT, (int)panel.getHeight()-2*SHAPEMARGIN_TOPBOTTOM);
                
                // Get Point
                Font font = new Font((String)cboFont.getSelectedItem(),Font.BOLD,200);
                
                // Get Shape Point Varibles
                int shapepoint_mindensity = Integer.parseInt((String)cboShapePointMinDensity.getSelectedItem());
                int shapepoints;
                String strshapepoints = ((String)cboShapePoints.getSelectedItem()).toLowerCase();
                if ( strshapepoints.startsWith("n") ) {
                    shapepoints = 0;
                } else if ( strshapepoints.startsWith("m") ) {
                    shapepoints = Integer.MAX_VALUE;
                } else {
                    try {
                        shapepoints = Integer.parseInt(strshapepoints);
                    } catch ( NumberFormatException e ) {
                        displayError("Unrecognised number of points required - \"" + strshapepoints + "\"");
                        return;
                    }
                }
                
                // Get Internal Point Varibles
                int internal_mindensity = Integer.parseInt((String)cboInternalMinDensity.getSelectedItem());
                int internalpoints;
                String strinternalpoints = ((String)cboInternalPoints.getSelectedItem()).toLowerCase();
                if ( strinternalpoints.startsWith("n") ) {
                    internalpoints = 0;
                } else if ( strinternalpoints.startsWith("m") ) {
                    internalpoints = Integer.MAX_VALUE;
                } else {
                    try {
                        internalpoints = Integer.parseInt(strinternalpoints);
                    } catch ( NumberFormatException e ) {
                        displayError("Unrecognised number of points required - \"" + strinternalpoints + "\"");
                        return;
                    }
                }
                
                // Remove the set of old points
                points = null;
                borderpoints = null;
                
                // Create the shape
                ArrayList<VPoint> newborderpoints = null;
                if ( optLetterGeneration.isSelected() ) {
                    try {
                        newborderpoints = ShapeGeneration.createShapeOutline(txtLetter.getText(), shapebounds, font);
                    } catch ( ShapeGenerationException e ) {
                        displayError(e);
                        return;
                    }
                } else if ( optCountryGeneration.isSelected() ) {
                    // Get the selected country file
                   // String countryfile = countrylistmodel.getSelectedCountry();
                    
                    // Get the points that form this country
                   // try {
                    //    newborderpoints = CountryData.getCountryData(countryfile, shapebounds);
                   // } catch ( IOException e ) {
                   //     displayError(e);
                   //     return;
                   // }
                } else {
                    displayError("Unknown generation type selected");
                    return;
                }
                
                // Generate random points
                ArrayList<VPoint> newpoints = null;
                try {
                    boolean splitlonglines = chkAddShapePointsToSplitLongLines.isSelected();
                    newpoints = ShapeGeneration.addRandomPoints(newborderpoints, splitlonglines,
                            shapepoints, shapepoint_mindensity,
                            internalpoints, internal_mindensity);
                } catch ( ShapeGenerationException e ) {
                    displayError(e);
                    return;
                }
                
                // Calculate the expected area and perimeter
                expectedarea = VoronoiShared.calculateAreaOfShape(newborderpoints);
                txtExpectedArea.setText( String.format("%.1f",expectedarea) + " pixels^2");
                txtExpectedPerimeter.setText( String.format("%.1f",VoronoiShared.calculatePerimeterOfShape(newborderpoints)) + " pixels");
                
                // Convert points to the right form
                borderpoints = newborderpoints;
                if ( optNone.isSelected() ) {
                    points = newpoints;
                } else if ( optVoronoiCells.isSelected() ) {
                    points = RepresentationFactory.convertPointsToVoronoiCellPoints(newpoints);
                } else if ( optSimpleTriangulation.isSelected() ) {
                    points = RepresentationFactory.convertPointsToSimpleTriangulationPoints(newpoints);
                } else if ( optEdgeRemoval.isSelected() ) {
                    points = RepresentationFactory.convertPointsToTriangulationPoints(newpoints);
                } else if ( optClustering.isSelected() ) {
                    points = RepresentationFactory.convertPointsToTriangulationPoints(newpoints);
                } else {
                    throw new RuntimeException("Unknown option selected");
                }
                
                // Save the points
                try {
                    savePoints();
                } catch ( IOException e2 ) {
                    displayError(e2);
                }
                
                // Repaint
                repaint();
                
                // Update controls (post repaint)
                updateControls();
            }
        });
        // Repaint
        this.repaint();
        
        // Update controls (post repaint)
        updateControls();
    }//GEN-LAST:event_btnGenerateActionPerformed
    
    private void txtLetterKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLetterKeyTyped
        txtLetter.setText( Character.toString(evt.getKeyChar()) );
        evt.consume();
        btnGenerate.doClick();
    }//GEN-LAST:event_txtLetterKeyTyped
    
    private void optClusteringActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optClusteringActionPerformed
        enableEdgeRemovePanel( false );
        points = RepresentationFactory.convertPointsToTriangulationPoints(points);
        representation = RepresentationFactory.createTriangulationRepresentation();
        ((TriangulationRepresentation)representation).setDetermineClustersMode();
        this.repaint();
    }//GEN-LAST:event_optClusteringActionPerformed
    
    private void sliderApplyInProportionStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderApplyInProportionStateChanged
        // No need to updateControls() - just repaint
        this.repaint();
    }//GEN-LAST:event_sliderApplyInProportionStateChanged
    
    private void optApplyAboveMSTAndSmallestTEdgeInProportionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optApplyAboveMSTAndSmallestTEdgeInProportionActionPerformed
        updateControls();
    }//GEN-LAST:event_optApplyAboveMSTAndSmallestTEdgeInProportionActionPerformed
    
    private void chkShowMinimumSpanningTreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowMinimumSpanningTreeActionPerformed
        if ( representation==null ) return;
        TriangulationRepresentation triangularrep = (TriangulationRepresentation) representation;
        if ( chkShowMinimumSpanningTree.isSelected() ) {
            triangularrep.setDetermineMinSpanningTreeMode();
        } else {
            triangularrep.setReduceOuterBoundariesMode();
        }
        this.repaint();
    }//GEN-LAST:event_chkShowMinimumSpanningTreeActionPerformed
    
    private void chkShowDebugInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowDebugInfoActionPerformed
        TriangulationRepresentation.SHOW_DEBUG_INFO = chkShowDebugInfo.isSelected();
        this.repaint();
    }//GEN-LAST:event_chkShowDebugInfoActionPerformed
    
    private void sliderMaxEdgesToRemoveStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderMaxEdgesToRemoveStateChanged
        TriangulationRepresentation.MAX_EDGES_TO_REMOVE = sliderMaxEdgesToRemove.getValue();
        this.repaint();
    }//GEN-LAST:event_sliderMaxEdgesToRemoveStateChanged
    
    private void chkMaxEdgesToRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMaxEdgesToRemoveActionPerformed
        if ( chkMaxEdgesToRemove.isSelected() ) {
            sliderMaxEdgesToRemove.setEnabled(true);
            TriangulationRepresentation.MAX_EDGES_TO_REMOVE = sliderMaxEdgesToRemove.getValue();
        } else {
            sliderMaxEdgesToRemove.setEnabled(false);
            TriangulationRepresentation.MAX_EDGES_TO_REMOVE = -1;
        }
        this.repaint();
    }//GEN-LAST:event_chkMaxEdgesToRemoveActionPerformed
    
    private void chkShowInternalTrianglesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowInternalTrianglesActionPerformed
        TriangulationRepresentation.SHOW_INTERNAL_TRIANGLES = chkShowInternalTriangles.isSelected();
        this.repaint();
    }//GEN-LAST:event_chkShowInternalTrianglesActionPerformed
    
    private void chkShowEdgeLengthsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowEdgeLengthsActionPerformed
        TriangulationRepresentation.SHOW_EDGE_LENGTHS = chkShowEdgeLengths.isSelected();
        this.repaint();
    }//GEN-LAST:event_chkShowEdgeLengthsActionPerformed
    
    private void updateControls() {
        if ( optEdgeRemoval.isSelected() ) {
            TriangulationRepresentation trianglarrep = (TriangulationRepresentation) representation;
            
            // Set slider lengths
            if ( trianglarrep.getMinLength()>0 &&trianglarrep.getMaxLength()>0 ) {
                sliderLengthRestriction.setMinimum( trianglarrep.getMinLength()-1 );
                sliderLengthRestriction.setMaximum( (int)(trianglarrep.getMaxLength()*1.25) );
            }
            
            // Enable/disable slider, select new cutoff value
            TriangulationRepresentation.CalcCutOff calccutoff;
            if ( optNoLengthRestriction.isSelected() ) {
                sliderApplyInProportion.setEnabled(false);
                sliderLengthRestriction.setEnabled(false);
                sliderNormalisedLengthRestriction.setEnabled(false);
                calccutoff = new TriangulationRepresentation.CalcCutOff() {
                    public int calculateCutOff(TriangulationRepresentation rep) {
                        int val = 0;
                        updateLengthSlider(rep, val);
                        updateNormalisedLengthSlider(rep, val);
                        return val;
                    }
                };
            } else if ( optUserLengthRestriction.isSelected() ) {
                sliderApplyInProportion.setEnabled(false);
                sliderLengthRestriction.setEnabled(true);
                sliderNormalisedLengthRestriction.setEnabled(false);
                calccutoff = new TriangulationRepresentation.CalcCutOff() {
                    public int calculateCutOff(TriangulationRepresentation rep) {
                        // Update sliders
                        if ( rep.getMinLength()>0 && rep.getMaxLength()>0 ) {
                            sliderLengthRestriction.setMinimum( rep.getMinLength()-1 );
                            sliderLengthRestriction.setMaximum( (int)(rep.getMaxLength()*1.25) );
                        }
                        
                        // Calculate value
                        int val = sliderLengthRestriction.getValue();
                        //updateLengthSlider(rep, val);
                        updateNormalisedLengthSlider(rep, val);
                        return val;
                    }
                };
            } else if ( optNormalisedLengthRestriction.isSelected() ) {
                sliderApplyInProportion.setEnabled(false);
                sliderLengthRestriction.setEnabled(false);
                sliderNormalisedLengthRestriction.setEnabled(true);
                calccutoff = new TriangulationRepresentation.CalcCutOff() {
                    public int calculateCutOff(TriangulationRepresentation rep) {
                        // Get variables
                        double percentage = (double)sliderNormalisedLengthRestriction.getValue() / 100.0;
                        double min = rep.getMinLength();
                        double max = rep.getMaxLength();
                        
                        // Calculate normalised length based off percentage
                        int val = (int)( percentage * (max-min) + min );
                        
                        // Return value
                        updateLengthSlider(rep, val);
                        //updateNormalisedLengthSlider(rep, val);
                        return val;
                    }
                };
            } else if ( optMaxEdgeOfMinSpanningTree.isSelected() ) {
                sliderApplyInProportion.setEnabled(false);
                sliderLengthRestriction.setEnabled(false);
                sliderNormalisedLengthRestriction.setEnabled(false);
                calccutoff = new TriangulationRepresentation.CalcCutOff() {
                    public int calculateCutOff(TriangulationRepresentation rep) {
                        int val = rep.getMaxLengthOfMinimumSpanningTree();
                        updateLengthSlider(rep, val);
                        updateNormalisedLengthSlider(rep, val);
                        return val;
                    }
                };
            } else if ( optMaxEdgeOfSmallestTEdge.isSelected() ) {
                sliderApplyInProportion.setEnabled(false);
                sliderLengthRestriction.setEnabled(false);
                sliderNormalisedLengthRestriction.setEnabled(false);
                calccutoff = new TriangulationRepresentation.CalcCutOff() {
                    public int calculateCutOff(TriangulationRepresentation rep) {
                        int val = rep.getMaxLengthOfSmallestTriangleEdge();
                        updateLengthSlider(rep, val);
                        updateNormalisedLengthSlider(rep, val);
                        return val;
                    }
                };
            } else if ( optApplyAboveMSTAndSmallestTEdgeInProportion.isSelected() ) {
                sliderApplyInProportion.setEnabled(true);
                sliderLengthRestriction.setEnabled(false);
                sliderNormalisedLengthRestriction.setEnabled(false);
                calccutoff = new TriangulationRepresentation.CalcCutOff() {
                    public int calculateCutOff(TriangulationRepresentation rep) {
                        double proportion = (double)sliderApplyInProportion.getValue()/100.0;
                        int val = (int)(
                                (double)rep.getMaxLengthOfMinimumSpanningTree()*(1-proportion) +
                                (double)rep.getMaxLengthOfSmallestTriangleEdge()*proportion);
                        updateLengthSlider(rep, val);
                        updateNormalisedLengthSlider(rep, val);
                        return val;
                    }
                };
            } else {
                displayError("Unknown selection option");
                return;
            }
            
            // Set the appropriate cutoff calculator
            trianglarrep.setCalcCutOff(calccutoff);
            
            // Repaint Panel
            this.repaint();
        } else {
            return;
        }
    }
    
    private void updateLengthSlider(TriangulationRepresentation rep, int cutoff) {
        // Update sliders
        int min = rep.getMinLength();
        int max = rep.getMaxLength();
        if ( min>0 && max>0 ) {
            sliderLengthRestriction.setMinimum( min-1 );
            sliderLengthRestriction.setMaximum( (int)(max*1.25) );
        }
        
        // Set value
        sliderLengthRestriction.setValue(cutoff);
    }
    
    private void updateNormalisedLengthSlider(TriangulationRepresentation rep, int cutoff) {
        // Get variables
        int min = rep.getMinLength();
        int max = rep.getMaxLength();
        if ( min<=0 && max<=0 ) return;
        
        // Set slider position
        int percentage = (int)( (double)(cutoff - min) / (double)(max - min) * 100.0);
        sliderNormalisedLengthRestriction.setValue( percentage );
    }
    
    private void optMaxEdgeOfSmallestTEdgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optMaxEdgeOfSmallestTEdgeActionPerformed
        updateControls();
    }//GEN-LAST:event_optMaxEdgeOfSmallestTEdgeActionPerformed
    
    private void optMaxEdgeOfMinSpanningTreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optMaxEdgeOfMinSpanningTreeActionPerformed
        updateControls();
    }//GEN-LAST:event_optMaxEdgeOfMinSpanningTreeActionPerformed
    
    private void sliderLengthRestrictionStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderLengthRestrictionStateChanged
        // No need to updateControls() - just repaint
        this.repaint();
    }//GEN-LAST:event_sliderLengthRestrictionStateChanged
    
    private void optUserLengthRestrictionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optUserLengthRestrictionActionPerformed
        updateControls();
    }//GEN-LAST:event_optUserLengthRestrictionActionPerformed
    
    private void optNoLengthRestrictionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optNoLengthRestrictionActionPerformed
        updateControls();
    }//GEN-LAST:event_optNoLengthRestrictionActionPerformed
    
    private void optEdgeRemovalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optEdgeRemovalActionPerformed
        // Set up new points/representation
        points = RepresentationFactory.convertPointsToTriangulationPoints(points);
        representation = RepresentationFactory.createTriangulationRepresentation();
        
        // Update user selection
        enableEdgeRemovePanel( true );
        //optNoLengthRestriction.setSelected(true);
        //optNoLengthRestrictionActionPerformed(null);
        optApplyAboveMSTAndSmallestTEdgeInProportion.setSelected(true);
        optApplyAboveMSTAndSmallestTEdgeInProportionActionPerformed(null);
        
        // Repaint panel
        this.repaint();
    }//GEN-LAST:event_optEdgeRemovalActionPerformed
    
    private void enableEdgeRemovePanel(boolean flag) {
        optNoLengthRestriction.setEnabled( flag );
        optUserLengthRestriction.setEnabled( flag );
        sliderLengthRestriction.setEnabled( flag );
        optNormalisedLengthRestriction.setEnabled( flag );
        sliderNormalisedLengthRestriction.setEnabled( flag );
        sliderApplyInProportion.setEnabled( flag );
        optMaxEdgeOfMinSpanningTree.setEnabled( flag );
        optMaxEdgeOfSmallestTEdge.setEnabled( flag );
        optApplyAboveMSTAndSmallestTEdgeInProportion.setEnabled( flag );
        chkShowEdgeLengths.setEnabled( flag );
        chkShowInternalTriangles.setEnabled( flag );
        chkShowMinimumSpanningTree.setEnabled( flag );
        chkShowDebugInfo.setEnabled( flag );
        chkMaxEdgesToRemove.setEnabled( flag );
        if ( flag && chkMaxEdgesToRemove.isSelected()==false ) {
            sliderMaxEdgesToRemove.setEnabled( false );
        } else {
            sliderMaxEdgesToRemove.setEnabled( flag );
        }
    }
    
    private void sliderAreaCutOffToUseStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderAreaCutOffToUseStateChanged
        if ( BoundaryProblemRepresentation.VORONOICELLAREA_CUTOFF!=sliderAreaCutOffToUse.getValue() ) {
            BoundaryProblemRepresentation.VORONOICELLAREA_CUTOFF = sliderAreaCutOffToUse.getValue();
            backupboundaryenhancedvalue = sliderAreaCutOffToUse.getValue();
            this.repaint();
        }
    }//GEN-LAST:event_sliderAreaCutOffToUseStateChanged
    
    private void optBoundaryEnhancedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optBoundaryEnhancedActionPerformed
        enableEdgeRemovePanel( false );
        BoundaryProblemRepresentation.MIN_ANGLE_TO_ALLOW = 0.0 / 180.0 * Math.PI;
        if ( sliderAreaCutOffToUse.getValue()>0 ) {
            BoundaryProblemRepresentation.VORONOICELLAREA_CUTOFF = sliderAreaCutOffToUse.getValue();
            backupboundaryenhancedvalue = sliderAreaCutOffToUse.getValue();
        } else if ( backupboundaryenhancedvalue>0 ) {
            BoundaryProblemRepresentation.VORONOICELLAREA_CUTOFF = backupboundaryenhancedvalue;
            sliderAreaCutOffToUse.setValue(backupboundaryenhancedvalue);
        } else {
            backupboundaryenhancedvalue = 8000;
            BoundaryProblemRepresentation.VORONOICELLAREA_CUTOFF = 8000;
            sliderAreaCutOffToUse.setValue(8000);
        }
        points = RepresentationFactory.convertPointsToBoundaryProblemPoints(points);
        representation = RepresentationFactory.createBoundaryProblemRepresentation();
        this.repaint();
    }//GEN-LAST:event_optBoundaryEnhancedActionPerformed
    
    private void optVoronoiCellsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optVoronoiCellsActionPerformed
        enableEdgeRemovePanel( false );
        points = RepresentationFactory.convertPointsToVoronoiCellPoints(points);
        representation = RepresentationFactory.createVoronoiCellRepresentation();
        this.repaint();
    }//GEN-LAST:event_optVoronoiCellsActionPerformed
    
    private void optBoundaryUsingAngle30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optBoundaryUsingAngle30ActionPerformed
        enableEdgeRemovePanel( false );
        BoundaryProblemRepresentation.MIN_ANGLE_TO_ALLOW = 30.0 / 180.0 * Math.PI;
        BoundaryProblemRepresentation.VORONOICELLAREA_CUTOFF = 0;
        sliderAreaCutOffToUse.setValue(0);
        points = RepresentationFactory.convertPointsToBoundaryProblemPoints(points);
        representation = RepresentationFactory.createBoundaryProblemRepresentation();
        this.repaint();
    }//GEN-LAST:event_optBoundaryUsingAngle30ActionPerformed
    
    private void optBoundaryUsingAngle20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optBoundaryUsingAngle20ActionPerformed
        enableEdgeRemovePanel( false );
        BoundaryProblemRepresentation.MIN_ANGLE_TO_ALLOW = 20.0 / 180.0 * Math.PI;
        BoundaryProblemRepresentation.VORONOICELLAREA_CUTOFF = 0;
        sliderAreaCutOffToUse.setValue(0);
        points = RepresentationFactory.convertPointsToBoundaryProblemPoints(points);
        representation = RepresentationFactory.createBoundaryProblemRepresentation();
        this.repaint();
    }//GEN-LAST:event_optBoundaryUsingAngle20ActionPerformed
    
    private void optSimpleTriangulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optSimpleTriangulationActionPerformed
        enableEdgeRemovePanel( false );
        points = RepresentationFactory.convertPointsToSimpleTriangulationPoints(points);
        representation = RepresentationFactory.createSimpleTriangulationRepresentation();
        this.repaint();
    }//GEN-LAST:event_optSimpleTriangulationActionPerformed
    
    private void optBoundaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optBoundaryActionPerformed
        enableEdgeRemovePanel( false );
        BoundaryProblemRepresentation.MIN_ANGLE_TO_ALLOW = 0.0 / 180.0 * Math.PI;
        BoundaryProblemRepresentation.VORONOICELLAREA_CUTOFF = 0;
        sliderAreaCutOffToUse.setValue(0);
        points = RepresentationFactory.convertPointsToBoundaryProblemPoints(points);
        representation = RepresentationFactory.createBoundaryProblemRepresentation();
        this.repaint();
    }//GEN-LAST:event_optBoundaryActionPerformed
    
    private void optNoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optNoneActionPerformed
        enableEdgeRemovePanel( false );
        points = RepresentationFactory.convertPointsToVPoints(points);
        representation = null;
        this.repaint();
    }//GEN-LAST:event_optNoneActionPerformed
    
    private void chkShowPointCoordinatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowPointCoordinatesActionPerformed
        SHOW_POINT_COORDINATES = chkShowPointCoordinates.isSelected();
        this.repaint();
    }//GEN-LAST:event_chkShowPointCoordinatesActionPerformed
    
    private void chkShowCircleEventsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowCircleEventsActionPerformed
        SHOW_CIRCLEEVENTS = chkShowCircleEvents.isSelected();
        this.repaint();
    }//GEN-LAST:event_chkShowCircleEventsActionPerformed
    
    private void chkShowTreeStructureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowTreeStructureActionPerformed
        if ( chkShowTreeStructure.isSelected() ) {
            if ( treedialog==null ) {
                treedialog = new DebugTree(this);
            }
            treedialog.setVisible(true);
        } else {
            if ( treedialog!=null ) {
                treedialog.setVisible(false);
                treedialog.dispose();
                treedialog = null;
            }
        }
        this.repaint();
    }//GEN-LAST:event_chkShowTreeStructureActionPerformed
    
    private void chkShowSweeplineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowSweeplineActionPerformed
        SHOW_INTERACTIVE_SWEEPLINE = chkShowSweepline.isSelected();
        if ( chkShowSweepline.isSelected() ) {
            chkShowTreeStructure.setEnabled( true );
        } else {
            chkShowTreeStructure.setSelected( false );
            chkShowTreeStructure.setEnabled( false );
        }
        this.repaint();
    }//GEN-LAST:event_chkShowSweeplineActionPerformed
    
    private void btnClearPointsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearPointsActionPerformed
        // Clear and save the points
        points.clear();
        borderpoints = null;
        try {
            savePoints();
        } catch ( IOException e2 ) {
            displayError(e2);
        }
        
        // Repaint
        this.repaint();
    }//GEN-LAST:event_btnClearPointsActionPerformed
    
    private void btnTestSuiteFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTestSuiteFormActionPerformed
        //TestSuite form = new TestSuite(false, this);
       // form.setVisible(true);
    }//GEN-LAST:event_btnTestSuiteFormActionPerformed
    
    private void displayError(String message) {
    	if(true)return;
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    static private void displayError(JFrame parent, String message) {
    	if(true)return;
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void displayError(Throwable e) {
    	if(true)return;
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, e.getMessage(), e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
    }
    static private void displayError(JFrame parent, Throwable e) {
    	if(true)return;
        e.printStackTrace();
        JOptionPane.showMessageDialog(parent, e.getMessage(), e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClearPoints;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnExportToSVG;
    private javax.swing.JButton btnGenerate;
    private javax.swing.JButton btnLoad;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnTestSuiteForm;
    private javax.swing.JComboBox cboCountries;
    private javax.swing.JComboBox cboFont;
    private javax.swing.JComboBox cboInternalMinDensity;
    private javax.swing.JComboBox cboInternalPoints;
    private javax.swing.JComboBox cboShapePointMinDensity;
    private javax.swing.JComboBox cboShapePoints;
    private javax.swing.JCheckBox chkAddShapePointsToSplitLongLines;
    private javax.swing.JCheckBox chkMaxEdgesToRemove;
    private javax.swing.JCheckBox chkShowCircleEvents;
    private javax.swing.JCheckBox chkShowDebugInfo;
    private javax.swing.JCheckBox chkShowEdgeLengths;
    private javax.swing.JCheckBox chkShowExpectedBorder;
    private javax.swing.JCheckBox chkShowInternalTriangles;
    private javax.swing.JCheckBox chkShowIntersection;
    private javax.swing.JCheckBox chkShowMinimumSpanningTree;
    private javax.swing.JCheckBox chkShowMouseLocation;
    private javax.swing.JCheckBox chkShowPointCoordinates;
    private javax.swing.JCheckBox chkShowPoints;
    private javax.swing.JCheckBox chkShowSweepline;
    private javax.swing.JCheckBox chkShowTreeStructure;
    private javax.swing.ButtonGroup groupEdgeRemoval;
    private javax.swing.ButtonGroup groupGenerationType;
    private javax.swing.ButtonGroup groupRepresentations;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel lblActualArea;
    private javax.swing.JLabel lblActualPerimeter;
    private javax.swing.JLabel lblErrorFromExpectedArea;
    private javax.swing.JLabel lblExpectedArea;
    private javax.swing.JLabel lblExpectedPerimeter;
    private javax.swing.JLabel lblFont;
    private javax.swing.JLabel lblGenerationType;
    private javax.swing.JLabel lblInternalMinDensity;
    private javax.swing.JLabel lblInternalPoints;
    private javax.swing.JLabel lblL2Norm;
    private javax.swing.JLabel lblShapePointMinDensity;
    private javax.swing.JLabel lblShapePoints;
    private javax.swing.JRadioButton optApplyAboveMSTAndSmallestTEdgeInProportion;
    private javax.swing.JRadioButton optBoundary;
    private javax.swing.JRadioButton optBoundaryEnhanced;
    private javax.swing.JRadioButton optBoundaryUsingAngle20;
    private javax.swing.JRadioButton optBoundaryUsingAngle30;
    private javax.swing.JRadioButton optClustering;
    private javax.swing.JRadioButton optCountryGeneration;
    private javax.swing.JRadioButton optEdgeRemoval;
    private javax.swing.JRadioButton optLetterGeneration;
    private javax.swing.JRadioButton optMaxEdgeOfMinSpanningTree;
    private javax.swing.JRadioButton optMaxEdgeOfSmallestTEdge;
    private javax.swing.JRadioButton optNoLengthRestriction;
    private javax.swing.JRadioButton optNone;
    private javax.swing.JRadioButton optNormalisedLengthRestriction;
    private javax.swing.JRadioButton optSimpleTriangulation;
    private javax.swing.JRadioButton optUserLengthRestriction;
    private javax.swing.JRadioButton optVoronoiCells;
    private javax.swing.JPanel panelActions;
    private javax.swing.JPanel panelActionsInner;
    private javax.swing.JPanel panelBoundaryEnhanced;
    private javax.swing.JPanel panelCenter;
    private javax.swing.JPanel panelCheckBoxes;
    private javax.swing.JPanel panelClearPoints;
    private javax.swing.JPanel panelEdgeRemoval;
    private javax.swing.JPanel panelEdgeRemovalOptions;
    private javax.swing.JPanel panelGap;
    private javax.swing.JPanel panelGap1;
    private javax.swing.JPanel panelGapSouth;
    private javax.swing.JPanel panelGapSouth1;
    private javax.swing.JPanel panelGapWest;
    private javax.swing.JPanel panelGapWest1;
    private javax.swing.JPanel panelGapWest2;
    private javax.swing.JPanel panelGenerate;
    private javax.swing.JPanel panelGenerationSelection;
    private javax.swing.JPanel panelGenerationType;
    private javax.swing.JPanel panelLeft;
    private javax.swing.JPanel panelOptions;
    private javax.swing.JPanel panelPointOptions;
    private javax.swing.JPanel panelPoints;
    private javax.swing.JPanel panelPointsInner;
    private javax.swing.JPanel panelRepresentations;
    private javax.swing.JPanel panelRight;
    private javax.swing.JPanel panelStatCaptions;
    private javax.swing.JPanel panelStatLabels;
    private javax.swing.JPanel panelStatistics;
    private javax.swing.JPanel panelTop;
    private javax.swing.JScrollPane scrollRight;
    private javax.swing.JSlider sliderApplyInProportion;
    private javax.swing.JSlider sliderAreaCutOffToUse;
    private javax.swing.JSlider sliderLengthRestriction;
    private javax.swing.JSlider sliderMaxEdgesToRemove;
    private javax.swing.JSlider sliderNormalisedLengthRestriction;
    private javax.swing.JLabel txtActualArea;
    private javax.swing.JLabel txtActualPerimeter;
    private javax.swing.JLabel txtErrorFromExpectedArea;
    private javax.swing.JLabel txtExpectedArea;
    private javax.swing.JLabel txtExpectedPerimeter;
    private javax.swing.JLabel txtL2Norm;
    private javax.swing.JTextField txtLetter;
    // End of variables declaration//GEN-END:variables
    
    public class TestRepresentationWrapper implements RepresentationInterface {
        
        /* ***************************************************** */
        // Variables
        
        private final ArrayList<VPoint> circleevents = new ArrayList<VPoint>();
        
        private RepresentationInterface innerrepresentation = null;
        
        /* ***************************************************** */
        // Data/Representation Interface Method
        
        // Executed before the algorithm begins to process (can be used to
        //   initialise any data structures required)
        public void beginAlgorithm(Collection<VPoint> points) {
            // Reset the triangle array list
            circleevents.clear();
            
            // Call the inner representation
            if ( innerrepresentation!=null ) {
                innerrepresentation.beginAlgorithm(points);
            }
        }
        
        // Called to record that a vertex has been found
        public void siteEvent( VLinkedNode n1 , VLinkedNode n2 , VLinkedNode n3 ) {
            // Call the inner representation
            if ( innerrepresentation!=null ) {
                innerrepresentation.siteEvent(n1, n2, n3);
            }
        }
        public void circleEvent( VLinkedNode n1 , VLinkedNode n2 , VLinkedNode n3 , int circle_x , int circle_y ) {
            // Add the circle event
            circleevents.add( new VPoint(circle_x, circle_y) );
            
            // Call the inner representation
            if ( innerrepresentation!=null ) {
                innerrepresentation.circleEvent(n1, n2, n3, circle_x, circle_y);
            }
        }
        
        // Called when the algorithm has finished processing
        public void endAlgorithm(Collection<VPoint> points, double lastsweeplineposition, VLinkedNode headnode) {
            // Call the inner representation
            if ( innerrepresentation!=null ) {
                innerrepresentation.endAlgorithm(points, lastsweeplineposition, headnode);
            }
        }
        
        /* ***************************************************** */
    }
}
