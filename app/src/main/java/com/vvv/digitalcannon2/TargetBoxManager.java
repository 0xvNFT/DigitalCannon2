package com.vvv.digitalcannon2;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;

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
    private final Health health;
    private final Context context;
    private final Handler mainHandler;
    GameView gameView;

    public TargetBoxManager(Context context, int[] drawableResIds, int screenX, int screenY, int endLineY, Health health, Handler mainHandler) {
        this.context = context;
        this.screenX = screenX;
        this.screenY = screenY;
        this.endLineY = endLineY;
        this.health = health;
        this.mainHandler = mainHandler;

        for (int i = 0; i < drawableResIds.length; i++) {
            int delay = random.nextInt(100);
            int x, y;
            int resId = drawableResIds[i];
            int level = i + 1;
            do {
                x = random.nextInt(screenX - boxWidth);
                y = -boxHeight - random.nextInt(500);
            } while (doesOverlap(x, y, boxWidth, boxHeight, null));
            targetBoxes.add(new TargetBox(context, resId, x, y, 2, delay, level));
        }
    }

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

    public void updateAll() {

        for (TargetBox targetBox : new ArrayList<>(targetBoxes)) {
            targetBox.update();
            if (targetBox.hitCooldown > 0) {
                targetBox.hitCooldown--;
            }
            if (targetBox.y > endLineY) {
                health.decrement();
                if (health.isDepleted()) {
                    if (gameView != null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                GameOverDialog gameOverDialog = new GameOverDialog(context, new GameOverDialog.OnRetryListener() {
                                    @Override
                                    public void onRetry() {
                                        gameView.resetGameState();
                                    }
                                });
                                gameOverDialog.show();
                            }
                        });
                    }
                }
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

    public void checkCollision(CannonBall cannonBall, int[] targetBoxResIds, Context context, EventManager eventManager) {
        Rect cannonBallRect = new Rect(cannonBall.x, cannonBall.y,
                cannonBall.x + cannonBall.bitmap.getWidth(),
                cannonBall.y + cannonBall.bitmap.getHeight());

        Iterator<TargetBox> iterator = targetBoxes.iterator();
        while (iterator.hasNext()) {
            TargetBox targetBox = iterator.next();

            if (targetBox.hitCooldown > 0) {
                continue;
            }

            Rect targetBoxRect = new Rect(targetBox.x, targetBox.y,
                    targetBox.x + targetBox.bitmap.getWidth(),
                    targetBox.y + targetBox.bitmap.getHeight());

            if (Rect.intersects(cannonBallRect, targetBoxRect)) {
                int dx = Math.abs(cannonBall.x - targetBox.x);
                int dy = Math.abs(cannonBall.y - targetBox.y);

                if (dx > dy) {
                    cannonBall.velocityX = -cannonBall.velocityX;

                    while (Rect.intersects(cannonBallRect, targetBoxRect)) {
                        cannonBall.x += (int) Math.signum(cannonBall.velocityX);
                        cannonBall.y += (int) Math.signum(cannonBall.velocityY);
                        cannonBallRect.left = cannonBall.x;
                        cannonBallRect.right = cannonBall.x + cannonBall.bitmap.getWidth();
                        cannonBallRect.top = cannonBall.y;
                        cannonBallRect.bottom = cannonBall.y + cannonBall.bitmap.getHeight();
                    }
                } else {
                    cannonBall.velocityY = -cannonBall.velocityY;

                    while (Rect.intersects(cannonBallRect, targetBoxRect)) {
                        cannonBall.y += cannonBall.velocityY;
                        cannonBallRect.top = cannonBall.y;
                        cannonBallRect.bottom = cannonBall.y + cannonBall.bitmap.getHeight();
                    }
                }
                eventManager.incrementScore(1);

                targetBox.currentLevel--;
                if (targetBox.currentLevel <= 0) {
                    iterator.remove();
                    respawnTargetBox(targetBoxResIds, context, eventManager);
                    eventManager.incrementScore(1);

                } else if (targetBox.currentLevel <= targetBoxResIds.length) {
                    targetBox.bitmap = BitmapFactory.decodeResource(context.getResources(), targetBoxResIds[targetBox.currentLevel - 1]);
                }

                targetBox.hitCooldown = 5;

                return;
            }
        }
    }


    public void respawnTargetBox(int[] drawableResIds, Context context, EventManager eventManager) {
        int x, y;
        int level = random.nextInt(drawableResIds.length) + 1;
        int resId = drawableResIds[level - 1];
        do {
            x = random.nextInt(screenX - boxWidth);
            y = -boxHeight - random.nextInt(500);
        } while (doesOverlap(x, y, boxWidth, boxHeight, null));

        targetBoxes.add(new TargetBox(context, resId, x, y, 2, random.nextInt(100), level));

        eventManager.incrementScore(1);

    }
}
