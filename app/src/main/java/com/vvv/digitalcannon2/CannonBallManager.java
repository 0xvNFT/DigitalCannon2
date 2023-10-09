package com.vvv.digitalcannon2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class CannonBallManager {
    private final List<CannonBall> cannonBalls = new ArrayList<>();
    private final long fireInterval;
    private final int screenX;
    private long lastFiredTime = 0;

    public CannonBallManager(Context context, int drawableResId, long fireInterval, int screenX) {
        this.fireInterval = fireInterval;
        this.screenX = screenX;

        for (int i = 0; i < 10; i++) {
            cannonBalls.add(new CannonBall(context, drawableResId));
        }
    }

    public void tryFireCannonBall(float angle, Point tip) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFiredTime >= fireInterval) {
            lastFiredTime = currentTime;

            for (CannonBall cannonBall : cannonBalls) {
                if (!cannonBall.fired) {
                    cannonBall.setPosition(tip.x, tip.y);
                    cannonBall.setVelocity(angle);
                    cannonBall.fire();
                    return;
                }
            }
        }
    }

    public void updateAll(int screenX, int screenY) {
        for (CannonBall cannonBall : cannonBalls) {
            cannonBall.update(screenX, screenY);
        }
    }

    public void drawAll(Canvas canvas) {
        for (CannonBall cannonBall : cannonBalls) {
            cannonBall.draw(canvas);
        }
    }
}
