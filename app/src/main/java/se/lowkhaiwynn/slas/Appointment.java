package se.lowkhaiwynn.slas;

/**
 * Created by Calvin Low on 4/28/2016.
 */
public class Appointment {
    private int start_hour;
    private int start_min;
    private int day;
    private String student;
    private String lecturer;
    private int status;
    private String desc;
    private String date;

    public Appointment(int start_hour, int start_min, int day, String date, String student, String lecturer, int status, String desc) {
        this.start_hour = start_hour;
        this.start_min = start_min;
        this.day = day;
        this.date = date;
        this.student = student;
        this.lecturer = lecturer;
        this.status = status;
        this.desc = desc;
    }

    public int position() {
        int response = 0;
        switch(start_hour) {
            case 8: response = 0;break;
            case 9: response = 10;break;
            case 10: response = 20;break;
            case 11: response = 30;break;
            case 12: response = 40;break;
            case 13: response = 50;break;
            case 14: response = 60;break;
            case 15: response = 70;break;
            case 16: response = 80;break;
            case 17: response = 90;break;
        }
        if (start_min == 0) {
            if(day == 2)
                response += 0;
            else if (day == 3)
                response += 1;
            else if (day == 4)
                response += 2;
            else if (day == 5)
                response += 3;
            else if (day == 6)
                response += 4;
        } else {
            if(day == 2)
                response += 5;
            else if (day == 3)
                response += 6;
            else if (day == 4)
                response += 7;
            else if (day == 5)
                response += 8;
            else if (day == 6)
                response += 9;
        }
        return response;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "start_hour=" + start_hour +
                ", start_min=" + start_min +
                ", day=" + day +
                ", student='" + student + '\'' +
                ", lecturer='" + lecturer + '\'' +
                ", status=" + status +
                ", desc='" + desc + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public boolean equals(int start_hour, int start_min, int day, String lecturer) {
        if (start_hour != this.start_hour) return false;
        if (start_min != this.start_min) return false;
        if (day != this.day) return false;
        if(!lecturer.equals(this.lecturer)) return false;
        return true;

    }

    public String getStudent() {
        return student;
    }

    public String getDesc() {
        return desc;
    }

    public int getStart_hour() {
        return start_hour;
    }

    public int getStart_min() {
        return start_min;
    }

    public String getTime() {
        int hour;
        String hourS = null;
        String minS = null;

        if(start_hour>12)
            hour = start_hour - 12;
        else
            hour = start_hour;

        if(hour/10 == 0) {
            hourS = "0" + hour;
        } else {
            hourS = String.valueOf(hour);
        }
        if(start_min == 0) {
            minS = "00";
        } else {
            minS = String.valueOf(start_min);
        }
        return hourS + ":" + minS + " " + (start_hour>=12 ? "P.M." : "A.M.");
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getLecturer() {
        return lecturer;
    }
}
