package com.example.smart_attbook;


import java.io.IOException;
import java.nio.CharBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.example.smart_attbook.Professor_lecture_select.MainHandler;

import com.example.smart_attbook.data.AttendanceData;
import com.example.smart_attbook.data.LectureData;
import com.example.smart_attbook.data.StudentData;
import com.example.smart_attbook.listview.Professor_lectureListAdapter;
import com.example.smart_attbook.listview.Professor_studentGridAdapter;


import com.example.smart_attbook.network.NetworkThread.getAttendanceDataRunnable;
import com.example.smart_attbook.network.NetworkThread.getL_StudentDataRunnable;
import com.example.smart_attbook.network.NetworkThread.getLectureDataRunnable;
import com.example.smart_attbook.utils.Defines;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Attendance_Student extends Activity {

	
	public static final String				TAG											= "Attendance_Student";
	
	private GridView 						m_gvStudent								= null;
	
	private TextView						m_tvname								=null;
	private TextView						m_tvplace								=null;
	private TextView						m_tvtime								=null;
	private Professor_studentGridAdapter 	m_gridAdapter								= null;
	private ImageView						m_icstudent								=null;
	private MainHandler						m_mainHandler								= null;
	
	private getL_StudentDataRunnable 		m_getL_StudentDataRunnable 				=  null;
	private getAttendanceDataRunnable 		m_getAttendanceDataRunnable 				=  null;
	private ArrayList<StudentData>  mMyStudentData = new ArrayList<StudentData>();
	
	private ArrayList<StudentData>  mMy_A_StudentData = new ArrayList<StudentData>();
	
	private boolean threadFlag = true; 
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attendance__student);
		SimpleDateFormat formater =new SimpleDateFormat("yyyy-MM-dd",Locale.KOREA);
		
		Date current =new Date();
		
		String day =formater.format(current);

		Intent i= getIntent(); 
		String id = i.getStringExtra("id");
		String name =i.getStringExtra("name");
		String place =i.getStringExtra("place");
		
		m_tvname=(TextView)findViewById(R.id.name);
		m_tvname.setText("("+name+")");
		m_tvplace=(TextView)findViewById(R.id.place);
		m_tvplace.setText("("+place+")");
		m_tvtime=(TextView)findViewById(R.id.time);
		m_tvtime.setText(day);
		m_gvStudent	 = (GridView)findViewById(R.id.gridview);
        
		//레이아웃 전개하기  위해서 inflater 씀  
		
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.activity_attendance_student_item, null);

		m_icstudent= (ImageView)linearLayout.findViewById(R.id.iv_attendance_student_list_item_image);
		m_icstudent.setBackgroundColor(Color.CYAN);
        m_gridAdapter = new Professor_studentGridAdapter(this);
        m_gvStudent	.setAdapter(m_gridAdapter);
        
        LectureData param = new LectureData(id,"","","","","","");
        m_mainHandler = new MainHandler();
        m_getL_StudentDataRunnable = new getL_StudentDataRunnable(this, m_mainHandler);
        m_getL_StudentDataRunnable.setPHPParameter(param);
       
        runOnUiThread(m_getL_StudentDataRunnable);
        
        
        
        AttendanceData param2 = new AttendanceData(id,"","",day,"");
        m_mainHandler = new MainHandler();
        m_getAttendanceDataRunnable = new getAttendanceDataRunnable(this, m_mainHandler);
        m_getAttendanceDataRunnable.setPHPParameter(param2);
       
        runOnUiThread(m_getAttendanceDataRunnable);
	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		m_getAttendanceDataRunnable.endThread();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.attendance__student, menu);
		return true;
	}
	public class MainHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			
			
			switch (msg.what) {
//			case Defines.HND_MSG_EXIT:
//				m_bIsBackBtnTouched = false;
//				break;
			case Defines.HND_MSG_GET_L_STUDENT_DATA:
				Log.e("Error", "HND_MSG_GET_L_STUDENT_DATA");
				mMyStudentData = m_getL_StudentDataRunnable.getStudentDataList();
				 
				break;
			case Defines.HND_MSG_GET_ATTENDANCE_DATA:
				Log.e("Error", "HND_MSG_GET_ATTENDANCE_DATA");
				mMy_A_StudentData = m_getAttendanceDataRunnable.getStudentDataList();
				
				
				//출석된 학생 데이터 불러와서 그리드뷰에 갱신하는 모듈
				if (mMy_A_StudentData != null) {
					m_gridAdapter.setListItems(mMyStudentData);
					m_gridAdapter.setMyStudentData(mMy_A_StudentData);
					m_gridAdapter.notifyDataSetChanged();
					m_gvStudent.requestLayout();
					m_gvStudent.invalidateViews();
				}

				
//				for( int i=0;i < mMy_A_StudentData.size();i++)
//				{
//					
//					Log.e("mMy_A_StudentData","mMy_A_StudentData"+mMy_A_StudentData.get(i).dStudentNumber);
//				
//				}
				
				
//				if (mMy_A_StudentData != null)
//				m_gridAdapter.setListItems(mMy_A_StudentData);
//				
//				m_gridAdapter.notifyDataSetChanged();
//				
//				m_gvStudent.requestLayout();
//				m_gvStudent.invalidateViews();
				
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	}
}
