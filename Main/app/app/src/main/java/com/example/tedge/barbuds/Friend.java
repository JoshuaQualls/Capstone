package com.example.tedge.barbuds;

public class Friend implements Comparable {
    String name;
    String image;
    String id;

    public Friend(String name, String id) {
        this.name = name;
        this.id = id;
    }
    public Friend(String name, String id, String image){
        this.name = name;
        this.id = id;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getID() {
        return id;
    }

    @Override
    public int compareTo(Object o) {
       Friend f = (Friend)o;
       if(this.getName().equals(f.getName())) return 0;
       return this.getName().compareTo(f.getName());
    }

    @Override
    public String toString() {
        return this.name;
    }
}
