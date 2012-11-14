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

import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class ScanActivity extends Activity {
	private NfcAdapter adapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String[][] techListsArray;
    private EuroFormat euroFormat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        euroFormat = new EuroFormat();

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        final IntentFilter intentFilter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        intentFiltersArray = new IntentFilter[] { intentFilter };
        techListsArray = new String[][] { new String[] { MifareClassic.class.getName() } };

        setContentView(R.layout.activity_scan);
        resolveIntent(getIntent());
        setTitle(getResources().getString(R.string.app_name));
        NfcManager manager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        adapter = manager.getDefaultAdapter();
        if (!adapter.isEnabled())
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.activate_nfc), Toast.LENGTH_LONG).show();
            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_scan, menu);
        return true;
    }

    @Override
    protected void onResume() {
    	super.onResume();

    	if(balance == null)
    		updateBalance(0.0);
    	if(lastTransaction == null)
    		updateLastTransaction("");

    	// Debugger safeguard.
    	if(adapter == null)
    		return;

        adapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
    }

    @Override
    protected void onPause() {
    	super.onResume();

    	// Debugger safeguard.
    	if(adapter == null)
    		return;

    	adapter.disableForegroundDispatch(this);
    }

    private void resolveIntent(Intent intent) {
    	String action = intent.getAction();
    	if (!NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) &&
    			!action.equals("de.Ox539.kitcard.reader.TECH_DISCOVERED")) {
    		// Not a tag invocation.
        	return;
        }
    	// Reset the intent in case we're switched to landscape, for instance.
    	intent.setAction("android.intent.action.MAIN");
    	Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    	if(tag != null)
    		(new ReadCardTask(this)).execute(new Tag[] { tag });
    }

    public void updateCardNumber(String text) {
        cardNumber = text;
        TextView tv = (TextView)findViewById(R.id.cardno);
        if (tv == null) // optional
        	return;
        if (text != null) {
        	tv.setText(text);
        } else {
        	tv.setText("n/a");
        }
    }

    public void updateBalance(double value) {
    	updateBalance(euroFormat.format(value));
    }

    public void updateBalance(String text) {
    	TextView tv = (TextView)findViewById(R.id.balance);
    	tv.setText(text);
    	balance = text;
    }

    public void updateLastTransaction(double value) {
    	updateLastTransaction(euroFormat.formatPrefixed(value));
    }

    public void updateLastTransaction(String text) {
    	TextView tv = (TextView)findViewById(R.id.lasttransaction);
    	tv.setText(text);
    	lastTransaction = text;
    }

    public void updateCardType(CardType card_type) {
    	updateCardType(card_type.toTranslatedString(getResources()));
    }

    public void updateCardType(String text) {
    	cardType = text;
    	TextView tv = (TextView)findViewById(R.id.card_type);
    	if (tv == null) // optional
    		return;
    	tv.setText(text);
    }

    private String lastTransaction;
    private String balance;
    private String cardNumber;
    private String cardType;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	savedInstanceState.putString("CardNumber", cardNumber);
    	savedInstanceState.putString("Balance", balance);
    	savedInstanceState.putString("LastTransaction", lastTransaction);
    	savedInstanceState.putString("CardType", cardType);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
    	updateCardNumber(savedInstanceState.getString("CardNumber"));
    	updateBalance(savedInstanceState.getString("Balance"));
    	updateLastTransaction(savedInstanceState.getString("LastTransaction"));
    	updateCardType(savedInstanceState.getString("CardType"));
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }
    
    public void onHistoryClick(MenuItem item) {
    	startActivity(new Intent(this, HistoryActivity.class));
    }
}
