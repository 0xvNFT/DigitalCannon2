package com.vvv.digitalcannon2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Background {
    private Bitmap backgroundBitmap;

    public Background(Context context, int resourceId, int screenX, int screenY) {
        backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, screenX, screenY, false);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
    }

}
