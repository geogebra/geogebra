/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geogebraVoiceCommand.modifierstringcommand.object;

import static geogebraVoiceCommand.FieldViewer.DELTA_FIELD;
import static geogebraVoiceCommand.FieldViewer.xmin;
import static geogebraVoiceCommand.FieldViewer.xmax;
import static geogebraVoiceCommand.FieldViewer.ymin;
import static geogebraVoiceCommand.FieldViewer.ymax;
import static geogebraVoiceCommand.modifierstringcommand.object.CmdBuilder.fView;
import static geogebraVoiceCommand.modifierstringcommand.object.CmdBuilder.identificationCmd;
import static geogebraVoiceCommand.modifierstringcommand.object.CmdBuilder.stackPoint;
import java.util.Random;

/**
 *
 * @author Darkyver
 */
public class Point {

    private double X;
    private double Y;
    private int index;
    Random random;

    public Point() {
        random = new Random();
        X = getRandomX();
        Y = getRandomY();
        int index = -1;
    }

    public Point(double X, double Y) {
        random = new Random();
        this.X = X;
        this.Y = Y;
        index = -1;
    }

    public Point(double X, double Y, String name) {
        random = new Random();
        this.X = X;
        this.Y = Y;
        this.index = -1;
    }

    public Point(Point p) {
        this.setPoint(p);
    }

    private double getRandomX() {
        X = (random.nextInt(Math.abs(xmin) + Math.abs(xmax) - DELTA_FIELD) - (Math.abs(xmin) + DELTA_FIELD / 2));
        return X;
    }

    private double getRandomY() {
        Y = (random.nextInt(Math.abs(ymin) + Math.abs(ymax) - DELTA_FIELD) - (Math.abs(ymin) + DELTA_FIELD / 2));
        return Y;
    }

    public Point getRandomPoint() {
        Point p = new Point();
        p.X = getRandomX();
        p.Y = getRandomY();
        p.index = -1;
        return p;
    }

    public double getX() {
        return this.X;
    }

    public double getY() {
        return this.Y;
    }

    public int getIndex() {
        return this.index;
    }

    public void setPoint(double X, double Y) {
        this.X = X;
        this.Y = Y;
        index = -1;
    }

    public void setPoint() {
        X = getRandomX();
        Y = getRandomY();
        index = -1;
    }

    public void setPoint(Point p) {
        this.X = p.getX();
        this.Y = p.getX();
        this.index = p.getIndex();
    }

    public void randomSet() {
        X = getRandomX();
        Y = getRandomY();
    }

    @Override
    public String toString() {
        return ("Point({" + X + "," + Y + "})");
    }

    public boolean equals(Point p) {
        return (this.getX() == p.getX() && this.getY() == p.getY()); //To change body of generated methods, choose Tools | Templates.
    }

    void buildPoint() {

        if (identificationCmd.XYAvailable) {
            setPoint(identificationCmd.getX(), identificationCmd.getY());
        }
        executeCMD();
        index = fView.getGeoGebraAPI().getObjectNumber();
        fView.refresh(this);
    }

    public void executeCMD() {
        fView.sendCommand(toString());
        stackPoint.push(this);
    }
    
}
