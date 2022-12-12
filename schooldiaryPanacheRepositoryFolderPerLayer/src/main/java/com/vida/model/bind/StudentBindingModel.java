package com.vida.model.bind;

public class StudentBindingModel {
    private String firstName;
    private String lastName;
    private String EGN;
    private KlasBindingModel klas;

    public StudentBindingModel() {
    }

    public String getFirstName() {
        return firstName;
    }

    public StudentBindingModel setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public StudentBindingModel setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEGN() {
        return EGN;
    }

    public StudentBindingModel setEGN(String EGN) {
        this.EGN = EGN;
        return this;
    }

    public KlasBindingModel getKlas() {
        return klas;
    }

    public StudentBindingModel setKlas(KlasBindingModel klas) {
        this.klas = klas;
        return this;
    }
}
