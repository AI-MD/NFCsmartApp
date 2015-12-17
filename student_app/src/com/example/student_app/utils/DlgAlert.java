package com.example.student_app.utils;

import java.lang.reflect.Method;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;


public class DlgAlert {
	private static Activity mParent = null;
	private static Object mObject = null;
	private static String mOkMethod = null;
	private static String mAllMethod = null;
	private static String mOneMethod = null;
	private static String mCancelMethod = null;
	private static final String alertTitle = "알림";
	private static final String errorTitle = "경고";
	private static final String regularExpr = "&";
	public static final String mAlertTitle = alertTitle;
	private static Object[] mParams = null;
	
	private static class BtnTitle {
		public static String BTN_OK = "확인";
		public static String BTN_CANCEL = "취소";
	}

	public static void Alert(Activity atvt, String msg) {
		Alert(atvt, msg, null, null);
	}
	public static void Alert(Activity atvt, String msg, String method) {
		Alert(atvt, msg, atvt, method);
	}
	
	public static void Alert(Activity atvt, String msg, final int DialogIndex, final onOkMethodListener okListener) {
		mParent = atvt;

		AlertDialog.Builder ad = new AlertDialog.Builder(atvt);

		ad.setTitle(alertTitle);
		ad.setMessage(msg);
		ad.setNeutralButton(BtnTitle.BTN_OK, new DialogInterface.OnClickListener() {   
			public void onClick(DialogInterface dialog, int whichButton) {
				if(okListener != null) {
					try {
						okListener.onClickAlertOkButton(DialogIndex);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		ad.show();	
	}
	
	
	public static void Alert(Activity atvt, String msg, Object obj, String method){
		mParent = atvt;
		mObject = obj;
		mOkMethod = method;

		AlertDialog.Builder ad = new AlertDialog.Builder(atvt);

		ad.setTitle(alertTitle);
		ad.setMessage(msg);
		ad.setNeutralButton(BtnTitle.BTN_OK, new DialogInterface.OnClickListener() {   
			public void onClick(DialogInterface dialog, int whichButton) {
				if(mOkMethod != null) {
					try {
						Class<?> cls = mObject.getClass();
						Method method = cls.getMethod(mOkMethod);
						method.invoke(mObject);
					} catch(Exception e) {
						//PrintLog.printException(mParent, e);
					}
				}
			}
		});
		ad.show();
	}
	
	
	/**
	 * 알림메세지창을 활성화 한후, 확인버튼 클릭시 해당액티비티의 메소드를 호출한다
	 * @param atvt
	 * @param msg
	 * @param okMethod
	 */
	public static void AlertOk(Activity atvt, String msg, String okMethod){
		 AlertOk(atvt, msg, atvt, okMethod);
	}
	
	/**
	 * 알림메세지창을 활성화 한후, 확인버튼 클릭시 해당액티비티의 메소드를 호출한다
	 * @param atvt
	 * @param msg
	 * @param okObject
	 * @param okMethod
	 */
	private static void AlertOk(Activity atvt, String msg, Activity okObject, String okMethod) {
		mParent = atvt;
		mObject = okObject;
		mOkMethod = okMethod;

		AlertDialog.Builder ad = new AlertDialog.Builder(atvt);

		ad.setTitle(alertTitle);
		ad.setMessage(msg);
		ad.setNeutralButton(BtnTitle.BTN_OK, new DialogInterface.OnClickListener() {   
			public void onClick(DialogInterface dialog, int whichButton) {
				if(mOkMethod != null) {
					try {
						Class<?> cls = mObject.getClass();
						Method method = cls.getMethod(mOkMethod);
						method.invoke(mObject);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		ad.show();
		
	}
	
	/**
	 * 알림메세지창을 활성화 한후, 확인버튼 클릭시 해당액티비티의 메소드를 호출한다
	 * @param atvt
	 * @param msg
	 * @param okObject
	 * @param okMethod
	 */
	public static void AlertOkBackFalse(Activity atvt, String msg, Activity okObject, String okMethod) {
		mParent = atvt;
		mObject = okObject;
		mOkMethod = okMethod;

		AlertDialog.Builder ad = new AlertDialog.Builder(atvt);
	
		ad.setCancelable(false);
		ad.setTitle(alertTitle);
		ad.setMessage(msg);
		ad.setNeutralButton(BtnTitle.BTN_OK, new DialogInterface.OnClickListener() {   
			public void onClick(DialogInterface dialog, int whichButton) {
				if(mOkMethod != null) {
					try {
						Class<?> cls = mObject.getClass();
						Method method = cls.getMethod(mOkMethod);
						method.invoke(mObject);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		ad.show();
		
	}
	
	/**
	 * 알림메시지창을 활성화 한후, 버튼1메시지, 버튼2메시지, 클릭시 해당액티비티의 메소드를 호출한다. 
	 * @param atvt - 액티비티
	 * @param msg - 알림메시지
	 * @param okMsg - 버튼1메시지
	 * @param noMsg - 버튼2메시지
	 * @param okMethod - 메소드명
	 * @param cancelMethod - 메소드명
	 */
	public static void AlertOkCancle(Activity atvt, String msg, String okMsg, String noMsg, String okMethod, String cancelMethod ){
		AlertOkCancel(atvt, msg, atvt, okMsg, noMsg, okMethod, cancelMethod);
	}
	
	
	/**
	 * 알림메세지창을 활성화 한후, 확인버튼 클릭시 해당액티비티의 메소드를 호출한다
	 * 
	 * @param atvt - 액티비티
	 * @param msg - 알림메세지
	 * @param mtdnm - 메소드명
	 */
	public static void AlertOkCancel(Activity atvt, String msg, String okMethod, String cancelMethod){
		AlertOkCancel(atvt, msg, atvt, okMethod, cancelMethod);
	}
	
	
	
	public interface onOkMethodListener {
		public void onClickAlertOkButton(int DialogIndex);
	}
	public interface onCancelMethodListener {
		public void onClickAlertCancelButton(int DialogIndex);
	}
	
	public static void AlertOkCancel(Activity atvt, String msg, final int DialogIndex, final onOkMethodListener okListener, final onCancelMethodListener cancelListener){
				
		AlertOkCancel(atvt, msg, DialogIndex, BtnTitle.BTN_OK, okListener, BtnTitle.BTN_CANCEL, cancelListener);
		
	}
	
	public static void AlertOkCancel(Activity atvt, String msg, final int DialogIndex, String okBtnName, final onOkMethodListener okListener, String cancelBtnName, final onCancelMethodListener cancelListener){
		
		mParent = atvt;

		AlertDialog.Builder ad = new AlertDialog.Builder(atvt);

		ad.setTitle(alertTitle);
		ad.setMessage(msg);
		ad.setNeutralButton(okBtnName, new DialogInterface.OnClickListener() {   
			public void onClick(DialogInterface dialog, int whichButton) {
				if(okListener != null) {
					try {
						okListener.onClickAlertOkButton(DialogIndex);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		ad.setNegativeButton(cancelBtnName, new DialogInterface.OnClickListener() {   
			public void onClick(DialogInterface dialog, int whichButton) {
				if(cancelListener != null) {
					try {
						cancelListener.onClickAlertCancelButton(DialogIndex);
					} catch(Exception e) {
						e.printStackTrace();
					}
				} 
			}
		});
		ad.show();		
		
	}

	/**
	 * 알림메시지창을 활성화 한후, 버튼1메시지, 버튼2메시지, 클릭시 해당액티비티의 메소드를 호출한다. 
	 * @param atvt - 액티비티
	 * @param msg - 메시지
	 * @param okObject - 액티비티
	 * @param okMsg - 버튼1메시지
	 * @param noMsg - 버튼2메시지
	 * @param okMethod - 메소드명
	 * @param cancelMethod - 메소드명
	 */
	public static void AlertOkCancel(Activity atvt, String msg, Object okObject, String okMsg, String noMsg, String okMethod, String cancelMethod){
		mParent = atvt;
		mObject = okObject;
		mOkMethod = okMethod;
		mCancelMethod = cancelMethod;
		String okMessage =  okMsg;
		String noMessage =  noMsg;

		AlertDialog.Builder ad = new AlertDialog.Builder(atvt);

		ad.setTitle(alertTitle);
		ad.setMessage(msg);
		ad.setNeutralButton(okMessage, new DialogInterface.OnClickListener() {   
			public void onClick(DialogInterface dialog, int whichButton) {
				if(mOkMethod != null) {
					try {
						Class<?> cls = mObject.getClass();
						Method method = cls.getMethod(mOkMethod);
						method.invoke(mObject);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		ad.setNegativeButton(noMessage, new DialogInterface.OnClickListener() {   
			public void onClick(DialogInterface dialog, int whichButton) {
				if(mCancelMethod != null) {
					try {
						Class<?> cls = mObject.getClass();
						Method method = cls.getMethod(mCancelMethod);
						method.invoke(mObject);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		ad.show();
	}
	
	/**
	 * 알림메세지창을 활성화 한후, 확인버튼 클릭시 해당액티비티의 메소드를 호출한다
	 * 
	 * @param atvt - 액티비티
	 * @param msg - 알림메세지
	 * @param mtdnm - 메소드명
	 */
	public static void AlertOkCancel(Activity atvt, String msg, Object okObject, String okMethod, String cancelMethod){
		mParent = atvt;
		mObject = okObject;
		mOkMethod = okMethod;
		mCancelMethod = cancelMethod;

		AlertDialog.Builder ad = new AlertDialog.Builder(atvt);

		ad.setTitle(alertTitle);
		ad.setMessage(msg);
		ad.setNeutralButton(BtnTitle.BTN_OK, new DialogInterface.OnClickListener() {   
			public void onClick(DialogInterface dialog, int whichButton) {
				if(mOkMethod != null) {
					try {
						Class<?> cls = mObject.getClass();
						Method method = cls.getMethod(mOkMethod);
						method.invoke(mObject);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		ad.setNegativeButton(BtnTitle.BTN_CANCEL, new DialogInterface.OnClickListener() {   
			public void onClick(DialogInterface dialog, int whichButton) {
				if(mCancelMethod != null) {
					try {
						Class<?> cls = mObject.getClass();
						Method method = cls.getMethod(mCancelMethod);
						method.invoke(mObject);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		ad.show();
	}
	
	/**
	 * 알림메세지창을 활성화 한후, 확인버튼 클릭시 해당액티비티의 메소드를 호출한다
	 * 
	 * @param atvt - 액티비티
	 * @param msg - 알림메세지
	 * @param mtdnm - 메소드명
	 */
	public static void AlertOkCancelBackFalse(Activity atvt, String msg, Object okObject, String okMethod, String cancelMethod){
		mParent = atvt;
		mObject = okObject;
		mOkMethod = okMethod;
		mCancelMethod = cancelMethod;

		AlertDialog.Builder ad = new AlertDialog.Builder(atvt);

		ad.setTitle(alertTitle);
		ad.setCancelable(false);
		ad.setMessage(msg);
		ad.setNeutralButton(BtnTitle.BTN_OK, new DialogInterface.OnClickListener() {   
			public void onClick(DialogInterface dialog, int whichButton) {
				if(mOkMethod != null) {
					try {
						Class<?> cls = mObject.getClass();
						Method method = cls.getMethod(mOkMethod);
						method.invoke(mObject);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		ad.setNegativeButton(BtnTitle.BTN_CANCEL, new DialogInterface.OnClickListener() {   
			public void onClick(DialogInterface dialog, int whichButton) {
				if(mCancelMethod != null) {
					try {
						Class<?> cls = mObject.getClass();
						Method method = cls.getMethod(mCancelMethod);
						method.invoke(mObject);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		ad.show();
	}

	
	/**
	 * 알림메세지창을 활성화 한후, 확인버튼 클릭시 해당액티비티를 종료한다
	 * 
	 * @param atvt
	 *            - 액티비티
	 * @param msg
	 *            - 알림메세지
	 */
	public static void AlertAfterFinish(Activity atvt, String msg) {
		mParent = atvt;

		AlertDialog.Builder ad = new AlertDialog.Builder(atvt);

		ad.setTitle(alertTitle);
		ad.setMessage(msg);
		ad.setNeutralButton(BtnTitle.BTN_OK,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mParent.finish();
			}
		});
		ad.show();
	}


	public static void Error(Activity atvt, String msg, String... vargs) {
		mParent = atvt;

		for (String var : vargs) {
			msg = msg.replaceFirst(regularExpr, var);
		}

		new AlertDialog.Builder(mParent).setTitle(errorTitle).setMessage(msg)
		.setNeutralButton(BtnTitle.BTN_OK,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int whichButton) {
			}
		}).show();
	}


	// 이체 실거래시 오류일경우 메시지 출력후 이체 첫화면으로 이동한다.
	public static void tranError(Activity atvt, String msg)
	{
//		mParent = atvt;
//		AlertDialog.Builder ad = new AlertDialog.Builder(mParent);
//		ad.setTitle("경고");
//		ad.setMessage(msg);
//		ad.setCancelable(false);
//		ad.setNeutralButton(android.R.string.ok,
//			new DialogInterface.OnClickListener() {   
//				public void onClick(DialogInterface dialog, int whichButton) {
//					Intent intent;
//					intent = new Intent(mParent, Rmt_0101.class);
//					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);		 
//					mParent.startActivity(intent);
//				}
//			})
//		.show();
	}
}
