package com.example.admin_menu;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.admin_menu.data.StudentData;
import com.example.admin_menu.listview.StudentListAdapter;
import com.example.admin_menu.network.NetworkThread.deleteStudentTableRunnable;
import com.example.admin_menu.network.NetworkThread.getStudentDataRunnable;
import com.example.admin_menu.network.NetworkThread.updateStudentTableRunnable;
import com.example.admin_menu.utils.Defines;
import com.example.smart_attbook.R;




public class StudentActivity extends Activity implements OnScrollListener, OnClickListener {
	public static final String				TAG											= "StudentActivity";
	
	private ListView 						m_lvStudent									= null;
	private TextView						m_tvEmpty									= null;
	private ListViewItemClickListener		m_lvItemClickListener						= new ListViewItemClickListener();
	private StudentListAdapter 				m_listAdapter								= null;
	private View							m_viewFooter								= null;
	private boolean							m_bShowFooterView							= false;
	
	private MainHandler						m_mainHandler								= null;
	private getStudentDataRunnable			m_getStudentDataRunnable					= null;
	private deleteStudentTableRunnable		m_deleteStudentTableRunnable				= null;
	private updateStudentTableRunnable		m_updateStudentTableRunnable					= null;
	
	private ArrayList<StudentData>			m_listStudentData							= null;
	private int								m_nTotStudentDataCnt						= 0;
	private StudentData						m_editStudentData								= null;
	private boolean							m_bResetListView							= false;
	private int								m_selectedListItemPosition					= -1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        
        m_lvStudent = (ListView)findViewById(R.id.lv_student);
        m_tvEmpty = (TextView)findViewById(R.id.tv_main_view_empty);
        
        // Inflate Footer View & add in StudentListView
        m_viewFooter = getLayoutInflater().inflate(R.layout.list_footer, null);
        m_lvStudent.addFooterView(m_viewFooter, null, false);
        m_bShowFooterView = true;
        
        // create an StudentListAdapter
        m_listAdapter = new StudentListAdapter(this);
        
		// listView에 Adapter 연결
        m_lvStudent.setAdapter(m_listAdapter);
        
		// StudentListView set Item click listener
        m_lvStudent.setOnItemClickListener(m_lvItemClickListener);
        
        // BookListView set scroll listener
        m_lvStudent.setOnScrollListener(this);
        registerForContextMenu(m_lvStudent);
        
        m_mainHandler = new MainHandler();
        m_getStudentDataRunnable = new getStudentDataRunnable(StudentActivity.this, m_mainHandler);
        m_deleteStudentTableRunnable = new deleteStudentTableRunnable(StudentActivity.this, m_mainHandler);
       
        runOnUiThread(m_getStudentDataRunnable);
    }
	
	private class ListViewItemClickListener implements OnItemClickListener {
		
		
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d(TAG, m_listStudentData.get(position).strName);
			
		 }
	}
	
	
	@SuppressLint("HandlerLeak")
	public class MainHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
//			case Defines.HND_MSG_EXIT:
//				m_bIsBackBtnTouched = false;
//				break;
			
			case Defines.HND_MSG_GET_STUDENT_DATA:
				if (m_bResetListView == true) {
					m_nTotStudentDataCnt = 0;
					if (m_listStudentData != null) {
						m_listStudentData.clear();
						m_listStudentData = null;
					}
					m_bResetListView = false;
				}
				
				if (m_nTotStudentDataCnt == 0) {
					m_nTotStudentDataCnt = m_getStudentDataRunnable.getTotStudentDataCnt();
					Log.d(TAG, "Tot Student Data Count: "+m_nTotStudentDataCnt);
				}
				
				if (m_listStudentData == null) {
					m_listStudentData = m_getStudentDataRunnable.getStudentDataList();
				}
				
				if (m_listStudentData != null)
					m_listAdapter.setListItems(m_listStudentData);
				
				m_listAdapter.notifyDataSetChanged();
				
				// Data를 로딩 해 왔으므로 FooterView 삭제
				m_lvStudent.removeFooterView(m_viewFooter);
				m_bShowFooterView = false;
				
				// 조회한 데이터 내용이 없다면 empty message 표시
				if (m_nTotStudentDataCnt == 0)
					m_tvEmpty.setVisibility(View.VISIBLE);
				else
					m_tvEmpty.setVisibility(View.INVISIBLE);
				break;
				
			case Defines.HND_MSG_DELETE_STUDENT_DATA:
				m_deleteStudentTableRunnable.getProgressDialog().dismiss();
				
				if (m_deleteStudentTableRunnable.getResult() == 0) {
					// 삭제 실패
					showDialog(Defines.DIALOG_TABLE_DELETE_ALRET);
				} else {
					// 삭제 성공 listView 갱신
					if (m_selectedListItemPosition  >= 0) {
						m_listStudentData.remove(m_selectedListItemPosition);
						m_listAdapter.notifyDataSetChanged();
						m_selectedListItemPosition = -1;
					}
				}
				break;
				
				
			default:
				super.handleMessage(msg);
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}
	
    /*
     * Menu 등록
     */
    public boolean onCreateOptionsMenu(Menu menu) {
    	int base = Menu.FIRST;
    	
    	MenuItem itemNewStudent = menu.add(base, base, Menu.NONE, "new student");
        return true;
    }
    
    /*
     * Menu 선택 했을때 동작
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case 1:
			Intent intentEditStudent = new Intent(this, StudentEditActivity.class);
			intentEditStudent.putExtra(Defines.INTENT_STUDENT_EDIT_MODE, Defines.MODE_NEW);	// 신규 모드
			startActivityForResult(intentEditStudent, Defines.REQUEST_CODE_EDIT_STUDENT);
			break;

		default:
			break;
		}

    	return super.onOptionsItemSelected(item);
    }
    
@Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
   	if (v.getId() == R.id.lv_student) {
  		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			menu.setHeaderTitle(m_listStudentData.get(info.position).strName);
			menu.add(0, Defines.MODE_UPDATE, 0, R.string.list_context_menu_update);
			menu.add(0, Defines.MODE_DELETE, 1, R.string.list_context_menu_delete);
			m_editStudentData = m_listStudentData.get(info.position);
			
			m_selectedListItemPosition = info.position;
		}
	}
    
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		
		switch (menuItemIndex) {
	     case Defines.MODE_UPDATE:
			Intent intent = new Intent(this, StudentEditActivity.class);
			intent.putExtra(Defines.INTENT_STUDENT_EDIT_MODE, Defines.MODE_UPDATE);	// 수정 모드
			if (m_editStudentData != null)
				intent.putExtra(Defines.INTENT_STUDENT_DATA, m_editStudentData);
			startActivityForResult(intent, Defines.REQUEST_CODE_EDIT_STUDENT);
			break;
			
		case Defines.MODE_DELETE:
			if (m_editStudentData != null) {
				m_deleteStudentTableRunnable.setPHPParameter(m_editStudentData);
				runOnUiThread(m_deleteStudentTableRunnable);
				
				finish();
				Intent s = new Intent(StudentActivity.this, StudentActivity.class);
				startActivity(s);
				}
			break;

		default:
			break;
		}
		
		return true;
	}
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Defines.REQUEST_CODE_EDIT_STUDENT:
			if (resultCode == RESULT_OK) { // EditStudentActivity의 결과 코드
				// Book ListView 갱신
				resetStudentListView();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void resetStudentListView() {
		m_bResetListView = true;
		
		runOnUiThread(m_getStudentDataRunnable);
	}
	

}
