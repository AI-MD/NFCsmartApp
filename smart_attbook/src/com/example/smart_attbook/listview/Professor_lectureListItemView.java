package com.example.smart_attbook.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smart_attbook.R;


import com.example.smart_attbook.data.LectureData;
import com.example.smart_attbook.network.ImageDownloader;
import com.example.smart_attbook.utils.Defines;

public class Professor_lectureListItemView extends LinearLayout  {
	public static final String				TAG											= "Professor_lectureListItemView";
	
	
	private TextView						m_tvLectureName								= null;
	private TextView						m_tvLecturePlace							= null;
	
	public Professor_lectureListItemView(Context context, LectureData lectureData) {
		super(context);

		// Layout Inflation
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.professor_lecture_item, this, true);
		
		
		
		// Set student name
		m_tvLectureName = (TextView)findViewById(R.id.tv_professor_lecture_item_name);
		
		setTextViewLectureName(lectureData.strName);
		m_tvLecturePlace = (TextView)findViewById(R.id.tv_professor_lecture_item_place);
		setTextViewLecturePlace(lectureData.strPlace);
	}
	
	public void setTextViewLectureName(String strName) {
		m_tvLectureName.setText(strName);
	}
	
	public void setTextViewLecturePlace(String strPlace) {
		m_tvLecturePlace.setText(strPlace);
	}

	
	
}
