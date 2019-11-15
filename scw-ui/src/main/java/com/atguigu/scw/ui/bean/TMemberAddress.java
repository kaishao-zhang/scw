package com.atguigu.scw.ui.bean;

import java.io.Serializable;

public class TMemberAddress implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1068473068541531168L;

	private Integer id;

    private Integer memberid;

    private String address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMemberid() {
        return memberid;
    }

    public void setMemberid(Integer memberid) {
        this.memberid = memberid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }
}