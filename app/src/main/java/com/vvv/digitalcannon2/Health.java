package com.vvv.digitalcannon2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Health {
    private final int maxHealth;
    private final Bitmap diamondBitmap;
    private final int screenX;
    private int currentHealth;

    public Health(Context context, int maxHealth, int screenX) {
        this.currentHealth = maxHealth;
        this.maxHealth = maxHealth;
        this.screenX = screenX;
        this.diamondBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.diamond);
    }

    public void decrement() {
        if (currentHealth > 0) {
            currentHealth--;
        }
    }

    public boolean isDepleted() {
        return currentHealth <= 0;
    }

    public void draw(Canvas canvas) {
        int diamondSize = 50;
        int offset = 20;
        int xStart = screenX - (diamondSize + offset);
        int yStart = 20;

        for (int i = 0; i < currentHealth; i++) {
            canvas.drawBitmap(
                    Bitmap.createScaledBitmap(diamondBitmap, diamondSize, diamondSize, false),
                    xStart - i * (diamondSize + offset),
                    yStart,
                    null
            );
        }
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void reset() {
        currentHealth = maxHealth;
    }
}
