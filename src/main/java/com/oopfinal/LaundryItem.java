package com.oopfinal;

import java.util.ArrayList;
import java.util.List;

public class LaundryItem {
    private String id;
    private String studentId;
    private String studentName;
    private double clothWeight;
    private int clothCount;
    private boolean pickedUp;
    private List<Cloth> clothTypes;

    public LaundryItem(String id, String studentId, String studentName, double clothWeight, int clothCount) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.clothWeight = clothWeight;
        this.clothCount = clothCount;
        this.pickedUp = false;
        this.clothTypes = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public double getClothWeight() {
        return clothWeight;
    }

    public void setClothWeight(double clothWeight) {
        this.clothWeight = clothWeight;
    }

    public int getClothCount() {
        return clothCount;
    }

    public void setClothCount(int clothCount) {
        this.clothCount = clothCount;
    }

    public boolean isPickedUp() {
        return pickedUp;
    }

    public void setPickedUp(boolean pickedUp) {
        this.pickedUp = pickedUp;
    }

    public List<Cloth> getClothTypes() {
        return clothTypes;
    }

    public void addClothType(Cloth cloth) {
        this.clothTypes.add(cloth);
    }

    public void updateClothCount() {
        this.clothCount = this.clothTypes.stream().mapToInt(Cloth::getCount).sum();
    }

    @Override
    public String toString() {
        return "LaundryItem{" +
                "id='" + id + '\'' +
                ", studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", clothWeight=" + clothWeight +
                ", clothCount=" + clothCount +
                ", pickedUp=" + pickedUp +
                '}';
    }
}