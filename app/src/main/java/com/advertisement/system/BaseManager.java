package com.advertisement.system;


import android.os.Handler;

public abstract class BaseManager {

	public SystemManager mSystemManager=null;
	public BaseManager(SystemManager systemManager) {
		// TODO Auto-generated constructor stub
		mSystemManager=systemManager;
	}

	public abstract void start();
	public abstract void stop();
	public abstract int getState();
	public abstract Handler getHandler();
}
