package com.example.smart_attbook;

import java.util.ArrayList;


import com.example.smart_attbook.data.LectureData;
import com.example.smart_attbook.listview.Professor_lectureListAdapter;
import com.example.smart_attbook.network.NetworkThread.getLectureDataRunnable;
import com.example.smart_attbook.utils.Defines;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;



public class Professor_lecture_select extends Activity {
	public static final String				TAG											= "Professor_lecture_selectActivity";
	
	private ListView 						m_lvLecture									= null;
	private TextView						m_tvEmpty									= null;
	private ListViewItemClickListener		m_lvItemClickListener						= new ListViewItemClickListener();
	private Professor_lectureListAdapter 	m_listAdapter								= null;
	private View							m_viewFooter								= null;
	private boolean							m_bShowFooterView							= false;
	private MainHandler	m_mainHandler									= null;
    private getLectureDataRunnable m_getLectureDataRunnable =  null;
    
    // �� ���� ���
    private ArrayList<LectureData>  mMyLectureData = new ArrayList<LectureData>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_professor_lecture_select);
		
		
		m_lvLecture = (ListView)findViewById(R.id.lv_professor_lecture_select);
        m_tvEmpty = (TextView)findViewById(R.id.tv_main_view_empty);
        
        // Inflate Footer View & add in LectureListView
        m_viewFooter = getLayoutInflater().inflate(R.layout.list_footer, null);
        m_lvLecture.addFooterView(m_viewFooter, null, false);
        m_bShowFooterView = true;
        
        // create an ProfessorListAdapter
        m_listAdapter = new Professor_lectureListAdapter(this);
        
		// listView�� Adapter ����
        m_lvLecture.setAdapter(m_listAdapter);
        
		// ProfessorListView set Item click listener
        m_lvLecture.setOnItemClickListener(m_lvItemClickListener);
        // lectureListView set scroll listener
        
        
        
        registerForContextMenu(m_lvLecture);
		
		
		
		
	      /*
         * Server���� ������� ��ȸ�ϱ�
         */        
        
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
	    String text = pref.getString("editText", "");  
	    Log.e("text","text="+text);
	    
	    LectureData param = new LectureData("","",text,"","","","");
        m_mainHandler = new MainHandler();
        m_getLectureDataRunnable = new getLectureDataRunnable(this, m_mainHandler);
        m_getLectureDataRunnable.setPHPParameter(param);
       
        runOnUiThread(m_getLectureDataRunnable);
        Log.e("lecture","lecture="+mMyLectureData);
    }
	
	
     private class ListViewItemClickListener implements OnItemClickListener {
		
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d(TAG,  mMyLectureData.get(position).strName);
			
		
			Intent i = new Intent(Professor_lecture_select.this,Attendance_Student.class);
			
			
			
			
			i.putExtra("id",mMyLectureData.get(position).dLectureId );
			i.putExtra("name",mMyLectureData.get(position).strName);
			i.putExtra("place",mMyLectureData.get(position).strPlace);
			
			
			
			startActivity(i);
			
			
			
		 }
		
	}
     
     
	public class MainHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			
			
			switch (msg.what) {
//			case Defines.HND_MSG_EXIT:
//				m_bIsBackBtnTouched = false;
//				break;
			
			case Defines.HND_MSG_GET_LECTURE_DATA:
				mMyLectureData = m_getLectureDataRunnable.getLectureDataList();
				
				
				if (mMyLectureData != null)
					m_listAdapter.setListItems(mMyLectureData);
				
				m_listAdapter.notifyDataSetChanged();
				
				// Data�� �ε� �� �����Ƿ� FooterView ����
				m_lvLecture.removeFooterView(m_viewFooter);
				m_bShowFooterView = false;
				
				m_lvLecture.requestLayout();
				m_lvLecture.invalidateViews();
				
			default:
				super.handleMessage(msg);
				break;
			}
		}
	}
	}



