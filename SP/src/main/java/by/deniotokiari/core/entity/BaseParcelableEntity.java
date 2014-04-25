package by.deniotokiari.core.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @param <T> Simple class to parcelable who extends this class 
 * 
 **/

public abstract class BaseParcelableEntity<T> implements Parcelable {

	protected abstract Object[] getFields();

	protected abstract T setFields(String[] items);

	protected T create(Parcel parcel) {
		String[] items = null;
		parcel.readStringArray(items);
		return setFields(items);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeArray(getFields());
	}

	protected final Parcelable.Creator<T> CREATOR = new Creator<T>() {

		@Override
		public T createFromParcel(Parcel source) {
			return create(source);
		}

		@SuppressWarnings("unchecked")
		@Override
		public T[] newArray(int size) {
			return (T[]) new Object[size];
		}

	};

}
