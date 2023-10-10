package com.vvv.digitalcannon2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

public class MiniCannon extends Cannon {
    private final Bitmap bitmap;
    private final float scale;
    private final Cannon mainCannon;

    public MiniCannon(Context context, int drawableResId, int screenX, int screenY, int offsetX, float scale, Cannon mainCannon) {
        super(context, drawableResId, screenX, screenY);
        bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);
        this.x += offsetX;
        this.scale = scale;
        this.mainCannon = mainCannon;

    }

    @Override
    public void update() {
        this.angle = -mainCannon.getAngle();
    }

    @Override
    public void draw(Canvas canvas) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale, bitmap.getWidth() / 2.0f, bitmap.getHeight() / 2.0f);
        matrix.postTranslate(-bitmap.getWidth() / 2.0f, -bitmap.getHeight() / 2.0f);
        matrix.postRotate(getAngle());
        matrix.postTranslate(x + bitmap.getWidth() / 2.0f, y + bitmap.getHeight() / 2.0f);

        canvas.drawBitmap(bitmap, matrix, null);
    }
}
