package com.bairuitech.anychat;

//AnyChat�û���Ϣ�¼��ӿ�
public interface AnyChatUserInfoEvent {
	// �û���Ϣ����֪ͨ��wParam��INT����ʾ�û�ID�ţ�lParam��INT����ʾ�������
    public void OnAnyChatUserInfoUpdate(int dwUserId, int dwType);
	// ��������״̬�仯��wParam��INT����ʾ�����û�ID�ţ�lParam��INT����ʾ�û��ĵ�ǰ�״̬��0 ���ߣ� 1 ����
    public void OnAnyChatFriendStatus(int dwUserId, int dwStatus);
}
