package com.vvv.digitalcannon2;

public class FPSCounter {
    private int frameCount = 0;
    private long lastFPSTime = System.currentTimeMillis();
    private int fps;

    public int countFPS() {
        frameCount++;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFPSTime >= 1000) {
            fps = frameCount;
            frameCount = 0;
            lastFPSTime = currentTime;
        }
        return fps;
    }
}

