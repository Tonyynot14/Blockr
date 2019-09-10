package com.android.internal.telephony;

//Internal class that has to be called to end calls,
//Android made it so only Apps preinstalled on systems could end calls
public interface ITelephony {
    boolean endCall();
    void answerRingingCall();
    void silenceRinger();
}