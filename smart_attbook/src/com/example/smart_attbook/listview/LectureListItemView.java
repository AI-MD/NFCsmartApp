//package com.example.smart_attbook.listview;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.example.smart_attbook.R;
//
//
//import com.example.smart_attbook.data.LectureData;
//import com.example.smart_attbook.network.ImageDownloader;
//import com.example.smart_attbook.utils.Defines;
//
//public class LectureListItemView extends LinearLayout  {
//	public static final String				TAG											= "LectureListItemView";
//	
//	
//	private TextView						m_tvLectureName								= null;
//	
//	
//	public LectureListItemView(Context context, LectureData lectureData) {
//		super(context);
//
//		// Layout Inflation
//		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		inflater.inflate(R.layout.lecture_list_item, this, true);
//		
//		
//		
//		// Set student name
//		m_tvLectureName = (TextView)findViewById(R.id.tv_lecture_list_item_name);
//		setTextViewLectureName(lectureData.strName);
//		
//		
//	}
//	
//	public void setTextViewLectureName(String strName) {
//		m_tvLectureName.setText(strName);
//	}
//	
//	
//
//	
//	
//}
