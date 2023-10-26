package com.vvv.digitalcannon2;

import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private final List<MotionEvent> eventQueue = new ArrayList<>();
    private int score = 0;

    public void addEvent(MotionEvent event) {
        synchronized (eventQueue) {
            eventQueue.add(event);
        }
    }

    public void incrementScore(int value) {
        score += value;
    }


    public void processEvents() {
        synchronized (eventQueue) {
//            Testing only
//            for (MotionEvent event : eventQueue) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    score++;
//                }
//            }
            eventQueue.clear();
        }
    }

    public int getScore() {
        return score;
    }

    public void resetScore() {
        score = 0;
    }
}

