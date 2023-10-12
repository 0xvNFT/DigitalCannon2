package com.vvv.digitalcannon2;

import android.content.Context;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

public class TargetBoxManager {
    private final List<TargetBox> targetBoxes = new ArrayList<>();
    private final int screenY;

    public TargetBoxManager(Context context, int[] drawableResIds, int screenY) {
        this.screenY = screenY;
        for (int resId : drawableResIds) {
            targetBoxes.add(new TargetBox(context, resId, 5));
        }
    }

    public void updateAll() {
        for (TargetBox targetBox : targetBoxes) {
            targetBox.update();
            if (targetBox.y > screenY) {
                // Reset position here
            }
        }
    }

    public void drawAll(Canvas canvas) {
        for (TargetBox targetBox : targetBoxes) {
            targetBox.draw(canvas);
        }
    }
}


