package com.example.fanoronayc;

import logic.LogicBoard;
import logic.Judge;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.util.Log;

public class GameActivity extends Activity {
	private static final String TAG = GameActivity.class.getSimpleName();
	public BoardView boardView;
	private LogicBoard board;
	private int row;
	private int col;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			Intent intent = getIntent();
			row = intent.getIntExtra("row", 0);
			col = intent.getIntExtra("col", 0);
			String piece = intent.getStringExtra("piece");
			int hard = intent.getIntExtra("diff", 0);
			Judge.getInstance().Initial(piece, row, col, hard);
			boardView = new BoardView(this, row, col);
			setContentView(boardView);
			boardView.requestFocus();
		}catch(Exception e){
			Log.e(TAG, "error", e);
		}

	}
}
