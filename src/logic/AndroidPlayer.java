package logic;

import java.util.ArrayList;
import java.util.List;

import logic.LogicBoard.PieceOwner;

import android.graphics.Point;
import android.support.v4.util.Pair;
import android.util.Log;

/**
 * the computer calculate the next piece to choose and which direction to move.
 * 
 * @author yaochen
 * 
 */
public class AndroidPlayer {
	private int duration;
	private final static String TAG = AndroidPlayer.class.getSimpleName();
	private final static AndroidPlayer INSTANCE = new AndroidPlayer();
	private LogicBoard.PieceOwner curPlayer;
	private LogicBoard.PieceOwner opp;
	private Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> nextAct;
	private Rules rules;
	private Actions act;
	private int MAX;
	private int MIN;
	private double startTime;
	private int whiteCount, blackCount;
	public AlgInformation info;
	private int curDrawFlag, oppDrawFlag;

	private AndroidPlayer() {
	}

	/**
	 * singleton pattern
	 * 
	 * @return
	 */
	public static AndroidPlayer getInstance() {
		return INSTANCE;
	}

	/**
	 * set the piece color of computer, the row and collumn information of
	 * board, the difficult level of current game
	 * 
	 * @param player
	 * @param row
	 * @param col
	 * @param hard
	 */
	public void Initial(LogicBoard.PieceOwner player, int row, int col, int hard) {
		curPlayer = player;
		opp = curPlayer == LogicBoard.PieceOwner.BLACK ? LogicBoard.PieceOwner.WITHE
				: LogicBoard.PieceOwner.BLACK;
		rules = Rules.getInstance();
		act = Actions.getInstance();
		MAX = 0;
		MIN = -row * col / 2;
		Log.i(TAG, "Hard: " + hard);
		info = new AlgInformation();
		switch (hard) {
		case 1:
			duration = 3000;
			break;
		case 2:
			duration = 6000;
			break;
		case 3:
			duration = 10000;
			break;
		default:
			duration = 1000;
			break;
		}
	}

	/**
	 * The judge will call this function to calculate the computer's next steps.
	 * 
	 * @param w
	 * @param b
	 * @return
	 */
	public Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> choosePiece(
			int w, int b) {
		info.reset();
		curDrawFlag = 0;
		oppDrawFlag = 0;
		whiteCount = w;
		blackCount = b;
		startTime = System.currentTimeMillis();
		maxValue(MIN, MAX, 0);
		return nextAct;
	}

	/**
	 * get all candidate piece for next steps
	 * 
	 * @param player
	 * @return
	 */
	private List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getActions(
			LogicBoard.PieceOwner player) {
		List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> res = new ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>();
		List<Pair<Integer, Integer>> candidates = new ArrayList<Pair<Integer, Integer>>();
		rules.setPlayer(player);
		act.setPlayer(player);
		candidates = rules.getCandidatePiece();

		for (Pair<Integer, Integer> p : candidates) {
			for (Pair<Integer, Integer> next : rules.getCandidateMove(p.first,
					p.second)) {
				res.add(new Pair(p, next));
			}
		}
		return res;
	}

	/**
	 * move from Point cur to Point next
	 * 
	 * @param cur
	 * @param next
	 * @return
	 */
	private List<Pair<Integer, Integer>> move(Pair<Integer, Integer> cur,
			Pair<Integer, Integer> next) {
		List<Pair<Integer, Integer>> res = rules.getRemovedPieces(cur.first,
				cur.second, next.first, next.second);
		act.moveRemovePiece(cur.first, cur.second, next.first, next.second, res);
		return res;
	}

	/**
	 * the minValue function of Alpha-bata algorithm
	 * 
	 * @param alpha
	 * @param beta
	 * @param level
	 * @return
	 */
	private int minValue(int alpha, int beta, int level) {
		if (whiteCount == 0 || blackCount == 0 || timeDone() || isDraw()) {
			if (curPlayer == PieceOwner.WITHE)
				return -whiteCount;
			else
				return -blackCount;
		}
		act.clearVirtualBoard();
		int v = MAX;
		for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> a : getActions(opp)) {
			info.nodesCount++;
			List<Pair<Integer, Integer>> pieces = move(a.first, a.second);
			// Log.i(TAG, "pieces.size(): " + pieces.size());
			oppDrawFlag = pieces.size() == 0 ? oppDrawFlag + 1 : 0;
			// Log.i(TAG, "oppDrawFlag : " + oppDrawFlag);
			int count = pieces.size();
			calculateScore(-count, curPlayer);
			v = Math.min(v, maxValue(alpha, beta, level + 1));
			act.restorePiece(a, pieces, opp);
			calculateScore(count, curPlayer);
			if (v <= alpha) {
				info.minPruningCount++;
				return v;
			}
			beta = Math.min(beta, v);
		}
		return v;
	}

	/**
	 * test whether current board is draw
	 * 
	 * @return
	 */
	private boolean isDraw() {
		// TODO Auto-generated method stub
		if (curDrawFlag > 0 && oppDrawFlag > 0)
			return true;
		return false;
	}

	/**
	 * detect whether it is timeout
	 * 
	 * @return
	 */
	private boolean timeDone() {
		// TODO Auto-generated method stub
		if (System.currentTimeMillis() - startTime > duration) {
			info.cutoff = true;
			return true;
		}
		return false;
	}

	/**
	 * the maxValue of alpha-bata algorithm
	 * 
	 * @param alpha
	 * @param beta
	 * @param level
	 * @return
	 */
	private int maxValue(int alpha, int beta, int level) {
		info.level = info.level > level ? info.level : level;
		if (whiteCount == 0 || blackCount == 0 || timeDone() || isDraw()) {
			if (opp == PieceOwner.WITHE)
				return -whiteCount;
			else
				return -blackCount;
		}
		act.clearVirtualBoard();
		int v = MIN;
		for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> a : getActions(curPlayer)) {
			info.nodesCount++;
			List<Pair<Integer, Integer>> pieces = move(a.first, a.second);
			curDrawFlag = pieces.size() == 0 ? curDrawFlag + 1 : 0;
			int count = pieces.size();
			calculateScore(-count, opp);
			int res = minValue(alpha, beta, level + 1);
			act.restorePiece(a, pieces, curPlayer);
			calculateScore(count, opp);
			if (v <= res) {
				if (level == 0) {
					nextAct = new Pair(new Pair(a.first.first, a.first.second),
							new Pair(a.second.first, a.second.second));
				}
				v = res;
			}
			if (v >= beta) {
				info.maxPruningCount++;
				return v;
			}
			alpha = Math.max(alpha, v);
		}
		return v;
	}

	/**
	 * the evaluation function of current board
	 * 
	 * @param count
	 * @param player
	 */
	private void calculateScore(int count, PieceOwner player) {
		if (player == PieceOwner.BLACK)
			blackCount += count;
		else
			whiteCount += count;
	}

}
