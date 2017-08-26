package com.fanny.bluetoothbloodpresuretest.bean;

/**
 * Created by Fanny on 17/4/5.
 */

public class User {
    private String name;
    private String sex;
    private int age;
    private int height;
    private int weight;

    public User() {
        super();
    }

    public User(String name,String sex,int age,int height,int weight) {
        super();
        this.name=name;
        this.sex=sex;
        this.age=age;
        this.height=height;
        this.weight=weight;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public int getAge() {
        return age;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
