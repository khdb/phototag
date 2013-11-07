package com.khoahuy.phototag;

import android.os.Build;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.text.Html;
import android.view.Menu;
import android.widget.TextView;
   
public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        //actionBar.setHomeButtonEnabled(false);
       // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
        	actionBar.setDisplayHomeAsUpEnabled(true);
        }
        
        TextView foo = (TextView)findViewById(R.id.about_text);
        foo.setText(Html.fromHtml(getString(R.string.about_html_content)));
        
	}

	//There are no submenu for about activity yet planned
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.about, menu);
//		return true;
//	}

}
