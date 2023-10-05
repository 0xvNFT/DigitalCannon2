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
    private volatile boolean isPlaying;
    private final Object lock = new Object();
    private int score;
    private final int screenX;
    private final int screenY;
    private volatile boolean isPaused = false;
    private long lastTime;
    private int fps;
    private int frameCount = 0;
    private long lastFPSTime = System.currentTimeMillis();
    private Rect pauseButton, resumeButton;

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
                if (!isPaused) {
                    synchronized (eventQueue) {
                        eventQueue.add(event);
                    }
                }

                touchStateButtons(x, y);
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
            isPlaying = false;
            isPaused = true;
        } else if (resumeButton.contains(x, y)) {
            isPlaying = true;
            isPaused = false;
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }



    @Override
    public void run() {
        lastTime = System.nanoTime();
        long targetTime = 1000 / 60;
        long startTime, waitTime, elapsedTime;

        while (true) {
            if (isPlaying) {
                startTime = System.nanoTime();

                draw();
                update();

                elapsedTime = System.nanoTime() - startTime;
                waitTime = targetTime - elapsedTime / 1000000;

                if (waitTime > 0) {
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else if (isPaused) {
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {

                break;
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
        isPlaying = true;
        isPaused = false;

        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new Thread(this);
            gameThread.start();
        }

        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public void pause() {
        isPlaying = false;
        isPaused = true;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        gameThread = null;
    }

}

