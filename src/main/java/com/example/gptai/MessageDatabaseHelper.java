package com.example.gptai;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class MessageDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "message_database.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_MESSAGES = "messages";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CONVERSATION_ID = "conversation";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_SENDER = "sender";

    public MessageDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_MESSAGES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CONVERSATION_ID + " INTEGER, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_SENDER + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }


    public ArrayList<Message> getAllMessages() {
        ArrayList<Message> messageList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MESSAGES, null);

        int contentIndex = cursor.getColumnIndex(COLUMN_CONTENT);
        int sentByIndex = cursor.getColumnIndex(COLUMN_SENDER);
        int conversationIdIndex = cursor.getColumnIndex(COLUMN_CONVERSATION_ID);

        while (cursor.moveToNext()) {
            String content = cursor.getString(contentIndex);
            String sentBy = cursor.getString(sentByIndex);
            int conversationId = cursor.getInt(conversationIdIndex);

            Message message = new Message(content, sentBy, conversationId);
            messageList.add(message);
        }

        cursor.close();
        db.close();

        return messageList;
    }

    public void clearDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_MESSAGES, null, null);
        db.close();
    }



}