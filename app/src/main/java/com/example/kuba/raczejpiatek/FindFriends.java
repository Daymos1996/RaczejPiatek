package com.example.kuba.raczejpiatek;

public class FindFriends {
    public String profilURl, first_name, last_name=" ";

    public FindFriends(String profilURl, String first_name, String last_name) {
        this.last_name = last_name;
        this.profilURl = profilURl;
        this.first_name = first_name;
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
        return first_name + " " + last_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }
}
