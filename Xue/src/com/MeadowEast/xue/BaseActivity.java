package com.MeadowEast.xue;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends Activity {
	
	static final String TAG = "XUE BaseActivity";
	SharedPreferences _preferences;
	OnSharedPreferenceChangeListener _sharedPrefListener;
	SoundManager _soundManager;
		
	 @Override
     protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
		// Initialize preferences
		_preferences = PreferenceManager.getDefaultSharedPreferences(this);

		_soundManager = SoundManager.getInstance();
		_soundManager.init(this.getApplicationContext());
		
		// Handler for when a preference is changed
		_sharedPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			  public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
				  
				  // change the audio feedback in the sound manager instance
				  if ( key.equals( "pref_audio_feedback" ) ) {
					  
					  _soundManager.setPlayAudioSoundFX( getAudioFeedbackPreference() );
				  }
			  }
			};
			
		_preferences.registerOnSharedPreferenceChangeListener( _sharedPrefListener );	
		
     }
	 
	 protected int getTargetPreference() {
		 String targetPref = _preferences.getString( "pref_deck_target", "NULL" );
		  int nTarget = Integer.parseInt( targetPref );
		  return nTarget;
	 }
	 
	 protected int getECDeckSizePreference() {
		 String strDeckSize = _preferences.getString( "pref_ec_decksize", "NULL" );
		  int nECDeckSize = Integer.parseInt( strDeckSize );
		  return nECDeckSize;
	 }
	 protected int getCEDeckSizePreference() {
		 String strDeckSize = _preferences.getString( "pref_ce_decksize", "NULL" );
		  int nCEDeckSize = Integer.parseInt( strDeckSize );
		  return nCEDeckSize;
	 }
	 protected boolean getAudioFeedbackPreference() {
		 boolean bAudioFeed = _preferences.getBoolean("pref_audio_feedback", true);
		 return bAudioFeed;
	 }
 
	 /*public void onProgressChanging( final Activity activity ) { 
		 new AlertDialog.Builder(this)
         .setIcon(android.R.drawable.ic_dialog_alert)
         .setTitle(R.string.quit)
         .setMessage(R.string.reallyQuit)
         .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int which) {
            	 activity.finish();    
             }
         })
         .setNegativeButton(R.string.no, null)
         .show();
	 }
	 */
	 
	 @Override
     public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.activity_main, menu );
        return true;
     } 
	 
	 @Override
	 public boolean onOptionsItemSelected( MenuItem item ) {
		 
		 Intent i;
		 switch( item.getItemId() ) {
		 	case R.id.menu_settings:
		 		// Open the settings menu
		 		i = new Intent( this, PrefsActivity.class );
	    		startActivity( i );
		 		return true;
		 		
		 	default:
		 		return super.onOptionsItemSelected( item );
		 
		 }
	 }
	 
	 @Override     
	 protected void onResume() {
	     super.onResume();          
	     _preferences.registerOnSharedPreferenceChangeListener( _sharedPrefListener );     
	 }



	 @Override     
	 protected void onPause() {         
	     super.onPause();          
	     //_preferences.unregisterOnSharedPreferenceChangeListener( _sharedPrefListener );

	 }
	 @Override     
	 protected void onDestroy() {    
		 _preferences.unregisterOnSharedPreferenceChangeListener( _sharedPrefListener );
	     super.onDestroy();          
	     

	 }

}
