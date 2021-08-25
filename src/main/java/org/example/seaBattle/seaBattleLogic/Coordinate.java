package org.example.seaBattle.seaBattleLogic;

public class Coordinate implements Comparable<Coordinate> {
    private final int x;
    private final int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int compareTo(Coordinate other) {
        if (getX() == other.getX()) {
            return getY() - other.getY();
        }
        return getX() - other.getX();
    }
}
