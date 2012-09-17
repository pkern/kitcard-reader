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
 * ReadCardTask: Read an NFC tag using the Wallet class asynchronously.
 *
 * This class provides the glue calling the Wallet class and passing
 * the information back to the Android UI layer. Detailed error
 * information is not provided yet.
 *
 * @author Philipp Kern <pkern@debian.org>
 */

import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;
import android.widget.Toast;

public class ReadCardTask extends AsyncTask<Tag, Integer, Wallet> {
	private MainActivity mActivity;

	public ReadCardTask(MainActivity activity) {
		super();
		this.mActivity = activity;
	}

	protected Wallet doInBackground(Tag... tags) {
		MifareClassic card = MifareClassic.get(tags[0]);
		if(card == null)
			return null;

		final Wallet wallet = new Wallet(card);
		if(!wallet.readCard())
			return null;
		return wallet;
	}

    protected void onPostExecute(Wallet wallet) {
    	if(wallet == null) {
    		// read failed
    		Toast.makeText(mActivity, mActivity.getResources().getString(R.string.kitcard_read_failed), Toast.LENGTH_LONG).show();
    		return;
    	}

   		mActivity.updateCardNumber(wallet.getCardNumber());
		mActivity.updateBalance(wallet.getCurrentBalance());
		mActivity.updateLastTransaction(wallet.getLastTransactionValue());
		mActivity.updateCardType(wallet.getCardType());
    }
}
