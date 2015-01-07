package com.example.fanoronayc;

import logic.LogicBoard;
import logic.LogicBoard.PieceOwner;
import logic.Judge;
import logic.AlgInformation;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
/**
 * this class is the view of board
 * @author sunny
 *
 */
public class BoardView extends View {
	private static final String TAG = BoardView.class.getSimpleName();
	private final GameActivity game;
	private float width; // width of one tile
	private float height; // height of one tile
	private float border;
	private int row;
	private int col;
	private float radius;
	private final Rect selRect = new Rect();
	private PointF[][] loc;
	private boolean bSelect;
	private final LogicBoard.PieceOwner[][] board;
	private final LogicBoard.PieceState[][] stageBoard;
	private final Judge judge;
	private final Paint backgroundP;
	private final Paint blackP;
	private final Paint whiteP;
	private final Paint selectP;
	private final Paint nextP;
	private final Paint biChooseP;
	private final Paint candidateP;
	private int selX, selY;

	public BoardView(Context context, int r, int c) {
		super(context);
		// TODO Auto-generated constructor stub
		this.game = (GameActivity) context;

		row = r;
		col = c;
		if (col == 3) {
			border = 80;
			radius = 70;
		} else if (col == 5) {
			border = 60;
			radius = 50;
		} else if (col == 9) {
			border = 40;
			radius = 30;
		}
		loc = new PointF[row][col];
		setFocusable(true);
		setFocusableInTouchMode(true);
		LogicBoard b = LogicBoard.getInstance();
		stageBoard = b.getVirtualBoard();
		board = b.getPieceArrange();
		judge = Judge.getInstance();
		
		for (int i = 0; i < row; i++)
			for (int j = 0; j < col; j++)
				loc[i][j] = new PointF();
		bSelect = false;
		backgroundP = new Paint();
		backgroundP.setColor(getResources().getColor(R.color.board_background));

		blackP = new Paint();
		blackP.setColor(getResources().getColor(R.color.board_dark));

		whiteP = new Paint();
		whiteP.setColor(getResources().getColor(R.color.board_hilite));

		selectP = new Paint();
		selectP.setColor(getResources().getColor(R.color.board_light));

		nextP = new Paint();
		nextP.setColor(getResources().getColor(R.color.next));

		biChooseP = new Paint();
		biChooseP.setColor(getResources().getColor(R.color.bi_dic));

		candidateP = new Paint();
		candidateP.setColor(getResources().getColor(R.color.candidate));
		if(judge.curPlayer == PieceOwner.BLACK){
			judge.turnPlayer();
			androidPlay();
		}
	}

	@Override
	/**
	 * draw the board, piece according to the data store in boardLogic 
	 */
	protected void onDraw(Canvas canvas) {
		canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundP);

		for (int i = 0; i < row; i++) {
			canvas.drawLine(loc[i][0].x, loc[i][0].y, loc[i][col - 1].x,
					loc[i][col - 1].y, whiteP);
		}
		for (int i = 0; i < col; i++) {
			canvas.drawLine(loc[0][i].x, loc[0][i].y, loc[row - 1][i].x,
					loc[row - 1][i].y, whiteP);
		}
		if (col == 3) {
			canvas.drawLine(loc[0][0].x, loc[0][0].y, loc[2][2].x, loc[2][2].y,
					whiteP);
			canvas.drawLine(loc[0][2].x, loc[0][2].y, loc[2][0].x, loc[2][0].y,
					whiteP);
		}
		if (col == 5 || col == 9) {
			int flag = 0;
			int mid = row / 2;
			for (int i = 0; i < col; i += 2) {
				if (flag % 2 == 0) { // diagonal
					if (i + row - 1 < col)
						canvas.drawLine(loc[0][i].x, loc[0][i].y,
								loc[row - 1][i + row - 1].x, loc[row - 1][i
										+ row - 1].y, whiteP);
					if (i - row + 1 >= 0)
						canvas.drawLine(loc[0][i].x, loc[0][i].y,
								loc[row - 1][i - row + 1].x, loc[row - 1][i
										- row + 1].y, whiteP);
					flag++;
				} else if (flag % 2 == 1) {// rect
					canvas.drawLine(loc[0][i].x, loc[0][i].y,
							loc[mid][i + mid].x, loc[mid][i + mid].y, whiteP);
					canvas.drawLine(loc[mid][i + mid].x, loc[mid][i + mid].y,
							loc[row - 1][i].x, loc[row - 1][i].y, whiteP);
					canvas.drawLine(loc[row - 1][i].x, loc[row - 1][i].y,
							loc[mid][i - mid].x, loc[mid][i - mid].y, whiteP);
					canvas.drawLine(loc[mid][i - mid].x, loc[mid][i - mid].y,
							loc[0][i].x, loc[0][i].y, whiteP);
					flag++;
				}
			}
		}
		for (int i = 0; i < row; i++)
			for (int j = 0; j < col; j++)
				if (board[i][j] == LogicBoard.PieceOwner.BLACK)
					canvas.drawCircle(loc[i][j].x, loc[i][j].y, radius, blackP);
				else if (board[i][j] == LogicBoard.PieceOwner.WITHE)
					canvas.drawCircle(loc[i][j].x, loc[i][j].y, radius, whiteP);

		for (int i = 0; i < row; i++)
			for (int j = 0; j < col; j++)
				if (stageBoard[i][j] == LogicBoard.PieceState.CHOOSE_DIC)
					canvas.drawCircle(loc[i][j].x, loc[i][j].y, radius,
							biChooseP);
				else if (stageBoard[i][j] == LogicBoard.PieceState.SELECT)
					canvas.drawCircle(loc[i][j].x, loc[i][j].y, radius + 20,
							selectP);
				else if (stageBoard[i][j] == LogicBoard.PieceState.NEXT)
					canvas.drawCircle(loc[i][j].x, loc[i][j].y, radius, nextP);
				else if (stageBoard[i][j] == LogicBoard.PieceState.CAN_SEL)
					canvas.drawCircle(loc[i][j].x, loc[i][j].y, radius + 20,
							candidateP);
	}

	@Override
	/**
	 * touch event when use choose piece, move piece
	 */
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		float x = event.getX();
		float y = event.getY();
		float dis = (float) Math.pow(radius, 2);
		for (int i = 0; i < row; i++)
			for (int j = 0; j < col; j++) {
				if (Math.pow(x - loc[i][j].x, 2) + Math.pow(y - loc[i][j].y, 2) <= dis) {
					if (bSelect
							&& stageBoard[i][j] == LogicBoard.PieceState.NEXT) {
						if (judge.movePiece(selX, selY, i, j)) {
							if(!isWin()){
								bSelect = false;
								judge.turnPlayer();
								androidPlay();
							}
						}
					} else if (bSelect
							&& stageBoard[i][j] == LogicBoard.PieceState.CHOOSE_DIC) {
						judge.removePiece(selX, selY, i, j);
						if(!isWin()){
							bSelect = false;
							judge.turnPlayer();
							androidPlay();
						}
					} else if (judge.selectPiece(i, j)) {
						selX = i;
						selY = j;
						bSelect = true;
					} else
						Toast.makeText(game,
								game.getString(R.string.wrong_piece),
								Toast.LENGTH_SHORT).show();
					break;
				}
			}
		invalidate();
		return true;
	}
	/**
	 * computer select and move its pieces
	 */
	private void androidPlay() {
		Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> actions = judge.action;
		AlgInformation info = judge.computer.info;
		AlertDialog.Builder builder = new AlertDialog.Builder(game);
		builder.setTitle("The next move of Andorid")
				.setMessage(
						"Andorid moves Piece from " + actions.first.first + ","
								+ actions.first.second + " to "
								+ actions.second.first + " , "
								+ actions.second.second + "\n cutoff is used: "
								+ info.cutoff + "\n total number of nodes generated: "
								+ info.nodesCount + "\n maximum depth of game tree:  " + info.level
								+ "\n pruning times occured in MAX-VALUE: "
								+ info.maxPruningCount
								+ "\n pruning time soccured in MIN-VALUE: "
								+ info.minPruningCount)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								judge.androidMove();
								isWin();
								game.boardView.invalidate();
							}

						}) // Do nothing on no
				.show();

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = (float) (w - border * 2) / (col - 1);
		height = (float) (h - border * 2) / (row - 1);
//		getRect(selX, selY, selRect);
		setLocation();
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	/**
	 * test whether user or computer is win.
	 * @return
	 */
	private boolean isWin() {
		LogicBoard.PieceOwner res = judge.isWin();
		if (res == LogicBoard.PieceOwner.BLACK) {
			AlertDialog.Builder builder = new AlertDialog.Builder(game);
			builder.setTitle("Game over")
					.setMessage(game.getString(R.string.black_win))
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									game.finish();
								}

							}) // Do nothing on no
					.show();
			return true;
		} else if (res == LogicBoard.PieceOwner.WITHE) {
			AlertDialog.Builder builder = new AlertDialog.Builder(game);
			builder.setTitle("Game over")
					.setMessage(game.getString(R.string.white_win))
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									game.finish();
								}

							}) // Do nothing on no
					.show();
			return true;
		}
		return false;
	}
	/**
	 * calculate the coordinate of each point in the board
	 */
	private void setLocation() {
		for (int i = 0; i < row; i++)
			for (int j = 0; j < col; j++)
				loc[i][j].set(j * width + border, i * height + border);
	}
	
//	private void getRect(int x, int y, Rect rect) {
//		float big_width = width;
//		float big_height = height;
//		rect.set((int) (x * big_width), (int) (y * big_height), (int) (x
//				* big_width + big_width), (int) (y * big_height + big_height));
//	}
}
