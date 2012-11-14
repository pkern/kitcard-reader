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

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.support.v4.app.NavUtils;

public class HistoryActivity extends Activity {
	SimpleCursorAdapter adapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setupListView();

    }

    private void setupListView() {
        Cursor cursor = CardHistory.getScanResults(new CardHistory(this).getReadableDatabase());
        initAdapter(cursor);
        ListView listview = (ListView)findViewById(R.id.transaction_list);
        listview.setAdapter(adapter);    	
    }
    
    private void initAdapter(Cursor cursor) {
    	String[] fromColumns = {
    			CardHistory.COLUMNS.CARD_NUMBER,
    			CardHistory.COLUMNS.CARD_VALUE
    	};
    	int[] toViews = {
    			R.id.item_card_number,
    			R.id.item_card_value
    	};
    	adapter = new SimpleCursorAdapter(this, R.layout.item_history, cursor, fromColumns, toViews, 0);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_history, menu);
        return true;
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	setupListView();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
