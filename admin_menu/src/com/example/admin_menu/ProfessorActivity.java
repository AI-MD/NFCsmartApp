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

import com.example.admin_menu.data.ProfessorData;
import com.example.admin_menu.listview.ProfessorListAdapter;
import com.example.admin_menu.network.NetworkThread.deleteProfessorTableRunnable;
import com.example.admin_menu.network.NetworkThread.getProfessorDataRunnable;
import com.example.admin_menu.network.NetworkThread.updateProfessorTableRunnable;
import com.example.admin_menu.utils.Defines;
import com.example.smart_attbook.R;






public class ProfessorActivity extends Activity implements OnScrollListener, OnClickListener {
	public static final String				TAG											= "ProfessorActivity";
	
	private ListView 						m_lvProfessor								= null;
	private TextView						m_tvEmpty									= null;
	private ListViewItemClickListener		m_lvItemClickListener						= new ListViewItemClickListener();
	private ProfessorListAdapter 				m_listAdapter								= null;
	private View							m_viewFooter								= null;
	private boolean							m_bShowFooterView							= false;
	
	private MainHandler						m_mainHandler								= null;
	private getProfessorDataRunnable			m_getProfessorDataRunnable					= null;
	private deleteProfessorTableRunnable		m_deleteProfessorTableRunnable				= null;
	private updateProfessorTableRunnable		m_updateProfessorTableRunnable					= null;
	
	private ArrayList<ProfessorData>			m_listProfessorData							= null;
	private int								m_nTotProfessorDataCnt						= 0;
	private ProfessorData						m_editProfessorData								= null;
	private boolean							m_bResetListView							= false;
	private int								m_selectedListItemPosition					= -1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor);
        
        m_lvProfessor = (ListView)findViewById(R.id.lv_professor);
        m_tvEmpty = (TextView)findViewById(R.id.tv_main_view_empty);
        
        // Inflate Footer View & add in ProfessorListView
        m_viewFooter = getLayoutInflater().inflate(R.layout.list_footer, null);
        m_lvProfessor.addFooterView(m_viewFooter, null, false);
        m_bShowFooterView = true;
        
        // create an ProfessorListAdapter
        m_listAdapter = new ProfessorListAdapter(this);
        
		// listView에 Adapter 연결
        m_lvProfessor.setAdapter(m_listAdapter);
        
		// ProfessorListView set Item click listener
        m_lvProfessor.setOnItemClickListener(m_lvItemClickListener);
        
        // ProfessorListView set scroll listener
        m_lvProfessor.setOnScrollListener(this);
        registerForContextMenu(m_lvProfessor);
        
        m_mainHandler = new MainHandler();
        m_getProfessorDataRunnable = new getProfessorDataRunnable(ProfessorActivity.this, m_mainHandler);
        m_deleteProfessorTableRunnable = new deleteProfessorTableRunnable(ProfessorActivity.this, m_mainHandler);
        
        runOnUiThread(m_getProfessorDataRunnable);
    }
	
	private class ListViewItemClickListener implements OnItemClickListener {
		
		
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d(TAG, m_listProfessorData.get(position).strName);
			
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
			
			case Defines.HND_MSG_GET_PROFESSOR_DATA:
				if (m_bResetListView == true) {
					m_nTotProfessorDataCnt = 0;
					if (m_listProfessorData != null) {
						m_listProfessorData.clear();
						m_listProfessorData = null;
					}
					m_bResetListView = false;
				}
				
				if (m_nTotProfessorDataCnt == 0) {
					m_nTotProfessorDataCnt = m_getProfessorDataRunnable.getTotProfessorDataCnt();
					Log.d(TAG, "Tot Professor Data Count: "+m_nTotProfessorDataCnt);
				}
				
				if (m_listProfessorData == null) {
					m_listProfessorData = m_getProfessorDataRunnable.getProfessorDataList();
				}
				
				if (m_listProfessorData != null)
					m_listAdapter.setListItems(m_listProfessorData);
				
				m_listAdapter.notifyDataSetChanged();
				
				// Data를 로딩 해 왔으므로 FooterView 삭제
				m_lvProfessor.removeFooterView(m_viewFooter);
				m_bShowFooterView = false;
				
				// 조회한 데이터 내용이 없다면 empty message 표시
				if (m_nTotProfessorDataCnt == 0)
					m_tvEmpty.setVisibility(View.VISIBLE);
				else
					m_tvEmpty.setVisibility(View.INVISIBLE);
				break;
				
			case Defines.HND_MSG_DELETE_PROFESSOR_DATA:
				m_deleteProfessorTableRunnable.getProgressDialog().dismiss();
				
				if (m_deleteProfessorTableRunnable.getResult() == 0) {
					// 삭제 실패
					showDialog(Defines.DIALOG_TABLE_DELETE_ALRET);
				} else {
					// 삭제 성공 listView 갱신
					if (m_selectedListItemPosition  >= 0) {
						m_listProfessorData.remove(m_selectedListItemPosition);
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
    	
    	MenuItem itemNewProfessor = menu.add(base, base, Menu.NONE, "new professor");
        return true;
    }
    
    /*
     * Menu 선택 했을때 동작
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case 1:
			Intent intentEditProfessor = new Intent(this, ProfessorEditActivity.class);
			intentEditProfessor.putExtra(Defines.INTENT_PROFESSOR_EDIT_MODE, Defines.MODE_NEW);	// 신규 모드
			startActivityForResult(intentEditProfessor, Defines.REQUEST_CODE_EDIT_PROFESSOR);
			break;

		default:
			break;
		}

    	return super.onOptionsItemSelected(item);
    }
    
@Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
   	if (v.getId() == R.id.lv_professor) {
  		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			menu.setHeaderTitle(m_listProfessorData.get(info.position).strName);
			menu.add(0, Defines.MODE_UPDATE, 0, R.string.list_context_menu_update);
			menu.add(0, Defines.MODE_DELETE, 1, R.string.list_context_menu_delete);
			m_editProfessorData = m_listProfessorData.get(info.position);
			
			m_selectedListItemPosition = info.position;
		}
	}
    
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		
		switch (menuItemIndex) {
	     case Defines.MODE_UPDATE:
			Intent intent = new Intent(this, ProfessorEditActivity.class);
			intent.putExtra(Defines.INTENT_PROFESSOR_EDIT_MODE, Defines.MODE_UPDATE);	// 수정 모드
			if (m_editProfessorData != null)
				intent.putExtra(Defines.INTENT_PROFESSOR_DATA, m_editProfessorData);
			startActivityForResult(intent, Defines.REQUEST_CODE_EDIT_PROFESSOR);
			break;
			
		case Defines.MODE_DELETE:
			if (m_editProfessorData != null) {
				m_deleteProfessorTableRunnable.setPHPParameter(m_editProfessorData);
				runOnUiThread(m_deleteProfessorTableRunnable);
				finish();
				Intent t = new Intent(ProfessorActivity.this, ProfessorActivity.class);
				startActivity(t);
				
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
		case Defines.REQUEST_CODE_EDIT_PROFESSOR:
			if (resultCode == RESULT_OK) { // EditProfessorActivity의 결과 코드
				// Professor ListView 갱신
				resetProfessorListView();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void resetProfessorListView() {
		m_bResetListView = true;
		
		runOnUiThread(m_getProfessorDataRunnable);
	}
	

}
