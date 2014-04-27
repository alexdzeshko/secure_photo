package com.sckftr.android.utils;

import java.util.Arrays;

/**
 * Created by Andrei_Stupiak on 4/9/2014 based on com.google.i18n.phonenumbers.AsYouTypeFormatter
 */
public class RangeAsYouTypeFormatter {
    private int mCorrectedPosition;
    private StringBuilder mResultStringBuilder = new StringBuilder();
    private char mSeparator;
    private int[] mRangeBoundaries;

    public RangeAsYouTypeFormatter(int[] ranges, char separator){
        this.mRangeBoundaries = Arrays.copyOf(ranges, ranges.length);
        this.mSeparator = separator;
    }

    public String reformat(CharSequence s, int cursorPosition){
        mCorrectedPosition = cursorPosition;
        int[] ranges = getRanges();
        if(ranges==null || ranges.length==0){
            return s.toString();
        }

        final int originalLength = s.length();
        final int lastRangeIndex = ranges.length - 1;

        int currentRangeIndex = 0;
        int separatorFreePosition = 0;
        int currentRangeRightBound = ranges[currentRangeIndex];
        char currentChar;

        for(int i = 0; i < originalLength; i++ ){
            currentChar = s.charAt(i);

            //adding only none separators to keep formatting valid
            if(currentChar != mSeparator){
                mResultStringBuilder.append(currentChar);
                separatorFreePosition++;
            }else{
                //shift cursor back as we skip character
                if(mCorrectedPosition>=i){
                    mCorrectedPosition--;
                }
            }

            //end of next range add separator and shift right bound to the right
            if(separatorFreePosition==currentRangeRightBound){
                //if it is last range skip rest of the sequence
                if(currentRangeIndex==lastRangeIndex) {
                    break;
                }

                mResultStringBuilder.append(mSeparator);
                //shift cursor as we append chars before it
                if (i < mCorrectedPosition) {
                    mCorrectedPosition++;
                }


                //next range length
                currentRangeRightBound += ranges[++currentRangeIndex];
            }

        }

        mCorrectedPosition = Math.min(mCorrectedPosition, mResultStringBuilder.length());
        return mResultStringBuilder.toString();
    }

    public int getCorrectedPosition(){
        return mCorrectedPosition;
    }

    public void clear(){
        mCorrectedPosition=0;
        mResultStringBuilder.setLength(0);
    }

    protected int[] getRanges(){
        return mRangeBoundaries;
    }
}
