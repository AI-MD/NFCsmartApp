package com.example.student_app.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Taking_LectureData implements Parcelable {
	
	public String	dLectureId;
	
	public String	dStudentNumber;
	
	
	
	public Taking_LectureData() {

	}
	
	public Taking_LectureData(String lectureId, String studentNumber)
	{
		dLectureId = lectureId;
		
		dStudentNumber = studentNumber;
		
		
	}
	
	public Taking_LectureData(Parcel src)
	{
		dLectureId= src.readString();
		
		dStudentNumber = src.readString();
		
		
	}
	
	public static final Parcelable.Creator<Taking_LectureData> CREATOR = new Parcelable.Creator<Taking_LectureData>() {

		public Taking_LectureData createFromParcel(Parcel source) {
			return new Taking_LectureData(source);
		}

		public Taking_LectureData[] newArray(int size) {
			return new Taking_LectureData[size];
		}
		
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(dLectureId);
		
		dest.writeString(dStudentNumber);
		
		
	}
}
