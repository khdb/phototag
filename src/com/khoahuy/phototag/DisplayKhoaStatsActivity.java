package com.khoahuy.phototag;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import com.khoahuy.database.NFCItemProvider;
import com.khoahuy.phototag.statistic.BarGraph;
import com.khoahuy.phototag.statistic.DateBarGraph;
import com.khoahuy.phototag.statistic.MonthBarGraph;
import com.khoahuy.phototag.statistic.PieGraph;
import com.khoahuy.phototag.statistic.WeekBarGraph;
import com.khoahuy.phototag.statistic.YearBarGraph;
import com.khoahuy.utils.DateUtils;
import com.khoahuy.utils.FileUtils;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayKhoaStatsActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the three primary sections of the app. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	AppSectionsPagerAdapter mAppSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will display the three primary sections of the
	 * app, one at a time.
	 */
	ViewPager mViewPager;

	// used for datepicker
	static int year;
	static int month;
	static int day;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_khoa_stats);

		// Create the adapter that will return a fragment for each of the three
		// primary sections
		// of the app.
		mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();

		// Specify that the Home/Up button should not be enabled, since there is
		// no hierarchical
		// parent.
		// actionBar.setHomeButtonEnabled(false);
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// // Show the Up button in the action bar.
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// }

		// Specify that we will be displaying tabs in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Set up the ViewPager, attaching the adapter and setting up a listener
		// for when the
		// user swipes between sections.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						// When swiping between different app sections, select
						// the corresponding tab.
						// We can also use ActionBar.Tab#select() to do this if
						// we have a reference to the
						// Tab.
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter.
			// Also specify this Activity object, which implements the
			// TabListener interface, as the
			// listener for when this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mAppSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.reports, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_date_select:
			try {
				// Toast.makeText(this, "Date pcicker selected",
				// Toast.LENGTH_SHORT)
				// .show();
				// DialogFragment newFragment = new DatePickerFragment();
				// newFragment.show(this.getFragmentManager(), "datePicker");

				// showDatePickerDialog(this.getCurrentFocus());

				showDatePicker();

				// Toast.makeText(getApplicationContext(), "Date selected is:"+
				// day
				// +"-"+ month+"-"+ year, Toast.LENGTH_LONG).show();

				// Intent shareIntent = new Intent(this, shareIntent.class);
				// startActivity(shareIntent);

			} catch (Exception ex) {

			}
			return true;
		case R.id.share_reports:
			try {
				Toast.makeText(this, "Share reports selected",
						Toast.LENGTH_SHORT).show();
				sendReportEmail();
			} catch (Exception ex) {

			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void sendReportEmail() throws Exception {
		Calendar cal = Calendar.getInstance();
		NFCItemProvider nfcProvider = new NFCItemProvider(
				this.getContentResolver());

		// Take day graph image
		ArrayList<Uri> attachments = new ArrayList<Uri>();
		BarGraph barD = new DateBarGraph(cal.getTime(), nfcProvider);
		BarGraph barW = new WeekBarGraph(cal.getTime(), nfcProvider);
		BarGraph barM = new MonthBarGraph(cal.get(Calendar.MONTH),
				cal.get(Calendar.YEAR), nfcProvider);
		BarGraph barY = new YearBarGraph(cal.get(Calendar.YEAR), nfcProvider);

		int[] thresholdArray = { 60, 120, 180, 240, 300, 360, 420, 480, 540,
				600 };
		long from = DateUtils.getTimestampFirstDateOfMonth(cal.get(Calendar.MONTH), 2013);
		long to = DateUtils.getTimestampEndDateOfMonth(cal.get(Calendar.MONTH), 2013);
		PieGraph pie = new PieGraph(from, to, thresholdArray, nfcProvider);

		attachments.add(saveBarGraphToFile((GraphicalView) barD.getView(this), "DayStats"));
		attachments.add(saveBarGraphToFile((GraphicalView) barW.getView(this), "WeekStats"));
		attachments.add(saveBarGraphToFile((GraphicalView) barM.getView(this), "MonthStats"));
		attachments.add(saveBarGraphToFile((GraphicalView) barY.getView(this), "YearStats"));
		attachments.add(saveBarGraphToFile((GraphicalView) pie.getView(this), "OtherStats"));

		String content = "Hello sir, \n\n";
		content += "Thống kê số lượng: \n";
		content += "\t" + barD.getTextStatistic() + "\n";
		content += "\t" + barW.getTextStatistic() + "\n";
		content += "\t" + barM.getTextStatistic() + "\n";
		content += "\t" + barY.getTextStatistic() + "\n";
		content += pie.getTextStatistic();
		content += "\n";
		content += "Cheer,\n";

		Intent emailIntent = new Intent(
				android.content.Intent.ACTION_SEND_MULTIPLE);
		emailIntent.setType("application/image");
		// emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new
		// String[]{"thanhhuy89vn@gmail.com", "kk.a@gmail.com"});
		emailIntent.putExtra(
				android.content.Intent.EXTRA_SUBJECT,
				"Photo Tag - Thống kê theo mốc ngày "
						+ DateUtils.getDateString(cal.getTime(), "dd/MM/yyyy"));
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);

		emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
				attachments);
		startActivity(Intent.createChooser(emailIntent, "Send mail..."));

	}

	private Uri saveBarGraphToFile(GraphicalView v, String name) {
		try {
			v.setDrawingCacheEnabled(true);
			v.layout(0, 0, 1280, 590);
			v.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
			v.buildDrawingCache(true);
			Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
			v.setDrawingCacheEnabled(false);
			File out = FileUtils.createImageFile(
					getString(R.string.album_name), name);
			if (out.exists())
				out.delete();
			FileOutputStream outStream = new FileOutputStream(out);
			b.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
			outStream.flush();
			outStream.close();
			return Uri.fromFile(out);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the primary sections of the app.
	 */
	public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

		public List<Fragment> fragmentList;

		public AppSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			fragmentList = new ArrayList<Fragment>();
			for (int i = 0; i < getCount(); i++) {
				Fragment fragment = new ReportSectionFragment();
				Bundle args = new Bundle();
				args.putInt(ReportSectionFragment.ARG_SECTION_NUMBER, i + 1);
				args.putInt(ReportSectionFragment.ARG_REPORT_TYPE, i);
				fragment.setArguments(args);
				fragmentList.add(fragment);
			}
		}

		@Override
		public Fragment getItem(int i) {
			return fragmentList.get(i);
		}

		@Override
		public int getCount() {
			return 5;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			// return "Section " + (position + 1);
			switch (position) {
			case 0:
				return "Ngày";
			case 1:
				return "Tuần";
			case 2:
				return "Tháng";
			case 3:
				return "Năm";
			case 4:
				return "Khác";
			default:
				return "Error";
			}

		}

	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

	private void showDatePicker() {
		DatePickerFragment date = new DatePickerFragment();

		/**
		 * Set Call back to capture selected date
		 */
		date.setCallBack(ondate);
		date.show(getFragmentManager(), "Date Picker");
	}

	OnDateSetListener ondate = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			Toast.makeText(
					DisplayKhoaStatsActivity.this,
					String.valueOf(selectedYear) + "-"
							+ String.valueOf(selectedMonth) + "-"
							+ String.valueOf(selectedDay), Toast.LENGTH_LONG)
					.show();

			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

		}

	};

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class ReportSectionFragment extends Fragment {

		public static final String ARG_SECTION_NUMBER = "section_number";
		public static final String ARG_REPORT_TYPE = "report_type"; // Day|Week|Month|Year
		protected NFCItemProvider nfcProvider;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_section_reports,
					container, false);
			Bundle args = getArguments();
			// ((TextView) rootView.findViewById(android.R.id.text1)).setText(
			// getString(R.string.dummy_section_text,
			// args.getInt(ARG_SECTION_NUMBER)));
			//

			// test
			nfcProvider = new NFCItemProvider(getActivity()
					.getContentResolver());
			Calendar cal = Calendar.getInstance();
			// args.getInt(ARG_REPORT_TYPE);

			switch (args.getInt(ARG_REPORT_TYPE)) {
			case 0:
				BarGraph barD = new DateBarGraph(cal.getTime(), nfcProvider);
				View dayView = barD.getView(getActivity());

				// ScrollView scrollView = new ScrollView(getActivity());
				// scrollView.addView(dayView);
				// rootView = scrollView;

				rootView = dayView;

				break;
			case 1:
				BarGraph barW = new WeekBarGraph(cal.getTime(), nfcProvider);
				View weekView = barW.getView(getActivity());
				rootView = weekView;
				break;
			case 2:
				BarGraph barM = new MonthBarGraph(cal.get(Calendar.MONTH),
						cal.get(Calendar.YEAR), nfcProvider);
				View monthView = barM.getView(getActivity());
				rootView = monthView;
				break;
			case 3:
				BarGraph barY = new YearBarGraph(cal.get(Calendar.YEAR),
						nfcProvider);
				View yearView = barY.getView(getActivity());
				rootView = yearView;
				break;
			case 4:
				try {
					int[] thresholdArray = { 60, 120, 180, 240, 300, 360, 420,
							480, 540, 600 };
					long from = DateUtils
							.getTimestampFirstDateOfMonth(10, 2013);
					long to = DateUtils.getTimestampEndDateOfMonth(10, 2013);
					PieGraph pie = new PieGraph(from, to, thresholdArray,
							nfcProvider);
					View otherView = pie.getView(getActivity());
					// return view;
					rootView = otherView;
					break;
				} catch (Exception ex) {
					Log.e("Huy", ex.toString());
				}
			default:
				((TextView) rootView.findViewById(android.R.id.text1))
						.setText(getString(R.string.dummy_section_text,
								args.getInt(ARG_SECTION_NUMBER)));
			}

			// end test

			return rootView;
		}
	}

	// khoa
	public static class DatePickerFragment extends DialogFragment {
		// implements DatePickerDialog.OnDateSetListener {
		// implements DatePickerDialog.OnDateSetListener
		OnDateSetListener ondateSet;

		// constructor
		public DatePickerFragment() {
		}

		// Callback to be set in parent activity class
		public void setCallBack(OnDateSetListener ondate) {
			ondateSet = ondate;
		}

		private int year, month, day;

		@Override
		public void setArguments(Bundle args) {
			super.setArguments(args);
			year = args.getInt("year");
			month = args.getInt("month");
			day = args.getInt("day");
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			// return new DatePickerDialog(getActivity(), this, year, month,
			// day);
			return new DatePickerDialog(getActivity(), ondateSet, year, month,
					day);
			// return new DatePickerDialog(getActivity(), (OnDateSetListener)
			// this, year, month, day);
		}

		// not needed to be implemented here since it will be set in the parent
		// activity class
		// public void onDateSet(DatePicker view, int selectedYear, int
		// selectedMonth, int selectedDay) {
		// // Do something with the date chosen by the user
		// year = selectedYear;
		// month = selectedMonth;
		// day = selectedDay;
		//
		// }

	}

}