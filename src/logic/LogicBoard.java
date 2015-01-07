package logic;

import java.util.List;

import android.support.v4.util.Pair;
import android.util.Log;

import com.example.fanoronayc.BoardView;

/**
 * the logic board store the piece information in each location in 2D Array.
 * 
 * @author yaochen
 * 
 */
public class LogicBoard {
	private static final LogicBoard INSTANCE = new LogicBoard();

	private static final String TAG = LogicBoard.class.getSimpleName();
	public int blackCount;
	public int whiteCount;

	public static enum PieceOwner {
		BLACK, WITHE, EMPTY
	};

	public static enum PieceState {
		CAN_SEL, SELECT, NEXT, CHOOSE_DIC, EMPTY
	}

	private PieceOwner[][] pieceArrange; // store the owner of each piece.

	private PieceState[][] virtualBoard; // store the player choose of each
											// piece.
	private boolean[][] moveDir;

	private LogicBoard() {
	}

	public static LogicBoard getInstance() {
		return INSTANCE;
	}

	/**
	 * initial board with row and collumn. Also set the piece color in each
	 * point
	 * 
	 * @param row
	 * @param col
	 */
	public void Initial(int row, int col) {
		blackCount = whiteCount = row * col / 2;
		pieceArrange = new PieceOwner[row][col];
		for (int i = 0, nLen = row / 2; i < nLen; i++)
			for (int j = 0; j < col; j++) {
				pieceArrange[i][j] = LogicBoard.PieceOwner.BLACK;
				pieceArrange[row - i - 1][j] = LogicBoard.PieceOwner.WITHE;
			}
		int midCol = col / 2;
		int midRow = row / 2;
		for (int i = 0; i <= midCol; i++) {
			pieceArrange[midRow][i] = LogicBoard.PieceOwner.BLACK;
			pieceArrange[midRow][col - i - 1] = LogicBoard.PieceOwner.WITHE;
		}
		pieceArrange[midRow][midCol] = LogicBoard.PieceOwner.EMPTY;

		moveDir = new boolean[row][col];
		boolean flag = true;
		for (int i = 0; i < row; i++)
			for (int j = 0; j < col; j++) {
				moveDir[i][j] = flag;
				flag = !flag;
			}

		virtualBoard = new PieceState[row][col];
	}
	
	public PieceOwner[][] getPieceArrange() {
		return pieceArrange;
	}

	public PieceState[][] getVirtualBoard() {
		return virtualBoard;
	}

	public boolean[][] getDicBoard() {
		return moveDir;
	}

}
