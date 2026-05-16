package com.example.brainbloom.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.brainbloom.R;
import com.example.brainbloom.models.LeaderboardRecord;
import com.example.brainbloom.models.Question;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BrainBloomDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "brainbloom_game.db";
    private static final int DATABASE_VERSION = 1;

    private static BrainBloomDatabaseHelper instance;
    private final Context appContext;

    public static synchronized BrainBloomDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new BrainBloomDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private BrainBloomDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.appContext = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createQuestionTable(db);
        createLeaderboardTable(db);
        executeSeedFile(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS leaderboard");
        db.execSQL("DROP TABLE IF EXISTS questions");
        onCreate(db);
    }

    private void createQuestionTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS questions (" +
                "question_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "book_number INTEGER NOT NULL, " +
                "category TEXT NOT NULL, " +
                "difficulty TEXT NOT NULL, " +
                "question_text TEXT NOT NULL, " +
                "choice_a TEXT NOT NULL, " +
                "choice_b TEXT NOT NULL, " +
                "choice_c TEXT NOT NULL, " +
                "choice_d TEXT NOT NULL, " +
                "correct_answer TEXT NOT NULL" +
                ")");
    }

    private void createLeaderboardTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS leaderboard (" +
                "record_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "player_name TEXT NOT NULL, " +
                "game_mode TEXT NOT NULL, " +
                "book_completed_count INTEGER DEFAULT 0, " +
                "final_score INTEGER NOT NULL, " +
                "time_left INTEGER DEFAULT 0, " +
                "highest_combo INTEGER DEFAULT 0, " +
                "difficulty TEXT, " +
                "winner_name TEXT, " +
                "date_played TEXT NOT NULL" +
                ")");
    }

    private void executeSeedFile(SQLiteDatabase db) {
        if (getQuestionCount(db) > 0) {
            return;
        }

        db.beginTransaction();
        try {
            InputStream inputStream = appContext.getResources().openRawResource(R.raw.brainbloom_seed);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder statement = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    continue;
                }

                statement.append(trimmed);

                if (trimmed.endsWith(";")) {
                    db.execSQL(statement.toString());
                    statement.setLength(0);
                }
            }

            reader.close();
            db.setTransactionSuccessful();
        } catch (Exception error) {
            throw new RuntimeException("Failed to seed Brain Bloom questions.", error);
        } finally {
            db.endTransaction();
        }
    }

    private int getQuestionCount(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM questions", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public List<Question> getRandomQuestions(int bookNumber, String difficulty, int limit) {
        List<Question> questions = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT question_id, book_number, category, difficulty, question_text, choice_a, choice_b, choice_c, choice_d, correct_answer " +
                        "FROM questions WHERE book_number = ? AND difficulty = ? ORDER BY RANDOM() LIMIT ?",
                new String[]{String.valueOf(bookNumber), difficulty, String.valueOf(limit)}
        );

        while (cursor.moveToNext()) {
            Question question = new Question(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9)
            );
            questions.add(question);
        }

        cursor.close();
        return questions;
    }

    public void saveLeaderboardRecord(LeaderboardRecord record) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("player_name", record.getPlayerName());
        values.put("game_mode", record.getGameMode());
        values.put("book_completed_count", record.getBookCompletedCount());
        values.put("final_score", record.getFinalScore());
        values.put("time_left", record.getTimeLeft());
        values.put("highest_combo", record.getHighestCombo());
        values.put("difficulty", record.getDifficulty());
        values.put("winner_name", record.getWinnerName());
        values.put("date_played", record.getDatePlayed());

        db.insert("leaderboard", null, values);
    }

    public List<LeaderboardRecord> getTopRecords(String gameMode, int limit) {
        List<LeaderboardRecord> records = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT record_id, player_name, game_mode, book_completed_count, final_score, time_left, highest_combo, difficulty, winner_name, date_played " +
                        "FROM leaderboard WHERE game_mode = ? ORDER BY final_score DESC, time_left DESC, highest_combo DESC LIMIT ?",
                new String[]{gameMode, String.valueOf(limit)}
        );

        while (cursor.moveToNext()) {
            records.add(new LeaderboardRecord(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getInt(4),
                    cursor.getInt(5),
                    cursor.getInt(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9)
            ));
        }

        cursor.close();
        return records;
    }

    public void clearLeaderboard(String gameMode) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("leaderboard", "game_mode = ?", new String[]{gameMode});
    }
}
