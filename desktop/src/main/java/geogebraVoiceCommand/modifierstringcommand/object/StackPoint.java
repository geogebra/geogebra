/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geogebraVoiceCommand.modifierstringcommand.object;

/**
 *
 * @author Darkyver
 */
public class StackPoint {

    private Point[] arrPoint;
    private int top;

    public StackPoint(int N) {
        //реализуем мини стек для точек
        //
        //массив точек так и назовём
        arrPoint = new Point[N];
        //утсановим изначальный указатель на верхушку -1.
        top = -1;
    }
    

    public void push(Point p) {
        arrPoint[++top] = p;
    }

    public Point pop() {
        return arrPoint[top--];
    }

    public int getTop() {
        return top;
    }
    
    

}
