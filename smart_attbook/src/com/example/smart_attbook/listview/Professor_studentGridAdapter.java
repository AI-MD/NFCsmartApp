package com.example.smart_attbook.listview;


import java.util.ArrayList;

import com.example.smart_attbook.data.StudentData;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;



public class Professor_studentGridAdapter extends BaseAdapter {
	public static final String				TAG											= "Professor_studentGridAdapter";
	
	private Context							m_context									= null;
	private ArrayList<StudentData>			m_listStudentData							= new ArrayList<StudentData>();
	private ArrayList<StudentData>			m_MyStudentData								= new ArrayList<StudentData>();
	
	
	public Professor_studentGridAdapter(Context context) {
		m_context = context;
	}
	
	public void setListItems(ArrayList<StudentData> StudentList) {
		m_listStudentData	 =StudentList;
	}
	
	public void setMyStudentData(ArrayList<StudentData> MyStudentData) {
		this.m_MyStudentData = MyStudentData;
	}

	@Override
	public int getCount() {
		return m_listStudentData.size();
	}

	@Override
	public Object getItem(int position) {
		return m_listStudentData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Professor_studentGridItemView itemView;
		if (convertView == null) {
			itemView = new Professor_studentGridItemView(m_context,m_listStudentData.get(position));
		} else {
			itemView = (Professor_studentGridItemView)convertView;
			
			itemView.setStudentPictureImageViewDownload(m_listStudentData.get(position).strPictureURL);
			itemView.setTextViewStudentName(m_listStudentData.get(position).strName);
			
		}
		//수강학생과 출석한 학생 데이터 비교하면서 이미지 뷰 invisible에서 visible하기 
		for( int j=0;j< m_MyStudentData.size();j++){
	
			if(m_listStudentData.get(position).dStudentNumber.equals(m_MyStudentData.get(j).dStudentNumber)) {
				Log.e("mMyStudentData","mMyStudentData="+m_listStudentData.get(position).dStudentNumber);
				
				itemView.setImageVisible();
			}
		}

		
		return itemView;
	}
}
