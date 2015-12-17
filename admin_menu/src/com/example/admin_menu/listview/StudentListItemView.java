package com.example.admin_menu.listview;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.admin_menu.data.StudentData;
import com.example.admin_menu.network.ImageDownloader;
import com.example.admin_menu.utils.Defines;
import com.example.smart_attbook.R;


public class StudentListItemView extends LinearLayout  {
	public static final String				TAG											= "StudentListItemView";
	
	private ImageView 						m_ivStudentPicture							= null;
	private TextView						m_tvStudentName								= null;
	private TextView						m_tvStudentPhone							= null;
	
	public StudentListItemView(Context context, StudentData studentData) {
		super(context);

		// Layout Inflation
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.student_list_item, this, true);
		
		// Set student picture 
		m_ivStudentPicture = (ImageView)findViewById(R.id.iv_student_list_item_image);
		setStudentPictureImageViewDownload(studentData.strPictureURL);
		
		// Set student name
		m_tvStudentName = (TextView)findViewById(R.id.tv_student_list_item_name);
		setTextViewStudentName(studentData.strName);
		
		// Set student phone
		m_tvStudentPhone = (TextView)findViewById(R.id.tv_student_list_item_phone);
		setTextViewStudentPhone(studentData.strPhoneNumber);
	}
	
	public void setTextViewStudentName(String strName) {
		m_tvStudentName.setText(strName);
	}
	
	public void setTextViewStudentPhone(String strName) {
		m_tvStudentPhone.setText(strName);
	}

	public void setStudentPictureImageViewDownload(String strStudentPicturePath) {
		if (strStudentPicturePath != null) {
			if (strStudentPicturePath.compareTo("") != 0) {
				// Shop image loading from thread
				ImageDownloader.download(Defines.SERVER_IMAGE_PATH+strStudentPicturePath, m_ivStudentPicture);
			}
		} else {
			m_ivStudentPicture.setImageDrawable(getResources().getDrawable(R.drawable.default_pic));
		}
	}
}
