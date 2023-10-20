package com.vvv.digitalcannon2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class CannonBallManager {
    final List<CannonBall> cannonBalls = new ArrayList<>();
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

    public void tryFireMiniCannonBall(MiniCannon miniCannon, int speedFactor) {
        float angle = miniCannon.getAngle();
        Point tip = miniCannon.getTipCoordinates();

        float offsetX = (1 - scale) * 10;
        float offsetY = (1 - scale) * 10;

        float angleRad = (float) Math.toRadians(angle);
        float rotatedOffsetX = (float) (offsetX * Math.cos(angleRad) - offsetY * Math.sin(angleRad));
        float rotatedOffsetY = (float) (offsetX * Math.sin(angleRad) + offsetY * Math.cos(angleRad));

        tip.x += (int) rotatedOffsetX;
        tip.y += (int) rotatedOffsetY;

        tryFireCannonBall(angle, tip, speedFactor);
    }


    public void updateAll(int screenX, int endLineY) {
        for (CannonBall cannonBall : cannonBalls) {
            cannonBall.update(screenX, endLineY);

            if (cannonBall.y > endLineY) {
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
