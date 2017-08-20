package se.lowkhaiwynn.slas;

/**
 * Created by Calvin Low on 5/14/2016.
 */
public class User {
    protected String ID;
    protected String Name;
    protected String Email;
    protected String No;
    protected String Dept;

    public User(String ID, String name, String email, String no, String dept) {
        this.ID = ID;
        Name = name;
        Email = email;
        No = no;
        Dept = dept;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }
}
