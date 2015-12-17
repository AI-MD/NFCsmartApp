package com.example.admin_menu.data;

import android.os.Parcel;
import android.os.Parcelable;

public class LectureData implements Parcelable {
	
	public Double	dLectureId;
	public String 	strName;
	public Double	dProfessorId;
	public String	strWeek;
	public String 	strStrat_Time;
	public String	strEnd_Time;
	public String	strPlace;
	
	public LectureData() {

	}
	
	public LectureData(Double lectureId, String name, Double professorId, String week,
				String strat_time, String end_time, String place)
	{
		dLectureId = lectureId;
		strName = name;
		dProfessorId = professorId;
		strWeek = week;
		strStrat_Time =strat_time;
		strEnd_Time = end_time;
		strPlace= place;
	}
	
	public LectureData(Parcel src)
	{
		dLectureId= src.readDouble();
		strName = src.readString();
		dProfessorId = src.readDouble();
		strWeek=src.readString();
		strStrat_Time=src.readString();
		strEnd_Time=src.readString();
		strPlace= src.readString();
	}
	
	public static final Parcelable.Creator<LectureData> CREATOR = new Parcelable.Creator<LectureData>() {

		public LectureData createFromParcel(Parcel source) {
			return new LectureData(source);
		}

		public LectureData[] newArray(int size) {
			return new LectureData[size];
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
		dest.writeString(strName);
		dest.writeDouble(dProfessorId);
		dest.writeString(strWeek);
		dest.writeString(strStrat_Time);
		dest.writeString(strEnd_Time);
		dest.writeString(strPlace);
	}
}
