package com.example.student_app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Student_number_save extends Activity {

	 Button btn1;
	  
	 EditText et1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_student_number_save);
		
		btn1 = (Button)findViewById(R.id.button1);
		btn1.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				
				et1= (EditText)findViewById(R.id.editText1);
				editor.putString("editText", et1.getText().toString());
				editor.commit();
				
				Intent s = new Intent(Student_number_save.this,Student_lecture_select.class);
				
				startActivity(s);
				finish();
				
			}
		});
		
		
		
		
	}

//	@Override
//	protected void onStop() {
//		
//		super.onStop();
//	}
//	
}
