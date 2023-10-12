package com.vvv.digitalcannon2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class TargetBox {
    private final Bitmap bitmap;
    public int x, y;
    private final int speed;

    public TargetBox(Context context, int drawableResId, int speed) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);
        this.speed = speed;
    }

    public void update() {
        y += speed;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }
}

