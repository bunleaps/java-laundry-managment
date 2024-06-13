package com.oopfinal;

public class Cloth {
    private String type;
    private int count;

    public Cloth(String type, int count) {
        this.type = type;
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}

// Derived classes for specific cloth types
class Pants extends Cloth {
    public Pants(int count) {
        super("Pants", count);
    }
}

class Shirts extends Cloth {
    public Shirts(int count) {
        super("Shirts", count);
    }
}

class Shorts extends Cloth {
    public Shorts(int count) {
        super("Shorts", count);
    }
}