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

/**
 * MifareMad: Read a MifareClassic's Application Directory.
 *
 * Sector 0 of a MifareClassic card can contain an Application Directory
 * describing the sectors. This can be used to allocate sectors dynamically.
 * This class parses this directory and allows to query which sectors
 * are tagged with a specified application ID.
 *
 * @author Philipp Kern <pkern@debian.org>
 */


import java.io.IOException;
import java.util.ArrayList;

import android.nfc.tech.MifareClassic;
import android.util.Log;

class MifareApp {
	final int fcc;
	final int ac;

	MifareApp(byte fcc, byte ac) {
		this.fcc = MifareUtils.byteToInt(fcc);
		this.ac = MifareUtils.byteToInt(ac);
	}
}

public class MifareMad {
	private final MifareClassic card;

	public MifareMad(MifareClassic card) throws IOException {
		this.card = card;

		if(!card.isConnected()) {
			try {
				card.connect();
				readMad();
			} finally {
				card.close();
			}
		} else {
			readMad();
		}
	}

	private int mad_version;
	private boolean multi_app;
	private boolean mad_available;
	private int publisher_sector;
	private final ArrayList<MifareApp> app_list = new ArrayList<MifareApp>();

	private boolean readMad() throws IOException {
		if(!card.authenticateSectorWithKeyA(0, MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY)) {
			Log.e("KITCard Reader", "Authentication with sector 0 failed");
			return false;
		}

		byte[] mad_descriptor = card.readBlock(card.sectorToBlock(0) + 3);
		byte general_purpose_byte = mad_descriptor[9];

		mad_version = general_purpose_byte & 3;
		multi_app = (general_purpose_byte & (1 << 7)) != 0;
		mad_available = (general_purpose_byte & (1 << 6)) != 0;
		publisher_sector = 0;

		if (mad_available) {
			byte[] mad = new byte[16*2];
			byte[] data = card.readBlock(card.sectorToBlock(0) + 1);
			System.arraycopy(data, 0, mad, 0, 16);
			data = card.readBlock(card.sectorToBlock(0) + 2);
			System.arraycopy(data, 0, mad, 16, 16);

			publisher_sector = mad[1] & 0x3f;
			for(int a = 2; a < mad.length; a += 2) {
				app_list.add(new MifareApp(mad[a], mad[a + 1]));
			}
		}

		return true;
	}

	@SuppressWarnings("unused")
	private void logMad() {
		Log.d("KITCard Reader", String.format("MAD: version=%d, multi_app=%b, mad_available=%b, publisher_sector=%d", mad_version, multi_app, mad_available, publisher_sector));
		for(int i = 0; i < app_list.size(); ++i) {
			final MifareApp app = app_list.get(i);
			Log.d("KITCard Reader", String.format("MAD: sector %d: fcc=%d, ac=%d", i + 1, app.fcc, app.ac));
		}
	}

	public ArrayList<Integer> getSectorList(int fcc, int ac) {
		final ArrayList<Integer> result = new ArrayList<Integer>();
		for(int i = 0; i < app_list.size(); ++i) {
			final MifareApp app = app_list.get(i);
			if(app.fcc == fcc && app.ac == ac)
				result.add(i + 1);
		}
		return result;
	}
}

