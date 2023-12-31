package com.vvv.digitalcannon2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class CannonBall {
    final Bitmap bitmap;
    public int x, y;
    public boolean fired = false;
    public int velocityX = 0;
    public int velocityY = -10;
    private boolean isFirstLaunch = true;

    public CannonBall(Context context, int drawableResId) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setVelocity(float angle, int speedFactor) {
        float angleRad = (float) Math.toRadians(angle);
        velocityX = (int) (speedFactor * Math.sin(angleRad));
        velocityY = (int) (-speedFactor * Math.cos(angleRad));
    }


    public void fire() {
        fired = true;
        isFirstLaunch = true;
    }

    public void update(int screenX, int endLineY) {
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

            if (y > endLineY) {
                if (isFirstLaunch) {
                    isFirstLaunch = false;
                } else {
                    fired = false;
                }
            }
        }
    }



    public void draw(Canvas canvas) {
        if (fired) {
            canvas.drawBitmap(bitmap, x - (float) bitmap.getWidth() / 2, y - (float) bitmap.getHeight() / 2, null);
        }
    }

}
