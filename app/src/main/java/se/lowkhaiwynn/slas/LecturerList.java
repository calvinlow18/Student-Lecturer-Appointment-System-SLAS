package se.lowkhaiwynn.slas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 */
public class LecturerList extends Fragment/* implements View.OnKeyListener*/ {

    private LinkedList<String> lecturerName = new LinkedList<String>();
    private ListView listView;
    private SearchView searchView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lecturer_list_fragment, container, false);

        setHasOptionsMenu(true);
        getActivity().setTitle("Choose Lecturer");


        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.row_layout, R.id.textitem, convert(DatabaseTask.lecturerlist));
        listView = (ListView) rootView.findViewById(R.id.lectlist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(searchView != null) {
                    searchView.clearFocus();
                }
                int lectPosition = 0;
                for(int i = 0; i< DatabaseTask.lecturerlist.size(); i++) {
                    if(((TextView)view.findViewById(R.id.textitem)).getText().equals(DatabaseTask.lecturerlist.get(i).getName())) {
                        lectPosition = i;
                        break;
                    }
                }
                Intent intent = new Intent(getActivity(), AppointmentActivity.class);
                intent.putExtra("position", lectPosition);
                intent.putExtra("edit", false);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")) {
                    listView.setAdapter(new ArrayAdapter(getActivity(), R.layout.row_layout, R.id.textitem, convert(DatabaseTask.lecturerlist)));
                } else {
                    LinkedList<Lecturer> toParse = new LinkedList<Lecturer>();
                    for(Lecturer l : DatabaseTask.lecturerlist) {
                        if(l.getName().toLowerCase().contains(newText.toLowerCase())) {
                            toParse.add(l);
                        }
                    }
                    listView.setAdapter(new ArrayAdapter(getActivity(), R.layout.row_layout, R.id.textitem, convert(toParse)));
                }
                return false;
            }
        });
        return super.onOptionsItemSelected(item);
    }

    public String[] convert(LinkedList<Lecturer> toParse) {
        lecturerName = new LinkedList<String>();
        for(Lecturer x : toParse) {
            lecturerName.add(x.Name);
        }

        return lecturerName.toArray(new String[lecturerName.size()]);
    }

}
