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

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements Runnable {

    private Thread gameThread;
    private final SurfaceHolder surfaceHolder;
    private final Paint redPaint;
    private final Paint greenPaint;
    private Canvas canvas;
    private final Paint paint;
    private final Paint whitePaint;
    private final List<MotionEvent> eventQueue = new ArrayList<>();
    private final Object lock = new Object();
    private int score;
    private final int screenX;
    private final int screenY;
    private long lastTime;
    private int fps;
    private int frameCount = 0;
    private long lastFPSTime = System.currentTimeMillis();
    private Rect pauseButton, resumeButton;
    private GameState currentState;
    private volatile boolean isRunning = true;
    public GameView(Context context) {
        super(context);

        surfaceHolder = getHolder();
        paint = new Paint();
        redPaint = new Paint();
        redPaint.setColor(Color.RED);
        greenPaint = new Paint();
        greenPaint.setColor(Color.GREEN);
        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setTextSize(40);

        currentState = GameState.STARTING;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        screenX = point.x;
        screenY = point.y;
        initStateButtons();
    }

    public void initStateButtons() {
        int buttonSize = 100;
        pauseButton = new Rect(screenX - buttonSize, 0, screenX, buttonSize);
        resumeButton = new Rect(screenX - buttonSize, buttonSize, screenX, buttonSize * 2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStateButtons(x, y);
                if (currentState == GameState.PLAYING) {
                    synchronized (eventQueue) {
                        eventQueue.add(event);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    private void touchStateButtons(int x, int y) {
        if (pauseButton.contains(x, y)) {
            currentState = GameState.PAUSED;
        } else if (resumeButton.contains(x, y)) {
            currentState = GameState.PLAYING;
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

            switch (currentState) {
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

                case STARTING:
                    currentState = GameState.PLAYING;
                    break;

                case GAME_OVER:
                    isRunning = false;
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
        synchronized (eventQueue) {
            for (MotionEvent event : eventQueue) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    score++;
                }
            }
            eventQueue.clear();
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            if (canvas == null) return;
            //clear
            canvas.drawColor(Color.BLACK);

            drawUIElements(canvas);

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void drawUIElements(Canvas canvas) {

        //Pause&Resume
        canvas.drawRect(pauseButton, redPaint);
        canvas.drawRect(resumeButton, greenPaint);
        //Score
        canvas.drawText("Score: " + score, (float) screenX / 2 - 60, 50, whitePaint);
        //FPS
        int fps = fpsCounter();
        canvas.drawText("FPS: " + fps, 10, 50, whitePaint);
    }

    public int fpsCounter() {
        frameCount++;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFPSTime >= 1000) {
            fps = frameCount;
            frameCount = 0;
            lastFPSTime = currentTime;
        }
        return fps;
    }


    public void resume() {
        currentState = GameState.PLAYING;
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
        currentState = GameState.PAUSED;
        synchronized (lock) {
            lock.notifyAll();
        }
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public enum GameState {
        STARTING,
        PLAYING,
        PAUSED,
        GAME_OVER
    }
}