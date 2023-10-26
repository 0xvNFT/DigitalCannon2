package com.vvv.digitalcannon2;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class GameOverDialog extends Dialog {

    private final OnRetryListener onRetryListener;

    public GameOverDialog(Context context, OnRetryListener onRetryListener) {
        super(context);
        this.onRetryListener = onRetryListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_game_over);

        TextView gameOverText = findViewById(R.id.game_over_text);
        gameOverText.setText("Game Over");

        Button retryButton = findViewById(R.id.retry_button);
        retryButton.setOnClickListener(v -> {
            if (onRetryListener != null) {
                onRetryListener.onRetry();
            }
            dismiss();
        });
    }

    public interface OnRetryListener {
        void onRetry();
    }
}

