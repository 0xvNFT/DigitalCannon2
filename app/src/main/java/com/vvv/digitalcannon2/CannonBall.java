package com.vvv.digitalcannon2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class CannonBall {
    private Bitmap bitmap;

    public CannonBall(Context context, int drawableResId, int screenX, int screenY) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);
        bitmap = Bitmap.createScaledBitmap(bitmap, screenX, screenY, false);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

}
