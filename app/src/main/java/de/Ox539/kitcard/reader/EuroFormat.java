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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class EuroFormat {
	final DecimalFormat currencyFormat;

	static private Locale getLocale() {
		String language = Locale.getDefault().getLanguage();
		Locale locale;
		// All locales supported by this project need to be listed here.
		// The US have a special format for negative values in accouting.
		// Displaying it would confuse users that really just want to
		// have the UI in English, hence use the UK locale as fallback.
		if(language.equals("de"))
			locale = Locale.GERMANY;
		else
			locale = Locale.UK;
		return locale;
	}

	public EuroFormat() {
		this(getLocale());
	}

	public EuroFormat(Locale locale) {
		Currency currency = Currency.getInstance("EUR");

		currencyFormat = (DecimalFormat)NumberFormat.getCurrencyInstance(locale);
		currencyFormat.setCurrency(currency);
		currencyFormat.setMinimumFractionDigits(currency.getDefaultFractionDigits());
		currencyFormat.setMaximumFractionDigits(currency.getDefaultFractionDigits());

		DecimalFormatSymbols symbols = currencyFormat.getDecimalFormatSymbols();
		symbols.setCurrencySymbol(currency.getSymbol(Locale.GERMANY));
		currencyFormat.setDecimalFormatSymbols(symbols);
	}

	public String format(double value) {
		return currencyFormat.format(value);
	}

	public String formatPrefixed(double value) {
		// XXX: This works with English and German but probably not with
		// other locales. getPositivePrefix does contain the currency
		// and the sign rules cannot be fetched programatically.
		if(value < 0) {
			return "- " + currencyFormat.format(-value);
		} else if(value == 0) {
			return currencyFormat.format(value);
		} else {
			return "+ " + currencyFormat.format(value);
		}
	}
}
