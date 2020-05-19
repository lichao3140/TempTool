package com.common.pos.api.util;

public class PosUtil {

	public synchronized static native int setFlushLedPower(int powerStatus);

	public synchronized static native int setLedPower(int powerStatus);
	
	public synchronized static native int setLedPower400b(int powerStatus,int selectNum);

	public synchronized static native int setJiaJiPower(int powerStatus);

	public synchronized static native int getPriximitySensorStatus();
	
	public synchronized static native int getPriximitySensorStatus400b(int selectNum);

	public synchronized static native int setRelayPower(int powerStatus);

	public static synchronized int setRs485Status(int powerStatus) {
		return setRs485StatusJNI(powerStatus, 100000);
	}

	public static synchronized int setRs485Status(int powerStatus, int delay) {
		return setRs485StatusJNI(powerStatus, delay * 1000);
	}

	private static synchronized native int setRs485StatusJNI(int var0, int var1);
	
	public synchronized static native int getWg26Status(long powerStatus);
	
	public synchronized static native int getWg34Status(long powerStatus);
	
	public synchronized static native int setEMC(int arg);
	
	public synchronized static native int setColorLed(int status, int led);

	static {
		System.loadLibrary("posutil");
	}

}
