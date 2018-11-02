package com.example.myapplication4;;

public class Teacher {
    private String number;
    private String name;

    public Teacher() {
    }
    public Teacher(String number,String name){
        this.number=number;
        this.name =name;
    }
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "Teacher{" +
                "number='" + number + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
