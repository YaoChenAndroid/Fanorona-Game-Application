package logic;

import android.util.Log;

/**
 * store all information of the computer decision.
 * 
 * @author Yao Chen
 * 
 */
public class AlgInformation {
	private final static String TAG = AlgInformation.class.getSimpleName();
	public boolean cutoff;
	public int level;
	public int nodesCount;
	public int maxPruningCount;
	public int minPruningCount;

	public AlgInformation() {
		reset();
	}

	/**
	 * reset all attribute the initial value, it will be called at the begin of
	 * computer calculation
	 */
	public void reset() {
		cutoff = false;
		level = 0;
		nodesCount = 0;
		maxPruningCount = minPruningCount = 0;
	}
}
