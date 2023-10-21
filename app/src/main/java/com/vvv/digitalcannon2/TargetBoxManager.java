package com.vvv.digitalcannon2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class TargetBoxManager {
    private final List<TargetBox> targetBoxes = new ArrayList<>();
    private final int screenY;
    private final int boxWidth = 50;
    private final int boxHeight = 50;
    private final int screenX;
    private final Random random = new Random();
    private final int endLineY;

    public TargetBoxManager(Context context, int[] drawableResIds, int screenX, int screenY, int endLineY) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.endLineY = endLineY;
        for (int resId : drawableResIds) {
            int delay = random.nextInt(100);
            int x, y;
            do {
                x = random.nextInt(screenX - boxWidth);
                y = -boxHeight - random.nextInt(500);
            } while (doesOverlap(x, y, boxWidth, boxHeight, null));
            targetBoxes.add(new TargetBox(context, resId, x, y, 1, delay));
        }
    }

    public void updateAll() {
        for (TargetBox targetBox : new ArrayList<>(targetBoxes)) {
            targetBox.update();
            if (targetBox.y > endLineY) {
                int x, y;
                do {
                    x = random.nextInt(screenX - boxWidth);
                    y = -boxHeight;
                } while (doesOverlap(x, y, boxWidth, boxHeight, targetBox));
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

    private boolean doesOverlap(int x, int y, int width, int height, TargetBox exclude) {
        Rect newRect = new Rect(x, y, x + width, y + height);
        for (TargetBox box : targetBoxes) {
            if (box == exclude) {
                continue;
            }
            Rect existingRect = new Rect(box.x, box.y, box.x + width, box.y + height);
            if (newRect.intersect(existingRect)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkCollision(CannonBall cannonBall, int[] drawableResIds, Context context) {
        Rect cannonBallRect = new Rect(cannonBall.x, cannonBall.y,
                cannonBall.x + cannonBall.bitmap.getWidth(),
                cannonBall.y + cannonBall.bitmap.getHeight());

        for (Iterator<TargetBox> iterator = targetBoxes.iterator(); iterator.hasNext(); ) {
            TargetBox targetBox = iterator.next();
            Rect targetBoxRect = new Rect(targetBox.x, targetBox.y,
                    targetBox.x + boxWidth,
                    targetBox.y + boxHeight);

            if (Rect.intersects(cannonBallRect, targetBoxRect)) {
                iterator.remove();

                int delay = random.nextInt(100);
                int x, y;
                do {
                    x = random.nextInt(screenX - boxWidth);
                    y = -boxHeight - random.nextInt(500);
                } while (doesOverlap(x, y, boxWidth, boxHeight, null));

                int resId = drawableResIds[random.nextInt(drawableResIds.length)];
                targetBoxes.add(new TargetBox(context, resId, x, y, 1, delay));

                return true;
            }
        }
        return false;
    }


}
