package se.lowkhaiwynn.slas;

/**
 * Created by Calvin Low on 5/14/2016.
 */
public class Lecturer extends User {

    private String LectOffice;

    public Lecturer(String lectID, String lectName, String lectEmail, String lectDept, String lectOffice, String lectNo) {
        super(lectID, lectName, lectEmail, lectNo, lectDept);
        LectOffice = lectOffice;
    }

    @Override
    public String toString() {
        return "Lecturer{" +
                "LectOffice='" + LectOffice + '\'' +
                '}';
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public String getLectOffice() {
        return LectOffice;
    }

    public String findName(String id) {
        if(id.equals(ID))
            return Name;
        return null;
    }
}
