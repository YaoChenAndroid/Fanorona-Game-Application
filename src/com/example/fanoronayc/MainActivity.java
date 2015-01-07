package com.example.fanoronayc;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
/**
 * this function the entry point of whole project
 * @author sunny
 *
 */
public class MainActivity extends ActionBarActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	/**
	 * start new activity and get user's choose of piece color and difficult level
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final RadioGroup choosePiece = (RadioGroup) findViewById(R.id.radioGroup1);
		final Button button33 = (Button) findViewById(R.id.button33);
		final RadioGroup chooseDiff = (RadioGroup) findViewById(R.id.radioGroup2);
		button33.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				Intent intent = new Intent(MainActivity.this,
						GameActivity.class);
				intent.putExtra("row", 3);
				intent.putExtra("col", 3);
				RadioButton chose = (RadioButton) findViewById(choosePiece
						.getCheckedRadioButtonId());
				intent.putExtra("piece", chose.getText().toString());
				RadioButton diffNum = (RadioButton)findViewById(chooseDiff.getCheckedRadioButtonId());
				intent.putExtra("diff", Integer.valueOf(diffNum.getText().toString()));
				startActivity(intent);
				// startActivity(new Intent(MainActivity.this,
				// GameActivity.class));
			}
		});
		final Button button55 = (Button) findViewById(R.id.button55);
		button55.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						GameActivity.class);
				intent.putExtra("row", 5);
				intent.putExtra("col", 5);
				RadioButton chose = (RadioButton) findViewById(choosePiece
						.getCheckedRadioButtonId());
				intent.putExtra("piece", chose.getText().toString());

				RadioButton diffNum = (RadioButton)findViewById(chooseDiff.getCheckedRadioButtonId());
				intent.putExtra("diff", Integer.valueOf(diffNum.getText().toString()));
				startActivity(intent);
			}
		});
		final Button button59 = (Button) findViewById(R.id.button59);
		button59.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						GameActivity.class);
				intent.putExtra("row", 5);
				intent.putExtra("col", 9);
				RadioButton chose = (RadioButton) findViewById(choosePiece
						.getCheckedRadioButtonId());
				intent.putExtra("piece", chose.getText().toString());

				RadioButton diffNum = (RadioButton)findViewById(chooseDiff.getCheckedRadioButtonId());
				intent.putExtra("diff", Integer.valueOf(diffNum.getText().toString()));
				startActivity(intent);
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
