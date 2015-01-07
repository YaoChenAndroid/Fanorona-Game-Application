package logic;

import java.util.ArrayList;
import java.util.List;

import logic.LogicBoard.PieceOwner;
import logic.LogicBoard.PieceState;
import android.support.v4.util.Pair;
import android.util.Log;

/**
 * calculate the candidate moves and pieces
 * 
 * @author YAO Chen
 * 
 */
public class Rules {
	private final static Rules INSTANCE = new Rules();
	private final static String TAG = Rules.class.getSimpleName();
	private LogicBoard.PieceOwner[][] curBoard;
	private int row, col;
	private boolean[][] moveDir;
	LogicBoard.PieceOwner testedPlayer;
	LogicBoard.PieceOwner opp;
	public boolean noCapture;

	private Rules() {
	}

	/**
	 * singleton pattern
	 * 
	 * @return
	 */
	public static Rules getInstance() {
		return INSTANCE;
	}

	public void Initial(int r, int c, boolean[][] m) {
		row = r;
		col = c;
		moveDir = m;
	}

	public void setBoardPlayer(LogicBoard.PieceOwner[][] board,
			LogicBoard.PieceOwner player) {
		curBoard = board;
		setPlayer(player);
	}

	public void setPlayer(LogicBoard.PieceOwner player) {
		testedPlayer = player;
		opp = (testedPlayer == PieceOwner.BLACK) ? PieceOwner.WITHE
				: PieceOwner.BLACK;
	}

	/**
	 * test the move direct of current point(x,y)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean canDiagonal(int x, int y) {
		return moveDir[x][y];
	}

	/**
	 * get candidate pieces of current player
	 * 
	 * @return
	 */
	public List<Pair<Integer, Integer>> getCandidatePiece() {
		List<Pair<Integer, Integer>> res = new ArrayList<Pair<Integer, Integer>>();
		for (int i = 0; i < row; i++)
			for (int j = 0; j < col; j++) {
				if (curBoard[i][j] == testedPlayer && canBeSelect(i, j)) {
					res.add(new Pair(i, j));
				}
			}
		if (res.size() == 0) {
			for (int i = 0; i < row; i++)
				for (int j = 0; j < col; j++) {
					if (curBoard[i][j] == testedPlayer && canMove(i, j)) {
						res.add(new Pair(i, j));
					}
				}
		}
		return res;
	}

	/**
	 * test whether cur piece can move to its neighbor point
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	private boolean canMove(int i, int j) {
		// TODO Auto-generated method stub
		if (isValid(i - 1, j) || isValid(i + 1, j) || isValid(i, j + 1)
				|| isValid(i, j - 1))
			return true;
		if (this.moveDir[i][j])
			return isValid(i + 1, j + 1) || isValid(i - 1, j - 1)
					|| isValid(i + 1, j - 1) || isValid(i - 1, j + 1);
		return false;
	}

	/**
	 * get candidate move for choosed piece in point(x,y), which will capture
	 * the pieces of opponent. If there is no capture, the candiate move will
	 * include all possible move
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public List<Pair<Integer, Integer>> getCandidateMove(int x, int y) {
		List<Pair<Integer, Integer>> res = new ArrayList<Pair<Integer, Integer>>();
		if (canCapture(x, y, x + 1, y))
			res.add(new Pair(x + 1, y));
		if (canCapture(x, y, x - 1, y))
			res.add(new Pair(x - 1, y));
		if (canCapture(x, y, x, y + 1))
			res.add(new Pair(x, y + 1));
		if (canCapture(x, y, x, y - 1))
			res.add(new Pair(x, y - 1));
		if (this.moveDir[x][y]) { // 8 direction
			if (canCapture(x, y, x + 1, y + 1))
				res.add(new Pair(x + 1, y + 1));
			if (canCapture(x, y, x - 1, y - 1))
				res.add(new Pair(x - 1, y - 1));
			if (canCapture(x, y, x - 1, y + 1))
				res.add(new Pair(x - 1, y + 1));
			if (canCapture(x, y, x + 1, y - 1))
				res.add(new Pair(x + 1, y - 1));
		}
		if (res.size() == 0) {
			if (isValid(x + 1, y))
				res.add(new Pair(x + 1, y));
			if (isValid(x - 1, y))
				res.add(new Pair(x - 1, y));
			if (isValid(x, y + 1))
				res.add(new Pair(x, y + 1));
			if (isValid(x, y - 1))
				res.add(new Pair(x, y - 1));
			if (this.moveDir[x][y]) { // 8 direction
				if (isValid(x + 1, y + 1))
					res.add(new Pair(x + 1, y + 1));
				if (isValid(x - 1, y - 1))
					res.add(new Pair(x - 1, y - 1));
				if (isValid(x - 1, y + 1))
					res.add(new Pair(x - 1, y + 1));
				if (isValid(x + 1, y - 1))
					res.add(new Pair(x + 1, y - 1));
			}
		}
		return res;
	}

	/**
	 * test whether current point is valid move
	 * 
	 * @param curX
	 * @param curY
	 * @return
	 */
	private boolean isValid(int curX, int curY) {
		// TODO Auto-generated method stub
		if (curX < 0 || curX >= row || curY < 0 || curY >= col
				|| curBoard[curX][curY] != LogicBoard.PieceOwner.EMPTY)
			return false;
		return true;
	}

	/**
	 * current move will cause bi-direction remove piece, remove piece in
	 * Point(x,y) direction.
	 * 
	 * @param curX
	 * @param curY
	 * @param x
	 * @param y
	 * @return
	 */
	public List<Pair<Integer, Integer>> getBidicRemovedPieces(int curX,
			int curY, int x, int y) {
		List<Pair<Integer, Integer>> res = new ArrayList<Pair<Integer, Integer>>();
		if (curX == x) {
			if (curY > y) {
				for (int i = y - 1; i >= 0 && curBoard[curX][i] == opp; i--)
					res.add(new Pair(curX, i));
			} else {
				for (int i = y + 1; i < col && curBoard[curX][i] == opp; i++)
					res.add(new Pair(curX, i));
			}
		} else if (curY == y) {
			if (curX > x) {
				for (int i = x - 1; i >= 0 && curBoard[i][y] == opp; i--)
					res.add(new Pair(i, y));
			} else {
				for (int i = x + 1; i < row && curBoard[i][y] == opp; i++)
					res.add(new Pair(i, y));
			}
		} else if ((y - curY) == (x - curX)) {
			if (curX > x) {
				for (int i = 1; x - i >= 0 && y - i >= 0
						&& curBoard[x - i][y - i] == opp; i++)
					res.add(new Pair(x - i, y - i));
			} else {
				for (int i = 1; x + i < row && y + i < col
						&& curBoard[x + i][y + i] == opp; i++)
					res.add(new Pair(x + i, y + i));
			}
		} else {
			if (curX < x) {
				for (int i = 1; x + i < row && y - i >= 0
						&& curBoard[x + i][y - i] == opp; i++)
					res.add(new Pair(x + i, y - i));
			} else {
				for (int i = 1; x - i >= 0 && y + i < col
						&& curBoard[x - i][y + i] == opp; i++)
					res.add(new Pair(x - i, y + i));
			}
		}
		return res;
	}

	/**
	 * get removed pieces for computer player, if it will caused bi-direct
	 * remove, choose the direction which will capture more opponent pieces
	 * 
	 * @param curX
	 * @param curY
	 * @param x
	 * @param y
	 * @return
	 */
	public List<Pair<Integer, Integer>> getAndroidRemovedPieces(int curX,
			int curY, int x, int y) {
		List<Pair<Integer, Integer>> res = new ArrayList<Pair<Integer, Integer>>();
		List<Pair<Integer, Integer>> first = new ArrayList<Pair<Integer, Integer>>();
		List<Pair<Integer, Integer>> second = new ArrayList<Pair<Integer, Integer>>();
		int minX = Math.min(curX, x);
		int minY = Math.min(curY, y);
		int maxX = Math.max(curX, x);
		int maxY = Math.max(curY, y);
		if (curX == x) {
			boolean left = leftCapture(minX, minY, maxX, maxY);
			boolean right = rightCapture(minX, minY, maxY, maxY);
			if (left) {
				for (int i = minY - 1; i >= 0 && this.curBoard[curX][i] == opp; i--)
					first.add(new Pair(curX, i));
			}
			if (right) {
				for (int i = maxY + 1; i < col && curBoard[curX][i] == opp; i++)
					second.add(new Pair(curX, i));
			}

		} else if (curY == y) {
			boolean up = upCapture(minX, curY, maxX, y);
			boolean down = downCapture(minX, curY, maxX, curY);
			if (up) {
				for (int i = minX - 1; i >= 0 && curBoard[i][y] == opp; i--)
					first.add(new Pair(i, y));
			}
			if (down) {
				for (int i = maxX + 1; i < row && curBoard[i][y] == opp; i++)
					second.add(new Pair(i, y));
			}

		} else if ((y - curY) == (x - curX)) {
			boolean leftUp = leftUpCapture(minX, minY, maxX, maxY);
			boolean rightDown = rightDownCapture(minX, minY, maxX, maxY);
			if (leftUp) {
				for (int i = 1; minX - i >= 0 && minY - i >= 0
						&& curBoard[minX - i][minY - i] == opp; i++)
					first.add(new Pair(minX - i, minY - i));
			}
			if (rightDown) {
				for (int i = 1; maxX + i < row && maxY + i < col
						&& curBoard[maxX + i][maxY + i] == opp; i++)
					second.add(new Pair(maxX + i, maxY + i));
			}
		} else {
			boolean leftDown = leftDownCapture(maxX, minY, minX, maxY);
			boolean rightUp = rightUpCapture(maxX, minY, minX, maxY);
			if (leftDown) {
				for (int i = 1; maxX + i < row && minY - i >= 0
						&& curBoard[maxX + i][minY - i] == opp; i++)
					first.add(new Pair(maxX + i, minY - i));
			}
			if (rightUp) {
				for (int i = 1; minX - i >= 0 && maxY + i < col
						&& curBoard[minX - i][maxY + i] == opp; i++)
					second.add(new Pair(minX - i, maxY + i));
			}
		}
		res.addAll(first.size() > second.size() ? first : second);
		return res;
	}

	/**
	 * get removed pieces after one move from point(curX, curY) to point(x,y)
	 * 
	 * @param curX
	 * @param curY
	 * @param x
	 * @param y
	 * @return
	 */
	public List<Pair<Integer, Integer>> getRemovedPieces(int curX, int curY,
			int x, int y) {
		List<Pair<Integer, Integer>> res = new ArrayList<Pair<Integer, Integer>>();
		int minX = Math.min(curX, x);
		int minY = Math.min(curY, y);
		int maxX = Math.max(curX, x);
		int maxY = Math.max(curY, y);
		if (curX == x) {
			boolean left = leftCapture(minX, minY, maxX, maxY);
			boolean right = rightCapture(minX, minY, maxY, maxY);
			if (left && right)
				res.add(null);
			if (left) {
				for (int i = minY - 1; i >= 0 && this.curBoard[curX][i] == opp; i--)
					res.add(new Pair(curX, i));
			}
			if (right) {
				for (int i = maxY + 1; i < col && curBoard[curX][i] == opp; i++)
					res.add(new Pair(curX, i));
			}
		} else if (curY == y) {
			boolean up = upCapture(minX, curY, maxX, y);
			boolean down = downCapture(minX, curY, maxX, curY);
			if (up && down)
				res.add(null);
			if (up) {
				for (int i = minX - 1; i >= 0 && curBoard[i][y] == opp; i--)
					res.add(new Pair(i, y));
			}
			if (down) {
				for (int i = maxX + 1; i < row && curBoard[i][y] == opp; i++)
					res.add(new Pair(i, y));
			}

		} else if ((y - curY) == (x - curX)) {
			boolean leftUp = leftUpCapture(minX, minY, maxX, maxY);
			boolean rightDown = rightDownCapture(minX, minY, maxX, maxY);
			if (leftUp && rightDown)
				res.add(null);
			if (leftUp) {
				for (int i = 1; minX - i >= 0 && minY - i >= 0
						&& curBoard[minX - i][minY - i] == opp; i++)
					res.add(new Pair(minX - i, minY - i));
			}
			if (rightDown) {
				for (int i = 1; maxX + i < row && maxY + i < col
						&& curBoard[maxX + i][maxY + i] == opp; i++)
					res.add(new Pair(maxX + i, maxY + i));
			}
		} else {
			boolean leftDown = leftDownCapture(maxX, minY, minX, maxY);
			boolean rightUp = rightUpCapture(maxX, minY, minX, maxY);
			if (leftDown && rightUp)
				res.add(null);
			if (leftDown) {
				for (int i = 1; maxX + i < row && minY - i >= 0
						&& curBoard[maxX + i][minY - i] == opp; i++)
					res.add(new Pair(maxX + i, minY - i));
			}
			if (rightUp) {
				for (int i = 1; minX - i >= 0 && maxY + i < col
						&& curBoard[minX - i][maxY + i] == opp; i++)
					res.add(new Pair(minX - i, maxY + i));
			}
		}
		return res;
	}

	/**
	 * test whether piece in point(testX, testY) can be capture by piece in
	 * point(curX, curY)
	 * 
	 * @param curX
	 * @param curY
	 * @param testX
	 * @param testY
	 * @return
	 */
	public boolean canCapture(int curX, int curY, int testX, int testY) {
		if (curX < 0 || curX >= row || curY < 0 || curY >= col || testX < 0
				|| testX >= row || testY < 0 || testY >= col)
			return false;
		if (curBoard[testX][testY] != LogicBoard.PieceOwner.EMPTY)
			return false;
		int minY = Math.min(curY, testY);
		int maxY = Math.max(curY, testY);
		int minX = Math.min(curX, testX);
		int maxX = Math.max(curX, testX);

		return (curX == testX && (leftCapture(curX, minY, curX, maxY) || rightCapture(
				curX, minY, curX, maxY)))
				|| (curY == testY && (upCapture(minX, curY, maxX, curY) || downCapture(
						minX, curY, maxX, curY)))
				|| ((curY - testY) == (curX - testX) && (leftUpCapture(minX,
						minY, maxX, maxY) || rightDownCapture(minX, minY, maxX,
						maxY)))
				|| ((curY - testY) == (testX - curX) && (leftDownCapture(maxX,
						minY, minX, maxY) || rightUpCapture(maxX, minY, minX,
						maxY)));
	}

	public boolean upCapture(int curX, int curY, int x, int y) {
		return (curX - 1 >= 0 && curBoard[curX - 1][curY] == opp);
	}

	public boolean downCapture(int curX, int curY, int x, int y) {
		return (x + 1 < row && curBoard[x + 1][curY] == opp);
	}

	public boolean leftCapture(int curX, int curY, int x, int y) {
		return (curY - 1 >= 0 && curBoard[curX][curY - 1] == opp);
	}

	public boolean rightCapture(int curX, int curY, int x, int y) {
		return (y + 1 < col && curBoard[curX][y + 1] == opp);
	}

	public boolean leftUpCapture(int curX, int curY, int x, int y) {
		return (curY - 1 >= 0 && curX - 1 >= 0 && curBoard[curX - 1][curY - 1] == opp);
	}

	public boolean leftDownCapture(int curX, int curY, int x, int y) {
		return (curY - 1 >= 0 && curX + 1 < row && curBoard[curX + 1][curY - 1] == opp);
	}

	public boolean rightDownCapture(int curX, int curY, int x, int y) {
		return (y + 1 < col && x + 1 < row && curBoard[x + 1][y + 1] == opp);
	}

	public boolean rightUpCapture(int curX, int curY, int x, int y) {
		return (x - 1 >= 0 && y + 1 < col && curBoard[x - 1][y + 1] == opp);
	}

	/**
	 * test piece in point(x,y) can be selected
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean canBeSelect(int x, int y) {
		boolean flag = canCapture(x, y, x + 1, y) || canCapture(x, y, x - 1, y)
				|| canCapture(x, y, x, y + 1) || canCapture(x, y, x, y - 1);
		if (moveDir[x][y]) { // 8 direction
			flag = flag || canCapture(x, y, x + 1, y + 1)
					|| canCapture(x, y, x - 1, y - 1)
					|| canCapture(x, y, x + 1, y - 1)
					|| canCapture(x, y, x - 1, y + 1);
		}
		return flag;
	}

}
