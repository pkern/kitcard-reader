package de.Ox539.kitcard.reader;

/**
 * Wallet: Read a MifareClassic tag, assuming a KITCard wallet.
 *
 * The first 3 blocks of sector 6 are accessed read-only. They contain
 * the current balance, the previous balance (the blocks are written in
 * an alternating fashion), transaction counters, a card type, and some
 * crypto keys.
 *
 * The card number can also be queried. It is stored as a string in the
 * first 12 bytes of sector 11.
 *
 * Based on reverse engineering work by Fabian Knittel <fabian@lettink.de>.
 *
 * @author Philipp Kern <pkern@debian.org>
 */

import java.io.IOException;
import java.util.Arrays;

import android.nfc.tech.MifareClassic;
import android.util.Log;

public class Wallet {
	private static final String LOG_TAG = "KITCard Reader";
	private static final byte[] CARD_NUMBER_KEY = {(byte)0x56, (byte)0x38, (byte)0x9f, (byte)0x80, (byte)0xa5, (byte)0xcf};
	private static final byte[] WALLET_KEY = MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY;
	private static final int WALLET_TCOUNT_KEY = 0x0404;
	private static final int WALLET_FCC = 23;
	private static final int WALLET_AC = 137;

	private final MifareClassic card;
	private String cardNumber;
	private double currentBalance;
	private double lastBalance;
	private CardType cardType;

	public Wallet(MifareClassic card) {
		this.card = card;
	}

	public boolean readCard() {
		try {
			try {
				if(!card.isConnected()) {
					card.connect();
					Log.d(LOG_TAG, "Connect to tag successful");
					if(card.authenticateSectorWithKeyA(11, CARD_NUMBER_KEY)) {
						parseCardNumber(card.readBlock(card.sectorToBlock(11)));
					} else {
						Log.e(LOG_TAG, "Authentication with sector 11 failed");
					}
					final MifareMad mad = new MifareMad(card);
					int sector = mad.getSectorList(WALLET_FCC, WALLET_AC).get(0);
					if(!card.authenticateSectorWithKeyA(sector, WALLET_KEY)) {
						Log.e(LOG_TAG, String.format("Authentication with sector %d (wallet) failed", sector));
						return false;
					}
					parseWalletData(
							card.readBlock(card.sectorToBlock(sector)),
							card.readBlock(card.sectorToBlock(sector) + 1),
							card.readBlock(card.sectorToBlock(sector) + 2)
							);
				}
			} finally {
				card.close();
			}
			return true;
		} catch(IOException e) {
			Log.e(LOG_TAG, "IOException caught: " + e.toString());
			return false;
		}
	}

	private static String bytesToString(byte[] ary) {
		final StringBuilder result = new StringBuilder();
		for(int i = 0; i < ary.length; ++i) {
			result.append(Character.valueOf((char)ary[i]));
		}
		return result.toString();
	}

	private void parseCardNumber(byte[] data) {
		final byte[] number = Arrays.copyOfRange(data, 0, 12);
		cardNumber = bytesToString(number);
	}

	/**
	 * Logs the content of a byte array as hex at debug level.
	 *
	 * @param block The block to print.
	 */
	private static void debugPrintBlock(byte[] block) {
		final StringBuilder builder = new StringBuilder("hex ");
		for(byte b : block) {
			final String hexPart = Integer.toHexString(MifareUtils.byteToInt(b)).toUpperCase();
			if(hexPart.length() < 2)
				builder.append("0");
			builder.append(hexPart);
			builder.append(" ");
		}

		Log.d(LOG_TAG, builder.toString());
	}

	private void parseWalletData(byte[] block1, byte[] block2, byte[] block3) {
		final byte[] data = new byte[16*3];
		System.arraycopy(block1, 0, data, 0, 16);
		System.arraycopy(block2, 0, data, 16, 16);
		System.arraycopy(block3, 0, data, 32, 16);
		debugPrintBlock(block1);
		debugPrintBlock(block2);
		debugPrintBlock(block3);

		// Decrypt in-place.
		int data_key = MifareUtils.byteToInt(data[41]);
		for(int i = 9; i <= 44; ++i) {
			data[i] = MifareUtils.intToByte(MifareUtils.byteToInt(data[i]) ^ data_key);
		}

		int value_key = MifareUtils.toUInt16BE(data[42], data[43]);
		int front_value = MifareUtils.toUInt16LE(data[26], data[27]) ^ value_key;
		int back_value = MifareUtils.toUInt16LE(data[31], data[32]) ^ value_key ^ 0x3b05;
		int front_count = MifareUtils.toUInt16BE(data[28], data[29]) ^ WALLET_TCOUNT_KEY;
		int back_count = MifareUtils.toUInt16BE(data[33], data[34]) ^ WALLET_TCOUNT_KEY ^ 0x3d3e;

		Log.d(LOG_TAG, String.format("Front count: %d; Back count: %d", front_count, back_count));

		if(front_count == back_count) {
			currentBalance = (double)front_value / 100;
			lastBalance = (double)back_value / 100;
		} else {
			currentBalance = (double)back_value / 100;
			lastBalance = (double)front_value / 100;
		}

		int status = MifareUtils.byteToInt(data[38]) ^ 0x3e;
		Log.d(LOG_TAG, "Status: " + Integer.toString(status));
		switch(status) {
		case 100:
			cardType = CardType.STUDENT;
			break;
		case 144:
			cardType = CardType.EMPLOYEE;
			break;
		case 201:
			cardType = CardType.GUEST;
			break;
		default:
			cardType = CardType.UNKNOWN;
			break;
		}
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public double getCurrentBalance() {
		return currentBalance;
	}

	public double getLastBalance() {
		return lastBalance;
	}

	public double getLastTransactionValue() {
		return currentBalance - lastBalance;
	}

	public CardType getCardType() {
		return cardType;
	}
}
