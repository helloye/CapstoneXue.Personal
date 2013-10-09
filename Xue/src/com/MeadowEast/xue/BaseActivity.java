package com.MeadowEast.xue;

import android.app.Activity;
<<<<<<< HEAD
import android.content.Intent;
import android.content.SharedPreferences;
=======
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
>>>>>>> incoming.merge.branch
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends Activity {
	
<<<<<<< HEAD
	static final String TAG = "XUE BaseActivity";
	SharedPreferences _preferences;
	OnSharedPreferenceChangeListener _sharedPrefListener;
	SoundManager _soundManager;
		
=======
	static final String TAG = 			"XUE BaseActivity";
	static SharedPreferences 			_preferences;
	static Editor 						_prefEditor;
	OnSharedPreferenceChangeListener 	_sharedPrefListener;
	SoundManager 						_soundManager;
	boolean								_fIsMain;
	
>>>>>>> incoming.merge.branch
	 @Override
     protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
		// Initialize preferences
<<<<<<< HEAD
		_preferences = PreferenceManager.getDefaultSharedPreferences(this);

=======
		//this._preferences = getPreferences(MODE_PRIVATE );
		//this._preferences = this.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
	    _preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
			

		// Need this to adjust sound on sound settings changes
>>>>>>> incoming.merge.branch
		_soundManager = SoundManager.getInstance();
		_soundManager.init(this.getApplicationContext());
		
		// Handler for when a preference is changed
		_sharedPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			  public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
				  
				  // change the audio feedback in the sound manager instance
				  if ( key.equals( "pref_audio_feedback" ) ) {
					  
<<<<<<< HEAD
					  _soundManager.setPlayAudioSoundFX( getAudioFeedbackPreference() );
=======
					  _soundManager.setPlayAudioSoundFX( getAudioFeedbackPreference( ));
>>>>>>> incoming.merge.branch
				  }
			  }
			};
			
		_preferences.registerOnSharedPreferenceChangeListener( _sharedPrefListener );	
		
     }
<<<<<<< HEAD
	 
	 protected int getTargetPreference() {
		 String targetPref = _preferences.getString( "pref_deck_target", "NULL" );
=======
	 public static void setDefaults(String key, String value ) {
		 _prefEditor.putString(key, value);
		 _prefEditor.commit();
		}

	public static String getDefaults(String key) {
	    return _preferences.getString(key, null);
	}
		
	 protected int getTargetPreference( ) {
		 String targetPref = getDefaults( "pref_deck_target" );
>>>>>>> incoming.merge.branch
		  int nTarget = Integer.parseInt( targetPref );
		  return nTarget;
	 }
	 
<<<<<<< HEAD
	 protected int getECDeckSizePreference() {
		 String strDeckSize = _preferences.getString( "pref_ec_decksize", "NULL" );
=======
	 protected int getECDeckSizePreference( ) {
		 String strDeckSize = getDefaults( "pref_ec_decksize" );
>>>>>>> incoming.merge.branch
		  int nECDeckSize = Integer.parseInt( strDeckSize );
		  return nECDeckSize;
	 }
	 protected int getCEDeckSizePreference() {
<<<<<<< HEAD
		 String strDeckSize = _preferences.getString( "pref_ce_decksize", "NULL" );
		  int nCEDeckSize = Integer.parseInt( strDeckSize );
		  return nCEDeckSize;
	 }
	 protected boolean getAudioFeedbackPreference() {
		 boolean bAudioFeed = _preferences.getBoolean("pref_audio_feedback", true);
		 return bAudioFeed;
	 }
=======
		 String strDeckSize = getDefaults( "pref_ce_decksize" );
		  int nCEDeckSize = Integer.parseInt( strDeckSize );
		  return nCEDeckSize;
	 }
	 
	 protected boolean getAudioFeedbackPreference( ) {
		 boolean bAudioFeed = _preferences.getBoolean("pref_audio_feedback", true);
		 return bAudioFeed;
	 }
	 protected void setAudioFeedbackPreference( boolean fAudioFB ) {
		 _prefEditor.putBoolean("pref_audio_feedback", fAudioFB);
		 _prefEditor.commit();
	 }
	 protected void setTargetPreference( String value ) {
		 setDefaults("pref_deck_target", value );
	 }
	
	 protected void setECDeckSizePreference( String value ) {
		 setDefaults("pref_ec_decksize", value );
	 }
	
	 protected void setCEDeckSizePreference( String value ) {
		 setDefaults("pref_ce_decksize", value );
	 }
	
	
>>>>>>> incoming.merge.branch
 
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
<<<<<<< HEAD
=======
		 	case R.id.menu_log:
		 		// Open the settings menu
		 		i = new Intent( this, LogActivity.class );
	    		startActivity( i );
		 		return true;
>>>>>>> incoming.merge.branch
		 		
		 	default:
		 		return super.onOptionsItemSelected( item );
		 
		 }
	 }
	 
	 @Override     
	 protected void onResume() {
	     super.onResume();          
<<<<<<< HEAD
	     _preferences.registerOnSharedPreferenceChangeListener( _sharedPrefListener );     
=======
	     //_preferences.registerOnSharedPreferenceChangeListener( _sharedPrefListener );     
>>>>>>> incoming.merge.branch
	 }



	 @Override     
	 protected void onPause() {         
	     super.onPause();          
	     //_preferences.unregisterOnSharedPreferenceChangeListener( _sharedPrefListener );

	 }
	 @Override     
	 protected void onDestroy() {    
<<<<<<< HEAD
		 _preferences.unregisterOnSharedPreferenceChangeListener( _sharedPrefListener );
	     super.onDestroy();          
	     

=======
		 //_preferences.unregisterOnSharedPreferenceChangeListener( _sharedPrefListener );
	     super.onDestroy();          
	    
>>>>>>> incoming.merge.branch
	 }

}
