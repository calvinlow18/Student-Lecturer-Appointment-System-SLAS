package se.lowkhaiwynn.slas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Calvin Low on 5/31/2016.
 */
public class AppointmentListAdapter extends BaseAdapter {

    LayoutInflater inflater;

    public AppointmentListAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return DatabaseTask.appList.size();
    }

    @Override
    public Object getItem(int position) {
        return DatabaseTask.appList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        view = inflater.inflate(R.layout.appointment_row_layout, null);
        TextView date = (TextView) view.findViewById(R.id.app_date);

        TextView lecturer = (TextView) view.findViewById(R.id.app_lect);
        TextView student = (TextView) view.findViewById(R.id.app_student);
        TextView time = (TextView) view.findViewById(R.id.app_Time);
        LinearLayout status = (LinearLayout) view.findViewById(R.id.color_row);

        Appointment appointment = DatabaseTask.appList.get(position);

        String lecturer_name = null;
        String student_name = null;
        date.setText( "Date : " + appointment.getDate());
        time.setText("Time : " + appointment.getTime());
        for(Lecturer l : DatabaseTask.lecturerlist) {
            if(l.getID().equals(appointment.getLecturer())) {
                lecturer_name = l.getName();
                break;
            }
        }

        if(appointment.getStudent().equals(" - ")) {
            student_name = appointment.getStudent();
        } else {
            for(Student s : DatabaseTask.studentlist) {
                if(s.getID().equals(appointment.getStudent())) {
                    student_name = s.getName();
                    break;
                }
            }
        }

        if(DatabaseTask.user instanceof Student) {
            lecturer.setText("Lecturer : " + lecturer_name);
            student.setText("Student : " + DatabaseTask.user.getName());
        } else {
            lecturer.setText("Lecturer : " + DatabaseTask.user.getName());
            student.setText("Student : " + student_name);
        }


        switch(appointment.getStatus()) {
            case 1 :
                status.setBackgroundColor(view.getResources().getColor(R.color.lightYellow));
                break;
            case 2 :
                status.setBackgroundColor(view.getResources().getColor(R.color.mediumSeaGreen));
                break;
            case 3 :
                status.setBackgroundColor(view.getResources().getColor(R.color.tomato));
                break;
        }

        return view;
    }

}
