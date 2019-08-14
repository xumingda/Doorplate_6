package com.common;

public class Mutex {
	private boolean syncLock;
	public Mutex() {
		// TODO Auto-generated constructor stub
		syncLock=false;
	}
	/**
	 * @param timeout ��ʱʱ�䣬�Ժ���Ϊ��λ,������0ʱ����һֱ����ȥ
	 * @return true->��ȡ����  false->δ��ȡ����
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
