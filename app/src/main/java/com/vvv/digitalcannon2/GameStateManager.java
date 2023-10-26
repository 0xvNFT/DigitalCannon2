package com.vvv.digitalcannon2;

public class GameStateManager {
    private GameState currentState;

    public GameStateManager() {
        currentState = GameState.STARTING;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(GameState newState) {
        currentState = newState;
    }

    public enum GameState {
        STARTING,
        PLAYING,
        PAUSED,
        GAME_OVER
    }
}

