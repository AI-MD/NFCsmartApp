package com.example.admin_menu.data;

import android.os.Parcel;
import android.os.Parcelable;

public class StudentData implements Parcelable {
	public Double	dStudentNumber;
	public String 	strName;
	public String	strPhoneNumber;
	public String 	strAddress;
	public String	strPictureURL;
	
	public StudentData() {

	}
	
	public StudentData(Double studentNumber, String name, String phoneNumber, String address, String pictureURL)
	{
		dStudentNumber = studentNumber;
		strName = name;
		strPhoneNumber = phoneNumber;
		strAddress = address;
		strPictureURL = pictureURL;
	}
	
	public StudentData(Parcel src)
	{
		dStudentNumber = src.readDouble();
		strName = src.readString();
		strPhoneNumber = src.readString();
		strAddress = src.readString();
		strPictureURL = src.readString();
	}
	
	public static final Parcelable.Creator<StudentData> CREATOR = new Parcelable.Creator<StudentData>() {

		public StudentData createFromParcel(Parcel source) {
			return new StudentData(source);
		}

		public StudentData[] newArray(int size) {
			return new StudentData[size];
		}
		
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(dStudentNumber);
		dest.writeString(strName);
		dest.writeString(strPhoneNumber);
		dest.writeString(strAddress);
		dest.writeString(strPictureURL);
	}
}
