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
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.widget.Toast;

// TODO: foreground NFC dispatch to LauncherActivity

public class LauncherActivity extends Activity {
	private NfcAdapter adapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String[][] techListsArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent("de.Ox539.kitcard.reader.TECH_DISCOVERED").addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        final IntentFilter intentFilter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        intentFiltersArray = new IntentFilter[] { intentFilter };
        techListsArray = new String[][] { new String[] { MifareClassic.class.getName() } };

        NfcManager manager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        adapter = manager.getDefaultAdapter();
        if (!adapter.isEnabled())
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.activate_nfc), Toast.LENGTH_LONG).show();
            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }
    }

    @Override
    protected void onResume() {
    	super.onResume();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_launcher, menu);
        return true;
    }
}
