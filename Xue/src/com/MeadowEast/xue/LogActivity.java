package com.MeadowEast.xue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class LogActivity extends Activity {

	TextView logView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log);
		// Show the Up button in the action bar.
		setupActionBar();
		
		String fileName;
		File logFileHandle;
		BufferedReader reader;
		logView = (TextView) findViewById(R.id.logTextView);
		
		if(MainActivity.mode.equals("ec"))
			fileName = "EnglishChinese.log.txt";
		else
			fileName = "ChineseEnglish.log.txt";
		
		logFileHandle = new File(MainActivity.filesDir, fileName);
		
		//String parsing starts below!!
		String read, lastLine = null;
		int deckCount = 0;
		int learnedItems = 0;
		int totalDailyLearned = 0;
		
		try {
			reader = new BufferedReader(new FileReader(logFileHandle));
			while((read = reader.readLine()) != null){
				lastLine = read;
				totalDailyLearned =+ getLearned(lastLine);
				deckCount++;
			}
		learnedItems = getLearned(lastLine); 
		logView.setText("Last line in log file:\n" + lastLine + "\n");
		logView.append("---------------------------------------------------------\n");
		logView.append("\n+ You have completed: " + deckCount + " decks.\n");
		logView.append("\n+ You have learned: " + learnedItems + " items.\n");
		logView.append("\n+ An average of: " + String.format("%.2f", (double)totalDailyLearned/deckCount) + " items daily.\n");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logView.setText("Log File Not Found!!");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private int getLearned(String line) {
		String learnedString;
		int totalLearned = 0;
		
		//Parsing and adding total count for level 2.
		learnedString = line.substring(line.indexOf("+")+2, line.indexOf("=")-1);
		totalLearned+=Integer.parseInt(learnedString);
		
		//Parsing and count for levels 3 and 4
		learnedString = line.substring(line.lastIndexOf("=")+2, line.indexOf(" ", line.lastIndexOf("=")+2));
		totalLearned+=Integer.parseInt(learnedString);
		
		return totalLearned;
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
