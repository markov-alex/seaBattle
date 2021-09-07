package org.example.seaBattle.seaBattleLogic.game;

import org.example.seaBattle.seaBattleLogic.battleField.ResultOfShot;
import org.example.seaBattle.seaBattleLogic.ship.Ship;

public class ResponseRecovery {
    private Ship[] ships;
    private ResultOfShot[] playerShots;
    private ResultOfShot[] opponentShots;
    private String nextMethod;

    public ResponseRecovery() {
    }

    public ResponseRecovery(String nextMethod) {
        this.nextMethod = nextMethod;
    }

    public ResponseRecovery(Ship[] ships, ResultOfShot[] playerShots, ResultOfShot[] opponentShots) {
        this.ships = ships;
        this.playerShots = playerShots;
        this.opponentShots = opponentShots;
    }

    public Ship[] getShips() {
        return ships;
    }

    public void setShips(Ship[] ships) {
        this.ships = ships;
    }

    public ResultOfShot[] getPlayerShots() {
        return playerShots;
    }

    public void setPlayerShots(ResultOfShot[] playerShots) {
        this.playerShots = playerShots;
    }

    public ResultOfShot[] getOpponentShots() {
        return opponentShots;
    }

    public void setOpponentShots(ResultOfShot[] opponentShots) {
        this.opponentShots = opponentShots;
    }

    public String getNextMethod() {
        return nextMethod;
    }

    public void setNextMethod(String nextMethod) {
        this.nextMethod = nextMethod;
    }
}
