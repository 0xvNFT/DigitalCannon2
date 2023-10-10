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
    private final float scale;

    public CannonBallManager(Context context, int drawableResId, long fireInterval, int screenX, float scale) {
        this.fireInterval = fireInterval;
        this.screenX = screenX;
        this.scale = scale;

        for (int i = 0; i < 20; i++) {
            cannonBalls.add(new CannonBall(context, drawableResId));
        }
    }

    public void tryFireCannonBall(float angle, Point tip, int speedFactor) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFiredTime >= fireInterval) {
            lastFiredTime = currentTime;

            for (CannonBall cannonBall : cannonBalls) {
                if (!cannonBall.fired) {
                    cannonBall.setPosition(tip.x, tip.y);
                    cannonBall.setVelocity(angle, speedFactor);
                    cannonBall.fire();
                    return;
                }
            }
        }
    }

    public void tryFireMiniCannonBall(Cannon cannon, int speedFactor) {
        float angle = cannon.getAngle();
        Point tip = cannon.getTipCoordinates();
        tip.x += (int) ((1 - scale) * 50);
        tip.y += (int) ((1 - scale) * 50);

        tryFireCannonBall(angle, tip, speedFactor);
    }


    public void updateAll(int screenX, int screenY) {
        for (CannonBall cannonBall : cannonBalls) {
            cannonBall.update(screenX, screenY);

            if (cannonBall.y > screenY) {
                cannonBall.fired = false;
            }
        }
    }

    public void drawAll(Canvas canvas) {
        for (CannonBall cannonBall : cannonBalls) {
            cannonBall.draw(canvas);
        }
    }
}
