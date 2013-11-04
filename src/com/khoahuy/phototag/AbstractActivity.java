package com.khoahuy.phototag;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.khoahuy.database.NFCItemProvider;
import com.khoahuy.phototag.statistic.BarGraph;
import com.khoahuy.phototag.statistic.DateBarGraph;
import com.khoahuy.phototag.statistic.MonthBarGraph;
import com.khoahuy.phototag.statistic.PieGraph;
import com.khoahuy.phototag.statistic.WeekBarGraph;
import com.khoahuy.phototag.statistic.YearBarGraph;
import com.khoahuy.utils.DateUtils;

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

public abstract class AbstractActivity extends Activity {

	protected NfcAdapter mAdapter;
	protected PendingIntent mPendingIntent;
	protected AlertDialog mDialog;
	protected String nfcid;
	protected NFCItemProvider nfcProvider;

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

		nfcProvider = new NFCItemProvider(this.getContentResolver());
	}

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
		case R.id.action_settings:
			// Intent intent = new Intent(this, SetPreferenceActivity.class);
			// startActivityForResult(intent, ACTION_PREFS);
			BarGraph bar2 = new MonthBarGraph();
			Calendar cal2 = Calendar.getInstance();
			Map<String, Integer> data2 = nfcProvider
					.getWaitingItemOfMonthStatistic(9, cal2.get(Calendar.YEAR));
			// data2 = StatisticUtils.normalizationDateData(data2);
			Intent barIntent2 = bar2.getIntent(this, data2);
			startActivity(barIntent2);
			return true;
		case R.id.action_statistic:
			BarGraph bar = new WeekBarGraph();
			Calendar cal = Calendar.getInstance();
			// cal.add(Calendar.DATE, -1);
			Map<String, Integer> data = nfcProvider
					.getWaitingItemOfWeekStatistic(cal.getTime());
			Intent barIntent = bar.getIntent(this, data);
			startActivity(barIntent);
			return true;
		case R.id.action_about:
			/*
			BarGraph bar1 = new DateBarGraph();
			Calendar cal1 = Calendar.getInstance();
			cal1.add(Calendar.DATE, -1);
			Map<String, Integer> data1 = nfcProvider
					.getWaitingItemOfDayStatistic(cal1.getTime());
			Intent barIntent1 = bar1.getIntent(this, data1);
			startActivity(barIntent1);*/
			BarGraph bar1 = new YearBarGraph();
			Calendar cal1 = Calendar.getInstance();
			cal1.add(Calendar.DATE, -1);
			Map<String, Integer> data1 = nfcProvider
					.getWaitingItemOfYearStatistic(2013);
			Intent barIntent1 = bar1.getIntent(this, data1);
			startActivity(barIntent1);
			return true;
		case R.id.action_test:
			try {
				int[] thresholdArray = { 60, 120, 180, 240, 300, 360, 420, 480,
						540, 600 };
				long from = DateUtils.getTimestampFirstDateOfMonth(0, 2013);
				long to = DateUtils.getTimestampEndDateOfMonth(11, 2013);
				Map<String, Integer> data4 = nfcProvider.getUsedItemStatistic(
						from, to, thresholdArray);
				PieGraph pie = new PieGraph();
				Intent pieIntent = pie.getIntent(this, data4);
				startActivity(pieIntent);
			} catch (Exception ex) {
				Log.e("Huy", ex.toString());
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
