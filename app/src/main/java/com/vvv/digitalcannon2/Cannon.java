package com.vvv.digitalcannon2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;

public class Cannon {
    private final Bitmap bitmap;
    public int x, y;
    public float angle = 160;
    private float time = 0;

    public Cannon(Context context, int drawableResId, int screenX, int screenY) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);
        x = (screenX / 2) - (bitmap.getWidth() / 2);
        y = screenY - bitmap.getHeight() - 50;
    }

    public void update() {
        time += 0.04F;

        angle = 0 + 60 * (float) Math.sin(time);
    }

    public void draw(Canvas canvas) {
        Matrix matrix = new Matrix();
        matrix.postTranslate(-bitmap.getWidth() / 2.0f, -bitmap.getHeight() / 2.0f);
        matrix.postRotate(angle);
        matrix.postTranslate(x + bitmap.getWidth() / 2.0f, y + bitmap.getHeight() / 2.0f);

        canvas.drawBitmap(bitmap, matrix, null);
    }

    public float getAngle() {
        return angle;
    }

    public Point getTipCoordinates() {

        float tipX = 0;
        float tipY = -bitmap.getHeight() / 2.0f;

        float angleRad = (float) Math.toRadians(angle);

        float rotatedX = (float) (tipX * Math.cos(angleRad) - tipY * Math.sin(angleRad));
        float rotatedY = (float) (tipX * Math.sin(angleRad) + tipY * Math.cos(angleRad));

        int finalX = (int) (x + bitmap.getWidth() / 2.0f + rotatedX);
        int finalY = (int) (y + bitmap.getHeight() / 2.0f + rotatedY);

        return new Point(finalX, finalY);
    }

    public int getHeight() {
        return bitmap.getHeight();
    }
}

