package com.lkf.mybatis.demo.model;
/**
 * @author kaifeng
 */
@SuppressWarnings("unused")
public class Department {
    private int deptNo;
    private String deptName;
    private String deptLocation;

    public int getDeptNo() {
        return deptNo;
    }

    public void setDeptNo(int deptNo) {
        this.deptNo = deptNo;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptLocation() {
        return deptLocation;
    }

    public void setDeptLocation(String deptLocation) {
        this.deptLocation = deptLocation;
    }

    @Override
    public String toString() {
        return "Department{" +
                "deptNo=" + deptNo +
                ", deptName='" + deptName + '\'' +
                ", deptLocation='" + deptLocation + '\'' +
                '}';
    }
}
