package com.demo.carservicetracker2.model;
import java.util.Objects;
public class FuelNameModel {
    String Name;
    public FuelNameModel(String name) {
        this.Name = name;
    }
    public String getName() {
        return this.Name;
    }
    public void setName(String name) {
        this.Name = name;
    }
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return Objects.equals(this.Name, ((FuelNameModel) o).Name);
    }
    public int hashCode() {
        return Objects.hash(this.Name);
    }
}
