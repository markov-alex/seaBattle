package org.example.seaBattle.seaBattleLogic.battleField;

import org.example.seaBattle.seaBattleLogic.Coordinate;

public class ResultOfShot {
    private final Coordinate coordinate;
    private final CellState cellState;

    public ResultOfShot() {
        this.coordinate = null;
        this.cellState = null;
    }

    public ResultOfShot(Coordinate coordinate, CellState cellState) {
        this.coordinate = coordinate;
        this.cellState = cellState;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public CellState getCellState() {
        return cellState;
    }
}
