package com.qiao.picturedepot.pojo;

import java.math.BigInteger;
import java.util.Date;

public class Role {
    private BigInteger id;
    private String rolename;
    private int privilegeLevel;
    private Date createTime;
    private Date updateTime;

    public Role() {
    }
    public Role(BigInteger id, String name, int privilegeLevel, Date creatTime, Date updateTime) {
        this.id = id;
        this.rolename = name;
        this.privilegeLevel = privilegeLevel;
        this.createTime = creatTime;
        this.updateTime = updateTime;
    }
    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public int getPrivilegeLevel() {
        return privilegeLevel;
    }

    public void setPrivilegeLevel(int privilegeLevel) {
        this.privilegeLevel = privilegeLevel;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", rolename='" + rolename + '\'' +
                ", privilegeLevel=" + privilegeLevel +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
