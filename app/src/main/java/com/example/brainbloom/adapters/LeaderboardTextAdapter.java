package com.example.brainbloom.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.example.brainbloom.R;
import com.example.brainbloom.game.GameConstants;
import com.example.brainbloom.models.LeaderboardRecord;

import java.util.List;

public class LeaderboardTextAdapter {

    public void render(Context context, LinearLayout targetLayout, List<LeaderboardRecord> records, String gameMode) {
        targetLayout.removeAllViews();
        targetLayout.setOrientation(LinearLayout.VERTICAL);
        targetLayout.setClipChildren(false);
        targetLayout.setClipToPadding(false);

        Typeface arcadeFont = ResourcesCompat.getFont(context, R.font.arcade);

        addHeaderRow(context, targetLayout, arcadeFont, gameMode);

        if (records == null || records.isEmpty()) {
            TextView emptyText = createBaseText(context, arcadeFont, "No saved records yet.", 11);
            emptyText.setGravity(Gravity.START);
            emptyText.setPadding(0, 8, 0, 0);

            targetLayout.addView(emptyText, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            return;
        }

        for (int index = 0; index < records.size(); index++) {
            LeaderboardRecord record = records.get(index);

            String rank = (index + 1) + ".";
            String name;
            String score;
            String time;

            if (GameConstants.MODE_TWO_PLAYER.equals(record.getGameMode())) {
                name = record.getPlayerName(); // "P1 vs P2"
                score = record.getWinnerName(); // "S1 - S2"
                time = record.getDifficulty();  // reuse time column for difficulty or similar
            } else {
                name = safeName(record.getPlayerName());
                score = String.valueOf(record.getFinalScore());
                time = record.getTimeLeft() + "s";
            }

            addRecordRow(context, targetLayout, arcadeFont, rank, name, score, time);
        }
    }

    private void addHeaderRow(Context context, LinearLayout targetLayout, Typeface arcadeFont, String gameMode) {
        LinearLayout row = createRow(context);

        boolean isTwoPlayer = GameConstants.MODE_TWO_PLAYER.equals(gameMode);
        String nameHeader = isTwoPlayer ? "Matchup" : "Name";
        String scoreHeader = isTwoPlayer ? "Result" : "Score";
        String timeHeader = isTwoPlayer ? "Diff." : "Time";

        row.addView(createColumnText(context, arcadeFont, "Rank", 0.9f, Gravity.START, 10, true));
        row.addView(createColumnText(context, arcadeFont, nameHeader, 1.8f, Gravity.CENTER, 10, true));
        row.addView(createColumnText(context, arcadeFont, scoreHeader, 1.2f, Gravity.CENTER, 10, true));
        row.addView(createColumnText(context, arcadeFont, timeHeader, 1.1f, Gravity.END, 10, true));

        targetLayout.addView(row);
    }

    private void addRecordRow(Context context, LinearLayout targetLayout, Typeface arcadeFont,
                              String rank, String name, String score, String time) {
        LinearLayout row = createRow(context);

        row.addView(createColumnText(context, arcadeFont, rank, 0.9f, Gravity.START, 10, false));
        row.addView(createColumnText(context, arcadeFont, name, 1.8f, Gravity.CENTER, 10, false));
        row.addView(createColumnText(context, arcadeFont, score, 1.2f, Gravity.CENTER, 10, false));
        row.addView(createColumnText(context, arcadeFont, time, 1.1f, Gravity.END, 10, false));

        targetLayout.addView(row);
    }

    private LinearLayout createRow(Context context) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setClipChildren(false);
        row.setClipToPadding(false);
        row.setPadding(0, 2, 0, 2);

        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                30
        ));

        return row;
    }

    private TextView createColumnText(Context context, Typeface arcadeFont, String text,
                                      float weight, int gravity, int textSizeSp, boolean isHeader) {
        TextView textView = createBaseText(context, arcadeFont, text, textSizeSp);

        textView.setGravity(gravity);
        textView.setSingleLine(true);
        textView.setIncludeFontPadding(true);
        textView.setTypeface(arcadeFont, isHeader ? Typeface.BOLD : Typeface.NORMAL);
        textView.setPadding(2, 0, 2, 0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                weight
        );

        textView.setLayoutParams(params);

        return textView;
    }

    private TextView createBaseText(Context context, Typeface arcadeFont, String text, int textSizeSp) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextColor(0xFF111111);
        textView.setTextSize(textSizeSp);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setIncludeFontPadding(true);

        if (arcadeFont != null) {
            textView.setTypeface(arcadeFont);
        }

        return textView;
    }

    private String safeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Player";
        }

        name = name.trim();

        return name;
    }
}