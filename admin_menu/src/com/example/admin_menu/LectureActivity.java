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

import com.example.admin_menu.data.LectureData;
import com.example.admin_menu.listview.LectureListAdapter;
import com.example.admin_menu.network.NetworkThread.deleteLectureTableRunnable;
import com.example.admin_menu.network.NetworkThread.getLectureDataRunnable;
import com.example.admin_menu.network.NetworkThread.updateLectureTableRunnable;
import com.example.admin_menu.utils.Defines;
import com.example.smart_attbook.R;












public class LectureActivity extends Activity implements OnScrollListener, OnClickListener {
	public static final String				TAG											= "LectureActivity";
	
	private ListView 						m_lvLecture								= null;
	private TextView						m_tvEmpty									= null;
	private ListViewItemClickListener		m_lvItemClickListener						= new ListViewItemClickListener();
	private LectureListAdapter 				m_listAdapter								= null;
	private View							m_viewFooter								= null;
	private boolean							m_bShowFooterView							= false;
	
	private MainHandler						m_mainHandler								= null;
	private getLectureDataRunnable			m_getLectureDataRunnable					= null;
	private deleteLectureTableRunnable		m_deleteLectureTableRunnable				= null;
	private updateLectureTableRunnable		m_updateLectureTableRunnable					= null;
	
	private ArrayList<LectureData>			m_listLectureData							= null;
	private int								m_nTotLectureDataCnt						= 0;
	private LectureData						m_editLectureData								= null;
	private boolean							m_bResetListView							= false;
	private int								m_selectedListItemPosition					= -1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture);
        
        m_lvLecture = (ListView)findViewById(R.id.lv_lecture);
        m_tvEmpty = (TextView)findViewById(R.id.tv_main_view_empty);
        
        // Inflate Footer View & add in LectureListView
        m_viewFooter = getLayoutInflater().inflate(R.layout.list_footer, null);
        m_lvLecture.addFooterView(m_viewFooter, null, false);
        m_bShowFooterView = true;
        
        // create an ProfessorListAdapter
        m_listAdapter = new LectureListAdapter(this);
        
		// listView에 Adapter 연결
        m_lvLecture.setAdapter(m_listAdapter);
        
		// ProfessorListView set Item click listener
        m_lvLecture.setOnItemClickListener(m_lvItemClickListener);
        
        // lectureListView set scroll listener
        m_lvLecture.setOnScrollListener(this);
        registerForContextMenu(m_lvLecture);
        
        m_mainHandler = new MainHandler();
        m_getLectureDataRunnable = new getLectureDataRunnable(LectureActivity.this, m_mainHandler);
        m_deleteLectureTableRunnable = new deleteLectureTableRunnable(LectureActivity.this, m_mainHandler);
        
        runOnUiThread(m_getLectureDataRunnable);
    }
	
	private class ListViewItemClickListener implements OnItemClickListener {
		
		
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d(TAG, m_listLectureData.get(position).strName);
			
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
			
			case Defines.HND_MSG_GET_LECTURE_DATA:
				if (m_bResetListView == true) {
					m_nTotLectureDataCnt = 0;
					if (m_listLectureData != null) {
						m_listLectureData.clear();
						m_listLectureData = null;
					}
					m_bResetListView = false;
				}
				
				if (m_nTotLectureDataCnt == 0) {
					m_nTotLectureDataCnt = m_getLectureDataRunnable.getTotLectureDataCnt();
					Log.d(TAG, "Tot Lecture Data Count: "+m_nTotLectureDataCnt);
				}
				
				if (m_listLectureData == null) {
					m_listLectureData = m_getLectureDataRunnable.getLectureDataList();
				}
				
				if (m_listLectureData != null)
					m_listAdapter.setListItems(m_listLectureData);
				
				m_listAdapter.notifyDataSetChanged();
				
				// Data를 로딩 해 왔으므로 FooterView 삭제
				m_lvLecture.removeFooterView(m_viewFooter);
				m_bShowFooterView = false;
				
				// 조회한 데이터 내용이 없다면 empty message 표시
				if (m_nTotLectureDataCnt == 0)
					m_tvEmpty.setVisibility(View.VISIBLE);
				else
					m_tvEmpty.setVisibility(View.INVISIBLE);
				break;
				
			case Defines.HND_MSG_DELETE_LECTURE_DATA:
				m_deleteLectureTableRunnable.getProgressDialog().dismiss();
				
				if (m_deleteLectureTableRunnable.getResult() == 0) {
					// 삭제 실패
					showDialog(Defines.DIALOG_TABLE_DELETE_ALRET);
				} else {
					// 삭제 성공 listView 갱신
					if (m_selectedListItemPosition  >= 0) {
						m_listLectureData.remove(m_selectedListItemPosition);
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
    	
    	MenuItem itemNewLecture = menu.add(base, base, Menu.NONE, "new lecture");
        return true;
    }
    
    /*
     * Menu 선택 했을때 동작
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case 1:
			Intent intentEditLecture = new Intent(this, LectureEditActivity.class);
			intentEditLecture.putExtra(Defines.INTENT_LECTURE_EDIT_MODE, Defines.MODE_NEW);	// 신규 모드
			startActivityForResult(intentEditLecture, Defines.REQUEST_CODE_EDIT_LECTURE);
			break;

		default:
			break;
		}

    	return super.onOptionsItemSelected(item);
    }
    
@Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
   	if (v.getId() == R.id.lv_lecture) {
  		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			menu.setHeaderTitle(m_listLectureData.get(info.position).strName);
			menu.add(0, Defines.MODE_UPDATE, 0, R.string.list_context_menu_update);
			menu.add(0, Defines.MODE_DELETE, 1, R.string.list_context_menu_delete);
			m_editLectureData = m_listLectureData.get(info.position);
			
			m_selectedListItemPosition = info.position;
		}
	}
    
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		
		switch (menuItemIndex) {
	     case Defines.MODE_UPDATE:
			Intent intent = new Intent(this, LectureEditActivity.class);
			intent.putExtra(Defines.INTENT_LECTURE_EDIT_MODE, Defines.MODE_UPDATE);	// 수정 모드
			if (m_editLectureData != null)
				intent.putExtra(Defines.INTENT_LECTURE_DATA, m_editLectureData);
			startActivityForResult(intent, Defines.REQUEST_CODE_EDIT_LECTURE);
			break;
			
		case Defines.MODE_DELETE:
			if (m_editLectureData != null) {
				m_deleteLectureTableRunnable.setPHPParameter(m_editLectureData);
				runOnUiThread(m_deleteLectureTableRunnable);
				finish();
				Intent f = new Intent(LectureActivity.this, LectureActivity.class);
				startActivity(f);
				
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
		case Defines.REQUEST_CODE_EDIT_LECTURE:
			if (resultCode == RESULT_OK) { // EditLectureActivity의 결과 코드
				// Lecture ListView 갱신
				resetLectureListView();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void resetLectureListView() {
		m_bResetListView = true;
		
		runOnUiThread(m_getLectureDataRunnable);
	}
	

}
