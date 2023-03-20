package com.cpis498.rushd.models;

import java.util.List;
import java.util.Map;

public class Campaign {

    String name;
    String organizer_id;


    public Campaign() {
    }
    public Campaign(Map<String,Object> map) {
        this.name= (String) map.get("name");
        this.organizer_id= (String) map.get("organizer_id");

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizer_id() {
        return organizer_id;
    }

    public void setOrganizer_id(String organizer_id) {
        this.organizer_id = organizer_id;
    }



}
