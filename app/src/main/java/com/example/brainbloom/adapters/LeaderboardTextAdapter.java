package com.example.brainbloom.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.brainbloom.R;
import com.example.brainbloom.game.GameConstants;
import com.example.brainbloom.models.LeaderboardRecord;

import java.util.List;

public class LeaderboardTextAdapter {

    public void render(Context context, LinearLayout targetLayout, List<LeaderboardRecord> records, String gameMode) {
        targetLayout.removeAllViews();
        targetLayout.setOrientation(LinearLayout.VERTICAL);

        boolean isTwoPlayer = GameConstants.MODE_TWO_PLAYER.equals(gameMode);
        
        if (isTwoPlayer) {
            renderTwoPlayer(context, targetLayout, records);
        } else {
            renderSinglePlayer(context, targetLayout, records);
        }
    }

    private void renderSinglePlayer(Context context, LinearLayout targetLayout, List<LeaderboardRecord> records) {
        addSinglePlayerRow(context, targetLayout, "RANK", "NAME", "SCORE", "TIME", true);

        if (records == null || records.isEmpty()) {
            addEmptyState(context, targetLayout);
            return;
        }

        for (int index = 0; index < records.size(); index++) {
            LeaderboardRecord record = records.get(index);
            String rank = (index + 1) + ".";
            String name = record.getPlayerName();
            String score = String.valueOf(record.getFinalScore());
            String time = record.getTimeLeft() + "S";

            addSinglePlayerRow(context, targetLayout, rank, name, score, time, false);
        }
    }

    private void renderTwoPlayer(Context context, LinearLayout targetLayout, List<LeaderboardRecord> records) {
        // Headers: Matchup, Winner, Result, Diff.
        addTwoPlayerRow(context, targetLayout, "MATCHUP", "WINNER", "RESULT", "DIFF.", true);

        if (records == null || records.isEmpty()) {
            addEmptyState(context, targetLayout);
            return;
        }

        for (LeaderboardRecord record : records) {
            String matchup = record.getPlayerName(); 
            // In our latest WinnerResultFragment:
            // winnerName field stores "WINNER_NAME (SCORE1 - SCORE2)"
            // Let's parse it back or just use it.
            
            String winnerAndResult = record.getWinnerName(); 
            String winner = "N/A";
            String result = "0-0";
            
            if (winnerAndResult != null && winnerAndResult.contains("(") && winnerAndResult.contains(")")) {
                winner = winnerAndResult.substring(0, winnerAndResult.indexOf("(")).trim();
                result = winnerAndResult.substring(winnerAndResult.indexOf("(") + 1, winnerAndResult.indexOf(")")).trim();
            } else {
                winner = winnerAndResult;
            }
            
            String difficulty = record.getDifficulty().toUpperCase();
            
            addTwoPlayerRow(context, targetLayout, matchup, winner, result, difficulty, false);
        }
    }

    private void addEmptyState(Context context, LinearLayout targetLayout) {
        TextView emptyText = new TextView(context);
        emptyText.setText("No saved records yet.");
        emptyText.setPadding(0, 16, 0, 0);
        emptyText.setGravity(android.view.Gravity.CENTER);
        emptyText.setTypeface(androidx.core.content.res.ResourcesCompat.getFont(context, R.font.arcade));
        targetLayout.addView(emptyText);
    }

    private void addSinglePlayerRow(Context context, LinearLayout targetLayout, 
                                    String rank, String name, String score, String time, boolean isHeader) {
        
        View rowView = LayoutInflater.from(context).inflate(R.layout.item_leaderboard_row, targetLayout, false);
        
        TextView textRank = rowView.findViewById(R.id.textColRank);
        TextView textName = rowView.findViewById(R.id.textColName);
        TextView textScore = rowView.findViewById(R.id.textColScore);
        TextView textTime = rowView.findViewById(R.id.textColTime);

        textRank.setText(rank);
        textName.setText(name);
        textScore.setText(score);
        textTime.setText(time);

        if (isHeader) {
            textRank.setTypeface(textRank.getTypeface(), Typeface.BOLD);
            textName.setTypeface(textName.getTypeface(), Typeface.BOLD);
            textScore.setTypeface(textScore.getTypeface(), Typeface.BOLD);
            textTime.setTypeface(textTime.getTypeface(), Typeface.BOLD);
        }

        targetLayout.addView(rowView);
    }

    private void addTwoPlayerRow(Context context, LinearLayout targetLayout, 
                                 String matchup, String winner, String result, String difficulty, boolean isHeader) {
        
        View rowView = LayoutInflater.from(context).inflate(R.layout.item_leaderboard_2p_row, targetLayout, false);
        
        TextView textMatchup = rowView.findViewById(R.id.textColMatchup);
        TextView textWinner = rowView.findViewById(R.id.textColWinner);
        TextView textResult = rowView.findViewById(R.id.textColResult);
        TextView textDifficulty = rowView.findViewById(R.id.textColDifficulty);

        textMatchup.setText(matchup);
        textWinner.setText(winner);
        textResult.setText(result);
        textDifficulty.setText(difficulty);

        if (isHeader) {
            textMatchup.setTypeface(textMatchup.getTypeface(), Typeface.BOLD);
            textWinner.setTypeface(textWinner.getTypeface(), Typeface.BOLD);
            textResult.setTypeface(textResult.getTypeface(), Typeface.BOLD);
            textDifficulty.setTypeface(textDifficulty.getTypeface(), Typeface.BOLD);
        }

        targetLayout.addView(rowView);
    }
}
