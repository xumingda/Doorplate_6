package com.common;

public class Mutex {
	private boolean syncLock;
	public Mutex() {
		// TODO Auto-generated constructor stub
		syncLock=false;
	}
	/**
	 * @param timeout 超时时间，以毫秒为单位,当传入0时，将一直等下去
	 * @return true->获取到锁  false->未获取到锁
	 **/
	public synchronized boolean lock(int timeout){
		while(syncLock==true){
			try{
				if(timeout==0){
					wait();
				}else{
					wait(timeout);
				}
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
		syncLock=true;
		
		return syncLock;
	}
	

	
	/**
	 * unlock
	 **/
	public synchronized void unlock(){
		syncLock=false;
		notifyAll();
	}
	
	public synchronized boolean islocked(){
		return syncLock;
	}
}
