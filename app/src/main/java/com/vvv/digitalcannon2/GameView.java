package com.vvv.digitalcannon2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class GameView extends SurfaceView implements Runnable {
    private Thread gameThread;
    private final SurfaceHolder surfaceHolder;
    private final Paint redPaint, greenPaint, whitePaint;
    private Canvas canvas;
    private final Object lock = new Object();
    private Rect pauseButton, resumeButton;
    private final FPSCounter fpsCounter;
    private volatile boolean isRunning = true;
    private final GameStateManager gameStateManager;
    private final EventManager eventManager;
    private final int screenX;
    private final int screenY;
    private final Background backgroundBitmap;
    private final Cannon cannon;
    private final CannonBallManager cannonBallManager;
    private final CannonBallManager leftMiniCannonBallManager;
    private final CannonBallManager rightMiniCannonBallManager;
    private final MiniCannon leftMiniCannon;
    private final MiniCannon rightMiniCannon;
    private final TargetBoxManager targetBoxManager;
    private final int endLineY;
    public GameView(Context context) {
        super(context);

        surfaceHolder = getHolder();

        redPaint = new Paint();
        redPaint.setColor(Color.RED);
        greenPaint = new Paint();
        greenPaint.setColor(Color.GREEN);
        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setTextSize(40);

        fpsCounter = new FPSCounter();
        gameStateManager = new GameStateManager();
        eventManager = new EventManager();

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        screenX = point.x;
        screenY = point.y;

        backgroundBitmap = new Background(context, R.drawable.game_bg, screenX, screenY);
        cannon = new Cannon(context, R.drawable.cannon, screenX, screenY);

        int offsetXLeft = -screenX / 3;
        int offsetXRight = screenX / 3;
        float miniCannonScale = screenY / 3000f;

        endLineY = screenY - cannon.getHeight() + 100;

        leftMiniCannon = new MiniCannon(context, R.drawable.cannon, screenX, screenY, offsetXLeft, miniCannonScale, cannon);
        rightMiniCannon = new MiniCannon(context, R.drawable.cannon, screenX, screenY, offsetXRight, miniCannonScale, cannon);

        cannonBallManager = new CannonBallManager(context, R.drawable.cannonball, 1500, screenX, 1);
        int[] targetBoxResIds = {R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four, R.drawable.five};
        targetBoxManager = new TargetBoxManager(context, targetBoxResIds, screenX, screenY, endLineY);

        leftMiniCannonBallManager = new CannonBallManager(context, R.drawable.mini_cannonball, 2000, screenX, miniCannonScale);
        rightMiniCannonBallManager = new CannonBallManager(context, R.drawable.mini_cannonball, 2000, screenX, miniCannonScale);
        initStateButtons();

    }

    private void initStateButtons() {
        int buttonSize = 100;
        pauseButton = new Rect(screenX - buttonSize, 0, screenX, buttonSize);
        resumeButton = new Rect(screenX - buttonSize, buttonSize, screenX, buttonSize * 2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (gameStateManager.getCurrentState() == GameStateManager.GameState.PLAYING) {
            eventManager.addEvent(event);
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float angle = cannon.getAngle();
            Point tip = cannon.getTipCoordinates();
            cannonBallManager.tryFireCannonBall(angle, tip, 15);
            leftMiniCannonBallManager.tryFireMiniCannonBall(leftMiniCannon, 12);
            rightMiniCannonBallManager.tryFireMiniCannonBall(rightMiniCannon, 12);
        }
        touchStateButtons(x, y);
        return true;
    }

    private void touchStateButtons(int x, int y) {
        if (pauseButton.contains(x, y)) {
            gameStateManager.setCurrentState(GameStateManager.GameState.PAUSED);
        } else if (resumeButton.contains(x, y)) {
            gameStateManager.setCurrentState(GameStateManager.GameState.PLAYING);
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

    @Override
    public void run() {
        long targetUpdateTime = 1000 / 60;
        long targetRenderTime = 1000 / 120;
        long startTime, elapsedUpdateTime = 0, elapsedRenderTime = 0;
        long currentTime, lastTime = System.currentTimeMillis();

        while (isRunning) {
            currentTime = System.currentTimeMillis();
            long deltaTime = currentTime - lastTime;

            elapsedUpdateTime += deltaTime;
            elapsedRenderTime += deltaTime;

            switch (gameStateManager.getCurrentState()) {
                case PLAYING:
                    if (elapsedUpdateTime >= targetUpdateTime) {
                        update();
                        elapsedUpdateTime -= targetUpdateTime;
                    }
                    if (elapsedRenderTime >= targetRenderTime) {
                        draw();
                        elapsedRenderTime -= targetRenderTime;
                    }
                    break;
                case PAUSED:
                    synchronized (lock) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
            }

            lastTime = currentTime;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        eventManager.processEvents();
        cannon.update();

        leftMiniCannon.update();
        rightMiniCannon.update();

        targetBoxManager.updateAll();
        cannonBallManager.updateAll(screenX, endLineY);
        leftMiniCannonBallManager.updateAll(screenX, endLineY);
        rightMiniCannonBallManager.updateAll(screenX, endLineY);

        for (CannonBall cannonBall : cannonBallManager.cannonBalls) {
            if (cannonBall.fired) {
                if (targetBoxManager.checkCollision(cannonBall)) {

                    cannonBall.fired = false;
                }
            }
        }

    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            if (canvas == null) return;
            canvas.drawColor(Color.BLACK);

            backgroundBitmap.draw(canvas);
            cannon.draw(canvas);
            leftMiniCannon.draw(canvas);
            rightMiniCannon.draw(canvas);
            cannonBallManager.drawAll(canvas);

            leftMiniCannonBallManager.drawAll(canvas);
            rightMiniCannonBallManager.drawAll(canvas);

            Point tip = cannon.getTipCoordinates();
            float circleRadius = 5.0f;
            canvas.drawCircle(tip.x, tip.y, circleRadius, redPaint);
            Point leftTip = leftMiniCannon.getTipCoordinates();
            canvas.drawCircle(leftTip.x, leftTip.y, circleRadius, redPaint);
            Point rightTip = rightMiniCannon.getTipCoordinates();
            canvas.drawCircle(rightTip.x, rightTip.y, circleRadius, redPaint);

            targetBoxManager.drawAll(canvas);

            Paint linePaint = new Paint();
            linePaint.setColor(Color.WHITE);
            linePaint.setStrokeWidth(5);
            canvas.drawLine(0, endLineY, screenX, endLineY, linePaint);
            drawUIElements(canvas);

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawUIElements(Canvas canvas) {
        canvas.drawRect(pauseButton, redPaint);
        canvas.drawRect(resumeButton, greenPaint);

        int score = eventManager.getScore();
        canvas.drawText("Score: " + score, (float) screenX / 2 - 60, 50, whitePaint);

        int fps = fpsCounter.countFPS();
        canvas.drawText("FPS: " + fps, 10, 50, whitePaint);
    }

    public void resume() {
        isRunning = true;
        gameStateManager.setCurrentState(GameStateManager.GameState.PLAYING);
        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new Thread(this);
            gameThread.start();
        }
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public void pause() {
        isRunning = false;
        gameStateManager.setCurrentState(GameStateManager.GameState.PAUSED);
        try {
            if (gameThread != null && gameThread.isAlive()) {
                gameThread.join();
                gameThread = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
