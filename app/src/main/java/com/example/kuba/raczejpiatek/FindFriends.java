package com.example.kuba.raczejpiatek;

public class FindFriends {
    private String profilURl, first_name, id;
    private boolean is_sharing;


    public FindFriends(String profilURl, String first_name) {
        this.profilURl = profilURl;
        this.first_name = first_name;
    }

    public FindFriends(String profilURl, String first_name,String id) {
        this.profilURl = profilURl;
        this.first_name = first_name;
        this.id = id;
    }

    public FindFriends(String profilURl, String first_name, String id, boolean is_sharing) {
        this.profilURl = profilURl;
        this.first_name = first_name;
        this.id = id;
        this.is_sharing = is_sharing;
    }

    public FindFriends(){

    }

    public String getProfilURl() {
        return profilURl;
    }

    public void setProfilURl(String profilURl) {
        this.profilURl = profilURl;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isIs_sharing() {
        return is_sharing;
    }

    public void setIs_sharing(boolean is_sharing) {
        this.is_sharing = is_sharing;
    }
}
