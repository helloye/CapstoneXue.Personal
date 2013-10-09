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
		
		String ECfileName, CEfileName;
		File logFileHandle;
		BufferedReader reader;
		logView = (TextView) findViewById(R.id.logTextView);
		
		ECfileName = "EnglishChinese.log.txt";
		CEfileName = "ChineseEnglish.log.txt";
		String read, lastLine = null;
		int deckCount = 0;
		int learnedItems = 0;
		int totalDailyLearned = 0;
		
		
		logFileHandle = new File(MainActivity.filesDir, ECfileName);
		
		//String parsing starts below!!
		
		try {
			reader = new BufferedReader(new FileReader(logFileHandle));
			String[] initialParse = reader.readLine().split("[\\t ]+");
			deckCount++;
			int firstDay = Integer.parseInt(initialParse[0].substring(0, initialParse[0].indexOf("/", 0)));
			int dayCount = firstDay;
			while((read = reader.readLine()) != null && dayCount < firstDay+7){ //Will stop if it's more than 7 days
				lastLine = read;
				String[] parsedItems = lastLine.split("[\\t ]+");
				totalDailyLearned += Integer.parseInt(parsedItems[5]);
				totalDailyLearned += Integer.parseInt(parsedItems[12]);
			    learnedItems = Integer.parseInt(parsedItems[5]) + Integer.parseInt(parsedItems[12]);
				dayCount = Integer.parseInt(parsedItems[0].substring(0, parsedItems[0].indexOf("/", 0))); // Set the next day
			    deckCount++;
			}
			
		//logView.setText(lastLine + "\n");
			
		
		//Sets the very first day
		
		/*for(int i=0; i<parsedItems.length; i++)
			logView.append(parsedItems[i] + "\n");*/
		
		
		//logView.append("\n" + day);
			
		
		logView.setText("English-Chinese Learning Progress\n");
		//logView.append(lastLine);
		logView.append("\n+ You have completed: " + deckCount + " decks.\n");
		logView.append("\n+ You have learned: " + learnedItems + " items.\n");
		logView.append("\n+ An average of: " + String.format("%.2f", (double)totalDailyLearned/deckCount) + " items daily.\n");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logView.setText("No progress on English-Chinese decks.\n");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logView.append("\n-----------------------------------------------------------\n\n");
		
		//
		//CHINESE - ENGLISH SECTION, RESET ALL VALUE
		//
		
		deckCount = 0;
		learnedItems = 0;
		totalDailyLearned = 0;
		
		logFileHandle = new File(MainActivity.filesDir, CEfileName);
		try {
			reader = new BufferedReader(new FileReader(logFileHandle));
			String[] initialParse = reader.readLine().split("[\\t ]+");
			deckCount++;
			int firstDay = Integer.parseInt(initialParse[0].substring(0, initialParse[0].indexOf("/", 0)));
			int dayCount = firstDay;
			while((read = reader.readLine()) != null && dayCount < firstDay+7){ //Will stop if it's more than 7 days
				lastLine = read;
				String[] parsedItems = lastLine.split("[\\t ]+");
				totalDailyLearned += Integer.parseInt(parsedItems[5]);
				totalDailyLearned += Integer.parseInt(parsedItems[12]);
				learnedItems = Integer.parseInt(parsedItems[5]) + Integer.parseInt(parsedItems[12]);
				dayCount = Integer.parseInt(parsedItems[0].substring(0, parsedItems[0].indexOf("/", 0))); // Set the next day
			    deckCount++;
			}
			
		
		logView.append("Chinese-English Learning Progress\n");
		logView.append("\n+ You have completed: " + deckCount + " decks.\n");
		logView.append("\n+ You have learned: " + learnedItems + " items.\n");
		logView.append("\n+ An average of: " + String.format("%.2f", (double)totalDailyLearned/deckCount) + " items daily.\n");
		
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logView.append("\nNo progress on Chinese-English decks.\n");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
