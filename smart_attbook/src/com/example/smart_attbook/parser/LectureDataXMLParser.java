package com.example.smart_attbook.parser;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.example.smart_attbook.data.LectureData;

import com.example.smart_attbook.utils.TypeConvertUtils;

public class LectureDataXMLParser {
	public static final String				TAG											= "LectureDataXMLParser";
	
	public static final int					XMLTAG_NONE									= 0x0000;
	
	public static final int					XMLTAG_NODES								= 0x1000;
	public static final int					XMLTAG_NODE									= 0x1100;
	public static final int					XMLTAG_NODE_LECTURE_ID						= 0x1110;
	public static final int					XMLTAG_NODE_NAME							= 0x1120;
	public static final int					XMLTAG_NODE_PROFESSOR_ID					= 0x1130;
	public static final int					XMLTAG_NODE_WEEK							= 0x1140;
	public static final int					XMLTAG_NODE_START_TIME						= 0x1150;
	public static final int					XMLTAG_NODE_END_TIME						= 0x1160;
	public static final int					XMLTAG_NODE_PLACE						    = 0x1170;
	
	private int								m_nTagState									= XMLTAG_NONE;
	
	public ArrayList<LectureData>			m_listLectureData							= null;
	
	public LectureDataXMLParser() {
		
	}
	
	public boolean Parse(String file) {
		boolean bResult = true;

		try {
			bResult = Parse(new BufferedInputStream(new FileInputStream(file)));
		} catch (FileNotFoundException e) {
			bResult = false;
			Log.e(TAG, e.toString());
		}
				
		return bResult;
	}
	
	public boolean Parse(ByteBuffer pData) {
		boolean bResult = true;
		
		bResult = Parse(TypeConvertUtils.ByteBufferToInputStream(pData));
		
		return bResult;
	}
	
	public boolean Parse(InputStream in) {
		boolean bResult = true;
		
		String strTag;
		String strValue;
		
		LectureData lectureData = null;
		m_listLectureData = new ArrayList<LectureData>();
		
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); 
			factory.setNamespaceAware(true); 
			XmlPullParser xpp = factory.newPullParser();
			
			xpp.setInput(in, "utf-8");
			
			int eventType = xpp.getEventType();
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				
				if(eventType == XmlPullParser.START_DOCUMENT) { 
//					Log.d(TAG, "Start document"); 
				} else if(eventType == XmlPullParser.START_TAG) {
//					Log.d(TAG, "Start tag "+xpp.getName());
					strTag = xpp.getName();
					
					if (strTag.equals("node")) {
						lectureData = new LectureData();
						m_nTagState = XMLTAG_NODE;
					}
					else if (strTag.equals("lecture_id"))
						m_nTagState = XMLTAG_NODE_LECTURE_ID;
					else if (strTag.equals("name"))
						m_nTagState = XMLTAG_NODE_NAME;
					else if (strTag.equals("professor_id"))
						m_nTagState = XMLTAG_NODE_PROFESSOR_ID;
					else if (strTag.equals("week"))
						m_nTagState = XMLTAG_NODE_WEEK;
					else if (strTag.equals("start_time"))
						m_nTagState = XMLTAG_NODE_START_TIME;
					else if (strTag.equals("end_time"))
						m_nTagState = XMLTAG_NODE_END_TIME;
					else if (strTag.equals("place"))
						m_nTagState = XMLTAG_NODE_PLACE;
					else
						m_nTagState = XMLTAG_NONE;
					
				} else if(eventType == XmlPullParser.END_TAG) {
//					Log.d(TAG, "End tag "+xpp.getName());
					strTag = xpp.getName();
					
					if (strTag.equals("node")) 
					{
						m_listLectureData.add(lectureData);
						lectureData = null;
					}
				} else if(eventType == XmlPullParser.TEXT) {
//					Log.d(TAG, "Text "+xpp.getText());
					strValue = xpp.getText();
					
					switch (m_nTagState) {
					case XMLTAG_NODE_LECTURE_ID:
						lectureData.dLectureId = strValue;
						break;
						
					case XMLTAG_NODE_NAME:
						lectureData.strName = strValue;
						break;
						
					case XMLTAG_NODE_PROFESSOR_ID:
						lectureData.dProfessorId = strValue;
						break;
						
					case XMLTAG_NODE_WEEK:
						lectureData.strWeek = strValue;
						break;
						
					case XMLTAG_NODE_START_TIME:
						lectureData.strStrat_Time = strValue;
						break;
					case XMLTAG_NODE_END_TIME:
						lectureData.strEnd_Time = strValue;
						break;
					case XMLTAG_NODE_PLACE:
						lectureData.strPlace = strValue;
						break;
					
					default:
						break;
					}
					
					m_nTagState = XMLTAG_NONE;
				}
				eventType = xpp.next();
			}
//			Log.d(TAG, "End document");

		} catch (Exception e) {
			bResult = false;
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
		}
		
		return bResult;
	}
	
	public ArrayList<LectureData> getLectureDataList() {
		return m_listLectureData;
	}
}
