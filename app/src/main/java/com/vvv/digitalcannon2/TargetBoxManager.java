package com.vvv.digitalcannon2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TargetBoxManager {
    private final List<TargetBox> targetBoxes = new ArrayList<>();
    private final int screenY;
    private final int boxWidth = 50;
    private final int boxHeight = 50;
    private final int screenX;
    private final Random random = new Random();

    public TargetBoxManager(Context context, int[] drawableResIds, int screenX, int screenY) {
        this.screenX = screenX;
        this.screenY = screenY;
        for (int resId : drawableResIds) {
            int delay = random.nextInt(100);
            int x, y;
            do {
                x = random.nextInt(screenX - boxWidth);
                y = -boxHeight - random.nextInt(500);
            } while (doesOverlap(x, y, boxWidth, boxHeight));
            targetBoxes.add(new TargetBox(context, resId, x, y, 3, delay));
        }
    }

    public void updateAll() {
        for (TargetBox targetBox : new ArrayList<>(targetBoxes)) {
            targetBox.update();
            if (targetBox.y > screenY) {
                int x, y;
                do {
                    x = random.nextInt(screenX - boxWidth);
                    y = -boxHeight;
                } while (doesOverlap(x, y, boxWidth, boxHeight));
                targetBox.x = x;
                targetBox.y = y;
                targetBox.delay = random.nextInt(100);
            }
        }
    }

    public void drawAll(Canvas canvas) {
        for (TargetBox targetBox : targetBoxes) {
            targetBox.draw(canvas);
        }
    }

    private boolean doesOverlap(int x, int y, int width, int height) {
        Rect newRect = new Rect(x, y, x + width, y + height);
        for (TargetBox box : targetBoxes) {
            Rect existingRect = new Rect(box.x, box.y, box.x + width, box.y + height);
            if (newRect.intersect(existingRect)) {
                return true;
            }
        }
        return false;
    }

}
