package se.lowkhaiwynn.slas;

/**
 * Created by Calvin Low on 5/14/2016.
 */
public class Student extends User {
    private String StuIntake;
    private int semester;

    public Student(String ID, String name, String email, String no, String stuIntake, int semester, String dept) {
        super(ID, name, email, no, dept);
        StuIntake = stuIntake;
        this.semester = semester;
    }

    @Override
    public boolean equals(Object o) {
        if(o.equals(super.ID)) {
            return true;
        }
        return false;
    }
}
