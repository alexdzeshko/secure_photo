package com.sckftr.android.securephoto.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public final class ParcelUtils {

    private final Parcel in;
    final ClassLoader classLoader = ParcelUtils.class.getClassLoader();

    public ParcelUtils(Parcel in) {

        this.in = in;
    }
    
    public String readString() {

        return in.readString();
    }


	public Integer readInteger() {
		
		return in.readInt();
	}
	
	public byte readByte() {
		return in.readByte();
	}

	public Float readFloat() {
		
		return in.readFloat();
	}

    public boolean readBoolean() {
        return in.readByte()==1;
    }

    public ParcelUtils write(String value) {

        in.writeString(value);

        return this;
    }

    public ParcelUtils write(Integer value) {

        in.writeInt(value);

        return this;
    }

    public ParcelUtils write(Byte value) {
        in.writeByte(value);

        return this;
    }

    public ParcelUtils write(Float value) {

        in.writeFloat(value);

        return this;
    }

    public ParcelUtils write(boolean value) {
        in.writeByte((byte)(value?1:0));

        return this;
    }
    public ParcelUtils write(Date value) {
        writeDate(in, value);

        return this;
    }

    public static Date readDate(Parcel in) {
        Long ms = in.readLong();
        return ms==null || ms==0? null : new Date(ms);
    }

    public static void writeDate(Parcel out, Date date) {
        out.writeLong(date == null ? 0 : date.getTime());
    }

    public Date readDate() {
        return readDate(in);
    }

    public <T extends Parcelable> T readParcelable() {
        return in.readParcelable(classLoader);
    }

    public void write(Parcelable parcelable, int i) {
        in.writeParcelable(parcelable, i);
    }
}
