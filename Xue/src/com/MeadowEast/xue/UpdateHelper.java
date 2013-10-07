
package com.MeadowEast.xue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
 
import android.app.Activity;
import android.content.Context;

import android.util.Log;
 
public class UpdateHelper extends Activity {
 
	static final String TAG = "Xue UpdateCardsActivity";
    // button to show progress dialog
    //Button btnShowProgress;

    
    private static String strVocabDestPath = MainActivity.filesDir + MainActivity.gStrVocabFileName;


    public void cancelUpdate( ) {
		
    	deleteTemp();
    	// finish the activity
		finish();
       	
	 }
    
    @SuppressWarnings("deprecation")
	public boolean shouldUpdate( Context context, String filePath, String url ) {
    	
    	// Get the last time since the app was updated 
    	Date lastUpdated = readLastUpdated();
    	// Add 2 weeks to see if it should be updated
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    	Calendar c = Calendar.getInstance();
    	if ( lastUpdated == null ) {
    		lastUpdated = new Date( 1970, 1, 1 );
    	}
    	
    	c.setTime( lastUpdated ); // Now use today date.
    	c.add(Calendar.DATE, 14); // Adding 2 weeks, 14 days
    	Date dayCanUpdate = c.getTime();
    	
    	// Compare it to today, if the time is not greater than 2 weeks, don't update
    	Date now = new Date();
    	if ( !now.after(dayCanUpdate) )
    		return false;
    	
    	// Check if there is a network connection
    	NetworkManager networkManager = NetworkManager.getInstance();
    	if ( !networkManager.isOnline(context) )
    		return false;
    	
    	// Check the last modified date for the file on the webserver, update if it's newer
    	File file = new File( filePath );
    	URLConnection connection = null;
    	long updateModified = 0;
    	try {
    		connection = new URL( url ).openConnection();
    		updateModified = connection.getLastModified();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e( TAG, e.getMessage());
		}
    	if ( updateModified <= file.lastModified() )
    		return false;
    	
    	return true;
    }
    
    public void deleteTemp() {
    	// delete the tmp file
    	File fileTmpVocab = new File( strVocabDestPath + ".tmp" );
    	if ( fileTmpVocab.exists() ) {
	    	if ( !fileTmpVocab.delete() )
	    		Log.d (TAG, "Update Cancelled, unable to delete tmp file.  Not created?");
    	}
    }
    
    public void writeLastUpdated() throws IOException {
		
		File modfiedobjectfile = new File( MainActivity.filesDir, ".date.ser");
		FileOutputStream modfiedobjectFOS = new FileOutputStream(modfiedobjectfile);
		ObjectOutputStream modfiedobjectOOS = new ObjectOutputStream(modfiedobjectFOS);
		
		Log.d(TAG, "writing objects");
		Date d = new Date();
		modfiedobjectOOS.writeObject( d );
		modfiedobjectFOS.close();
	}
	
	@SuppressWarnings("unchecked")
	private Date readLastUpdated() {
		FileInputStream modfiedobjectFIS;
		ObjectInputStream modfiedobjectOIS;
		Date d = null;
		try {
			File modfiedobjectfile = new File(MainActivity.filesDir, ".date.ser" );
			modfiedobjectFIS = new FileInputStream(modfiedobjectfile);
			modfiedobjectOIS = new ObjectInputStream(modfiedobjectFIS);
		} catch (Exception e) {
			Log.d(TAG, "No last vocab updated file to read.");
			return d;
		} 
		try {
			d = (Date)modfiedobjectOIS.readObject();
			modfiedobjectFIS.close();
			return d;
		} catch (Exception e) { Log.d(TAG, "Error in readStatus"); return null; }
	}


}