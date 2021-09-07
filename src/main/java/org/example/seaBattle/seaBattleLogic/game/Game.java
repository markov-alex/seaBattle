package org.example.seaBattle.seaBattleLogic.game;

import org.example.seaBattle.domain.User;
import org.example.seaBattle.seaBattleLogic.Coordinate;
import org.example.seaBattle.seaBattleLogic.battleField.BattleField;
import org.example.seaBattle.seaBattleLogic.battleField.ResultOfShot;

public class Game {
    private String id;
    private User firstUser;
    private User secondUser;
    private BattleField firstBattleField;
    private BattleField secondBattleField;
    private Integer countOfReceivedRequests;
    private boolean firstUserReady;
    private boolean secondUserReady;
    private ResponseGame responseGameOfPostRequest;
    private GameState gameState;
    private String resultFirstUser;
    private String resultSecondUser;
    private String currentMethodFirstUser;
    private String currentMethodSecondUser;

    public Game(String id, User firstUser, User secondUser) {
        this.id = id;
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.countOfReceivedRequests = 0;
        this.gameState = GameState.permissionPost;
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

    public Integer getCountOfReceivedRequests() {
        return countOfReceivedRequests;
    }

    public void setCountOfReceivedRequests(Integer countOfReceivedRequests) {
        this.countOfReceivedRequests = countOfReceivedRequests;
    }

    public boolean isFirstUserReady() {
        return firstUserReady;
    }

    public void setFirstUserReady(boolean firstUserReady) {
        this.firstUserReady = firstUserReady;
    }

    public boolean isSecondUserReady() {
        return secondUserReady;
    }

    public void setSecondUserReady(boolean secondUserReady) {
        this.secondUserReady = secondUserReady;
    }

    public ResponseGame getResponseGameOfPostRequest() {
        return responseGameOfPostRequest;
    }

    public void setResponseGameOfPostRequest(ResponseGame responseGameOfPostRequest) {
        this.responseGameOfPostRequest = responseGameOfPostRequest;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public String getResultFirstUser() {
        return resultFirstUser;
    }

    public void setResultFirstUser(String resultFirstUser) {
        this.resultFirstUser = resultFirstUser;
    }

    public String getResultSecondUser() {
        return resultSecondUser;
    }

    public void setResultSecondUser(String resultSecondUser) {
        this.resultSecondUser = resultSecondUser;
    }

    public String getCurrentMethodFirstUser() {
        return currentMethodFirstUser;
    }

    public void setCurrentMethodFirstUser(String currentMethodFirstUser) {
        this.currentMethodFirstUser = currentMethodFirstUser;
    }

    public String getCurrentMethodSecondUser() {
        return currentMethodSecondUser;
    }

    public void setCurrentMethodSecondUser(String currentMethodSecondUser) {
        this.currentMethodSecondUser = currentMethodSecondUser;
    }
}
