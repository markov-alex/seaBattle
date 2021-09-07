package org.example.seaBattle.seaBattleLogic.game;

import org.example.seaBattle.seaBattleLogic.battleField.ResultOfShot;

public class ResponseGame {
    private String nextMethod;
    private ResultOfShot[] resultOfShot;

    public ResponseGame() {
    }

    public ResponseGame(String nextMethod) {
        this.nextMethod = nextMethod;
    }

    public ResponseGame(String nextMethod, ResultOfShot[] resultOfShot) {
        this.nextMethod = nextMethod;
        this.resultOfShot = resultOfShot;
    }

    public String getNextMethod() {
        return nextMethod;
    }

    public void setNextMethod(String nextMethod) {
        this.nextMethod = nextMethod;
    }

    public ResultOfShot[] getResultOfShot() {
        return resultOfShot;
    }

    public void setResultOfShot(ResultOfShot[] resultOfShot) {
        this.resultOfShot = resultOfShot;
    }
}
