package com.example.admin_menu;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin_menu.data.ProfessorData;
import com.example.admin_menu.network.ImageDownloader;
import com.example.admin_menu.network.NetworkThread.imageUploadRunnable;
import com.example.admin_menu.network.NetworkThread.insertProfessorTableRunnable;
import com.example.admin_menu.network.NetworkThread.updateProfessorTableRunnable;
import com.example.admin_menu.utils.Defines;
import com.example.admin_menu.utils.TypeConvertUtils;
import com.example.smart_attbook.R;




public class ProfessorEditActivity extends Activity implements OnClickListener {
	public static final String				TAG											= "ProfessorEditActivity";
	
	private int								m_nMode										= Defines.MODE_NEW;
	private ProfessorData					m_professorData								= null;
	
	private EditText						m_etNumber									= null;
	private EditText						m_etName									= null;
	private EditText						m_etPhone									= null;
	private EditText						m_etAddress									= null;
	private TextView						m_tvPicture									= null;
	private Button							m_btnPicture								= null;
	private Button							m_btnSave									= null;
	private ImageView						m_ivPicture									= null;
	
	private NewProfessorActivityHandler		m_handler									= new NewProfessorActivityHandler();
	private imageUploadRunnable				m_imageUploadRunnable						= null;
	private insertProfessorTableRunnable	m_insertProfessorTableRunnable				= null;
	private updateProfessorTableRunnable	m_updateProfessorTableRunnable					= null;
	private String 							m_strImageFilePath							= null;
	private String							m_strOldImageFilePath						= "";	// ���� ����϶� �̹����� ���� �Ǿ����� check�ϱ� ���� ����
	private Bitmap							m_bmpImage									= null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_professor_edit);
		
		
		m_etNumber = (EditText)findViewById(R.id.edit_professor_number);
		m_etName = (EditText)findViewById(R.id.edit_professor_name);
		m_etPhone = (EditText)findViewById(R.id.edit_professor_phone);
		m_etAddress = (EditText)findViewById(R.id.edit_professor_address);
		m_tvPicture = (TextView)findViewById(R.id.tv_professor_picture);
		m_btnSave = (Button)findViewById(R.id.btn_save);
		m_btnPicture = (Button)findViewById(R.id.btn_professor_picture_select);
		m_ivPicture = (ImageView)findViewById(R.id.iv_professor);
		m_btnSave.setOnClickListener(this);
		m_btnPicture.setOnClickListener(this);
		
		Intent intent = getIntent(); // �� ��Ƽ��Ƽ�� �����ϰ� �� ����Ʈ�� ȣ���Ѵ�.
		m_nMode = intent.getExtras().getInt(Defines.INTENT_PROFESSOR_EDIT_MODE);
		if (m_nMode == Defines.MODE_UPDATE) {
			// Update
			m_professorData = intent.getExtras().getParcelable(Defines.INTENT_PROFESSOR_DATA);
			
			// Shop Image Setting
			if (m_professorData != null) {
				if (m_professorData.strPictureURL.compareTo("") != 0) {
					m_bmpImage = ImageDownloader.getCachedImage(Defines.SERVER_IMAGE_PATH+m_professorData.strPictureURL);
					m_strImageFilePath = m_professorData.strPictureURL;
					m_strOldImageFilePath = m_professorData.strPictureURL;
				}
				
				m_etNumber.setText(String.format("%.0f", m_professorData.dProfessorId));
				m_etNumber.setEnabled(false);
				m_etName.setText(m_professorData.strName);
				m_etPhone.setText(m_professorData.strPhoneNumber);
				m_etAddress.setText(m_professorData.strAddress);
			} else {
				m_bmpImage = TypeConvertUtils.getBitmap(getResources().getDrawable(R.drawable.default_pic));
			}
		} else {
			// New
			m_professorData = new ProfessorData();
		}

		
		m_imageUploadRunnable = new imageUploadRunnable(ProfessorEditActivity.this, m_handler);
		m_insertProfessorTableRunnable = new insertProfessorTableRunnable(ProfessorEditActivity.this, m_handler);
		m_updateProfessorTableRunnable = new updateProfessorTableRunnable(ProfessorEditActivity.this, m_handler);
	}
	
	private void saveData() {
    	if (m_professorData != null) {
    		// Name
    		m_professorData.dProfessorId = Double.valueOf(m_etNumber.getText().toString());
    		m_professorData.strName = m_etName.getText().toString();
    		m_professorData.strPhoneNumber = m_etPhone.getText().toString();
    		m_professorData.strAddress = m_etAddress.getText().toString();
    		m_professorData.strPhoneNumber = m_etPhone.getText().toString();
    		m_professorData.strPictureURL = m_tvPicture.getText().toString();
    	}
    	
    	if (checkNotNullData()) {	
			if (m_strImageFilePath != null) {
				if (m_nMode == Defines.MODE_UPDATE) {
					// ������ ��� ���� image file�� ���� �Ǿ����� �˻� ���� ������ ���ٸ� �̹����� ���ε� ���� �ʰ� �����͸� ���� ��
					if (m_strImageFilePath.compareTo(m_strOldImageFilePath) == 0) {
						
						m_updateProfessorTableRunnable.setPHPParameter(m_professorData);
						runOnUiThread(m_updateProfessorTableRunnable);
						
					} else {
						// �̹����� ���� ��쿣 �̹����� ���� ���ε� �ϰ� ���ε尡 �Ϸ��ϸ� Data ����
						// Image uploading to Web Server
						
						
						m_imageUploadRunnable.setParameter(m_strImageFilePath);
						runOnUiThread(m_imageUploadRunnable);
					}
				} else {
					// �̹����� ���� ��쿣 �̹����� ���� ���ε� �ϰ� ���ε尡 �Ϸ��ϸ� Data ����
					// Image uploading to Web Server
					m_imageUploadRunnable.setParameter(m_strImageFilePath);
					runOnUiThread(m_imageUploadRunnable);
				}
			} else {
				// �̹��� ���� Data ����
				
				if (m_nMode == Defines.MODE_UPDATE) {
					m_updateProfessorTableRunnable.setPHPParameter(m_professorData);
					runOnUiThread(m_updateProfessorTableRunnable);
				} else {
		    		m_insertProfessorTableRunnable.setPHPParameter(m_professorData);
					runOnUiThread(m_insertProfessorTableRunnable);
				}
			}
    	} else {
    		showDialog(Defines.DIALOG_INPUT_ALERT);
    	}
	}
	
    /** 
     * checkNotNullData
     *	- Professor Table�� �����Ҷ� Not Null�� Field üũ �Լ�
     * @param void
     * @return boolean
     */ 
    private boolean checkNotNullData() {
    	if (m_professorData != null) {
    		// Number
    		if ((m_professorData.dProfessorId == null) || (m_professorData.dProfessorId.toString().compareTo("") == 0))
    			return false;
    		
    		// Name �Է��� null �̰ų� �������� check
    		if ((m_professorData.strName == null) || (m_professorData.strName.compareTo("") == 0))
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
			
		case R.id.btn_professor_picture_select:
			// To open up a gallery browser
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent, "Select Picture"), Defines.REQUEST_CODE_GALLERY);
			break;
		}
	}
	
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (requestCode) {
		case Defines.REQUEST_CODE_GALLERY:	// To handle when an image is selected from the browser
			if (resultCode == RESULT_OK) {
				Uri currImageURI = data.getData();
				Log.d(TAG, "Current image Path is ----->" +	getRealPathFromURI(currImageURI));
				m_strImageFilePath = getRealPathFromURI(currImageURI);
				
				// UI Thread���� ���õ� Image�� ���� �ϵ��� ��û��.
				m_handler.sendEmptyMessage(Defines.HND_MSG_SET_PROFESSOR_PICTURE);
			}
			break;
    	}
    }
    
    //Convert the image URI to the direct file system path of the image file
    public String getRealPathFromURI(Uri contentUri) {
    	String [] proj={MediaStore.Images.Media.DATA};
    	android.database.Cursor cursor = managedQuery( contentUri,
    	proj,     // Which columns to return
    	null,     // WHERE clause; which rows to return (all rows)
    	null,     // WHERE clause selection arguments (none)
    	null);     // Order-by clause (ascending by name)
    	int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    	cursor.moveToFirst();
    	return cursor.getString(column_index);
    }
    
    private void setPicture(String strPicturePath) {
		if (strPicturePath != null) {
			Bitmap bitmapOrg = BitmapFactory.decodeFile(strPicturePath);
			
			// Resize the image
			double width = bitmapOrg.getWidth();
			double height = bitmapOrg.getHeight();
			double ratio = Defines.IMAGE_MAX_HEIGHT/width;
			int newheight = (int)(ratio*height);
			
			Log.d(TAG, "width: " + width);
			Log.d(TAG, "height: " + height);				
			
			bitmapOrg = Bitmap.createScaledBitmap(bitmapOrg, Defines.IMAGE_MAX_HEIGHT, newheight, true);
			
			if (m_ivPicture != null) {
				m_ivPicture.setImageBitmap(bitmapOrg);
				m_bmpImage = bitmapOrg;
			}
		}
    }

    /*
     * �ٸ� Thread�� ���� Message�� �޾� ó���ϴ� Handler
     * Android�� Main UI Thread������ ȭ�� ������ �����ϱ� ������ �̷��� Handler�� �̿��Ѵ�.
     */
	public class NewProfessorActivityHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Defines.HND_MSG_NETWORK_TIMEOUT:
				showDialog(Defines.DIALOG_NETWORK_TIMEOUT_ALRET);
				break;
				
			case Defines.HND_MSG_INSERT_PROFESSOR_DATA:
				m_insertProfessorTableRunnable.getProgressDialog().dismiss();
				
				if (m_insertProfessorTableRunnable.getResult() == 0) {
					// ���� ����
					showDialog(Defines.DIALOG_TABLE_INSERT_ALRET);
				} else if (m_insertProfessorTableRunnable.getResult() == -1) {
					// �̹� ���� Type�� �̸����� Shop Data ����
					//showDialog(Defines.DIALOG_DATA_ALREADY_EXIST);
				} else {
					// ���� ���� list view ����
					Intent intent = getIntent(); // �� ��Ƽ��Ƽ�� �����ϰ� �� ����Ʈ�� ȣ���Ѵ�.
					setResult(RESULT_OK, intent);
					finish();					
				}
				break;
				
			case Defines.HND_MSG_SET_PROFESSOR_PICTURE:
				setPicture(m_strImageFilePath);
				break;
				
			case Defines.HND_MSG_IMAGE_UPLOAD:
				m_imageUploadRunnable.getProgressDialog().dismiss();
				
				if (m_professorData != null) {
					m_professorData.strPictureURL = m_imageUploadRunnable.getOutput();
					Log.d(TAG, "strPictureURL: "+m_professorData.strPictureURL);
					if (m_professorData.strPictureURL != null) {
						// �̹��� upload ���� �Ͽ����Ƿ�, Shop Data ����
						
						if (m_nMode == Defines.MODE_UPDATE) {
						m_updateProfessorTableRunnable.setPHPParameter(m_professorData);
							runOnUiThread(m_updateProfessorTableRunnable);
						} else {		
				    		m_insertProfessorTableRunnable.setPHPParameter(m_professorData);
							runOnUiThread(m_insertProfessorTableRunnable);
						}
					} else {
						// �̹��� upload ����
						showDialog(Defines.DIALOG_IMAGE_UPLOAD_ALRET);
					}
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
     * - Dialog ����
     */
    @Override
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog = null;
    	
    	switch (id) {
    	case Defines.DIALOG_NETWORK_TIMEOUT_ALRET:
			AlertDialog.Builder abNetworkTimeout = null;
			abNetworkTimeout = new AlertDialog.Builder(ProfessorEditActivity.this);
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
			abTableInsert = new AlertDialog.Builder(ProfessorEditActivity.this);
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
			abBase = new AlertDialog.Builder(ProfessorEditActivity.this);
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
			abImageUpload = new AlertDialog.Builder(ProfessorEditActivity.this);
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
