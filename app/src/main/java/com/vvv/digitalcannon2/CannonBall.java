package com.vvv.digitalcannon2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class CannonBall {
    private final Bitmap bitmap;
    public int x, y;
    public boolean fired = false;
    private int velocityX = 0, velocityY = -10;


    public CannonBall(Context context, int drawableResId) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setVelocity(float angle) {
        float angleRad = (float) Math.toRadians(angle);
        velocityX = (int) (10 * Math.sin(angleRad));
        velocityY = (int) (-10 * Math.cos(angleRad));
    }

    public void fire() {
        fired = true;
    }

    public void update(int screenX, int screenY) {
        if (fired) {
            x += velocityX;
            y += velocityY;

            //left wall
            if (x <= 0) {
                velocityX = -velocityX;
            }

            //right wall
            if (x >= screenX) {
                velocityX = -velocityX;
            }

            //top wall
            if (y <= 0) {
                velocityY = -velocityY;
            }
        }
    }


    public void draw(Canvas canvas) {
        if (fired) {
            canvas.drawBitmap(bitmap, x - (float) bitmap.getWidth() / 2, y - (float) bitmap.getHeight() / 2, null);
        }
    }

}
