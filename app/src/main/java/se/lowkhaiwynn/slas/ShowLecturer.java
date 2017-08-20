package se.lowkhaiwynn.slas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class ShowLecturer extends AppCompatActivity {

    private Lecturer getLect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_lecturer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AfterLogin.check = true;
        String name = getIntent().getStringExtra("searchName");
        String dept = getIntent().getStringExtra("searchDept");
        getSupportActionBar().setTitle(name);

        for(Lecturer l : DatabaseTask.lecturerlist) {
            System.out.println(l.getName());
            if(!dept.equals("Others")) {
                if(l.getName().equals(name) && l.Dept.equals(dept)) {
                    getLect = l;
                    break;
                }
            } else {
                if(l.getName().equals(name)) {
                    getLect = l;
                    break;
                }
            }

        }

        TextView lectName = (TextView) findViewById(R.id.lectinfoName);
        TextView lectEmail = (TextView) findViewById(R.id.lectinfoEmail);
        TextView lectDept = (TextView) findViewById(R.id.lectinfoDept);
        TextView lectNumber = (TextView) findViewById(R.id.lectinfoNum);
        TextView lectOffice = (TextView) findViewById(R.id.lectinfoOffice);
        lectName.setText(getLect.getName());
        lectEmail.setText(getLect.Email);
        lectDept.setText(getLect.Dept);
        lectNumber.setText(getLect.No);
        lectOffice.setText(getLect.getLectOffice());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
