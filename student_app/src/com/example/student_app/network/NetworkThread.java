package com.example.student_app.network;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;




import com.example.student_app.data.AttendanceData;

import com.example.student_app.data.Taking_LectureData;
import com.example.student_app.parser.AttendanceDataXMLParser;

import com.example.student_app.parser.ResultXMLParser;
import com.example.student_app.parser.Taking_LectureDataXMLParser;
import com.example.student_app.utils.Defines;



public class NetworkThread {
	public static final String				TAG											= "NetworkThread";
	
	
	/*
	 * getProfessorDataRunnable
	 * - Web Server로 부터 Professor Data 정보를 조회하는 thread
	 *   Main UI Thread에서는 Network과 같은 시간이 지연 될 수 있는 동작을 수행 해서는 않된다.
	 *   1. Main UI Thread에서 getProfessorDataRunnable를 실행한다.
	 *   2. getProfessorDataRunnable에서 Web Server로 부터 Book Data 정보를 가져오는 getProfessorDataThread를 실행한다.
	 *   3. getProfessorDataThread에서 Professor Data 정보를 가져오면 Main UI Thread에게 HND_MSG_GET_Professor_DATA Message를 보낸다.
	 *   4. 다시 Main UI Thread에서 Professor List를 Web Server로 부터 가져온 데이타로 Setting한다.
	 */
	public static class getTaking_LectureDataRunnable implements Runnable {
		private Context							m_context									= null;
		private Handler							m_handler									= null;
		private int								m_nTotLectureDataCnt						= 0;
		private ArrayList<Taking_LectureData>			m_listTaking_LectureData						= null;
		private Taking_LectureData					    m_taking_lectureData								= null;
		
		public getTaking_LectureDataRunnable(Context context, Handler handler) {
			m_context = context;
			m_handler = handler;
		}
		
		public int getTotTaking_LectureDataCnt() {
			return m_nTotLectureDataCnt;
		}
		
		public ArrayList<Taking_LectureData> getTaking_LectureDataList() {
			return m_listTaking_LectureData;
		}
		
		public void setPHPParameter(Taking_LectureData taking_lectureData) {
			m_taking_lectureData = taking_lectureData;
		}
		
		public void run() {
			Log.d(TAG, "getLectureDataRunnable run()");
			
			getTaking_LectureDataThread();
		}
		
	    /*
	     * getTaking_LectureDataThread
	     */
		private void getTaking_LectureDataThread() {
			Log.d(TAG, "getTaking_LectureDataThread()");
			
			Thread thread = new Thread(new Runnable() {
				public void run() {
					Log.d(TAG, "getTaking_LectureDataThread run()");

					try {
						URL url = new URL(Defines.SERVER_ADDRESS+Defines.TAKING_LECTURE_SEARCH_PHP + "?text=" + String.valueOf(m_taking_lectureData.dStudentNumber));
						//url.openStream();
						URLConnection urlcon = url.openConnection();
						urlcon.setConnectTimeout(Defines.NETWORK_TIMEOUT);
						urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout 설정
						urlcon.getInputStream();
						
						URL server = new URL(Defines.SERVER_ADDRESS+Defines.TAKING_LECTURE_DATA_XML);
						InputStream is = server.openStream();
						Taking_LectureDataXMLParser sdxp = new Taking_LectureDataXMLParser();
						if (sdxp.Parse(is)) {
							m_listTaking_LectureData = sdxp.getTaking_LectureDataList();
						}
					} catch (java.net.SocketTimeoutException e) {
						Log.e("TimeoutException Error", e.toString());
						m_handler.sendEmptyMessage(Defines.HND_MSG_NETWORK_TIMEOUT);
					} catch(Exception e) {
						Log.e("Error", e.toString());
					} finally{
						m_handler.sendEmptyMessage(Defines.HND_MSG_GET_TAKING_LECTURE_DATA);
					}
				}
			});
			
			thread.start();
		}
	}

  
	/*
     * insertAttendanceTableRunnable
     */
	public static class insertAttendanceTableRunnable implements Runnable {
		private Context							m_context									= null;
		private Handler							m_handler									= null;
		private ProgressDialog					m_insertAttendanceTableProgressDialog			= null;
		private AttendanceData						m_attendanceData								= null;
		private int								m_nResult									= 0;	// -1: 이미 Attendance Data 존재함, 0: insert 실패, 1: insert 성공
		private Taking_LectureData					    m_taking_lectureData								= null;
		public insertAttendanceTableRunnable(Context context, Handler handler) {
			m_context = context;
			m_handler = handler;
		}
		
		public ProgressDialog getProgressDialog() {
			return m_insertAttendanceTableProgressDialog;
		}
		
		public int getResult() {
			return m_nResult;
		}
		
		public void setPHPParameter(Taking_LectureData taking_lectureData) {
			m_taking_lectureData = taking_lectureData;
		}
		@Override
		public void run() {
			Log.d(TAG, "insertAttendanceTableRunnable run()");
			m_insertAttendanceTableProgressDialog = ProgressDialog.show(m_context, "Attendance Table 저장 중...", "잠시만 기다려주세요.");
			
			insertAttendanceTableThread();
		}
		
	    /*
	     * insertAttendanceTableThread
	     */
		private void insertAttendanceTableThread() {
			Log.d(TAG, "insertAttendanceTableThread()");

			Thread thread = new Thread(new Runnable() {
				public void run() {
					Log.d(TAG, "insertAttendanceTableThread run()");
					
					URL url = null;
					URL server = null;
					URLConnection urlcon = null;
					InputStream is = null;

					if (m_taking_lectureData != null) {				
						try {
							url = new URL(Defines.SERVER_ADDRESS+Defines.ATTENDANCE_INSERT_PHP+"?"
									+ "id=" + m_taking_lectureData.dLectureId
									+ "&number=" + URLEncoder.encode(m_taking_lectureData.dStudentNumber, "UTF-8")
									
									);
							//url.openStream();
							urlcon = url.openConnection();
							urlcon.setConnectTimeout(Defines.NETWORK_TIMEOUT);
							urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout 설정
							urlcon.getInputStream();
							
							server = new URL(Defines.SERVER_ADDRESS+Defines.ATTENDANCE_DATA_INSERT_RESULT_XML);
							is = server.openStream();
							ResultXMLParser rxp = new ResultXMLParser();
							if (rxp.Parse(is)) {
								m_nResult = rxp.getResult();
							}

						} catch (java.net.SocketTimeoutException e) {
							Log.e("TimeoutException Error", e.toString());
							m_handler.sendEmptyMessage(Defines.HND_MSG_NETWORK_TIMEOUT);
						} catch(Exception e) {
							Log.e("Error", e.toString());
						} finally {
							m_handler.sendEmptyMessage(Defines.HND_MSG_INSERT_ATTENDANCE_DATA);
						}
					}
				}
			});
			
			thread.start();
		}		
	}
   
}
