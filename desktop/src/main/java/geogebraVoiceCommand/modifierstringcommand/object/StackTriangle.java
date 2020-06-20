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
public class StackTriangle {

    private Triangle[] arrTriangle;
    private int top;

    public StackTriangle(int N) {
        //реализуем мини стек для точек
        //
        //массив точек так и назовём
        arrTriangle = new Triangle[N];
        //утсановим изначальный указатель на верхушку -1.
        top = -1;
    }

    public void push(Triangle tri) {
        arrTriangle[++top] = tri;
    }

    public Triangle pop() {
        return arrTriangle[top--];
    }
    
    public Triangle peek() {
        return arrTriangle[top];
    }

    public int getTop() {
        return top;
    }

}
