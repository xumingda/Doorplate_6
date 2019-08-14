package com.yy.doorplate.model;

public class LeaveModel {

	public String leaveId; // 请假id
	public String leaveType; // 请假类型1事假2病假 13其他
	public String leaveInfo; // 请假事由
	public String leaveRybh; // 请假人人员编号
	public String dealRybh; // 审批人人员编号
	public String leaveStatus; // 请假状态审批0 通过1 拒绝2
	public String startDate; // 请假开始日期 (yyyy-MM-dd)
	public String startTime; // 请假开始时间 (HH:mm:ss)
	public String endDate; // 请假结束日期 (yyyy-MM-dd)
	public String endTime; // 请假结束时间 (HH:mm:ss)
	public String creatTime; // 申请时间 (yyyy-MMdd HH:mm:ss)
	public String updateTime; // 审批时间 (yyyy-MMdd HH:mm:ss)
	public String leaveName; // 请假人姓名
	public String dealName; // 审批人姓名
	public String bjdm; // 请假人班级
	public String bjmc; // 请假人班级名称
	public String leaveTimeLength; // 请假时长

	@Override
	public String toString() {
		return "LeaveModel [leaveId=" + leaveId + ", leaveType=" + leaveType
				+ ", leaveInfo=" + leaveInfo + ", leaveRybh=" + leaveRybh
				+ ", dealRybh=" + dealRybh + ", leaveStatus=" + leaveStatus
				+ ", startDate=" + startDate + ", startTime=" + startTime
				+ ", endDate=" + endDate + ", endTime=" + endTime
				+ ", creatTime=" + creatTime + ", updateTime=" + updateTime
				+ ", leaveName=" + leaveName + ", dealName=" + dealName
				+ ", bjdm=" + bjdm + ", bjmc=" + bjmc + ", leaveTimeLength="
				+ leaveTimeLength + "]";
	}
}
