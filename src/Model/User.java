package model;

public class User {
    private int id;
    private String name;
    private String password;
    private String epost;

    public User(int id, String name, String password, String epost) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.epost = epost;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEpost() {
        return epost;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEpost(String epost) {
        this.epost = epost;
    }
}
