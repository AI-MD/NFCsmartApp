package com.example.smart_attbook.network;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.example.smart_attbook.data.AttendanceData;
import com.example.smart_attbook.data.LectureData;
import com.example.smart_attbook.data.ProfessorData;
import com.example.smart_attbook.data.StudentData;
import com.example.smart_attbook.parser.AttendanceDataXMLParser;
import com.example.smart_attbook.parser.LectureDataXMLParser;
import com.example.smart_attbook.parser.ProfessorDataXMLParser;
import com.example.smart_attbook.parser.ResultXMLParser;
import com.example.smart_attbook.parser.StudentDataXMLParser;
import com.example.smart_attbook.utils.Defines;


public class NetworkThread {
	public static final String				TAG											= "NetworkThread";
	
	/*
	 * getStudentDataRunnable
	 * - Web Server로 부터 Student Data 정보를 조회하는 thread
	 *   Main UI Thread에서는 Network과 같은 시간이 지연 될 수 있는 동작을 수행 해서는 않된다.
	 *   1. Main UI Thread에서 getStudentDataRunnable를 실행한다.
	 *   2. getStudentDataRunnable에서 Web Server로 부터 Student Data 정보를 가져오는 getStudentDataThread를 실행한다.
	 *   3. getStudentDataThread에서 Student Data 정보를 가져오면 Main UI Thread에게 HND_MSG_GET_Student_DATA Message를 보낸다.
	 *   4. 다시 Main UI Thread에서 Student List를 Web Server로 부터 가져온 데이타로 Setting한다.
	 */
	public static class getStudentDataRunnable implements Runnable {
		private Context							m_context									= null;
		private Handler							m_handler									= null;
		private int								m_nTotStudentDataCnt						= 0;
		private ArrayList<StudentData>			m_listStudentData							= null;
		
		public getStudentDataRunnable(Context context, Handler handler) {
			m_context = context;
			m_handler = handler;
		}
		
		public int getTotStudentDataCnt() {
			return m_nTotStudentDataCnt;
		}
		
		public ArrayList<StudentData> getStudentDataList() {
			return m_listStudentData;
		}
		
		public void run() {
			Log.d(TAG, "getStudentDataRunnable run()");
			
			getStudentDataThread();
		}
		
	    /*
	     * getStudentDataThread
	     */
		private void getStudentDataThread() {
			Log.d(TAG, "getStudentDataThread()");
			
			Thread thread = new Thread(new Runnable() {
				public void run() {
					Log.d(TAG, "getStudentDataThread run()");

					try {
						URL url = new URL(Defines.SERVER_ADDRESS+Defines.STUDENT_SEARCH_PHP);
						//url.openStream();
						URLConnection urlcon = url.openConnection();
						urlcon.setConnectTimeout(Defines.NETWORK_TIMEOUT);
						urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout 설정
						urlcon.getInputStream();
						
						URL server = new URL(Defines.SERVER_ADDRESS+Defines.STUDENT_DATA_XML);
						InputStream is = server.openStream();
						StudentDataXMLParser sdxp = new StudentDataXMLParser();
						if (sdxp.Parse(is)) {
							m_listStudentData = sdxp.getStudentDataList();
						}
					} catch (java.net.SocketTimeoutException e) {
						Log.e("TimeoutException Error", e.toString());
						m_handler.sendEmptyMessage(Defines.HND_MSG_NETWORK_TIMEOUT);
					} catch(Exception e) {
						Log.e("Error", e.toString());
					} finally{
						m_handler.sendEmptyMessage(Defines.HND_MSG_GET_STUDENT_DATA);
					}
				}
			});
			
			thread.start();
		}
	}
	
    /*
     * insertStudentTableRunnable
     */
	public static class insertStudentTableRunnable implements Runnable {
		private Context							m_context									= null;
		private Handler							m_handler									= null;
		private ProgressDialog					m_insertStudentTableProgressDialog			= null;
		private StudentData						m_studentData								= null;
		private int								m_nResult									= 0;	// -1: 이미 Student Data 존재함, 0: insert 실패, 1: insert 성공
		
		public insertStudentTableRunnable(Context context, Handler handler) {
			m_context = context;
			m_handler = handler;
		}
		
		public ProgressDialog getProgressDialog() {
			return m_insertStudentTableProgressDialog;
		}
		
		public int getResult() {
			return m_nResult;
		}
		
		public void setPHPParameter(StudentData studentData) {
			m_studentData = studentData;
		}

		@Override
		public void run() {
			Log.d(TAG, "insertStudentTableRunnable run()");
			m_insertStudentTableProgressDialog = ProgressDialog.show(m_context, "Student Table 저장 중...", "잠시만 기다려주세요.");
			
			insertStudentTableThread();
		}
		
	    /*
	     * insertStudentTableThread
	     */
		private void insertStudentTableThread() {
			Log.d(TAG, "insertStudentTableThread()");

			Thread thread = new Thread(new Runnable() {
				public void run() {
					Log.d(TAG, "insertStudentTableThread run()");
					
					URL url = null;
					URL server = null;
					URLConnection urlcon = null;
					InputStream is = null;

					if (m_studentData != null) {				
						try {
							url = new URL(Defines.SERVER_ADDRESS+Defines.STUDENT_INSERT_PHP+"?"
									+ "number=" + m_studentData.dStudentNumber
									+ "&name=" + URLEncoder.encode(m_studentData.strName, "UTF-8")
									+ "&phone=" + URLEncoder.encode(m_studentData.strPhoneNumber, "UTF-8")
									+ "&address=" + URLEncoder.encode(m_studentData.strAddress, "UTF-8")
									+ "&picture=" + URLEncoder.encode(m_studentData.strPictureURL, "UTF-8"));
							//url.openStream();
							urlcon = url.openConnection();
							urlcon.setConnectTimeout(Defines.NETWORK_TIMEOUT);
							urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout 설정
							urlcon.getInputStream();
							
							server = new URL(Defines.SERVER_ADDRESS+Defines.STUDENT_DATA_INSERT_RESULT_XML);
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
							m_handler.sendEmptyMessage(Defines.HND_MSG_INSERT_STUDENT_DATA);
						}
					}
				}
			});
			
			thread.start();
		}		
	}
	
	
	/*
     * updateStudentTableRunnable
     */
	public static class updateStudentTableRunnable implements Runnable {
		private Context							m_context									= null;
		private Handler							m_handler									= null;
		private ProgressDialog					m_updateStudentTableProgressDialog				= null;
		private StudentData						m_studentData									= null;
		private int								m_nResult									= 0;	// 0: update 실패, 1: update 성공
		
		public updateStudentTableRunnable(Context context, Handler handler) {
			m_context = context;
			m_handler = handler;
		}
		
		public ProgressDialog getProgressDialog() {
			return m_updateStudentTableProgressDialog;
		}
		
		public int getResult() {
			Log.d(TAG, "m_nResult = " + m_nResult);
			return m_nResult;
		}
		
		public void setPHPParameter(StudentData studentData) {
			m_studentData = studentData;
		}

		@Override
		public void run() {
			Log.d(TAG, "updateStudentTableRunnable run()");
			m_updateStudentTableProgressDialog = ProgressDialog.show(m_context, "Student Table 수정 중...", "잠시만 기다려주세요.");
			
			updateStudentTableThread();
		}

		/*
		 * updateStudentTableThread
		 */
		private void updateStudentTableThread() {
			Log.d(TAG, "updateStudentTableThread()");

			Thread thread = new Thread(new Runnable() {
				public void run() {
					Log.d(TAG, "updateStudentTableThread run()");
					
					if (m_studentData != null) {
						try {
							URL url = new URL(Defines.SERVER_ADDRESS+Defines.STUDENT_UPDATE_PHP+"?"
									
									+ "&number=" + m_studentData.dStudentNumber
									+ "&name=" + URLEncoder.encode(m_studentData.strName, "UTF-8")
									+ "&phone=" + URLEncoder.encode(m_studentData.strPhoneNumber, "UTF-8")
									+ "&address=" + URLEncoder.encode(m_studentData.strAddress, "UTF-8")
									+ "&picture=" + URLEncoder.encode(m_studentData.strPictureURL, "UTF-8")); //변수값을 UTF-8로 인코딩하기 위해 URLEncoder를 이용하여 인코딩함
							//url.openStream();
							URLConnection urlcon = url.openConnection();
							urlcon.setConnectTimeout(Defines.NETWORK_TIMEOUT);
							urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout 설정
							urlcon.getInputStream();

							URL server = new URL(Defines.SERVER_ADDRESS+Defines.STUDENT_DATA_UPDATE_RESULT_XML);
							//Log.e("TimeoutException Error", "xml파일 없음");
							InputStream is = server.openStream();
							ResultXMLParser rxp = new ResultXMLParser();
							if (rxp.Parse(is)) {
								Log.d(TAG, "rxp.getResult() = " + rxp.getResult()); 
								m_nResult = rxp.getResult();
							}
						} catch (java.net.SocketTimeoutException e) {
							Log.e("TimeoutException Error", e.toString());
							m_handler.sendEmptyMessage(Defines.HND_MSG_NETWORK_TIMEOUT);
						} catch(Exception e) {
							Log.e("Error", e.toString());
						} finally {
							m_handler.sendEmptyMessage(Defines.HND_MSG_DELETE_STUDENT_DATA);
							
						}
					}
				}
			});
			
			thread.start();
		}
	}
	
    /*
     * deleteStudentTableRunnable
     */
	public static class  deleteStudentTableRunnable implements Runnable {
		private Context							m_context									= null;
		private Handler							m_handler									= null;
		private ProgressDialog					m_deleteStudentTableProgressDialog				= null;
		private StudentData						m_studentData									= null;
		private int								m_nResult									= 0;	// 0: delete 실패, 1: delete 성공
		
		public deleteStudentTableRunnable(Context context, Handler handler) {
			m_context = context;
			m_handler = handler;
		}
		
		public ProgressDialog getProgressDialog() {
			return m_deleteStudentTableProgressDialog;
		}
		
		public int getResult() {
			return m_nResult;
		}
		
		public void setPHPParameter(StudentData studentData) {
			m_studentData = studentData;
		}

		@Override
		public void run() {
			Log.d(TAG, "deleteStudentTableRunnable run()");
			m_deleteStudentTableProgressDialog = ProgressDialog.show(m_context, "Student Table 삭제 중...", "잠시만 기다려주세요.");
			
			deleteStudentTableThread();
		}

		/*
		 * deleteStudentTableThread
		 */
		private void deleteStudentTableThread() {
			Log.d(TAG, "deleteStudentTableThread()");

			Thread thread = new Thread(new Runnable() {
				public void run() {
					Log.d(TAG, "deleteStudentTableThread run()");
					
					if (m_studentData != null) {
						try {
							URL url = new URL(Defines.SERVER_ADDRESS+Defines.STUDENT_DELETE_PHP+"?"
									+ "number=" +  m_studentData.dStudentNumber
									);
							//url.openStream();
							URLConnection urlcon = url.openConnection();
							urlcon.setConnectTimeout(Defines.NETWORK_TIMEOUT);
							urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout 설정
							urlcon.getInputStream();

							URL server = new URL(Defines.SERVER_ADDRESS+Defines.STUDENT_DATA_DELETE_RESULT_XML);
							InputStream is = server.openStream();
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
							m_handler.sendEmptyMessage(Defines.HND_MSG_DELETE_STUDENT_DATA);
						}
					}
				}
			});
			
			thread.start();
		}
	}
    /*
     * imageUploadRunnable
     */
	public static class imageUploadRunnable implements Runnable {
		private Context							m_context									= null;
		private Handler							m_handler									= null;
		private ProgressDialog					m_imageUploadProgressDialog					= null;
		private String							m_strImagePath								= null;
		private String 							m_outPut 									= null;
		
		public imageUploadRunnable(Context context, Handler handler) {
			m_context = context;
			m_handler = handler;
		}
		
		public ProgressDialog getProgressDialog() {
			return m_imageUploadProgressDialog;
		}
		
		public void setParameter(String strImagePath) {
			m_strImagePath = strImagePath;
		}
		
		public String getOutput() {
			return m_outPut;
		}

		@Override
		public void run() {
			Log.d(TAG, "imageUploadRunnable run()");
			m_imageUploadProgressDialog = ProgressDialog.show(m_context, "Student picture upload...", "잠시만 기다려주세요.");
			
			imageUploadThread();
		}
		
	    /*
	     * imageUploadThread
	     */
		private void imageUploadThread() {
			Log.d(TAG, "imageUploadThread()");
			
			Thread thread = new Thread(new Runnable() {
				public void run() {
					Log.d(TAG, "imageUploadThread run()");

					if (m_strImagePath != null) {
						Bitmap bitmapOrg = BitmapFactory.decodeFile(m_strImagePath);
						ByteArrayOutputStream bao = new ByteArrayOutputStream();
						
						// Resize the image
						double width = bitmapOrg.getWidth();
						double height = bitmapOrg.getHeight();
						double ratio = Defines.IMAGE_MAX_HEIGHT/width;
						int newheight = (int)(ratio*height);
						
						Log.d(TAG, "width: " + width);
						Log.d(TAG, "height: " + height);				
						
						bitmapOrg = Bitmap.createScaledBitmap(bitmapOrg, Defines.IMAGE_MAX_HEIGHT, newheight, true);
						
						//Here you can define .PNG as well
						bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 90, bao);
						byte[] ba = bao.toByteArray();
						//String ba1 = Base64.encodeBytes(ba);
						String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
						
						ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("image", ba1));
						
						try {
							HttpClient httpclient = new DefaultHttpClient();
							HttpPost httppost = new HttpPost(Defines.SERVER_ADDRESS+Defines.IMAGE_UPLOAD_PHP);
							httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

							HttpResponse response = httpclient.execute(httppost);
							HttpEntity entity = response.getEntity();                

							// print responce
							m_outPut = EntityUtils.toString(entity);
							Log.i("GET RESPONSE?-", m_outPut);

							//is = entity.getContent();
							Log.e("log_tag ******", "good connection");

							bitmapOrg.recycle();

						} catch (java.net.SocketTimeoutException e) {
							Log.e("TimeoutException Error", e.toString());
							m_handler.sendEmptyMessage(Defines.HND_MSG_NETWORK_TIMEOUT);
						} catch (Exception e) {
							Log.e("log_tag ******", "Error in http connection " + e.toString());
						} finally {
							m_handler.sendEmptyMessage(Defines.HND_MSG_IMAGE_UPLOAD);
						}
					}
				}
			});
			thread.start();
		}
	}
	
	/*
	 * getProfessorDataRunnable
	 * - Web Server로 부터 Professor Data 정보를 조회하는 thread
	 *   Main UI Thread에서는 Network과 같은 시간이 지연 될 수 있는 동작을 수행 해서는 않된다.
	 *   1. Main UI Thread에서 getProfessorDataRunnable를 실행한다.
	 *   2. getProfessorDataRunnable에서 Web Server로 부터 Book Data 정보를 가져오는 getProfessorDataThread를 실행한다.
	 *   3. getProfessorDataThread에서 Professor Data 정보를 가져오면 Main UI Thread에게 HND_MSG_GET_Professor_DATA Message를 보낸다.
	 *   4. 다시 Main UI Thread에서 Professor List를 Web Server로 부터 가져온 데이타로 Setting한다.
	 */
	public static class getProfessorDataRunnable implements Runnable {
		private Context							m_context									= null;
		private Handler							m_handler									= null;
		private int								m_nTotProfessorDataCnt						= 0;
		private ArrayList<ProfessorData>			m_listProfessorData							= null;
		
		public getProfessorDataRunnable(Context context, Handler handler) {
			m_context = context;
			m_handler = handler;
		}
		
		public int getTotProfessorDataCnt() {
			return m_nTotProfessorDataCnt;
		}
		
		public ArrayList<ProfessorData> getProfessorDataList() {
			return m_listProfessorData;
		}
		
		public void run() {
			Log.d(TAG, "getProfessorDataRunnable run()");
			
			getProfessorDataThread();
		}
		
	    /*
	     * getProfessorDataThread
	     */
		private void getProfessorDataThread() {
			Log.d(TAG, "getProfessorDataThread()");
			
			Thread thread = new Thread(new Runnable() {
				public void run() {
					Log.d(TAG, "getProfessorDataThread run()");

					try {
						URL url = new URL(Defines.SERVER_ADDRESS+Defines.PROFESSOR_SEARCH_PHP);
						//url.openStream();
						URLConnection urlcon = url.openConnection();
						urlcon.setConnectTimeout(Defines.NETWORK_TIMEOUT);
						urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout 설정
						urlcon.getInputStream();
						
						URL server = new URL(Defines.SERVER_ADDRESS+Defines.PROFESSOR_DATA_XML);
						InputStream is = server.openStream();
						ProfessorDataXMLParser sdxp = new ProfessorDataXMLParser();
						if (sdxp.Parse(is)) {
							m_listProfessorData = sdxp.getProfessorDataList();
						}
					} catch (java.net.SocketTimeoutException e) {
						Log.e("TimeoutException Error", e.toString());
						m_handler.sendEmptyMessage(Defines.HND_MSG_NETWORK_TIMEOUT);
					} catch(Exception e) {
						Log.e("Error", e.toString());
					} finally{
						m_handler.sendEmptyMessage(Defines.HND_MSG_GET_PROFESSOR_DATA);
					}
				}
			});
			
			thread.start();
		}
	}

    /*
     * insertProfessorTableRunnable
     */
	public static class insertProfessorTableRunnable implements Runnable {
		private Context							m_context									= null;
		private Handler							m_handler									= null;
		private ProgressDialog					m_insertProfessorTableProgressDialog			= null;
		private ProfessorData					m_professorData								= null;
		private int								m_nResult									= 0;	// -1: 이미 Professor Data 존재함, 0: insert 실패, 1: insert 성공
		
		public insertProfessorTableRunnable(Context context, Handler handler) {
			m_context = context;
			m_handler = handler;
		}
		
		public ProgressDialog getProgressDialog() {
			return m_insertProfessorTableProgressDialog;
		}
		
		public int getResult() {
			return m_nResult;
		}
		
		public void setPHPParameter(ProfessorData professorData) {
			m_professorData = professorData;
		}

		@Override
		public void run() {
			Log.d(TAG, "insertProfessorTableRunnable run()");
			m_insertProfessorTableProgressDialog = ProgressDialog.show(m_context, "Professor Table 저장 중...", "잠시만 기다려주세요.");
			
			insertProfessorTableThread();
		}
		
	    /*
	     * insertProfessorTableThread
	     */
		
		private void insertProfessorTableThread() {
			Log.d(TAG, "insertProfessorTableThread()");

			Thread thread = new Thread(new Runnable() {
				public void run() {
					Log.d(TAG, "insertProfessorTableThread run()");
					
					URL url = null;
					URL server = null;
					URLConnection urlcon = null;
					InputStream is = null;

					if (m_professorData != null) {				
						try {
							url = new URL(Defines.SERVER_ADDRESS+Defines.PROFESSOR_INSERT_PHP+"?"
									+ "id=" + m_professorData.dProfessorId
									+ "&name=" + URLEncoder.encode(m_professorData.strName, "UTF-8")
									+ "&phone=" + URLEncoder.encode(m_professorData.strPhoneNumber, "UTF-8")
									+ "&address=" + URLEncoder.encode(m_professorData.strAddress, "UTF-8")
									+ "&picture=" + URLEncoder.encode(m_professorData.strPictureURL, "UTF-8"));
							//url.openStream();
							urlcon = url.openConnection();
							urlcon.setConnectTimeout(Defines.NETWORK_TIMEOUT);
							urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout 설정
							urlcon.getInputStream();
							
							server = new URL(Defines.SERVER_ADDRESS+Defines.PROFESSOR_DATA_INSERT_RESULT_XML);
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
							m_handler.sendEmptyMessage(Defines.HND_MSG_INSERT_PROFESSOR_DATA);
						}
					}
				}
			});
			
			thread.start();
		}		
	}
	

	/*
     * updateprofessorTableRunnable
     */
	public static class updateProfessorTableRunnable implements Runnable {
		private Context							m_context									= null;
		private Handler							m_handler									= null;
		private ProgressDialog					m_updateProfessorTableProgressDialog				= null;
		private ProfessorData					m_professorData									= null;
		private int								m_nResult									= 0;	// 0: update 실패, 1: update 성공
		
		public updateProfessorTableRunnable(Context context, Handler handler) {
			m_context = context;
			m_handler = handler;
		}
		
		public ProgressDialog getProgressDialog() {
			return m_updateProfessorTableProgressDialog;
		}
		
		public int getResult() {
			return m_nResult;
		}
		
		public void setPHPParameter(ProfessorData professorData) {
			m_professorData = professorData;
		}

		@Override
		public void run() {
			Log.d(TAG, "updateProfessorTabileRunnable run()");
			m_updateProfessorTableProgressDialog = ProgressDialog.show(m_context, "Professor Table 수정 중...", "잠시만 기다려주세요.");
			
			updateProfessorTableThread();
		}

		/*
		 * updateProfessorTableThread
		 */
		
		private void updateProfessorTableThread() {
			Log.d(TAG, "updateProfessorTableThread()");

			Thread thread = new Thread(new Runnable() {
				public void run() {
					Log.d(TAG, "updateProfessorTableThread run()");
					
					if (m_professorData != null) {
						try {
							URL url = new URL(Defines.SERVER_ADDRESS+Defines.PROFESSOR_UPDATE_PHP+"?"
									
									+ "&id=" + m_professorData.dProfessorId
									+ "&name=" + URLEncoder.encode(m_professorData.strName, "UTF-8")
									+ "&phone=" + URLEncoder.encode(m_professorData.strPhoneNumber, "UTF-8")
									+ "&address=" + URLEncoder.encode(m_professorData.strAddress, "UTF-8")
									+ "&picture=" + URLEncoder.encode(m_professorData.strPictureURL, "UTF-8")); //변수값을 UTF-8로 인코딩하기 위해 URLEncoder를 이용하여 인코딩함
							//url.openStream();
							URLConnection urlcon = url.openConnection();
							urlcon.setConnectTimeout(Defines.NETWORK_TIMEOUT);
							urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout 설정
							urlcon.getInputStream();

							URL server = new URL(Defines.SERVER_ADDRESS+Defines.PROFESSOR_DATA_UPDATE_RESULT_XML);
							InputStream is = server.openStream();
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
							m_handler.sendEmptyMessage(Defines.HND_MSG_DELETE_PROFESSOR_DATA);
							
						}
					}
				}
			});
			
			thread.start();
		}
	}
	

    /*
     * deleteProfessorTableRunnable
     */
	public static class  deleteProfessorTableRunnable implements Runnable {
		private Context							m_context									= null;
		private Handler							m_handler									= null;
		private ProgressDialog					m_deleteProfessorTableProgressDialog				= null;
		private ProfessorData					m_professorData									= null;
		private int								m_nResult									= 0;	// 0: delete 실패, 1: delete 성공
		
		public deleteProfessorTableRunnable(Context context, Handler handler) {
			m_context = context;
			m_handler = handler;
		}
		
		public ProgressDialog getProgressDialog() {
			return m_deleteProfessorTableProgressDialog;
		}
		
		public int getResult() {
			return m_nResult;
		}
		
		public void setPHPParameter(ProfessorData professorData) {
			m_professorData = professorData;
		
		}
        
		@Override
		public void run() {
			Log.d(TAG, "deleteProfessorTableRunnable run()");
			m_deleteProfessorTableProgressDialog = ProgressDialog.show(m_context, "Professor Table 삭제 중...", "잠시만 기다려주세요.");
			
			deleteProfessorTableThread();
		}

		/*
		 * deleteProfessorTableThread
		 */
		private void deleteProfessorTableThread() {
			Log.d(TAG, "deleteProfessorTableThread()");

			Thread thread = new Thread(new Runnable() {
				public void run() {
					Log.d(TAG, "deleteProfessorTableThread run()");
					
					if (m_professorData != null) {
						try {
							URL url = new URL(Defines.SERVER_ADDRESS+Defines.PROFESSOR_DELETE_PHP+"?"
									+ "id=" +  m_professorData.dProfessorId
									);
							//url.openStream();
							URLConnection urlcon = url.openConnection();
							urlcon.setConnectTimeout(Defines.NETWORK_TIMEOUT);
							urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout 설정
							urlcon.getInputStream();

							URL server = new URL(Defines.SERVER_ADDRESS+Defines.PROFESSOR_DATA_DELETE_RESULT_XML);
							InputStream is = server.openStream();
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
							m_handler.sendEmptyMessage(Defines.HND_MSG_DELETE_PROFESSOR_DATA);
						}
					}
				}
			});
			
			thread.start();
		}
	}
	
	/*
	 * getProfessorDataRunnable
	 * - Web Server로 부터 Professor Data 정보를 조회하는 thread
	 *   Main UI Thread에서는 Network과 같은 시간이 지연 될 수 있는 동작을 수행 해서는 않된다.
	 *   1. Main UI Thread에서 getProfessorDataRunnable를 실행한다.
	 *   2. getProfessorDataRunnable에서 Web Server로 부터 Book Data 정보를 가져오는 getProfessorDataThread를 실행한다.
	 *   3. getProfessorDataThread에서 Professor Data 정보를 가져오면 Main UI Thread에게 HND_MSG_GET_Professor_DATA Message를 보낸다.
	 *   4. 다시 Main UI Thread에서 Professor List를 Web Server로 부터 가져온 데이타로 Setting한다.
	 */
	
    /*
     * insertLectureTableRunnable
     */
	public static class insertLectureTableRunnable implements Runnable {
		private Context							m_context									= null;
		private Handler							m_handler									= null;
		private ProgressDialog					m_insertLectureTableProgressDialog			= null;
		private LectureData					    m_lectureData								= null;
		private int								m_nResult									= 0;	// -1: 이미 Professor Data 존재함, 0: insert 실패, 1: insert 성공
		
		public insertLectureTableRunnable(Context context, Handler handler) {
			m_context = context;
			m_handler = handler;
		}
		
		public ProgressDialog getProgressDialog() {
			return m_insertLectureTableProgressDialog;
		}
		
		public int getResult() {
			return m_nResult;
		}
		
		public void setPHPParameter(LectureData lectureData) {
			m_lectureData = lectureData;
		}

		@Override
		public void run() {
			Log.d(TAG, "insertLectureTableRunnable run()");
			m_insertLectureTableProgressDialog = ProgressDialog.show(m_context, "Lecture Table 저장 중...", "잠시만 기다려주세요.");
			
			insertLectureTableThread();
		}
		
	    /*
	     * insertLectureTableThread
	     */
		
		private void insertLectureTableThread() {
			Log.d(TAG, "insertLectureTableThread()");

			Thread thread = new Thread(new Runnable() {
				public void run() {
					Log.d(TAG, "insertLectureTableThread run()");
					
					URL url = null;
					URL server = null;
					URLConnection urlcon = null;
					InputStream is = null;

					if (m_lectureData != null) {				
						try {
							url = new URL(Defines.SERVER_ADDRESS+Defines.LECTURE_INSERT_PHP+"?"
									+ "lecture_id=" + m_lectureData.dLectureId
									+ "&name=" + URLEncoder.encode(m_lectureData.strName, "UTF-8")
									+ "&professor_id=" + m_lectureData.dProfessorId
									+ "&week=" + URLEncoder.encode(m_lectureData.strWeek, "UTF-8")
									+ "&start_time=" + URLEncoder.encode(m_lectureData.strStrat_Time, "UTF-8")
									+ "&end_time=" + URLEncoder.encode(m_lectureData.strEnd_Time, "UTF-8")
									+ "&place=" + URLEncoder.encode(m_lectureData.strPlace, "UTF-8")
									
									
									
									);
							//url.openStream();
							urlcon = url.openConnection();
							urlcon.setConnectTimeout(Defines.NETWORK_TIMEOUT);
							urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout 설정
							urlcon.getInputStream();
							
							server = new URL(Defines.SERVER_ADDRESS+Defines.LECTURE_DATA_INSERT_RESULT_XML);
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
							m_handler.sendEmptyMessage(Defines.HND_MSG_INSERT_LECTURE_DATA);
						}
					}
				}
			});
			
			thread.start();
		}		
	}
	

	/*
     * updateprofessorTableRunnable
     */
	public static class updateLectureTableRunnable implements Runnable {
		private Context							m_context									= null;
		private Handler							m_handler									= null;
		private ProgressDialog					m_updateLectureTableProgressDialog				= null;
		private LectureData					m_lectureData									= null;
		private int								m_nResult									= 0;	// 0: update 실패, 1: update 성공
		
		public updateLectureTableRunnable(Context context, Handler handler) {
			m_context = context;
			m_handler = handler;
		}
		
		public ProgressDialog getProgressDialog() {
			return m_updateLectureTableProgressDialog;
		}
		
		public int getResult() {
			return m_nResult;
		}
		
		public void setPHPParameter(LectureData lectureData) {
			m_lectureData = lectureData;
		}

		@Override
		public void run() {
			Log.d(TAG, "updateLectureTabileRunnable run()");
			m_updateLectureTableProgressDialog = ProgressDialog.show(m_context, "Lecture Table 수정 중...", "잠시만 기다려주세요.");
			
			updateLectureTableThread();
		}

		/*
		 * updateLectureTableThread
		 */
		
		private void updateLectureTableThread() {
			Log.d(TAG, "updateLectureTableThread()");

			Thread thread = new Thread(new Runnable() {
				public void run() {
					Log.d(TAG, "updateLectureTableThread run()");
					
					if (m_lectureData != null) {
						try {
							URL url = new URL(Defines.SERVER_ADDRESS+Defines.LECTURE_UPDATE_PHP+"?"
									
+ "l_id=" + m_lectureData.dLectureId
+ "&name=" + URLEncoder.encode(m_lectureData.strName, "UTF-8")
+ "&professor_id=" + m_lectureData.dProfessorId
+ "&week=" + URLEncoder.encode(m_lectureData.strWeek, "UTF-8")
+ "&start_time=" + URLEncoder.encode(m_lectureData.strStrat_Time, "UTF-8")
+ "&end_time=" + URLEncoder.encode(m_lectureData.strEnd_Time, "UTF-8")
+ "&place=" + URLEncoder.encode(m_lectureData.strPlace, "UTF-8")



); //변수값을 UTF-8로 인코딩하기 위해 URLEncoder를 이용하여 인코딩함
							//url.openStream();
							URLConnection urlcon = url.openConnection();
							urlcon.setConnectTimeout(Defines.NETWORK_TIMEOUT);
							urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout 설정
							urlcon.getInputStream();

							URL server = new URL(Defines.SERVER_ADDRESS+Defines.LECTURE_DATA_UPDATE_RESULT_XML);
							InputStream is = server.openStream();
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
							m_handler.sendEmptyMessage(Defines.HND_MSG_DELETE_LECTURE_DATA);
							
						}
					}
				}
			});
			
			thread.start();
		}
	}
	

    /*
     * deleteProfessorTableRunnable
     */
	public static class  deleteLectureTableRunnable implements Runnable {
		private Context							m_context									= null;
		private Handler							m_handler									= null;
		private ProgressDialog					m_deleteLectureTableProgressDialog				= null;
		private LectureData					    m_lectureData									= null;
		private int								m_nResult									= 0;	// 0: delete 실패, 1: delete 성공
		
		public deleteLectureTableRunnable(Context context, Handler handler) {
			m_context = context;
			m_handler = handler;
		}
		
		public ProgressDialog getProgressDialog() {
			return m_deleteLectureTableProgressDialog;
		}
		
		public int getResult() {
			return m_nResult;
		}
		
		public void setPHPParameter(LectureData lectureData) {
			m_lectureData = lectureData;
		}
		
        
		@Override
		public void run() {
			Log.d(TAG, "deleteLectureTableRunnable run()");
			m_deleteLectureTableProgressDialog = ProgressDialog.show(m_context, "Lecture Table 삭제 중...", "잠시만 기다려주세요.");
			
			deleteLectureTableThread();
		}

		/*
		 * deleteLectureTableThread
		 */
		private void deleteLectureTableThread() {
			Log.d(TAG, "deleteLectureTableThread()");

			Thread thread = new Thread(new Runnable() {
				public void run() {
					Log.d(TAG, "deleteLectureTableThread run()");
					
					if (m_lectureData != null) {
						try {
							URL url = new URL(Defines.SERVER_ADDRESS+Defines.LECTURE_DELETE_PHP+"?"
									+ "l_id=" +  m_lectureData.dLectureId
									);
							//url.openStream();
							URLConnection urlcon = url.openConnection();
							urlcon.setConnectTimeout(Defines.NETWORK_TIMEOUT);
							urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout 설정
							urlcon.getInputStream();

							URL server = new URL(Defines.SERVER_ADDRESS+Defines.LECTURE_DATA_DELETE_RESULT_XML);
							InputStream is = server.openStream();
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
							m_handler.sendEmptyMessage(Defines.HND_MSG_DELETE_LECTURE_DATA);
						}
					}
				}
			});
			
			thread.start();
		}
	}
	
	public static class getLectureDataRunnable implements Runnable {
		private Context							m_context									= null;
		private Handler							m_handler									= null;
		private int								m_nTotLectureDataCnt						= 0;
		private ArrayList<LectureData>			m_listLectureData						= null;
		private LectureData					   m_lectureData								= null;
		public getLectureDataRunnable(Context context, Handler handler) {
			m_context = context;
			m_handler = handler;
		}
		
		public int getTotLectureDataCnt() {
			return m_nTotLectureDataCnt;
		}
		
		public ArrayList<LectureData> getLectureDataList() {
			return m_listLectureData;
		}
		public void setPHPParameter(LectureData lectureData) {
			m_lectureData = lectureData;
		}
		public void run() {
			Log.d(TAG, "getLectureDataRunnable run()");
			
			getLectureDataThread();
		}
		
	    /*
	     * getLectureDataThread
	     */
		private void getLectureDataThread() {
			Log.d(TAG, "getLectureDataThread()");
			
			Thread thread = new Thread(new Runnable() {
				public void run() {
					Log.d(TAG, "getLectureDataThread run()");

					try {
						
						Log.e(TAG, "m_lectureData.dProfessorId = "+m_lectureData.dProfessorId);
						
						
						URL url = new URL(Defines.SERVER_ADDRESS+Defines.LECTURE_SEARCH_PHP+"?text=" + String.valueOf(m_lectureData.dProfessorId));
						//url.openStream();
						URLConnection urlcon = url.openConnection();
						urlcon.setConnectTimeout(Defines.NETWORK_TIMEOUT);
						urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout 설정
						urlcon.getInputStream();
						
						URL server = new URL(Defines.SERVER_ADDRESS+Defines.LECTURE_DATA_XML);
						InputStream is = server.openStream();
						LectureDataXMLParser sdxp = new LectureDataXMLParser();
						if (sdxp.Parse(is)) {
							m_listLectureData = sdxp.getLectureDataList();
						}
					} catch (java.net.SocketTimeoutException e) {
						Log.e("TimeoutException Error", e.toString());
						m_handler.sendEmptyMessage(Defines.HND_MSG_NETWORK_TIMEOUT);
					} catch(Exception e) {
						Log.e("Error", e.toString());
					} finally{
						m_handler.sendEmptyMessage(Defines.HND_MSG_GET_LECTURE_DATA);
					}
				}
			});
			
			thread.start();
		}
	}

	
	
	
	
	
	
	
	public static class getL_StudentDataRunnable implements Runnable {
		private Context							m_context									= null;
		private Handler							m_handler									= null;
		private int								m_nTotl_studentDataCnt						= 0;
		private ArrayList<StudentData>		m_liststudentData							= null;
		private LectureData					   m_lectureData								= null;
		public getL_StudentDataRunnable(Context context, Handler handler) {
			m_context = context;
			m_handler = handler;
		}
		
		public int getTotL_studentDataCnt() {
			return m_nTotl_studentDataCnt;
		}
		
		public ArrayList<StudentData> getStudentDataList() {
			return m_liststudentData;
		}
		
		public void setPHPParameter(LectureData lectureData) {
			
			m_lectureData = lectureData;
			
		}
		
		public void run() {
			Log.d(TAG, "getl_studentDataRunnable run()");
			
			getL_StudentDataThread();
		}
		
	    /*
	     * getL_StudentDataThread
	     */
		private void getL_StudentDataThread() {
			Log.d(TAG, "getL_StudentDataThread()");
			
			Thread thread = new Thread(new Runnable() {
				public void run() {
					Log.d(TAG, "getL_StudentDataThread run()");

					try {
						
						
						URL url = new URL(Defines.SERVER_ADDRESS+Defines.L_STUDENT_SEARCH_PHP+"?id=" + String.valueOf(m_lectureData.dLectureId));
						
						//url.openStream();
						URLConnection urlcon = url.openConnection();
						urlcon.setConnectTimeout(Defines.NETWORK_TIMEOUT);
						urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout 설정
						urlcon.getInputStream();
						
						URL server = new URL(Defines.SERVER_ADDRESS+Defines.L_STUDENT_DATA_XML);
						InputStream is = server.openStream();
						StudentDataXMLParser sdxp = new StudentDataXMLParser();
						if (sdxp.Parse(is)) {
							m_liststudentData = sdxp.getStudentDataList();
							
								}
					} catch (java.net.SocketTimeoutException e) {
						Log.e("TimeoutException Error", e.toString());
						m_handler.sendEmptyMessage(Defines.HND_MSG_NETWORK_TIMEOUT);
					} catch(Exception e) {
						Log.e("Error", e.toString());
					} finally{
						m_handler.sendEmptyMessage(Defines.HND_MSG_GET_L_STUDENT_DATA);
					}
				}
			});
			
			thread.start();
		}
	}
	
	
	
	public static class getAttendanceDataRunnable implements Runnable {
		private Context							m_context									= null;
		private Handler							m_handler									= null;
		private int								m_nTotattendanceDataCnt						= 0;
		private ArrayList<AttendanceData>		m_listattendanceData							= null;
		private AttendanceData					   m_attendanceData								= null;
		private ArrayList<StudentData>		m_lista_studentData							= null;
		
		private boolean threadFlag = true;
		
		public void endThread() {
			threadFlag = false;
		}
		public getAttendanceDataRunnable(Context context, Handler handler) {
			m_context = context;
			m_handler = handler;
		}
		
		public int getTotAttendanceDataCnt() {
			return m_nTotattendanceDataCnt;
		}
		
		public ArrayList<StudentData> getStudentDataList() {
			return m_lista_studentData;
		}
		
		public void setPHPParameter(AttendanceData attendanceData) {
			
			m_attendanceData	 = attendanceData;
			
		}
		
		public void run() {
			Log.d(TAG, "getAttendanceDataRunnable run()");
			
			getAttendanceDataThread();
		}
		
	    /*
	     * getAttendanceDataThread
	     */
		private void getAttendanceDataThread() {
			Log.d(TAG, "getAttendanceDataThread()");
			
			Thread thread = new Thread(new Runnable() {
				public void run() {
					while (threadFlag) {
						try {
							URL url = new URL(Defines.SERVER_ADDRESS+Defines.ATTENDANCE_SEARCH_PHP+"?id="
							+ String.valueOf(m_attendanceData.dLectureId)
							+"&day="+String.valueOf(m_attendanceData.strAttendance_Day)
									);
							
							//url.openStream();
							URLConnection urlcon = url.openConnection();
							urlcon.setConnectTimeout(Defines.NETWORK_TIMEOUT);
							urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout 설정
							urlcon.getInputStream();
							
							URL server = new URL(Defines.SERVER_ADDRESS+Defines.ATTENDANCE_DATA_XML);
							InputStream is = server.openStream();
							
							StudentDataXMLParser sdxp = new StudentDataXMLParser();
							if (sdxp.Parse(is)) {
								m_lista_studentData = sdxp.getStudentDataList();
							}
						} catch (java.net.SocketTimeoutException e) {
							Log.e("TimeoutException Error", e.toString());
							m_handler.sendEmptyMessage(Defines.HND_MSG_NETWORK_TIMEOUT);
						} catch(Exception e) {
							Log.e("Error", e.toString());
						} finally{
							m_handler.sendEmptyMessage(Defines.HND_MSG_GET_ATTENDANCE_DATA);
						}

						try {
							Thread.sleep(3000);
							Log.e("Error", "Thread sleep 3000");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
			
			thread.start();
		}
	}
	
	
}
