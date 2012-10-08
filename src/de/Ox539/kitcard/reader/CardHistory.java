/*
 * This file is part of KITCard Reader.
 * Ⓒ 2012 Philipp Kern <phil@philkern.de>
 *
 * KITCard Reader is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * KITCard Reader is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KITCard Reader. If not, see <http://www.gnu.org/licenses/>.
 */

package de.Ox539.kitcard.reader;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CardHistory extends SQLiteOpenHelper {
	private static final int DB_VERSION = 1;

	public CardHistory(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	public CardHistory(Context context) {
		super(context, "card_history", null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(
				"CREATE TABLE card_history (" +
				"card_scan_datetime TEXT, " + // ISO8601 format in UTC
				"card_number TEXT, " + // ASCII representation of the number
				"card_type TEXT, " + // STUDENT/EMPLOYEE/GUEST/UNKNOWN
				"card_value INTEGER)" // positive value in Euro cents
				);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	public static void insertScanResult(SQLiteDatabase db, String cardNumber, CardType cardType, double cardValue) {
		db.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			values.put("card_scan_datetime", (String)null); // TODO
			values.put("card_number", cardNumber);
			values.put("card_type", cardType.name());
			values.put("card_value", (int)(cardValue * 100));
			db.insert("card_history", null, values);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
}