package sample;

import java.util.Comparator;

public class Point implements Comparator<Point> {
    private int X;
    private int Y;

    public Point(int x, int y) {
        X = x;
        Y = y;
        if (x < 0)
            X=0;
        if (y<0)
            Y=0;
        if (x > 200)
            X =200;
        if (y > 160)
            Y= 160;
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    @Override
    public String toString() {
        return (X + " " + Y + "\n");
    }

    @Override
    public int compare(Point p1, Point p2) {
        if ( Math.sqrt(Math.pow(p1.getX(),2) + Math.pow(p1.getY(),2)) < Math.sqrt(Math.pow(p2.getX(),2) + Math.pow(p2.getY(),2)))
            return -1;
        else if (Math.sqrt(Math.pow(p1.getX(),2) + Math.pow(p1.getY(),2)) > Math.sqrt(Math.pow(p2.getX(),2) + Math.pow(p2.getY(),2)))
            return  1;
        else
            return 0;
    }

}
