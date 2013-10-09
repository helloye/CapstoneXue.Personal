package com.MeadowEast.xue;

import java.io.*;
//import java.text.DateFormat;
//import java.text.ParseException;
import java.util.*;

import com.MeadowEast.xue.SoundManager.Sounds;

import android.util.Log;

abstract public class LearningProject {
	
	private String _name;
	private int _nDeckSize, _seen;
	protected List<IndexSet> indexSets;
	protected Map<Integer, Date> timestamps;
	protected Stack<CardStatus> undoStack;
	protected Deck deck;
	protected CardStatus cardStatus = null;
	protected Card card = null;	
	final static String TAG = "CC LearningProject";
	private SoundManager _soundManager;
	private static String medFileRightPath = "R.raw.";
	private static String medFileWrongPath = "";
	final static int DEFAULT_TARGET = 700;

	public LearningProject( String name, int nDeckSize, int nTarget ) {
		this._nDeckSize = nDeckSize;
		this._name = name;
		this._seen = 0;
		undoStack = new Stack<CardStatus>();
		//this._nTarget = nTarget;
		Log.d(TAG, "Creating index sets");
		indexSets = new ArrayList<IndexSet>();
		for (int i=0; i<5; ++i){
			indexSets.add(new IndexSet());
		}
		timestamps = new HashMap<Integer, Date>();
		Log.d(TAG, "Reading status");
		readStatus();
		Log.d(TAG, "Making deck");
		deck = makeDeck( _nDeckSize, nTarget );
		
		_soundManager = SoundManager.getInstance();
		
		Log.d(TAG, "Exiting LearningProject constructor");

	}
	
//	public int getTarget() {
//	    return this._nTarget;
//	}
//	public void setTarget( int nNewtarget ) {
//	     this._nTarget = nNewtarget;
//	}

	// n is the size of the deck
	// target is used to limit the number at Levels 1 and 2, the ones
	//   mainly being learned
 	private Deck makeDeck(int n, int target){
 		Date now = new Date();
		Random r = new Random();
		Deck d = new Deck();
		float[] cutoffs = new float[4];
		// Set cutoffs so that new cards are only introduced so as to maintain a limit
		// of target at Level 1 and 2*target at (Level 1 + Level 2).  We always spend
		// 50 percent of our time on Level 1 (and 7 percent and 3 percent each on Levels
		// 3 and 4).  The remaining 40 percent gets divided between Levels 0 and 2, with
		// Level 0 time approaching zero as the in-play number gets to 2*target
		
		// Note: When Level 1 and Level 2 are both half full, this cuts the time on Level 0
		// to 12.5 percent, which is kind of low.  Easy to fix by setting target higher.
		float factor1 = Math.max(0,  1-indexSets.get(1).size()/(float) target);
		float factor1n2 = Math.max(0, 1-(indexSets.get(1).size()+indexSets.get(2).size())/((float) 2*target));
		cutoffs[0] = .40f * factor1 * factor1n2;
		if (cutoffs[0] < 0) cutoffs[0] = 0f;
		cutoffs[1] = cutoffs[0] + .5f;
		cutoffs[2] = .90f;
		cutoffs[3] = .97f;
		if (indexSets.get(0).size() < n)
			addNewItems(n);
		while (d.size()<n){
			double x = r.nextFloat();
			int level;
			if (x < cutoffs[0]){
				level = 0;
			} else if (x < cutoffs[1]) {
				level = 1;
			} else if (x < cutoffs[2]) {
				level = 2;
			} else if (x < cutoffs[3]) {
				level = 3;
			} else {
				level = 4;
			}
			IndexSet targetSet = indexSets.get(level);
			if (targetSet.size() > 0){
				int index = targetSet.pickOne();
				// add it if it hasn't recently been seen
				long hoursSinceSeen = (now.getTime() - timestamps.get(index).getTime()) / (60 * 60 * 1000);
				if (hoursSinceSeen > 24){
					timestamps.put(index, now);
					d.put(new CardStatus(index, level));
				} else { // if it's too recent, put it back
					indexSets.get(level).add(index);
				}
			}
		}
		return d;
	}
	
	public boolean next() {
		if (deck.isEmpty()) {
			_seen++;
			return false;
		}
		cardStatus = deck.get();
		_seen++;
		card = AllCards.getCard(cardStatus.getIndex());
		return true;
	}
	
	public boolean isUndoEmpty(){
		return undoStack.empty();
	}
	
	public void undo(){
		if(cardStatus!=null) //If there is a current card, put it back on top
			deck.putFront(cardStatus);
		
		cardStatus = undoStack.pop(); //Get the last card
		_seen--;
<<<<<<< HEAD
		if(!deck.contains(cardStatus))//If the last card is not in the deck, it means it was marked correct thus remove it from indexSets
			indexSets.get(cardStatus.getLevel()+1).add(cardStatus.getIndex());  //Should be in level +1
		else if(deck.contains(cardStatus)) //else if it's wrong there is a duplicate in deck. remove the duplicate
			deck.removeDuplicate(cardStatus);
=======
		if(cardStatus != deck.getLast())//If the last card is not in the deck, it means it was marked correct thus remove it from indexSets
			indexSets.get(cardStatus.getLevel()+1).add(cardStatus.getIndex());  //Should be in level +1
		else //if(deck.contains(cardStatus)) //else if it's wrong there is a duplicate in deck. remove the duplicate
			deck.removeLast(cardStatus);
>>>>>>> incoming.merge.branch
		
		card = AllCards.getCard(cardStatus.getIndex()); //set current card
	}
	
	public int currentIndex(){
		if (cardStatus==null)
			return -1;
		return cardStatus.getIndex();
	}
	
	abstract protected String prompt();
	abstract protected String answer();
	abstract protected String other();
	abstract public void addNewItems();
	abstract public void addNewItems(int n);
	
	public void right(){
		undoStack.push(cardStatus);
		cardStatus.right();
		// put it in the appropriate index set
		indexSets.get(cardStatus.getLevel()).add(cardStatus.getIndex());
		playRightSound();	
	}
	
	public void wrong() {
		undoStack.push(cardStatus);
		cardStatus.wrong();
		// return to the deck
		deck.put(cardStatus);
		playWrongSound();
	}
	
	public void playRightSound()
	{
		if ( _soundManager.isInitialized() )
			_soundManager.playSoundFX( Sounds.SND_RIGHT );
	}
	public void playWrongSound()
	{
		if ( _soundManager.isInitialized() )
			_soundManager.playSoundFX( Sounds.SND_WRONG);
		
	}
	
	String deckStatus(){
		String left = (deck.size()+1)+" left";
		return _seen > _nDeckSize ? left : _seen + " of " + _nDeckSize + " seen, " + left; 
	}
	
	String queueStatus(){
		int [] n = new int[5];
		for (int i=0; i<5; ++i) n[i] = indexSets.get(i).size();
//		return String.format("  %7d  %4d  %4d  %4d  %4d  %7d  %5d  ", n[0], n[1], n[2], n[3], n[4],
//				n[2]+n[3]+n[4], n[0]+n[1]+n[2]+n[3]+n[4]);
		return String.format("    %d   %d + %d = %d    %d + %d = %d    %d",
				n[0], n[1], n[2], n[1]+n[2], n[3], n[4], n[3]+n[4], n[0]+n[1]+n[2]+n[3]+n[4]);
	}
	
	public void log(String s) throws IOException {
		Log.d(TAG, "Entering log okay");
		boolean append = true;
		File logfilehandle = new File(MainActivity.filesDir, _name + ".log.txt");
		Log.d(TAG, "logfilehandle is: " +logfilehandle);
		FileWriter logfile = new FileWriter(logfilehandle, append);
		PrintWriter out = new PrintWriter(logfile);
		Date now = new Date();
		out.printf("%tD %tR %s\n", now, now, s);
		logfile.close();
		Log.d(TAG, "Exiting log okay");
	}
	
	public void writeStatus() throws IOException {
		
		File statusobjectfile = new File(MainActivity.filesDir, _name + ".status.ser");
		FileOutputStream statusobjectFOS = new FileOutputStream(statusobjectfile);
		ObjectOutputStream statusobjectOOS = new ObjectOutputStream(statusobjectFOS);
		
		Log.d(TAG, "writing objects");
		statusobjectOOS.writeObject(timestamps);
		statusobjectOOS.writeObject(indexSets);
		statusobjectFOS.close();
	}
	
	@SuppressWarnings("unchecked")
	private void readStatus() {
		FileInputStream statusobjectFIS;
		ObjectInputStream statusobjectOIS;
		try {
			File statusobjectfile = new File(MainActivity.filesDir, _name + ".status.ser");
			statusobjectFIS = new FileInputStream(statusobjectfile);
			statusobjectOIS = new ObjectInputStream(statusobjectFIS);
		} catch (Exception e) {
			Log.d(TAG, "No status file, adding 50 first items from AllCards");
			addNewItems(50);
			return;
		} 
		try {
			timestamps = (Map<Integer, Date>) statusobjectOIS.readObject();
			indexSets = (List<IndexSet>) statusobjectOIS.readObject();
			statusobjectFIS.close();
			Log.d(TAG, "OBJECT status file read without problems");
		} catch (Exception e) { Log.d(TAG, "Error in readStatus"); }
	}

}
