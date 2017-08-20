package se.lowkhaiwynn.slas;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

public class AppointmentActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ScheduleFragment sf;
    private AppointmentList al;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private int lectPosition;
    private Boolean edit;
    private String[] app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        edit = getIntent().getBooleanExtra("edit", false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lectPosition = getIntent().getIntExtra("position", 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        if(edit) {
            getSupportActionBar().setTitle("Changing Appointment");
            app = getIntent().getStringArrayExtra("app");
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkGreen)));
            tabLayout.setBackgroundColor(getResources().getColor(R.color.darkGreen));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.darkGreen));
            }
        } else {
            if(DatabaseTask.user instanceof Student) {
                getSupportActionBar().setTitle(DatabaseTask.lecturerlist.get(lectPosition).getName());
            } else {
                getSupportActionBar().setTitle(DatabaseTask.user.getName());
            }

        }

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position) {
                    case 0:
                        sf.refresh();
                        break;
                    case 1:
                        al.refresh();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(al != null) {
            al.refresh();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;

        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Bundle bundle = new Bundle();
            switch(position) {
                case 0:
                    if(DatabaseTask.user instanceof Lecturer) {
                        bundle.putString("id", DatabaseTask.user.getID());
                    } else {
                        bundle.putString("id", DatabaseTask.lecturerlist.get(lectPosition).getID());
                    }
                    if(edit) {
                        bundle.putBoolean("editAppointment", edit);
                        bundle.putStringArray("app", app);
                    }
                    sf = new ScheduleFragment();
                    sf.setArguments(bundle);
                    return sf;
                case 1:
                    bundle.putBoolean("editAppointment", edit);
                    al = new AppointmentList("http://www.smartkidsedu.tk/Android/lecturerAppointment.php?lecturer=" + DatabaseTask.user.getID());
                    al.setArguments(bundle);
                    return al;
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    if(DatabaseTask.user instanceof Lecturer) {
                        return "Book Slot";
                    } else {
                        return "Make Appointment";
                    }
                case 1:
                    if(DatabaseTask.user instanceof Lecturer) {
                        return "Current Appointments";
                    } else {
                        return "All Appointments Made";
                    }

            }
            return null;
        }
    }
}
