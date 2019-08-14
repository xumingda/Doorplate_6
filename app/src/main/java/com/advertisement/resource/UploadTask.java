package com.advertisement.resource;

import android.os.Handler;

public class UploadTask extends ResTask{

	public UploadTask(ResManager manager, int taskId, int type, String url) {
		super(manager, taskId, type, url);
		this.dir=ResTask.TASK_UPLOAD;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Handler getHandler() {
		// TODO Auto-generated method stub
		return null;
	}

}
