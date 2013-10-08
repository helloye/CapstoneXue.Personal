package com.MeadowEast.xue;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements OnClickListener {

	Button ecButton, ceButton, ecLogButton, ceLogButton, exitButton;
	public static File filesDir;
	public static String mode;
	static final String TAG = "XUE MainActivity";
	public static String gStrVocabFileName;
	
	//private Button 			_btnUpdateVocab;
	private NetworkManager 	_networkManager = null;
	private SoundManager 	_soundManager = null;
	private static boolean 	_bSDCardOkay;
	boolean fDesiresUpdate = false;
	
	// Progress Dialog
    private ProgressDialog pDialog;
    
    private UpdateHelper _updateCards;

    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0; 
 
    // File url to download
    private static String _strVocabURL = "http://www.meadoweast.com/capstone/vocabUTF8.txt";
    private String _strVocabDestPath;
	
    Thread quittingThread = new Thread(){
        @Override
       public void run() {
            try {
               Thread.sleep(2000); // As I am using LENGTH_SHORT in Toast
               MainActivity.this.finish();
           } catch (Exception e) {
               e.printStackTrace();
           }
        }  
      };
      
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  
        
        ecButton   = (Button) findViewById(R.id.ecButton);
        ceButton   = (Button) findViewById(R.id.ceButton);
        exitButton = (Button) findViewById(R.id.exitButton);
        ecLogButton = (Button) findViewById(R.id.ecLogButton);
        ceLogButton = (Button) findViewById(R.id.ceLogButton);
    	ecButton.setOnClickListener(this);
    	ceButton.setOnClickListener(this);
    	ecLogButton.setOnClickListener(this);
    	ceLogButton.setOnClickListener(this);
    	exitButton.setOnClickListener(this);
    	
    	
    	// Make sure the SD Card is available and writeable, if not, kill the app
    	checkSDCard();
    	
    	// construct the path to the card vocabulary
        File sdCard = Environment.getExternalStorageDirectory();
		filesDir = new File ( sdCard.getAbsolutePath() + "/Android/data/com.MeadowEast.xue/files" );
		gStrVocabFileName = "vocab.txt";
		_strVocabDestPath = filesDir + gStrVocabFileName;
		File fileVocab = new File( filesDir.getPath() + gStrVocabFileName );
		
		//get the file from the resource folder and copy it to the app dir if it doesn't exist already
		if ( !fileVocab.exists() ) {
			try {
				Common.CopyStreamToFile( this.getResources().openRawResource( R.raw.vocab ), 
												filesDir, gStrVocabFileName );
			}
			catch( Exception ex ) {
				Log.d( TAG, ex.getMessage() );
			}
    	}
		Log.d(TAG, "xxx filesDir="+filesDir);
		
		// Check if there is a newer version available
		fDesiresUpdate = false;
		try {
			UpdateVocab();
		} catch (Exception e) {
			Log.e(TAG, "Update failed.");
			Toast.makeText(this,"Attempt to update the cards failed!", Toast.LENGTH_LONG).show();
		} 
		
		Log.d(TAG, "Initializing Sound Manager.");
        _soundManager = SoundManager.getInstance();
        _soundManager.init( this.getApplicationContext() );
        _soundManager.setPlayAudioSoundFX( getAudioFeedbackPreference() );
		
		Log.d(TAG, "Initializing Network Manager.");
		_networkManager = NetworkManager.getInstance();
		
    }

	public void UpdateVocab() throws MalformedURLException, IOException {
		
		// Helper fucntions to determine if an update is possible
		_updateCards = new UpdateHelper();
		
	     // Get the new vocabulary
		//this._btnUpdateVocab = (Button)this.findViewById( R.id.btnGetVocab );
		  
		// check every 2 weeks if the vocab file on the server is newer
		//ANTHONY THIS IS CURRENTLY SET TO NOT UPDATE.
		if (! !_updateCards.shouldUpdate( this.getApplicationContext(), filesDir.getPath() + gStrVocabFileName, _strVocabURL ) ) {
			
			// Ask the user if they want to update
			new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.updateAvailable)
            .setMessage(R.string.desireUpdate)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	fDesiresUpdate = true;    
                }
            })
            .setNegativeButton(R.string.no, null)
            .show();

			// if they dont't want to update, escape
			if ( !fDesiresUpdate )
				return;
			
	        // They do!  Start the update.
	        DownloadFileFromURL task = new DownloadFileFromURL( this );
	        task.execute( _strVocabURL );
        }
		
	 }
	
    public void onClick( View view ) {
    	
    	Intent i;
    	switch ( view.getId() ){
    	case R.id.ecButton:
    		mode = "ec";
    		i = new Intent(this, LearnActivity.class);
    		startActivity(i);
			break;
    	case R.id.ceButton:
    		mode = "ce";
    		i = new Intent(this, LearnActivity.class);
    		startActivity(i);
			break;
    	case R.id.ecLogButton:
    		mode = "ec";
    		i = new Intent(this, LogActivity.class);
    		startActivity(i);
    		break;
    	case R.id.ceLogButton:
    		mode = "ce";
    		i = new Intent(this, LogActivity.class);
    		startActivity(i);
    		break;
    	case R.id.exitButton:
    		finish();
			break;
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);

        return true;
    }   
    
    @Override
    protected void onDestroy() {
    	_soundManager.dispose();
        super.onDestroy();
        // The activity is about to be destroyed.
    } 
    
    /*
     * Check if SD card is mounted and writable
     */
    private boolean checkSDCard(){
    
       String state = Environment.getExternalStorageState();
       boolean sdcard_avail =  state.equals(android.os.Environment.MEDIA_MOUNTED);
       boolean sdcard_readonly =  state.equals(android.os.Environment.MEDIA_MOUNTED_READ_ONLY);
    
           if ( ! sdcard_avail || sdcard_readonly ){
        	   _bSDCardOkay = false;
        	   
        	   /*
               LayoutInflater inflater = getLayoutInflater();
	           View dview = inflater.inflate(R.layout.alert_card, null);
	           
	           final Dialog dialog = new Dialog(this);
	           dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	           dialog.setContentView(dview);
	           
	           TextView tv = (TextView)  dview.findViewById(R.id.alert);
	           tv.setText( !sdcard_avail ? R.string.no_sdcard :  R.string.sdcard_read_only);
	           
	           Button btn_ok = (Button) dview.findViewById(R.id.btn_ok);
	           btn_ok.setOnClickListener( new View.OnClickListener() {
	               public void onClick( View v ) {
	               dialog.dismiss();
	               MainActivity.this.finish();
	               //Toast.makeText(this,"This is a Toast", Toast.LENGTH_LONG).show();
	               //quittingThread.start();
	              }
	           });
	           dialog.show();
	           }
	           */
        	   new AlertDialog.Builder(this)
               .setIcon(android.R.drawable.ic_dialog_alert)
               .setTitle( R.string.alert_tit )
               .setMessage( !sdcard_avail ? R.string.no_sdcard :  R.string.sdcard_read_only )
               .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                	   MainActivity.this.finish(); 
                   }
               })
               .show();
           }
           else {
        	   _bSDCardOkay = true;
           }
           return _bSDCardOkay;
    
   }
    /**
     * Showing Dialog
     * */
    @Override
    protected Dialog onCreateDialog( int id ) {
        switch ( id ) {
        case progress_bar_type: // we set this to 0
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Downloading cards...");
            pDialog.setIndeterminate(false);
            //pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(true);
            pDialog.show();
            //ViewGroup.LayoutParams lay = new ViewGroup.LayoutParams(1, 0);
            //pDialog.addContentView( (Button)this.findViewById( R.id.btnCancelUpdate ), lay);
            //pDialog.
            return pDialog;
            
        default:
            return null;
        }
    }
    
    
    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {
 
    	private Context _context;
        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @SuppressWarnings("deprecation")
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();//showDialog( progress_bar_type );
        }
 
        public DownloadFileFromURL( Context context ) {
        	_context = context;
        } 
        
        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground( String... strURL ) {
    		
    		int count;
            InputStream input = null;
            OutputStream output = null;
    		URLConnection urlConnection = null;   					
        	URL url = null;

        	// open the address
        	try {
        		
        		// Check the internet connection
        		if ( !_networkManager.isOnline( _context.getApplicationContext() ) )
        			throw new ConnectException( "Not connected to the internet." );
        		
        		url = new URL( strURL[0] );
        		urlConnection = url.openConnection();
        		
        		// this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = urlConnection.getContentLength();

                // download the file
                input = new BufferedInputStream(url.openStream(), 8192);

                // save it to a temporary file
                output = new FileOutputStream( _strVocabDestPath + ".tmp" );
 
                byte data[] = new byte[1024];
 
                long total = 0;
 
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
 
                    // writing data to file
                    output.write(data, 0, count);
                }
 
                // flushing output
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                //finish();
            }
 
            return null;
        }
 
        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
       }
 
        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @SuppressWarnings("deprecation")
		@Override
        protected void onPostExecute(String file_url) {
        	// Rename the temporary txt file
        	File fileOld = new File( _strVocabDestPath + ".tmp");
        	File fileUpdated = new File( _strVocabDestPath );
        	try {
        		fileOld.renameTo( fileUpdated );
        	}
        	catch( Exception ex ) {
        		Log.e( TAG, "Unable to save updated file." + _strVocabDestPath );
        		Toast toast = Toast.makeText( _context, "Update Error!", Toast.LENGTH_SHORT );
        		toast.show();
        		// get rid of the temp file if it exists
        		_updateCards.deleteTemp();

        	}
            // dismiss the dialog after the file was downloaded
        	pDialog.dismiss();
            //dismissDialog( progress_bar_type );
            // Displaying downloaded image into image view
            // Reading image path from sdcard
            //String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";
            // setting downloaded into image view
            //my_image.setImageDrawable(Drawable.createFromPath(imagePath));
        }     
 
    }

}
