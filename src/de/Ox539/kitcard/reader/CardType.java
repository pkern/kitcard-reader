package de.Ox539.kitcard.reader;

import android.content.res.Resources;

enum CardType {
	STUDENT,
	EMPLOYEE,
	GUEST,
	UNKNOWN;

	public String toString(Resources res) {
		switch(this) {
		case STUDENT:
			return res.getString(R.string.student);
		case EMPLOYEE:
			return res.getString(R.string.employee);
		case GUEST:
			return res.getString(R.string.guest);
		default:
			return res.getString(R.string.unknown);
		}
	}
}
