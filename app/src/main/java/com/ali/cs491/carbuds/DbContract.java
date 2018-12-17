package com.ali.cs491.carbuds;

import android.provider.BaseColumns;

public final class DbContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DbContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "message";
        public static final String COLUMN_MESSAGE = "message_body";
        public static final String COLUMN_SENDER_ID = "sender_id";
        public static final String COLUMN_EXCHANGE_NAME = "exchange";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_SENDER_NAME = "sender_name";
    }
}
