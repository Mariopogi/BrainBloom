package com.example.brainbloom.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.brainbloom.models.LeaderboardRecord;

import java.util.List;

public class LeaderboardTextAdapter {

    public void render(Context context, LinearLayout targetLayout, List<LeaderboardRecord> records) {
        targetLayout.removeAllViews();

        addRow(context, targetLayout, "Rank     Name          Score     Time", true);

        if (records.isEmpty()) {
            addRow(context, targetLayout, "No saved records yet.", false);
            return;
        }

        for (int index = 0; index < records.size(); index++) {
            LeaderboardRecord record = records.get(index);
            String name = record.getPlayerName();
            if (name.length() > 10) {
                name = name.substring(0, 10);
            }

            String row = (index + 1) + ".     " + name + "     " +
                    record.getFinalScore() + "     " + record.getTimeLeft() + "s";
            addRow(context, targetLayout, row, false);
        }
    }

    private void addRow(Context context, LinearLayout layout, String text, boolean header) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setTextSize(header ? 13 : 14);
        textView.setTextColor(0xFF111111);
        textView.setTypeface(Typeface.MONOSPACE, header ? Typeface.BOLD : Typeface.NORMAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                header ? 36 : 42
        );
        layout.addView(textView, params);
    }
}
