package com.example.admin_menu.listview;

import java.util.ArrayList;

import com.example.admin_menu.data.ProfessorData;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;



public class ProfessorListAdapter extends BaseAdapter {
	public static final String				TAG											= "ProfessorListAdapter";
	
	private Context							m_context									= null;
	private ArrayList<ProfessorData>			m_listProfessorData							= new ArrayList<ProfessorData>();
	
	public ProfessorListAdapter(Context context) {
		m_context = context;
	}
	
	public void setListItems(ArrayList<ProfessorData> ProfessorList) {
		m_listProfessorData	 =ProfessorList;
	}

	@Override
	public int getCount() {
		return m_listProfessorData.size();
	}

	@Override
	public Object getItem(int position) {
		return m_listProfessorData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ProfessorListItemView itemView;
		if (convertView == null) {
			itemView = new ProfessorListItemView(m_context,m_listProfessorData.get(position));
		} else {
			itemView = (ProfessorListItemView)convertView;
			
			itemView.setProfessorPictureImageViewDownload(m_listProfessorData.get(position).strPictureURL);
			itemView.setTextViewProfessorName(m_listProfessorData.get(position).strName);
			itemView.setTextViewProfessorPhone(m_listProfessorData.get(position).strPhoneNumber);
		}
		
		return itemView;
	}
}
