package com.vvv.digitalcannon2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

public class Cannon {
    private final Bitmap bitmap;
    private final int x, y;
    private float angle = 160;
    private float time = 0;
    private final float deltaAngle = 20;
    public Cannon(Context context, int drawableResId, int screenX, int screenY) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);
        x = (screenX / 2) - (bitmap.getWidth() / 2);
        y = screenY - bitmap.getHeight();
    }

    public void update() {
        time += 0.04F;

        angle = 0 + 60 * (float) Math.sin(time);

//        if (angle > 160) angle = 160;
//        if (angle < 20) angle = 20;
    }

    public void draw(Canvas canvas) {
        Matrix matrix = new Matrix();
        matrix.postTranslate(-bitmap.getWidth() / 2.0f, -bitmap.getHeight() / 2.0f);
        matrix.postRotate(angle);
        matrix.postTranslate(x + bitmap.getWidth() / 2.0f, y + bitmap.getHeight() / 2.0f);

        canvas.drawBitmap(bitmap, matrix, null);
    }
}

