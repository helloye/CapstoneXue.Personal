package com.MeadowEast.xue;

import java.util.HashMap;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

final public class SoundManager {

	private static SoundManager instance = null;
	static final String TAG = "CC SoundManager";
	
	/** Needs access to the application context which gets recreated every orientation change, etc */
	private Context 			_context;
	
	/** Wrapper for playing short sound files */
	private SoundPool 			_soundPool;
	
	/** Wrapper for accessing audio settings and capabilities */
	private AudioManager 		_audioManager;
	
	/** Wrapper for playing/streaming long media files */
	private MediaPlayer 		_mediaPlayer;
	
	/** Helps ensure an exception is not throws if the manager has not been initialized */
	private boolean _fIsInitialized;
	
	/** Whether to play audio sounds effects */
	private boolean _fPlayAudioSoundFX;
	
	/** Used to store all the media sound ids */
	public enum Sounds { SND_RIGHT, SND_WRONG, SND_BG };
	
	/** Hashmap containing the sound effects and the id */
	private HashMap<Sounds, Integer> _soundIDMap;

    /** Default volume for sound playback relative to current stream volume. */
    public static final float DEFAULT_VOLUME = 1.0f;

    /** Default rate for sound playback. Use 1.0f for normal speed playback. */
    public static final float DEFAULT_RATE = 1.0f;

    /** Default pan adjustment for sound playback. Use 0.0f for center. */
    public static final float DEFAULT_PAN = 0.0f;
    
	
	
	private SoundManager() {
	}
	public static SoundManager getInstance() {

		if( instance == null ) {
	        instance = new SoundManager();
	      }
	    return instance;
	}
	
	public void init( Context context ) {
		
		if ( _fIsInitialized )
			return;
		
		if ( context == null ) {
			throw new NullPointerException( "Context is null." );
		}
		_context = context;
		
		// Allocate
		_soundIDMap = new HashMap<Sounds, Integer>();
		_soundPool = new SoundPool( 5, AudioManager.STREAM_MUSIC, 0);
		_audioManager = (AudioManager)_context.getSystemService( Context.AUDIO_SERVICE );
		_mediaPlayer = new MediaPlayer();
		
		// Load all the sound effect files
		try {
			_soundIDMap.put( Sounds.SND_RIGHT, _soundPool.load( context, R.raw.xue_right, 1 ) );
			_soundIDMap.put( Sounds.SND_WRONG, _soundPool.load( context, R.raw.xue_wrong, 1 ) );
			// ..
		}
		catch( Exception ex ) {
			Log.d( TAG, "Error loading sound files.\n" + ex.getMessage() );
			throw new RuntimeException( "Error loading sound files.", ex );
			
		}
		
		_fIsInitialized = true;
	}
	
    /**
     * Plays the previously loaded sound associated with the specified id
     *
     * @param id The identifier associated with the sound.
     * @param rate The playback rate modifier, range {0...2}.
     * @param pan The panning value, range {-1...1} where 0 is center.
     * @throws IllegalArgumentException, RuntimeException
     */
    public void play( Sounds id, float rate, float pan ) {
    	
        int soundId = _soundIDMap.get( id );
        if ( soundId == 0 ) {
            throw new IllegalArgumentException( "Cannot play sound.  Id does not exist.");
        }

        float curVolume = _audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        float maxVolume = _audioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
        float leftVolume = ( curVolume/maxVolume ) * Math.min(1.0f, (1.0f - pan)) * curVolume;
        float rightVolume = ( curVolume/maxVolume ) * Math.min(1.0f, (1.0f + pan)) * curVolume;
        float playRate = DEFAULT_RATE * rate;

        int resultSoundId = _soundPool.play( soundId, leftVolume, rightVolume, 0, 0, playRate);
        if (resultSoundId == 0) {
            Log.e( TAG, "Failed to play sound id: " + id );
            throw new RuntimeException();
        }

    }
    
	public void playSoundFX( Sounds nSoundFXID )
	{
		if ( !_fPlayAudioSoundFX )
			return;
		
		play( nSoundFXID, 1, 0 );
	}

	public boolean getPlayAudioSoundFX( ) {
		return this._fPlayAudioSoundFX;
	}
	public void setPlayAudioSoundFX( boolean bPlayAudio ) {
		_fPlayAudioSoundFX = bPlayAudio;
	}
	
	public void playBGMusic( Context context, int nResourceID, boolean bLoop ) {
		//String strPath = "android.resource://" + context.getPackageName() + "/" + nResourceID;
		//FileInputStream fileInputStream = new FileInputStream( strPath );
		AssetFileDescriptor afd = context.getResources().openRawResourceFd( nResourceID );
		try {
			_mediaPlayer.reset();
			_mediaPlayer.setDataSource( afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength() );
			_mediaPlayer.prepare();
			_mediaPlayer.setLooping( bLoop );
			_mediaPlayer.start();
		}
		catch ( Exception ex )
	    {
	        Log.e( TAG, "Unable to play audio do to exception: " + ex.getMessage(), ex );
	        //ex.printStackTrace();
			//throw new RuntimeException( ex );
	    }
		finally {
			if ( afd != null ) {
				try {
					afd.close();
				}
				catch( Exception ex2 ) {
				}
			}
		}
		
			
	}
	
	public void pauseBGMusic() {
		_mediaPlayer.pause();
		
	}
	
	public void resumeBGMusic() {
		_mediaPlayer.start();
	}
	
	public void stopBGMusic() {
		_mediaPlayer.stop();
	}
	

	public void dispose( )
	{
<<<<<<< HEAD
		if(instance != null){
=======
		if ( instance != null ) {
>>>>>>> incoming.merge.branch
		_soundPool.release();
		_mediaPlayer.stop();
		_mediaPlayer.release();
		_mediaPlayer = null;
		_soundPool = null;
<<<<<<< HEAD
=======
		instance = null;
>>>>>>> incoming.merge.branch
		}
		
	}
	
	public boolean isInitialized() {
		return _fIsInitialized;
	}
	
}
