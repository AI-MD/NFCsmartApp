package com.example.admin_menu.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.admin_menu.data.ProfessorData;
import com.example.admin_menu.network.ImageDownloader;
import com.example.admin_menu.utils.Defines;
import com.example.smart_attbook.R;



public class ProfessorListItemView extends LinearLayout  {
	public static final String				TAG											= "ProfessorListItemView";
	
	private ImageView 						m_ivProfessorPicture							= null;
	private TextView						m_tvProfessorName								= null;
	private TextView						m_tvProfessorPhone							 = null;
	
	public ProfessorListItemView(Context context, ProfessorData professorData) {
		super(context);

		// Layout Inflation
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.professor_list_item, this, true);
		
		// Set student picture 
		m_ivProfessorPicture = (ImageView)findViewById(R.id.iv_professor_list_item_image);
		setProfessorPictureImageViewDownload(professorData.strPictureURL);
		
		// Set student name
		m_tvProfessorName = (TextView)findViewById(R.id.tv_professor_list_item_name);
		setTextViewProfessorName(professorData.strName);
		
		// Set student phone
		m_tvProfessorPhone = (TextView)findViewById(R.id.tv_professor_list_item_phone);
		setTextViewProfessorPhone(professorData.strPhoneNumber);
	}
	
	public void setTextViewProfessorName(String strName) {
		m_tvProfessorName.setText(strName);
	}
	
	public void setTextViewProfessorPhone(String strName) {
		m_tvProfessorPhone.setText(strName);
	}

	public void setProfessorPictureImageViewDownload(String strProfessorPicturePath) {
		if (strProfessorPicturePath != null) {
			if (strProfessorPicturePath.compareTo("") != 0) {
				// Shop image loading from thread
				ImageDownloader.download(Defines.SERVER_IMAGE_PATH+strProfessorPicturePath, m_ivProfessorPicture);
			}
		} else {
			m_ivProfessorPicture.setImageDrawable(getResources().getDrawable(R.drawable.default_pic));
		}
	}
}
