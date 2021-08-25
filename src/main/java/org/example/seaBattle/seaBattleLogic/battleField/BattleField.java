package org.example.seaBattle.seaBattleLogic.battleField;

import org.example.seaBattle.seaBattleLogic.Coordinate;
import org.example.seaBattle.seaBattleLogic.ship.Ship;

public class BattleField {
    private final static int BATTLE_FIELD_SIZE = 10;
    private final Cell[][] cells;
    private final Ship[] ships;

    public BattleField(Coordinate[][] coordinatesArray) {
        cells = new Cell[BATTLE_FIELD_SIZE][BATTLE_FIELD_SIZE];
        ships = new Ship[coordinatesArray.length];

        for (int i = 0; i < BATTLE_FIELD_SIZE; ++i) {
            for (int j = 0; j < BATTLE_FIELD_SIZE; ++j) {
                cells[i][j] = new Cell();
            }
        }

        for (int i = 0; i < coordinatesArray.length; ++i) {
            ships[i] = new Ship(coordinatesArray[i]);
            for (int j = 0; j < coordinatesArray[i].length; ++j) {
                int x = coordinatesArray[i][j].getX();
                int y = coordinatesArray[i][j].getY();
                cells[x][y].setState(CellState.UNBEATED_SHIP);
                cells[x][y].setId(i);
            }
        }
    }

    public ResultOfShot[] getResultOfShot(Coordinate coordinate) {
        int cellId = cells[coordinate.getX()][coordinate.getY()].getId();
        if (cellId == -1) {
            cells[coordinate.getX()][coordinate.getY()].setState(CellState.SHOT);
            return new ResultOfShot[]{new ResultOfShot(coordinate, CellState.SHOT)};
        } else {
            ships[cellId].takeShot();
            cells[coordinate.getX()][coordinate.getY()].setState(CellState.BEATED_SHIP);

            if (ships[cellId].isDowned()) {
                Coordinate[] coordinatesOfShip = ships[cellId].getCoordinates();

                int startX, endX, startY, endY;
                if (coordinatesOfShip[0].getX() == coordinatesOfShip[coordinatesOfShip.length - 1].getX()) {
                    startX = Math.max(coordinatesOfShip[0].getX() - 1, 0);
                    endX = Math.min(coordinatesOfShip[0].getX() + 1, BATTLE_FIELD_SIZE - 1);
                    startY = Math.max(coordinatesOfShip[0].getY() - 1, 0);
                    endY = Math.min(coordinatesOfShip[coordinatesOfShip.length - 1].getY() + 1, BATTLE_FIELD_SIZE - 1);
                } else {
                    startX = Math.max(coordinatesOfShip[0].getX() - 1, 0);
                    endX = Math.min(coordinatesOfShip[coordinatesOfShip.length - 1].getX() + 1, BATTLE_FIELD_SIZE - 1);
                    startY = Math.max(coordinatesOfShip[0].getY() - 1, 0);
                    endY = Math.min(coordinatesOfShip[0].getY() + 1, BATTLE_FIELD_SIZE - 1);
                }

                ResultOfShot[] result = new ResultOfShot[(endX - startX + 1) * (endY - startY + 1)];

                for (int i = startX; i <= endX; ++i) {
                    for (int j = startY; j <= endY; ++j) {
                        Cell cell = cells[i][j];
                        switch (cell.getState()) {
                            case SHOT:
                                result[(i - startX) * (endY - startY + 1) + (j - startY)] =
                                        new ResultOfShot(new Coordinate(i, j), CellState.SHOT);
                                break;
                            case UNSHOT:
                                cells[i][j].setState(CellState.SHOT);
                                result[(i - startX) * (endY - startY + 1) + (j - startY)] =
                                        new ResultOfShot(new Coordinate(i, j), CellState.SHOT);
                                break;
                            case BEATED_SHIP:
                                result[(i - startX) * (endY - startY + 1) + (j - startY)] =
                                        new ResultOfShot(new Coordinate(i, j), CellState.BEATED_SHIP);
                                break;
                        }
                    }
                }

                return result;
            } else {
                return new ResultOfShot[]{new ResultOfShot(coordinate, CellState.BEATED_SHIP)};
            }
        }
    }

}
