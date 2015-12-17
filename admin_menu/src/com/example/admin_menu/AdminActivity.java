package com.example.admin_menu;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.example.smart_attbook.R;

public class AdminActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin__menu);
		
		Button btn_student =(Button)findViewById(R.id.Btn_student);
		Button btn_professor =(Button)findViewById(R.id.Btn_professor);
		Button btn_lecture =(Button)findViewById(R.id.Btn_lecture);
		
		btn_student.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(AdminActivity.this, StudentActivity.class);
				startActivity(intent);
			}
		});
		
		btn_professor.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(AdminActivity.this, ProfessorActivity.class);
				startActivity(intent);
			}
		});
		
		btn_lecture.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(AdminActivity.this, LectureActivity.class);
				startActivity(intent);
			}
		});
		
	}
	

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.admin__menu, menu);
		return true;
	}

}
