/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package geogebraVoiceCommand.modifierstringcommand.object;

import geogebraVoiceCommand.FieldViewer;
import geogebraVoiceCommand.RunnerGGB;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;

/**
 *
 * @author Darkyver
 */
public class CmdBuilder {
    //создадим небольшой массив для построенных точек. на 500 точек
    public static StackPoint stackPoint = new StackPoint(500);
    
    //создадим небольшой массив для построенных треугольников. на 500 штук
    public static StackTriangle stackTriangle = new StackTriangle(500);
    
    private static final int DELTA_SHIFT_VIEW = 5;
    public static IdentificationCmd identificationCmd;
    public static FieldViewer fView;
    private RunnerGGB runnerGGB;
    private String result = "";

    public FieldViewer getViewer() {
        return fView;
    }

    public CmdBuilder() {
        runnerGGB = new RunnerGGB();
        runnerGGB.runGGB();
        identificationCmd = new IdentificationCmd();
        fView = new FieldViewer(runnerGGB.getPanel().getGeoGebraAPI());
        fView.setDefault();
    }

    public org.geogebra.desktop.plugin.GgbAPID getAPI(){
        return runnerGGB.getPanel().getGeoGebraAPI();
    }

    public RunnerGGB getRunnerGGB() {
        return runnerGGB;
    }

    public void build(String str) {
        try {
            identificationCmd.detect(str);
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException ex) {
            Logger.getLogger(CmdBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        build(identificationCmd);
    }

    public void executeCMD(String cmd) {
        runnerGGB.sendCommand(cmd);
    }

    

    private void build(IdentificationCmd IdCmd) {
        int Ax, Ay, Bx, By;
        if (IdCmd.getAction().equals("create")) {
            switch (IdCmd.getObject()) {
                case ("Point"):
                    Point p = new Point();
                    p.buildPoint();
                    break;
                case ("Segment"):
                    Segment s = new Segment();
                    s.buildSegment();
                    break;
                case ("Triangle"):
                    Triangle tri = new Triangle();
                    tri.buildTriangle();
                    break;
                case ("Rectangle"):
                    Ax = (int) (Math.random() * 10 - 5);
                    Ay = (int) (Math.random() * 10 - 5);
                    Bx = (int) (Math.random() * 10 - 5);
                    By = (int) (Math.random() * 10 - 5);
                    result = "Polygon((" + Ax + "," + Ay + "),"
                            + "(" + Bx + "," + Ay + "),(" + Bx + "," + By + "),"
                            + "(" + Ax + "," + By + "))";
                    break;
                case ("квадр"):
                    Ax = (int) (Math.random() * 10 - 5);
                    Ay = (int) (Math.random() * 10 - 5);
                    int tmp = (int) (Math.random() * 10 - 5);
                    Bx = Ax + tmp;
                    By = Ay + tmp;
                    result = "Polygon({(" + Ax + "," + Ay + "),"
                            + "(" + Bx + "," + Ay + "),(" + Bx + "," + By + "),"
                            + "(" + Ax + "," + By + ")})";
                    break;
                case ("кру"):

                    break;
            }
            //executeCMD(result);
        }

        if (IdCmd.getAction().equals("view")) {
            switch (IdCmd.getActionContext()) {
                case ("zoomOut"):
                    fView.zoomOut(DELTA_SHIFT_VIEW);
                    fView.refresh();
                    break;
                case ("zoomIn"):
                    fView.zoomIn(DELTA_SHIFT_VIEW);
                    fView.refresh();
                    break;
            }
        }
        if (IdCmd.getAction().equals("info")) {
            switch (IdCmd.getActionContext()) {
                case ("Triangle"):
                    stackTriangle.peek().showArea();
                    break;
            }
        }
    }

}
