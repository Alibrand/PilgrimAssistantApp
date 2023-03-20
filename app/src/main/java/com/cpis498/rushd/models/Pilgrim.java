package com.cpis498.rushd.models;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pilgrim {
    //related user account id
    private String uid;
    //national id
    private String nid;
    private String name;
    private String email;
    private String password;
    private String account_type;
    private String phone;
    private String campaign="";
    private String current_phase="";
    private String current_lap="0";
    private String current_day="0";
    private  String doc_id="";
    private List<Boolean> days_check=Arrays.asList(false,false,false,false,false,false);
    private GeoPoint location=new GeoPoint(0.0,0.0);

   // constructor to create object from firestore data directly
    public Pilgrim(Map<String,Object> map){
        this.uid= (String) map.get("uid");
        this.nid= (String) map.get("nid");
        this.name=(String) map.get("name");
        this.account_type=(String) map.get("account_type");
        this.campaign=(String) map.get("campaign");
        this.location=(GeoPoint) map.get("location");
        this.phone=(String) map.get("phone");
        if(map.containsKey("current_phase"))
        this.current_phase=(String) map.get("current_phase");
        if(map.containsKey("current_lap"))
            this.current_lap=(String) map.get("current_lap");
        if(map.containsKey("days_check"))
            this.days_check= (List<Boolean>) map.get("days_check");
        if(map.containsKey("current_day"))
            this.current_day= (String) map.get("current_day");

    }

 //function to convert object to firestore data map
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", this.uid);
        map.put("nid", this.nid);
        map.put("name", this.name);
        map.put("account_type", this.account_type);
        map.put("campaign", this.campaign);
        map.put("location", this.location);
        map.put("phone", this.phone);
        map.put("current_phase", this.current_phase);
        map.put("current_day", this.current_day);
        map.put("current_lap", this.current_lap);
        map.put("days_check", this.days_check);
        return map;
    }

    public String getCurrent_day() {
        return current_day;
    }

    public void setCurrent_day(String current_day) {
        this.current_day = current_day;
    }

    public List<Boolean> getDays_check() {
        return days_check;
    }

    public void setDays_check(List<Boolean> days_check) {
        this.days_check = days_check;
    }

    public Pilgrim(){

    }

    public String getCurrent_lap() {
        return current_lap;
    }

    public void setCurrent_lap(String current_lap) {
        this.current_lap = current_lap;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getCurrent_phase() {
        return current_phase;
    }

    public void setCurrent_phase(String current_phase) {
        this.current_phase = current_phase;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
