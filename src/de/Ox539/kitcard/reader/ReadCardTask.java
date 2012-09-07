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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

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
    	final NumberFormat form = DecimalFormat.getCurrencyInstance(Locale.GERMANY);
   		mActivity.updateCardNumber(wallet.getCardNumber());
		mActivity.updateBalance(form.format(wallet.getCurrentBalance()));
		mActivity.updateLastTransaction(form.format(wallet.getLastTransactionValue()));
		mActivity.updateCardType(wallet.getCardType());
    }
}
