package com.vvv.digitalcannon2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class TargetBox {
    private final Bitmap bitmap;
    public int x, y;
    private final int speed;
    public int delay;

    public TargetBox(Context context, int drawableResId, int x, int y, int speed, int delay) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.delay = delay;

    }


    public void update() {
        if (delay <= 0) {
            y += speed;
        } else {
            delay--;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }
}

