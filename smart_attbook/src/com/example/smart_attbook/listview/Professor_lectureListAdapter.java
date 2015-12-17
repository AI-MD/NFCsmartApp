package com.example.smart_attbook.listview;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.smart_attbook.data.LectureData;

public class Professor_lectureListAdapter extends BaseAdapter {
	public static final String				TAG											= "Professor_lectureListAdapter";
	
	private Context							m_context									= null;
	private ArrayList<LectureData>  mMyLectureData = new ArrayList<LectureData>();
	
	public Professor_lectureListAdapter(Context context) {
		m_context = context;
	}
	
	public void setListItems(ArrayList<LectureData> LectureList) {
		mMyLectureData	 =LectureList;
	}

	@Override
	public int getCount() {
		return mMyLectureData.size();
	}

	@Override
	public Object getItem(int position) {
		return mMyLectureData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Professor_lectureListItemView itemView;
		if (convertView == null) {
			itemView = new Professor_lectureListItemView(m_context,mMyLectureData.get(position));
		} else {
			itemView = (Professor_lectureListItemView)convertView;
			
			itemView.setTextViewLectureName(mMyLectureData.get(position).strName);
			itemView.setTextViewLecturePlace(mMyLectureData.get(position).strPlace);
			
		}
		
		return itemView;
	}
}
