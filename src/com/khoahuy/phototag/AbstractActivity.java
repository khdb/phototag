package com.khoahuy.phototag;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.khoahuy.database.NFCItemLocalProvider;
import com.khoahuy.database.NFCItemNetworkProvider;
import com.khoahuy.phototag.model.NFCItem;
import com.khoahuy.phototag.statistic.BarGraph;
import com.khoahuy.phototag.statistic.DateBarGraph;
import com.khoahuy.phototag.statistic.MonthBarGraph;
import com.khoahuy.phototag.statistic.PieGraph;
import com.khoahuy.phototag.statistic.WeekBarGraph;
import com.khoahuy.phototag.statistic.YearBarGraph;
import com.khoahuy.utils.DateUtils;
import com.khoahuy.utils.FileUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class AbstractActivity extends Activity {

	protected NfcAdapter mAdapter;
	protected PendingIntent mPendingIntent;
	protected AlertDialog mDialog;
	protected String nfcid;
	protected NFCItemLocalProvider nfcProvider;
	protected NFCItemNetworkProvider nfcProvider2;

	private static final int ACTION_PREFS = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null)
				.create();

		mAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mAdapter == null) {
			showMessage(R.string.error, R.string.no_nfc);
			finish();
			return;
		}

		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		nfcProvider = new NFCItemLocalProvider(this.getContentResolver());
		nfcProvider2 = new NFCItemNetworkProvider(this);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i("Huy", "on save instance state of Abstract");
        outState.putString("nfcid", nfcid);
	};
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.i("Huy", "on restore instance state of Abstract");
		nfcid = savedInstanceState.getString("nfcid");
	};


	@Override
	protected void onResume() {
		super.onResume();
		if (mAdapter != null) {
			if (!mAdapter.isEnabled()) {
				showWirelessSettingsDialog();
			}
			mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		if (mAdapter != null) {
			mAdapter.disableForegroundDispatch(this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_statistic:
			try {
				Intent khdIntent = new Intent(this, ReportsActivity.class);
				startActivity(khdIntent);

			} catch (Exception ex) {

			}
			return true;
		case R.id.action_about:
			try {
				Intent aboutIntent = new Intent(this, AboutActivity.class);
				startActivity(aboutIntent);

			} catch (Exception ex) {

			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		setIntent(data);
		switch (requestCode) {
		case ACTION_PREFS: {
			Log.i("Huy", "ACTION_PREFS return");
			break;
		}
		}
	}
	
	protected void checkoutNFCItem(String nfcid) {
		/*NFCItem nfcItem = nfcProvider.findWaitingItem(nfcid);
		if (nfcItem != null) {
			if (nfcProvider.deleteWaitingItem(nfcid)) {
				// Delete image file:
				//renameImageFileToTemp(nfcItem.getImage());
				nfcProvider2.addUsedItem(nfcItem);
			}
		}*/
		NFCItem nfcItem = nfcProvider2.findWaitingItem(nfcid);
		nfcProvider2.addUsedItem(nfcItem);
	}
	
	private void renameImageFileToTemp(String filePath) {
		try {
			File file = new File(filePath);
			File tempFile = FileUtils
					.createImageFileTemp(getString(R.string.album_name));
			if (tempFile.exists())
				tempFile.delete();
			file.renameTo(tempFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void showMessage(int title, int message) {
		mDialog.setTitle(title);
		mDialog.setMessage(getText(message));
		mDialog.show();
	}

	protected void showWirelessSettingsDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.nfc_disabled);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						Intent intent = new Intent(
								Settings.ACTION_WIRELESS_SETTINGS);
						startActivity(intent);
					}
				});
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						finish();
					}
				});
		builder.create().show();
		return;
	}

	@Override
	public void onNewIntent(Intent intent) {
		nfcid = "";
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			nfcid = this.ByteArrayToHexString(intent
					.getByteArrayExtra(NfcAdapter.EXTRA_ID));
			Log.i("Huy", "NDEF DISCOVERED = " + nfcid);

		} else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			nfcid = this.ByteArrayToHexString(intent
					.getByteArrayExtra(NfcAdapter.EXTRA_ID));
			Log.i("Huy", "TAG DISCOVERED = " + nfcid);

		} else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			nfcid = this.ByteArrayToHexString(getIntent().getByteArrayExtra(
					NfcAdapter.EXTRA_ID));
			Log.i("Huy", "TECH DISCOVERED = " + nfcid);
		}
		setIntent(intent);
		processNfcID();
		// resolveIntent(intent);
	}

	private String ByteArrayToHexString(byte[] inarray) {
		int i, j, in;
		String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
				"B", "C", "D", "E", "F" };
		String out = "";

		for (j = 0; j < inarray.length; ++j) {
			in = (int) inarray[j] & 0xff;
			i = (in >> 4) & 0x0f;
			out += hex[i];
			i = in & 0x0f;
			out += hex[i];
		}
		return out;
	}

	protected abstract void processNfcID();

}
