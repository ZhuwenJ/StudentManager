package model;

import java.util.ArrayList;
import java.util.Date;

public class User {
    public static User currentUser=null;
    private String uid;
    private String username;
    private String branch;
    private String classId;
    private String sex;
    private String phonenumber;
    private String email;
    private String rank;
    private String departments;
    private Date leavetime;
    private Date appointmentTime;
    private String extr;
    private String depId;
    public static ArrayList<User> users;

    public void setUid(String uid){
        this.uid=uid;
    }
    public  String getUid(){
        return uid;
    }

    public void setUsername(String username){
        this.username=username;
    }
    public  String getUsername(){
        return username;
    }

    public void setBranch(String branch){
        this.branch=branch;
    }
    public  String getBranch(){
        return branch;
    }

    public void setClassId(String classId){
        this.classId=classId;
    }
    public  String getClassId(){
        return classId;
    }

    public void setSex(String sex){
        this.sex=sex;
    }
    public  String getSex(){
        return sex;
    }

    public void setPhonenumber(String phonenumber){
        this.phonenumber=phonenumber;
    }
    public  String getPhonenumber(){
        return phonenumber;
    }

    public void setEmail(String email){
        this.email=email;
    }
    public  String getEmail(){
        return email;
    }

    public void setRank(String rank){
        this.rank=rank;
    }
    public  String getRank(){
        return rank;
    }

    public void setDepartments(String departments){
        this.departments=departments;
    }
    public  String getDepartments(){
        return departments;
    }

    public void setLeavetime(Date leavetime){
        this.leavetime=leavetime;
    }
    public  Date getLeavetime(){
        return leavetime;
    }

    public void setAppointmentTime(Date appointmentTime){
        this.appointmentTime=appointmentTime;
    }
    public  Date getAppointmentTime(){
        return appointmentTime;
    }

    public void setExtr(String extr){
        this.extr=extr;
    }
    public  String getExtr(){
        return extr;
    }

    public String getDepId() {
        return depId;
    }
    public void setDepId(String depId) {
        this.depId = depId;
    }
}
