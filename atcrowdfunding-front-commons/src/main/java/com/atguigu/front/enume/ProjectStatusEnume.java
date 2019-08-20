package com.atguigu.front.enume;

/**
 * 项目状态
 * @author lfy
 *
 */
public enum ProjectStatusEnume {
	
	DRAFT("0","草稿"),
	SUBMIT_AUTH("1","提交审核申请"),
	AUTHING("2","后台正在审核"),
	AUTHED("3","后台审核通过"),
	AUTHFAIL("4","审核失败");
	
	private String code;
	private String status;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	private ProjectStatusEnume(String code, String status) {
		this.code = code;
		this.status = status;
	}
	

}
