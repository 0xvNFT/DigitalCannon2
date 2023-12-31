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

public class GameViewTest extends SurfaceView implements Runnable {
    private final SurfaceHolder surfaceHolder;
    private final Paint redPaint, greenPaint, whitePaint;
    private final Object lock = new Object();
    private final FPSCounter fpsCounter;
    private final GameStateManager gameStateManager;
    private final EventManager eventManager;
    private Thread gameThread;
    private Canvas canvas;
    private final int screenX;
    private final int screenY;
    private Rect pauseButton, resumeButton;
    private volatile boolean isRunning = true;
    private long resumeDisplayTime = 0;
    private int resumeTextAlpha = 255;

    public GameViewTest(Context context) {
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
        touchStateButtons(x, y);
        return true;
    }

    private void touchStateButtons(int x, int y) {
        if (pauseButton.contains(x, y)) {
            gameStateManager.setCurrentState(GameStateManager.GameState.PAUSED);
        } else if (resumeButton.contains(x, y)) {
            gameStateManager.setCurrentState(GameStateManager.GameState.PLAYING);
            resumeDisplayTime = System.currentTimeMillis();
            resumeTextAlpha = 255;
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

            if (gameStateManager.getCurrentState() == GameStateManager.GameState.PAUSED) {
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (elapsedUpdateTime >= targetUpdateTime) {
                    update();
                    elapsedUpdateTime -= targetUpdateTime;
                }
                if (elapsedRenderTime >= targetRenderTime) {
                    draw();
                    elapsedRenderTime -= targetRenderTime;
                }
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
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            if (canvas == null) return;
            canvas.drawColor(Color.BLACK);

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
        gameStateManager.setCurrentState(GameStateManager.GameState.PLAYING);
        isRunning = true;
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
        // try {
        //     gameThread.join();
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
    }
}
