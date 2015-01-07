package logic;

import java.util.List;

import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.Toast;
import logic.LogicBoard.PieceOwner;
import logic.LogicBoard.PieceState;

/**
 * the judge of current board
 * 
 * @author sunny
 * 
 */
public class Judge {
	private static final String TAG = Judge.class.getSimpleName();
	private static final Judge INSTANCE = new Judge();
	private LogicBoard board;
	private Rules rules;
	private Actions act;
	public PieceOwner curPlayer;
	private PieceOwner human;
	public AndroidPlayer computer;
	public Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> action;

	private Judge() {
	}

	// singleton pattern
	public static Judge getInstance() {
		return INSTANCE;
	}

	/**
	 * set the parameter of judge
	 * 
	 * @param player
	 *            : the piece color which user want to use
	 * @param row
	 *            : the row number of current board
	 * @param col
	 *            : the collumn number of current board
	 * @param hard
	 *            : the difficult level choosed by user
	 */
	public void Initial(String player, int row, int col, int hard) {
		computer = AndroidPlayer.getInstance();
		if (player.compareTo("White") == 0) {
			human = PieceOwner.WITHE;
			computer.Initial(PieceOwner.BLACK, row, col, hard);
		} else {
			human = PieceOwner.BLACK;
			computer.Initial(PieceOwner.WITHE, row, col, hard);
		}
		curPlayer = human;

		board = LogicBoard.getInstance();
		board.Initial(row, col);

		rules = Rules.getInstance();
		act = Actions.getInstance();
		rules.Initial(row, col, board.getDicBoard());
		act.Initial(row, col, board.getDicBoard());
		rules.setBoardPlayer(board.getPieceArrange(), curPlayer);
		act.setBoardPlayer(board.getPieceArrange(), board.getVirtualBoard(),
				curPlayer);
		act.setCandidatePiece(rules.getCandidatePiece());

	}

	/**
	 * test where the computer of user is win
	 * 
	 * @return : recuter the winner, if there is no winner, return
	 *         PieceOwner.EMPTY
	 */
	public PieceOwner isWin() {
		Log.i(TAG, "board.blackCount : " + board.blackCount);
		Log.i(TAG, "board.whiteCount : " + board.whiteCount);
		if (board.blackCount == 0)
			return PieceOwner.WITHE;
		else if (board.whiteCount == 0)
			return PieceOwner.BLACK;
		return PieceOwner.EMPTY;
	}

	/**
	 * movePiece from Point1(curX, curY) to Point2(choosedX, choosedY)
	 * 
	 * @param curX
	 *            : the x coordinate of point1
	 * @param curY
	 *            : the y coordinate of point1
	 * @param choosedX
	 *            : the x coordinate of point2
	 * @param choosedY
	 *            : the y coordinate of point2
	 * @return
	 */
	public boolean movePiece(int curX, int curY, int choosedX, int choosedY) {
		List<Pair<Integer, Integer>> pieces = rules.getRemovedPieces(curX,
				curY, choosedX, choosedY);
		if (curPlayer == PieceOwner.BLACK)
			board.whiteCount -= act.moveRemovePiece(curX, curY, choosedX,
					choosedY, pieces);
		else
			board.blackCount -= act.moveRemovePiece(curX, curY, choosedX,
					choosedY, pieces);
		if (pieces.size() > 0 && pieces.get(0) == null)
			return false;
		return true;
	}

	/**
	 * user select Piece and mark the candidate move of current choosed piece
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean selectPiece(int x, int y) {
		return act.selectPiece(x, y, rules.getCandidateMove(x, y));
	}

	/**
	 * if current player is use, turn it to computer and calculate the computer
	 * choose using the Alpha-Bate algorithm. if current player is computer,
	 * turn it to user and mark the candidate pieces.
	 */
	public void turnPlayer() {

		curPlayer = (curPlayer == PieceOwner.BLACK) ? PieceOwner.WITHE
				: PieceOwner.BLACK;
		act.clearVirtualBoard();
		if (curPlayer == human) {
			rules.setBoardPlayer(board.getPieceArrange(), curPlayer);
			act.setBoardPlayer(board.getPieceArrange(),
					board.getVirtualBoard(), curPlayer);
			act.setCandidatePiece(rules.getCandidatePiece());
		} else {

			LogicBoard.PieceOwner[][] curBoard = copyVirtualBoard(board
					.getPieceArrange());

			rules.setBoardPlayer(curBoard, curPlayer);
			act.setBoardPlayer(curBoard,
					copyPieceBoard(board.getVirtualBoard()), curPlayer);

			action = computer.choosePiece(board.whiteCount, board.blackCount);

		}
	}

	/**
	 * android move piece on current board
	 */
	public void androidMove() {
		rules.setBoardPlayer(board.getPieceArrange(), curPlayer);
		act.setBoardPlayer(board.getPieceArrange(), board.getVirtualBoard(),
				curPlayer);
		selectPiece(action.first.first, action.first.second);

		movePiece(action.first.first, action.first.second, action.second.first,
				action.second.second);
		turnPlayer();
	}

	/**
	 * copy Board and the piece inforation
	 * 
	 * @param b
	 * @return
	 */
	private PieceState[][] copyPieceBoard(PieceState[][] b) {
		int row = b.length;
		int col = b[0].length;
		PieceState[][] res = new PieceState[row][col];
		for (int i = 0; i < row; i++)
			System.arraycopy(b[i], 0, res[i], 0, col);
		return res;
	}

	/**
	 * copy the board which is used to store the candidate piece or move
	 * information
	 * 
	 * @param b
	 * @return
	 */
	private LogicBoard.PieceOwner[][] copyVirtualBoard(
			LogicBoard.PieceOwner[][] b) {
		int row = b.length;
		int col = b[0].length;
		LogicBoard.PieceOwner[][] res = new LogicBoard.PieceOwner[row][col];
		for (int i = 0; i < row; i++)
			System.arraycopy(b[i], 0, res[i], 0, col);
		return res;
	}

	/**
	 * remove piece in Point(curX, curY). Since it is bi-direction remove,
	 * current user decided to remove pieces in Point2(choosedX, choosedY)
	 * direction
	 * 
	 * @param curX
	 * @param curY
	 * @param choosedX
	 * @param choosedY
	 */
	public void removePiece(int curX, int curY, int choosedX, int choosedY) {

		List<Pair<Integer, Integer>> pieces = rules.getBidicRemovedPieces(curX,
				curY, choosedX, choosedY);
		if (curPlayer == PieceOwner.BLACK)
			board.whiteCount -= act.removePieces(pieces);
		else
			board.blackCount -= act.removePieces(pieces);
	}
}
