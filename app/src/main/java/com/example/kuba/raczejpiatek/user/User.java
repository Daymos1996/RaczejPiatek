package com.example.kuba.raczejpiatek.user;

import com.example.kuba.raczejpiatek.ProfilActivity;

public class User {
    private String username;
    private String email;
    private String password;
    private String phone;
    private String gender;
    private String profilURl;
    private String first_name;
    private String last_name;
    private String lat;
    private String lng;

    public User(){

    }


    public User(String username, String email, String password, String phone) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public User(String email, String gender, String profilURl, String first_name, String last_name) {
        this.email = email;
        this.gender = gender;
        this.profilURl = profilURl;
        this.first_name = first_name;
        this.last_name = last_name;
    }
    public User(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
