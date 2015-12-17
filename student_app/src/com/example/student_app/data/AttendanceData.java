package com.example.student_app.data;

import android.os.Parcel;
import android.os.Parcelable;

public class AttendanceData implements Parcelable {
	
	public Double	dLectureId;
	
	public Double	dStudentNumber;
	public String	strWeek;
	public String 	strAttendance_Day;
	public String	strAttendance_Time;
	
	
	public AttendanceData() {

	}
	
	public AttendanceData(Double lectureId, Double studentNumber, String week,
				String attentance_day, String attendance_time)
	{
		dLectureId = lectureId;
		
		dStudentNumber = studentNumber;
		strWeek = week;
		strAttendance_Day =attentance_day;
		strAttendance_Time = attendance_time;
		
	}
	
	public AttendanceData(Parcel src)
	{
		dLectureId= src.readDouble();
		
		dStudentNumber = src.readDouble();
		strWeek=src.readString();
		strAttendance_Day=src.readString();
		strAttendance_Time=src.readString();
		
	}
	
	public static final Parcelable.Creator<AttendanceData> CREATOR = new Parcelable.Creator<AttendanceData>() {

		public AttendanceData createFromParcel(Parcel source) {
			return new AttendanceData(source);
		}

		public AttendanceData[] newArray(int size) {
			return new AttendanceData[size];
		}
		
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(dLectureId);
		dest.writeDouble(dStudentNumber);
		dest.writeString(strWeek);
		dest.writeString(strAttendance_Day);
		dest.writeString(strAttendance_Time);
		
	}
}
