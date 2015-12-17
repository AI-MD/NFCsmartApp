package com.example.admin_menu.listview;

import java.util.ArrayList;

import com.example.admin_menu.data.StudentData;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class StudentListAdapter extends BaseAdapter {
	public static final String				TAG											= "StudentListAdapter";
	
	private Context							m_context									= null;
	private ArrayList<StudentData>			m_listStudentData							= new ArrayList<StudentData>();
	
	public StudentListAdapter(Context context) {
		m_context = context;
	}
	
	public void setListItems(ArrayList<StudentData> StudentList) {
		m_listStudentData = StudentList;
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
		StudentListItemView itemView;
		if (convertView == null) {
			itemView = new StudentListItemView(m_context, m_listStudentData.get(position));
		} else {
			itemView = (StudentListItemView)convertView;
			
			itemView.setStudentPictureImageViewDownload(m_listStudentData.get(position).strPictureURL);
			itemView.setTextViewStudentName(m_listStudentData.get(position).strName);
			itemView.setTextViewStudentPhone(m_listStudentData.get(position).strPhoneNumber);
		}
		
		return itemView;
	}
}
