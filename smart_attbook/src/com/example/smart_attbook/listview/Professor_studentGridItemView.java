package com.example.smart_attbook.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smart_attbook.R;


import com.example.smart_attbook.data.LectureData;
import com.example.smart_attbook.data.StudentData;
import com.example.smart_attbook.network.ImageDownloader;
import com.example.smart_attbook.utils.Defines;

public class Professor_studentGridItemView extends LinearLayout  {
	public static final String				TAG											= "Professor_studentGridItemView";
	
	private ImageView 						m_iva_StudentPicture							= null;
	private TextView						m_tvStudentName								= null;

	//invisible에서 visible화하기 
	public void setImageVisible() {
		m_iva_StudentPicture.setVisibility(View.VISIBLE);
	}
	public Professor_studentGridItemView(Context context, StudentData studentData) {
		super(context);

		// Layout Inflation
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.activity_attendance_student_item, this, true);
		
		// Set student picture 
		m_iva_StudentPicture = (ImageView)findViewById(R.id.iv_attendance_student_list_item_image);
		
		setStudentPictureImageViewDownload(studentData.strPictureURL);
		
		// Set student name
		m_tvStudentName = (TextView)findViewById(R.id.tv_attendance_student_list_item_name);
		
		setTextViewStudentName(studentData.strName);
		
		
	}
	
	public void setTextViewStudentName(String strName) {
		m_tvStudentName.setText(strName);
	}
	
	
	public void setStudentPictureImageViewDownload(String strStudentPicturePath) {
		if (strStudentPicturePath != null) {
			if (strStudentPicturePath.compareTo("") != 0) {
				// Shop image loading from thread
				ImageDownloader.download(Defines.SERVER_IMAGE_PATH+strStudentPicturePath, m_iva_StudentPicture);
			
			}
		} else {
			
			m_iva_StudentPicture.setImageDrawable(getResources().getDrawable(R.drawable.default_pic));
			
		}
	}
	
	
}
