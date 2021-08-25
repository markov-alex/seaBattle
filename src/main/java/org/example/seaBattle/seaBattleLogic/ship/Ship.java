package org.example.seaBattle.seaBattleLogic.ship;

import org.example.seaBattle.seaBattleLogic.Coordinate;

import java.util.Arrays;

public class Ship {
    private int unshotPart;
    private final Coordinate[] coordinates;

    public Ship(Coordinate[] coordinates) {
        this.coordinates = coordinates;
        Arrays.sort(this.coordinates);
        this.unshotPart = coordinates.length;
    }

    public Coordinate[] getCoordinates() {
        return coordinates;
    }

    public void takeShot() {
        --unshotPart;
    }

    public boolean isDowned() {
        return unshotPart == 0;
    }
}
