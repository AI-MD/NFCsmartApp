//package com.example.smart_attbook.listview;
//
//import java.util.ArrayList;
//
//import android.content.Context;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//
//import com.example.smart_attbook.data.LectureData;
//
//public class LectureListAdapter extends BaseAdapter {
//	public static final String				TAG											= "LectureListAdapter";
//	
//	private Context							m_context									= null;
//	private ArrayList<LectureData>			m_listLectureData							= new ArrayList<LectureData>();
//	
//	public LectureListAdapter(Context context) {
//		m_context = context;
//	}
//	
//	public void setListItems(ArrayList<LectureData> LectureList) {
//		m_listLectureData	 =LectureList;
//	}
//
//	@Override
//	public int getCount() {
//		return m_listLectureData.size();
//	}
//
//	@Override
//	public Object getItem(int position) {
//		return m_listLectureData.get(position);
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position;
//	}
//	
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		LectureListItemView itemView;
//		if (convertView == null) {
//			itemView = new LectureListItemView(m_context,m_listLectureData.get(position));
//		} else {
//			itemView = (LectureListItemView)convertView;
//			
//			
//			itemView.setTextViewLectureName(m_listLectureData.get(position).strName);
//			
//		}
//		
//		return itemView;
//	}
//}
