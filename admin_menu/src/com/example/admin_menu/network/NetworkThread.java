package com.example.admin_menu.network;

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

import com.example.admin_menu.data.LectureData;
import com.example.admin_menu.data.ProfessorData;
import com.example.admin_menu.data.StudentData;
import com.example.admin_menu.parser.LectureDataXMLParser;
import com.example.admin_menu.parser.ProfessorDataXMLParser;
import com.example.admin_menu.parser.ResultXMLParser;
import com.example.admin_menu.parser.StudentDataXMLParser;
import com.example.admin_menu.utils.Defines;



public class NetworkThread {
	public static final String				TAG											= "NetworkThread";
	
	/*
	 * getStudentDataRunnable
	 * - Web Server�� ���� Student Data ������ ��ȸ�ϴ� thread
	 *   Main UI Thread������ Network�� ���� �ð��� ���� �� �� �ִ� ������ ���� �ؼ��� �ʵȴ�.
	 *   1. Main UI Thread���� getStudentDataRunnable�� �����Ѵ�.
	 *   2. getStudentDataRunnable���� Web Server�� ���� Student Data ������ �������� getStudentDataThread�� �����Ѵ�.
	 *   3. getStudentDataThread���� Student Data ������ �������� Main UI Thread���� HND_MSG_GET_Student_DATA Message�� ������.
	 *   4. �ٽ� Main UI Thread���� Student List�� Web Server�� ���� ������ ����Ÿ�� Setting�Ѵ�.
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
						urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout ����
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
		private int								m_nResult									= 0;	// -1: �̹� Student Data ������, 0: insert ����, 1: insert ����
		
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
			m_insertStudentTableProgressDialog = ProgressDialog.show(m_context, "Student Table ���� ��...", "��ø� ��ٷ��ּ���.");
			
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
							urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout ����
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
		private int								m_nResult									= 0;	// 0: update ����, 1: update ����
		
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
			m_updateStudentTableProgressDialog = ProgressDialog.show(m_context, "Student Table ���� ��...", "��ø� ��ٷ��ּ���.");
			
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
									+ "&picture=" + URLEncoder.encode(m_studentData.strPictureURL, "UTF-8")); //�������� UTF-8�� ���ڵ��ϱ� ���� URLEncoder�� �̿��Ͽ� ���ڵ���
							//url.openStream();
							URLConnection urlcon = url.openConnection();
							urlcon.setConnectTimeout(Defines.NETWORK_TIMEOUT);
							urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout ����
							urlcon.getInputStream();

							URL server = new URL(Defines.SERVER_ADDRESS+Defines.STUDENT_DATA_UPDATE_RESULT_XML);
							//Log.e("TimeoutException Error", "xml���� ����");
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
		private int								m_nResult									= 0;	// 0: delete ����, 1: delete ����
		
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
			m_deleteStudentTableProgressDialog = ProgressDialog.show(m_context, "Student Table ���� ��...", "��ø� ��ٷ��ּ���.");
			
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
							urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout ����
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
			m_imageUploadProgressDialog = ProgressDialog.show(m_context, "Student picture upload...", "��ø� ��ٷ��ּ���.");
			
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
	 * - Web Server�� ���� Professor Data ������ ��ȸ�ϴ� thread
	 *   Main UI Thread������ Network�� ���� �ð��� ���� �� �� �ִ� ������ ���� �ؼ��� �ʵȴ�.
	 *   1. Main UI Thread���� getProfessorDataRunnable�� �����Ѵ�.
	 *   2. getProfessorDataRunnable���� Web Server�� ���� Book Data ������ �������� getProfessorDataThread�� �����Ѵ�.
	 *   3. getProfessorDataThread���� Professor Data ������ �������� Main UI Thread���� HND_MSG_GET_Professor_DATA Message�� ������.
	 *   4. �ٽ� Main UI Thread���� Professor List�� Web Server�� ���� ������ ����Ÿ�� Setting�Ѵ�.
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
						urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout ����
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
		private int								m_nResult									= 0;	// -1: �̹� Professor Data ������, 0: insert ����, 1: insert ����
		
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
			m_insertProfessorTableProgressDialog = ProgressDialog.show(m_context, "Professor Table ���� ��...", "��ø� ��ٷ��ּ���.");
			
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
							urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout ����
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
		private int								m_nResult									= 0;	// 0: update ����, 1: update ����
		
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
			m_updateProfessorTableProgressDialog = ProgressDialog.show(m_context, "Professor Table ���� ��...", "��ø� ��ٷ��ּ���.");
			
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
									+ "&picture=" + URLEncoder.encode(m_professorData.strPictureURL, "UTF-8")); //�������� UTF-8�� ���ڵ��ϱ� ���� URLEncoder�� �̿��Ͽ� ���ڵ���
							//url.openStream();
							URLConnection urlcon = url.openConnection();
							urlcon.setConnectTimeout(Defines.NETWORK_TIMEOUT);
							urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout ����
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
		private int								m_nResult									= 0;	// 0: delete ����, 1: delete ����
		
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
			m_deleteProfessorTableProgressDialog = ProgressDialog.show(m_context, "Professor Table ���� ��...", "��ø� ��ٷ��ּ���.");
			
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
							urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout ����
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
	 * - Web Server�� ���� Professor Data ������ ��ȸ�ϴ� thread
	 *   Main UI Thread������ Network�� ���� �ð��� ���� �� �� �ִ� ������ ���� �ؼ��� �ʵȴ�.
	 *   1. Main UI Thread���� getProfessorDataRunnable�� �����Ѵ�.
	 *   2. getProfessorDataRunnable���� Web Server�� ���� Book Data ������ �������� getProfessorDataThread�� �����Ѵ�.
	 *   3. getProfessorDataThread���� Professor Data ������ �������� Main UI Thread���� HND_MSG_GET_Professor_DATA Message�� ������.
	 *   4. �ٽ� Main UI Thread���� Professor List�� Web Server�� ���� ������ ����Ÿ�� Setting�Ѵ�.
	 */
	public static class getLectureDataRunnable implements Runnable {
		private Context							m_context									= null;
		private Handler							m_handler									= null;
		private int								m_nTotLectureDataCnt						= 0;
		private ArrayList<LectureData>			m_listLectureData						= null;
		
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
						URL url = new URL(Defines.SERVER_ADDRESS+Defines.LECTURE_SEARCH_PHP);
						//url.openStream();
						URLConnection urlcon = url.openConnection();
						urlcon.setConnectTimeout(Defines.NETWORK_TIMEOUT);
						urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout ����
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

    /*
     * insertLectureTableRunnable
     */
	public static class insertLectureTableRunnable implements Runnable {
		private Context							m_context									= null;
		private Handler							m_handler									= null;
		private ProgressDialog					m_insertLectureTableProgressDialog			= null;
		private LectureData					    m_lectureData								= null;
		private int								m_nResult									= 0;	// -1: �̹� Professor Data ������, 0: insert ����, 1: insert ����
		
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
			m_insertLectureTableProgressDialog = ProgressDialog.show(m_context, "Lecture Table ���� ��...", "��ø� ��ٷ��ּ���.");
			
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
									+ "l_id=" + m_lectureData.dLectureId
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
							urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout ����
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
		private int								m_nResult									= 0;	// 0: update ����, 1: update ����
		
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
			m_updateLectureTableProgressDialog = ProgressDialog.show(m_context, "Lecture Table ���� ��...", "��ø� ��ٷ��ּ���.");
			
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



); //�������� UTF-8�� ���ڵ��ϱ� ���� URLEncoder�� �̿��Ͽ� ���ڵ���
							//url.openStream();
							URLConnection urlcon = url.openConnection();
							urlcon.setConnectTimeout(Defines.NETWORK_TIMEOUT);
							urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout ����
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
		private int								m_nResult									= 0;	// 0: delete ����, 1: delete ����
		
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
			m_deleteLectureTableProgressDialog = ProgressDialog.show(m_context, "Lecture Table ���� ��...", "��ø� ��ٷ��ּ���.");
			
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
							urlcon.setReadTimeout(Defines.NETWORK_TIMEOUT);	// Timeout ����
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
}
