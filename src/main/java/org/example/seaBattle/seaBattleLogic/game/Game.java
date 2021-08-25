package org.example.seaBattle.seaBattleLogic.game;

import org.example.seaBattle.domain.User;
import org.example.seaBattle.seaBattleLogic.Coordinate;
import org.example.seaBattle.seaBattleLogic.battleField.BattleField;

public class Game {
    private String id;
    private User firstUser;
    private User secondUser;
    private BattleField firstBattleField;
    private BattleField secondBattleField;

    public Game(String id, User firstUser, User secondUser) {
        this.id = id;
        this.firstUser = firstUser;
        this.secondUser = secondUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(User firstUser) {
        this.firstUser = firstUser;
    }

    public User getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(User secondUser) {
        this.secondUser = secondUser;
    }

    public BattleField getFirstBattleField() {
        return firstBattleField;
    }

    public void setFirstBattleField(BattleField firstBattleField) {
        this.firstBattleField = firstBattleField;
    }

    public BattleField getSecondBattleField() {
        return secondBattleField;
    }

    public void setSecondBattleField(BattleField secondBattleField) {
        this.secondBattleField = secondBattleField;
    }
}
