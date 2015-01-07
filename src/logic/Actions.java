package logic;

import java.util.List;

import logic.LogicBoard.PieceOwner;
import logic.LogicBoard.PieceState;
import android.support.v4.util.Pair;
import android.util.Log;

/**
 * move piece on the board
 * 
 * @author yaochen
 * 
 */
public class Actions {
	private static final Actions INSTANCE = new Actions();
	private final static String TAG = Actions.class.getSimpleName();
	private LogicBoard.PieceOwner[][] pieceArrange;
	private PieceState[][] virtualBoard; // store the player choose of each
											// piece.
	private boolean[][] moveDir;
	private int row, col;
	LogicBoard.PieceOwner curPlayer;
	LogicBoard.PieceOwner opp;

	private Actions() {
	}

	/**
	 * singleton pattern
	 * 
	 * @return
	 */
	public static Actions getInstance() {
		return INSTANCE;
	}

	public void Initial(int r, int c, boolean[][] m) {
		row = r;
		col = c;
		moveDir = m;
	}

	/**
	 * set board player and current board
	 * 
	 * @param b
	 * @param vb
	 * @param player
	 */
	public void setBoardPlayer(LogicBoard.PieceOwner[][] b, PieceState[][] vb,
			LogicBoard.PieceOwner player) {
		virtualBoard = vb;
		pieceArrange = b;
		setPlayer(player);
	}

	/**
	 * only set current player
	 * 
	 * @param player
	 */
	public void setPlayer(LogicBoard.PieceOwner player) {
		curPlayer = player;
		if (curPlayer == LogicBoard.PieceOwner.BLACK)
			opp = PieceOwner.WITHE;
		else
			opp = PieceOwner.BLACK;
	}

	// //////////////////////////////////////////////////////////////////////////
	// / before piece selected, show candidate piece
	public void setCandidatePiece(List<Pair<Integer, Integer>> res) {
		if (res.size() == 0)
			setAllCandidPiece();
		else {
			for (Pair<Integer, Integer> a : res)
				virtualBoard[a.first][a.second] = PieceState.CAN_SEL;
		}
	}

	/**
	 * If there is no piece can capture the opponent pieces, select all piece
	 * which can move as candidate pieces
	 */
	private void setAllCandidPiece() {
		for (int i = 0; i < row; i++)
			for (int j = 0; j < col; j++)
				if (pieceArrange[i][j] == PieceOwner.EMPTY)
					setCandidAround(i, j);
	}

	/**
	 * if one piece is selected, set the candiate move
	 * 
	 * @param x
	 * @param y
	 */
	private void setCandidAround(int x, int y) {
		setOneCandidPiece(x + 1, y);
		setOneCandidPiece(x - 1, y);
		setOneCandidPiece(x, y + 1);
		setOneCandidPiece(x, y - 1);
		if (moveDir[x][y]) {
			setOneCandidPiece(x + 1, y + 1);
			setOneCandidPiece(x - 1, y - 1);
			setOneCandidPiece(x + 1, y - 1);
			setOneCandidPiece(x - 1, y + 1);
		}
	}

	/**
	 * test whether one point is candidate move point
	 * 
	 * @param x
	 * @param y
	 */
	private void setOneCandidPiece(int x, int y) {
		if (x < 0 || x >= row || y < 0 || y >= col)
			return;
		if (pieceArrange[x][y] == curPlayer)
			virtualBoard[x][y] = PieceState.CAN_SEL;
	}

	// /////////////////////////////////////////////////////
	// After piece select
	public boolean selectPiece(int x, int y,
			List<Pair<Integer, Integer>> candidates) {
		if (virtualBoard[x][y] != PieceState.CAN_SEL)
			return false;
		replaceVBState(PieceState.NEXT, PieceState.EMPTY);
		replaceVBState(PieceState.SELECT, PieceState.CAN_SEL);
		virtualBoard[x][y] = PieceState.SELECT;
		setSelectPiece(x, y, candidates);
		return true;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param res
	 *            : candidate Pieces if there is no candidate pieces, select all
	 *            pieces which can move otherwise, select all candidate pieces
	 *            in res lis
	 */
	private void setSelectPiece(int x, int y, List<Pair<Integer, Integer>> res) {
		if (res.size() == 0) {
			setNextPieceNoTest(x, y, x + 1, y);
			setNextPieceNoTest(x, y, x - 1, y);
			setNextPieceNoTest(x, y, x, y + 1);
			setNextPieceNoTest(x, y, x, y - 1);
			if (this.moveDir[x][y]) {
				setNextPieceNoTest(x, y, x + 1, y + 1);
				setNextPieceNoTest(x, y, x - 1, y - 1);
				setNextPieceNoTest(x, y, x + 1, y - 1);
				setNextPieceNoTest(x, y, x - 1, y + 1);
			}
		} else {
			for (Pair<Integer, Integer> p : res)
				virtualBoard[p.first][p.second] = PieceState.NEXT;
		}
	}

	/**
	 * select candidate move and mark it in the virtual board
	 * 
	 * @param curX
	 * @param curY
	 * @param nextX
	 * @param nextY
	 */
	private void setNextPieceNoTest(int curX, int curY, int nextX, int nextY) {
		if (curX < 0 || curX >= row || curY < 0 || curY >= col || nextX < 0
				|| nextX >= row || nextY < 0 || nextY >= col)
			return;
		if (pieceArrange[nextX][nextY] == PieceOwner.EMPTY)
			virtualBoard[nextX][nextY] = PieceState.NEXT;
	}

	// /////////////////////////////////////////////////////////////////////////
	// //move Piece
	/**
	 * move pieces and remove the opponent pieces after this move
	 * 
	 * @param curX
	 * @param curY
	 * @param x
	 * @param y
	 * @param pieces
	 * @return
	 */
	public int moveRemovePiece(int curX, int curY, int x, int y,
			List<Pair<Integer, Integer>> pieces) {
		movePiece(curX, curY, x, y, curPlayer);
		if (pieces.size() == 0)
			return 0;
		if (pieces.get(0) == null) {
			pieces.remove(0);
			for (Pair<Integer, Integer> p : pieces)
				virtualBoard[p.first][p.second] = PieceState.CHOOSE_DIC;
			clearPartVirtualBoard(PieceState.CHOOSE_DIC);
			return 0;
		} else
			return removePieces(pieces);
	}

	/**
	 * remove pieces in input list
	 * 
	 * @param pieces
	 * @return
	 */
	public int removePieces(List<Pair<Integer, Integer>> pieces) {
		clearVirtualBoard();
		for (Pair<Integer, Integer> p : pieces)
			pieceArrange[p.first][p.second] = PieceOwner.EMPTY;
		return pieces.size();
	}

	/**
	 * move piece in point(curX, curY) to point(x,y)
	 * 
	 * @param curX
	 * @param curY
	 * @param x
	 * @param y
	 * @param p
	 */
	public void movePiece(int curX, int curY, int x, int y, PieceOwner p) {
		pieceArrange[x][y] = p;
		pieceArrange[curX][curY] = PieceOwner.EMPTY;
	}

	/**
	 * move piece from a.second to a.first. and restore piece which removed
	 * during calculate in alpha-bata algorithm
	 * 
	 * @param a
	 * @param pieces
	 * @param player
	 */
	public void restorePiece(
			Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> a,
			List<Pair<Integer, Integer>> pieces, LogicBoard.PieceOwner player) {
		movePiece(a.second.first, a.second.second, a.first.first,
				a.first.second, player);
		for (Pair<Integer, Integer> p : pieces)
			pieceArrange[p.first][p.second] = player;
	}

	// ////////////////////////////////////////////
	// reset board
	public void replaceVBState(PieceState preS, PieceState newS) {
		for (int i = 0; i < row; i++)
			for (int j = 0; j < col; j++)
				if (virtualBoard[i][j] == preS)
					virtualBoard[i][j] = newS;
	}

	/**
	 * reset virtual board
	 */
	public void clearVirtualBoard() {
		for (int i = 0; i < row; i++)
			for (int j = 0; j < col; j++)
				virtualBoard[i][j] = PieceState.EMPTY;
	}

	public void clearPartVirtualBoard(PieceState keep) {
		for (int i = 0; i < row; i++)
			for (int j = 0; j < col; j++)
				if (virtualBoard[i][j] != keep)
					virtualBoard[i][j] = PieceState.EMPTY;
	}

}
