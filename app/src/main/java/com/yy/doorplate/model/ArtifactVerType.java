package com.yy.doorplate.model;

public class ArtifactVerType {

	// 系统组件
	public String artifactVerId; // 组件版本ID
	public String sysCode; // 组件编码
	public String oldArtifactCode; // 上一版本版本号
	public String artifactVerCode; // 最新版本号
	public String artifactVerInfo; // 包名
	public String artifaceDownloadPath; // 路径
	public String md5Check; // MD5校验
	public String artifactType; // 类型 01：补丁 02：完整包
	public String fileName; // 文件名

	@Override
	public String toString() {
		return "ArtifactVerType [artifactVerId=" + artifactVerId + ", sysCode="
				+ sysCode + ", oldArtifactCode=" + oldArtifactCode
				+ ", artifactVerCode=" + artifactVerCode + ", artifactVerInfo="
				+ artifactVerInfo + ", artifaceDownloadPath="
				+ artifaceDownloadPath + ", md5Check=" + md5Check
				+ ", artifactType=" + artifactType + ", fileName=" + fileName
				+ "]";
	}

}
