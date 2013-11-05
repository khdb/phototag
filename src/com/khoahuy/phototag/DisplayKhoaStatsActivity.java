package com.khoahuy.phototag;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.achartengine.ChartFactory;
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

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;


public class DisplayKhoaStatsActivity extends FragmentActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khoa_stats);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        //actionBar.setHomeButtonEnabled(false);
     // Make sure we're running on Honeycomb or higher to use ActionBar APIs
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            // Show the Up button in the action bar.
//        	actionBar.setDisplayHomeAsUpEnabled(true);
//        }

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                //case 0:
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
                   // return new LaunchpadSectionFragment();

                default:
                    // The other sections of the app are dummy placeholders.
                    Fragment fragment = new ReportSectionFragment();
                    Bundle args = new Bundle();
                    args.putInt(ReportSectionFragment.ARG_SECTION_NUMBER, i + 1);
                    args.putInt(ReportSectionFragment.ARG_REPORT_TYPE, i);
                    fragment.setArguments(args);
                    
                   
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //return "Section " + (position + 1);
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
    
    

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class ReportSectionFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";
        public static final String ARG_REPORT_TYPE = "report_type"; //Day|Week|Month|Year 
        
        protected NFCItemProvider nfcProvider;
        

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_reports, container, false);
            Bundle args = getArguments();
//            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
//                    getString(R.string.dummy_section_text, args.getInt(ARG_SECTION_NUMBER)));
//            
            
            //test
            nfcProvider = new NFCItemProvider(getActivity().getContentResolver());
            Calendar cal = Calendar.getInstance();
            //args.getInt(ARG_REPORT_TYPE);
            
            switch (args.getInt(ARG_REPORT_TYPE)) {
            	case 0: 
            		BarGraph barD = new DateBarGraph(cal.getTime(), nfcProvider);
        			View dayView = barD.getView(getActivity());
        			        			
        			//ScrollView scrollView = new ScrollView(getActivity());
        			//scrollView.addView(dayView);
        			//rootView = scrollView;
        			
        			rootView = dayView;		
        			
        			break;
            	case 1:
            		BarGraph barW = new WeekBarGraph(cal.getTime(), nfcProvider);
        			View weekView = barW.getView(getActivity());
        			rootView = weekView;
        			break;
            	case 2:
        			BarGraph barM = new MonthBarGraph(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR), nfcProvider);
        			View monthView = barM.getView(getActivity());
        			rootView = monthView;
        			break;
            	case 3:
            		BarGraph barY = new YearBarGraph(cal.get(Calendar.YEAR), nfcProvider);
        			View yearView = barY.getView(getActivity());
        			rootView = yearView;
        			break;
            	case 4:
            		try {
        				int[] thresholdArray = { 60, 120, 180, 240, 300, 360, 420, 480,
        						540, 600 };
        				long from = DateUtils.getTimestampFirstDateOfMonth(0, 2013);
        				long to = DateUtils.getTimestampEndDateOfMonth(11, 2013);
        				Map<String, Integer> data4 = nfcProvider.getUsedItemStatistic(
        						from, to, thresholdArray);
        				PieGraph pie = new PieGraph();
        				View view = pie.getView(getActivity(), data4);
        				//return view;
        				rootView = view;
        				break;
        			} catch (Exception ex) {
        				Log.e("Huy", ex.toString());
        			}
            	default:
            		((TextView) rootView.findViewById(android.R.id.text1)).setText(
                            getString(R.string.dummy_section_text, args.getInt(ARG_SECTION_NUMBER)));
            }
            
			//end test
            
            return rootView;
        }
    }
}