package com.vvv.digitalcannon2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Cannon {
    private final Bitmap bitmap;
    private final int x, y;

    public Cannon(Context context, int drawableResId, int screenX, int screenY) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);
        x = (screenX / 2) - (bitmap.getWidth() / 2);
        y = screenY - bitmap.getHeight();
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }
}

