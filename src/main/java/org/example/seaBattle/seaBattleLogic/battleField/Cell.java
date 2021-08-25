package org.example.seaBattle.seaBattleLogic.battleField;

public class Cell {
    private CellState state;
    private int id;

    public Cell() {
        state = CellState.UNSHOT;
        id = -1;
    }

    public Cell(CellState state, int id) {
        this.state = state;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }
}
