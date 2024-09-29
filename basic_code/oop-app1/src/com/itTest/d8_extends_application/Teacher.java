package com.itTest.d8_extends_application;

public class Teacher extends People {
    private String Skill;

    public String getSkill() {
        return Skill;
    }

    public void setSkill(String skill) {
        Skill = skill;
    }

    public void PrintInfo(){
        System.out.println(getName() + "具备的技能" + Skill);
    }
}
