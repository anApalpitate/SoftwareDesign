package model;

abstract class BaseModel {
    protected String name;
    protected String visibility; // public, private, protected

    BaseModel() {
    }

    BaseModel(String name, String visibility) {
        this.name = name;
        this.visibility = visibility;
    }

    public void initialize(String name, String visibility) {
        this.name = name;
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public String getVisibility() {
        return visibility;
    }

}
