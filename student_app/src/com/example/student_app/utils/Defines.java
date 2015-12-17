package com.example.student_app.utils;

public class Defines {
	public static final String 				SERVER_ADDRESS 								= "http://61.84.23.30/Smart_AttBook/";
	public static final String				SERVER_IMAGE_PATH							= SERVER_ADDRESS+"images/";
	
	
	
	public static final String				ATTENDANCE_INSERT_PHP						= "insert_attendance.php";
	public static final String				TAKING_LECTURE_SEARCH_PHP					= "search_taking_lectures.php";
	
	public static final String				ATTENDANCE_DATA_INSERT_RESULT_XML			= "attendance_insert_result.xml";
	

	
	
	

	
	public static final String				TAKING_LECTURE_DATA_XML							= "taking_lectures.xml";
	

	public static final String				ATTENDANCE_DATA_XML							= "attendance.xml";
	public static final int					NETWORK_TIMEOUT								= 3000;		// Timeout 3초 설정 (ms단위)
	
    /*
     * Handle Message Enum
     */
	public static final int					HND_MSG_EXIT								= 0x00010000;

	public static final int					HND_MSG_INSERT_ATTENDANCE_DATA				= 0x000100015;
	
	public static final int					HND_MSG_GET_LECTURE_DATA				= 0x00010008;
	public static final int					HND_MSG_INSERT_LECTURE_DATA				= 0x00010009;
	public static final int					HND_MSG_DELETE_LECTURE_DATA				= 0x00010010;
	public static final int					HND_MSG_UPDATE_LECTURE_DATA				= 0x00010011;
	public static final int					HND_MSG_GET_ATTENDANCE_DATA				= 0x000100012;
	public static final int					HND_MSG_GET_TAKING_LECTURE_DATA				= 0x000100013;
	public static final int					HND_MSG_NETWORK_TIMEOUT						= 0x000F0001;
	
	public static final int					DIALOG_NETWORK_TIMEOUT_ALRET				= 0;
	public static final int					DIALOG_TABLE_INSERT_ALRET					= 1;
	public static final int					DIALOG_INPUT_ALERT							= 2;
	public static final int					DIALOG_IMAGE_UPLOAD_ALRET					= 3;
	public static final int					DIALOG_TABLE_UPDATE_ALRET					= 7;
	public static final int					DIALOG_TABLE_DELETE_ALRET					= 8;
	
	
	
	// INTENT EXTRA DATA

	
	
    /*
     * 입력 화면 상태를 나타내는 Enum
     */
	public static final int					MODE_NEW									= 0;
	public static final int					MODE_UPDATE									= 1;
	public static final int					MODE_DELETE									= 2;
	
	public static final int					IMAGE_MAX_HEIGHT							= 512;
	
	
}
