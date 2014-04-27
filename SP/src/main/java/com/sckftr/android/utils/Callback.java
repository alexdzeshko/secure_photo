package com.sckftr.android.utils;

/**
 * The concept of common error-aware callback.
 * To be used in various async flows.
 * 
 * @author Aliaksandr_Litskevic
 *
 */
public interface Callback<Result, Err> {
	
	/**
	 * Empty stub. May used if none to call back.
	 *  
	 */
	Callback<Void, Error> EMPTY = new Callback<Void, Error>() {
		
		@Override
		public void onSuccess(Void... results) {
			// NOOP
			
		}
		
		@Override
		public void onError(Error error) {
			// NOOP
			
		}
	};
	
	/**
	 * Handles with async results if success.
	 * 
	 * @param results a set of results of any nature returned by async 
	 */
	void onSuccess(Result... results);
	
	/**
	 * Handles in case of error. 
	 * 
	 * @param error error object
	 */
	void onError(Err error);
}
