package com.example.smart_attbook.utils;

public class Defines {
	public static final String 				SERVER_ADDRESS 								= "http://61.84.23.30/Smart_AttBook/";
	public static final String				SERVER_IMAGE_PATH							= SERVER_ADDRESS+"images/";
	
	public static final String				STUDENT_SEARCH_PHP							= "search_student.php";
	public static final String				STUDENT_INSERT_PHP							= "insert_student.php";
	public static final String				STUDENT_DELETE_PHP							= "delete_student.php";
	public static final String				STUDENT_UPDATE_PHP							= "update_student.php";
	
	public static final String				L_STUDENT_SEARCH_PHP							= "search_l_student.php";
	
	public static final String				PROFESSOR_SEARCH_PHP							= "search_professor.php";
	public static final String				PROFESSOR_INSERT_PHP							= "insert_professor.php";
	public static final String				PROFESSOR_DELETE_PHP							= "delete_professor.php";
	public static final String				PROFESSOR_UPDATE_PHP							= "update_professor.php";
	
	public static final String				LECTURE_SEARCH_PHP							= "search_a_lecture.php";
	public static final String				LECTURE_INSERT_PHP							= "insert_lecture.php";
	public static final String				LECTURE_DELETE_PHP							= "delete_lecture.php";
	public static final String				LECTURE_UPDATE_PHP							= "update_lecture.php";
	
	public static final String				ATTENDANCE_SEARCH_PHP						= "search_attendance.php";
	public static final String				ATTENDANCE_INSERT_PHP						= "insert_attendance.php";
	public static final String				ATTENDANCE_DELETE_PHP						= "delete_attendance.php";
	public static final String				ATTENDANCE_UPDATE_PHP						= "update_attendance.php";
	

	public static final String				IMAGE_UPLOAD_PHP							= "api.upload_image.php";
	
	public static final String				STUDENT_DATA_XML							= "student.xml";
	public static final String				STUDENT_DATA_INSERT_RESULT_XML				= "student_insert_result.xml";
	public static final String				STUDENT_DATA_DELETE_RESULT_XML				= "student_delete_result.xml";
	public static final String				STUDENT_DATA_UPDATE_RESULT_XML				= "student_update_result.xml";
	

	public static final String				L_STUDENT_DATA_XML							= "l_student.xml";

	
	public static final String				PROFESSOR_DATA_XML							= "professor.xml";
	public static final String				PROFESSOR_DATA_INSERT_RESULT_XML			= "professor_insert_result.xml";
	public static final String				PROFESSOR_DATA_DELETE_RESULT_XML			= "professor_delete_result.xml";
	public static final String				PROFESSOR_DATA_UPDATE_RESULT_XML			= "professor_update_result.xml";

	public static final String				LECTURE_DATA_XML							= "a_lecture.xml";
	public static final String				LECTURE_DATA_INSERT_RESULT_XML				= "lecture_insert_result.xml";
	public static final String				LECTURE_DATA_DELETE_RESULT_XML				= "lecture_delete_result.xml";
	public static final String				LECTURE_DATA_UPDATE_RESULT_XML				= "lecture_update_result.xml";

	public static final String				ATTENDANCE_DATA_XML							= "attendance.xml";
	public static final String				ATTENDANCE_DATA_INSERT_RESULT_XML			= "attendance_insert_result.xml";
	public static final String				ATTENDANCE_DATA_DELETE_RESULT_XML			= "attendance_delete_result.xml";
	public static final String				ATTENDANCE_DATA_UPDATE_RESULT_XML			= "attendance_update_result.xml";
	public static final int					NETWORK_TIMEOUT								= 30000;		// Timeout 3초 설정 (ms단위)
	
    /*
     * Handle Message Enum
     */
	public static final int					HND_MSG_EXIT								= 0x00010000;
	public static final int					HND_MSG_GET_STUDENT_DATA					= 0x00010001;
	public static final int					HND_MSG_INSERT_STUDENT_DATA					= 0x00010002;
	public static final int					HND_MSG_DELETE_STUDENT_DATA					= 0x00010002;
	public static final int					HND_MSG_UPDATE_STUDENT_DATA					= 0x00010003;
	
	public static final int					HND_MSG_GET_PROFESSOR_DATA					= 0x00010004;
	public static final int					HND_MSG_INSERT_PROFESSOR_DATA				= 0x00010005;
	public static final int					HND_MSG_DELETE_PROFESSOR_DATA				= 0x00010006;
	public static final int					HND_MSG_UPDATE_PROFESSOR_DATA				= 0x00010007;
	
	
	public static final int					HND_MSG_GET_LECTURE_DATA				= 0x00010008;
	public static final int					HND_MSG_INSERT_LECTURE_DATA				= 0x00010009;
	public static final int					HND_MSG_DELETE_LECTURE_DATA				= 0x00010010;
	public static final int					HND_MSG_UPDATE_LECTURE_DATA				= 0x00010011;
	
	
	public static final int					HND_MSG_GET_ATTENDANCE_DATA				= 0x000100012;
	
	public static final int					HND_MSG_GET_L_STUDENT_DATA				= 0x00010013;
	
	public static final int					HND_MSG_SET_STUDENT_PICTURE					= 0x00020001;
	public static final int					HND_MSG_IMAGE_UPLOAD						= 0x00030001;
	public static final int					HND_MSG_NETWORK_TIMEOUT						= 0x000F0001;
	public static final int					HND_MSG_SET_PROFESSOR_PICTURE				= 0x00050001;
	
	
	public static final int					DIALOG_NETWORK_TIMEOUT_ALRET				= 0;
	public static final int					DIALOG_TABLE_INSERT_ALRET					= 1;
	public static final int					DIALOG_INPUT_ALERT							= 2;
	public static final int					DIALOG_IMAGE_UPLOAD_ALRET					= 3;
	public static final int					DIALOG_TABLE_UPDATE_ALRET					= 7;
	public static final int					DIALOG_TABLE_DELETE_ALRET					= 8;
	
	public static final int					REQUEST_CODE_EDIT_STUDENT					= 1;
	public static final int					REQUEST_CODE_GALLERY						= 2;
	public static final int					REQUEST_CODE_EDIT_PROFESSOR					= 3;
	public static final int					REQUEST_CODE_EDIT_LECTURE				= 4;
	// INTENT EXTRA DATA
	public static final String				INTENT_STUDENT_EDIT_MODE					= "StudentEditMode";
	public static final String				INTENT_STUDENT_DATA							= "StudentData";

	public static final String				INTENT_PROFESSOR_EDIT_MODE					= "ProfessorEditMode";
	public static final String				INTENT_PROFESSOR_DATA						="ProfessorData";
	
	public static final String				INTENT_LECTURE_EDIT_MODE					= "LectureEditMode";
	public static final String				INTENT_LECTURE_DATA						    ="LectureData";
    /*
     * 입력 화면 상태를 나타내는 Enum
     */
	public static final int					MODE_NEW									= 0;
	public static final int					MODE_UPDATE									= 1;
	public static final int					MODE_DELETE									= 2;
	
	public static final int					IMAGE_MAX_HEIGHT							= 512;
	
	
}
