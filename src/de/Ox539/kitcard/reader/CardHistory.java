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
import android.database.Cursor;
import android.database.DatabaseUtils;
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
				"card_value INTEGER)" // positive value in Euro cents
				);
		db.execSQL(
				"CREATE TABLE cards (" +
				"card_number TEXT, " + // ASCII representation of the number
				"card_type TEXT)" // STUDENT/EMPLOYEE/GUEST/UNKNOWN
				);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	public static void insertScanResult(SQLiteDatabase db, String cardNumber, CardType cardType, double cardValue) {
		db.beginTransaction();
		try {
			ContentValues values;
			
			long count = DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM cards WHERE card_number = ?", new String[] {cardNumber});
			if (count == 0) {
				// Insert card.
				values = new ContentValues();
				values.put("card_number", cardNumber);
				values.put("card_type", cardType.name());
				db.insert("cards", null, values);
			}
			
			// Insert scan result record.
			values = new ContentValues();
			values.put("card_scan_datetime", (String)null); // TODO
			values.put("card_number", cardNumber);
			values.put("card_value", (int)(cardValue * 100));
			db.insert("card_history", null, values);
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	private static final String[] cardHistoryColumns = new String[] {"card_scan_datetime", "card_number", "card_type", "card_value"};
	private static final String[] cardsColumns = new String[] {"card_number", "card_type"};
	
	public static Cursor getScanResults(SQLiteDatabase db) {
		return db.query("card_history",	cardHistoryColumns,	null, null,	null, null,	"card_scan_datetime");
	}
	
	public static Cursor getCards(SQLiteDatabase db) {
		return db.query("cards", cardsColumns, null, null, null, null, "card_number");
	}
}