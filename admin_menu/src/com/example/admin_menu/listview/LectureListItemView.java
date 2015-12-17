package com.example.admin_menu.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.admin_menu.data.LectureData;
import com.example.smart_attbook.R;





public class LectureListItemView extends LinearLayout  {
	public static final String				TAG											= "LectureListItemView";
	
	
	private TextView						m_tvLectureName								= null;
	
	
	public LectureListItemView(Context context, LectureData lectureData) {
		super(context);

		// Layout Inflation
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.lecture_list_item, this, true);
		
		
		
		// Set student name
		m_tvLectureName = (TextView)findViewById(R.id.tv_lecture_list_item_name);
		setTextViewLectureName(lectureData.strName);
		
		
	}
	
	public void setTextViewLectureName(String strName) {
		m_tvLectureName.setText(strName);
	}
	
	

	
	
}
