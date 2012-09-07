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
