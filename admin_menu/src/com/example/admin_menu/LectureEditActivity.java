package com.example.admin_menu;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.admin_menu.data.LectureData;
import com.example.admin_menu.network.NetworkThread.insertLectureTableRunnable;
import com.example.admin_menu.network.NetworkThread.updateLectureTableRunnable;
import com.example.admin_menu.utils.Defines;
import com.example.smart_attbook.R;





public class LectureEditActivity extends Activity implements OnClickListener {
	public static final String				TAG											= "LectureEditActivity";
	
	private int								m_nMode										= Defines.MODE_NEW;
	private LectureData					    m_lectureData								= null;
	
	private EditText						m_etLecture_id							    = null;
	private EditText						m_etName									= null;
	private EditText						m_etProfessor_id							= null;
	private EditText						m_etWeek									= null;
	private EditText						m_etStart_time								= null;
	private EditText						m_etEnd_time								= null;
	private EditText						m_etPlace									= null;
	
	private Button							m_btnSave									= null;
	
	
	private NewLectureActivityHandler		m_handler									= new NewLectureActivityHandler();
	
	private insertLectureTableRunnable	   m_insertLectureTableRunnable				    = null;
	private updateLectureTableRunnable	   m_updateLectureTableRunnable				    = null;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lecture_edit);
		
		
		m_etLecture_id = (EditText)findViewById(R.id.edit_lecture_id);
		m_etName = (EditText)findViewById(R.id.edit_lecture_name);
		m_etProfessor_id = (EditText)findViewById(R.id.edit_professor_id);
		m_etWeek = (EditText)findViewById(R.id.edit_lecture_week);
		m_etStart_time = (EditText)findViewById(R.id.edit_lecture_start_time);
		m_etEnd_time = (EditText)findViewById(R.id.edit_lecture_end_time);
		m_etPlace = (EditText)findViewById(R.id.edit_lecture_place);
		
		
		
		m_btnSave.setOnClickListener(this);
		
		
		Intent intent = getIntent(); // 이 액티비티를 시작하게 한 인텐트를 호출한다.
		m_nMode = intent.getExtras().getInt(Defines.INTENT_LECTURE_EDIT_MODE);
		if (m_nMode == Defines.MODE_UPDATE) {
			// Update
			m_lectureData = intent.getExtras().getParcelable(Defines.INTENT_LECTURE_DATA);
			
			

		
		
		m_insertLectureTableRunnable = new insertLectureTableRunnable(LectureEditActivity.this, m_handler);
		m_updateLectureTableRunnable = new updateLectureTableRunnable(LectureEditActivity.this, m_handler);
	}
	}
	private void saveData() {
    	if (m_lectureData != null) {
    		// Name
    		m_lectureData.dLectureId = Double.valueOf(m_etLecture_id.getText().toString());
    		m_lectureData.strName = m_etName.getText().toString();
    		m_lectureData.dProfessorId= Double.valueOf(m_etProfessor_id.getText().toString());
    		m_lectureData.strWeek =  m_etWeek.getText().toString();
    		m_lectureData.strStrat_Time = m_etStart_time.getText().toString();
    		m_lectureData.strEnd_Time = m_etEnd_time.getText().toString();
    		m_lectureData.strPlace = m_etPlace.getText().toString();
    	}
    	
    	if (checkNotNullData()) {	
			
				
				
				if (m_nMode == Defines.MODE_UPDATE) {
					m_updateLectureTableRunnable.setPHPParameter(m_lectureData);
					runOnUiThread(m_updateLectureTableRunnable);
				} else {
		    		m_insertLectureTableRunnable.setPHPParameter(m_lectureData);
					runOnUiThread(m_insertLectureTableRunnable);
				}
				}
    	
	}
	
    /** 
     * checkNotNullData
     *	- Professor Table에 저장할때 Not Null인 Field 체크 함수
     * @param void
     * @return boolean
     */ 
    private boolean checkNotNullData() {
    	if (m_lectureData != null) {
    		// Number
    		if ((m_lectureData.dLectureId == null) || (m_lectureData.dLectureId.toString().compareTo("") == 0))
    			return false;
    		
    		// Name 입력이 null 이거나 공백인지 check
    		if ((m_lectureData.strName == null) || (m_lectureData.strName.compareTo("") == 0))
    			return false;
    	} else {
    		return false;
    	}
    	
    	return true;
    }
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_save:
			saveData();
			break;
			
		}
	}
	
   
    	
  
    
    
   

    /*
     * 다른 Thread로 부터 Message를 받아 처리하는 Handler
     * Android는 Main UI Thread에서만 화면 갱신이 가능하기 때문에 이렇게 Handler를 이용한다.
     */
	public class NewLectureActivityHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Defines.HND_MSG_NETWORK_TIMEOUT:
				showDialog(Defines.DIALOG_NETWORK_TIMEOUT_ALRET);
				break;
				
			case Defines.HND_MSG_INSERT_LECTURE_DATA:
				m_insertLectureTableRunnable.getProgressDialog().dismiss();
				
				if (m_insertLectureTableRunnable.getResult() == 0) {
					// 저장 실패
					showDialog(Defines.DIALOG_TABLE_INSERT_ALRET);
				} else if (m_insertLectureTableRunnable.getResult() == -1) {
					// 이미 같은 Type과 이름으로 Shop Data 존재
					//showDialog(Defines.DIALOG_DATA_ALREADY_EXIST);
				} else {
					// 저장 성공 list view 갱신
					Intent intent = getIntent(); // 이 액티비티를 시작하게 한 인텐트를 호출한다.
					setResult(RESULT_OK, intent);
					finish();
					
				}
				break;
				
		
			
				
			default:
				super.handleMessage(msg);
				break;
			}
		}
	}
	
    /*
     * onCreateDialog
     * - Dialog 생성
     */
    @Override
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog = null;
    	
    	switch (id) {
    	case Defines.DIALOG_NETWORK_TIMEOUT_ALRET:
			AlertDialog.Builder abNetworkTimeout = null;
			abNetworkTimeout = new AlertDialog.Builder(LectureEditActivity.this);
			abNetworkTimeout.setMessage(getString(R.string.alert_network_timeout));
			abNetworkTimeout.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked OK so do some stuff */
				}
			});
			abNetworkTimeout.setTitle("Book Info Alert");
			dialog = abNetworkTimeout.create();
			return dialog;
			
    	case Defines.DIALOG_TABLE_INSERT_ALRET:
			AlertDialog.Builder abTableInsert = null;
			abTableInsert = new AlertDialog.Builder(LectureEditActivity.this);
			abTableInsert.setMessage(getString(R.string.alert_table_insert_failed));
			abTableInsert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked OK so do some stuff */
				}
			});
			abTableInsert.setTitle("Sweet Consumer Alert");
			dialog = abTableInsert.create();
			return dialog;
			
    	case Defines.DIALOG_INPUT_ALERT:
			AlertDialog.Builder abBase = null;
			abBase = new AlertDialog.Builder(LectureEditActivity.this);
			abBase.setMessage(getString(R.string.alert_input));
			abBase.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked OK so do some stuff */
				}
			});
			abBase.setTitle("Sweet Consumer Alert");
			dialog = abBase.create();
			return dialog;
			
    	case Defines.DIALOG_IMAGE_UPLOAD_ALRET:
			AlertDialog.Builder abImageUpload = null;
			abImageUpload = new AlertDialog.Builder(LectureEditActivity.this);
			abImageUpload.setMessage(getString(R.string.alert_image_upload_failed));
			abImageUpload.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked OK so do some stuff */
				}
			});
			abImageUpload.setTitle("Sweet Consumer Alert");
			dialog = abImageUpload.create();
			return dialog;
    	}
    	return null;
    }
}
