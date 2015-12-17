package com.example.admin_menu.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ProfessorData implements Parcelable {
	public Double	dProfessorId;
	public String 	strName;
	public String	strPhoneNumber;
	public String 	strAddress;
	public String	strPictureURL;
	
	public ProfessorData() {

	}
	
	public ProfessorData(Double ProfessorId, String name, String phoneNumber, String address, String pictureURL)
	{
		dProfessorId = ProfessorId;
		strName = name;
		strPhoneNumber = phoneNumber;
		strAddress = address;
		strPictureURL = pictureURL;
	}
	
	public ProfessorData(Parcel src)
	{
		dProfessorId = src.readDouble();
		strName = src.readString();
		strPhoneNumber = src.readString();
		strAddress = src.readString();
		strPictureURL = src.readString();
	}
	
	public static final Parcelable.Creator<ProfessorData> CREATOR = new Parcelable.Creator<ProfessorData>() {

		public ProfessorData createFromParcel(Parcel source) {
			return new ProfessorData(source);
		}

		public ProfessorData[] newArray(int size) {
			return new ProfessorData[size];
		}
		
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(dProfessorId);
		dest.writeString(strName);
		dest.writeString(strPhoneNumber);
		dest.writeString(strAddress);
		dest.writeString(strPictureURL);
	}
}
