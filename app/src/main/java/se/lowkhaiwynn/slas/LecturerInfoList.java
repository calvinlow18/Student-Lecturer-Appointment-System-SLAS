package se.lowkhaiwynn.slas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Calvin Low on 5/26/2016.
 */
public class LecturerInfoList extends Fragment {

    LinkedHashMap<String, List<String>> lect_dept;
    List<String> lectList;
    ExpandableListView expandableListView;
    LecturerInfoAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrieve();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lecturer_info_list, container, false);
        expandableListView = (ExpandableListView) view.findViewById(R.id.lecturerExp);
        getActivity().setTitle("Lecturer Information");

        sort();
        TreeMap<String, List<String>> treeMap = new TreeMap(lect_dept);
        lectList = new ArrayList<>(lect_dept.keySet());
        adapter = new LecturerInfoAdapter(getActivity(), treeMap, lectList);
        expandableListView.setAdapter(adapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String name = lect_dept.get(lectList.get(groupPosition)).get(childPosition);
                String dept = lectList.get(groupPosition);
                Intent intent = new Intent(getActivity(), ShowLecturer.class);
                intent.putExtra("searchName", name);
                intent.putExtra("searchDept", dept);
                System.out.println(name + "    " + dept);
                startActivity(intent);
                return true;
            }
        });
        return view;
    }

    public void sort() {
        lect_dept = new LinkedHashMap<>();
        List<String> FST = new ArrayList<String>();
        List<String> ADTP = new ArrayList<String>();
        List<String> Others = new ArrayList<String>();
        List<String> alevel = new ArrayList<String>();
        List<String> ausmat = new ArrayList<String>();
        List<String> fist = new ArrayList<String>();
        List<String> fia = new ArrayList<String>();
        for(Lecturer l : DatabaseTask.lecturerlist) {
            switch(l.Dept) {
                case "FST":
                    FST.add(l.getName());
                    break;
                case "ADTP":
                    ADTP.add(l.getName());
                    break;
                case "A Level":
                    alevel.add(l.getName());
                    break;
                case "Ausmat":
                    ausmat.add(l.getName());
                    break;
                case "FIST":
                    fist.add(l.getName());
                    break;
                case "FIA":
                    fia.add(l.getName());
                    break;
                default:
                    Others.add(l.getName());
                    break;
            }
        }
        lect_dept.put("A Level", alevel);
        lect_dept.put("Ausmat", ausmat);
        lect_dept.put("ADTP", ADTP);
        lect_dept.put("FIA", fia);
        lect_dept.put("FIST", fist);
        lect_dept.put("FST", FST);
        lect_dept.put("Others", Others);

    }

    public void retrieve() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Retrieving Lecturer Information");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://www.smartkidsedu.tk/Android/lecturerlist.php");
                    URLConnection connection = url.openConnection();
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader
                            = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String response = "";
                    String line = "";
                    while((line = bufferedReader.readLine()) != null)
                    {
                        response+=line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    JSONObject jsonObject;
                    JSONArray jsonArray;
                    if (response != null) {
                        DatabaseTask.lecturerlist.clear();
                        try {
                            jsonObject = new JSONObject(response);
                            jsonArray = jsonObject.optJSONArray("server_response");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject JO = jsonArray.getJSONObject(i);
                                String lectID = JO.optString("LectID");
                                String lectName = JO.optString("LectName");
                                String lectEmail = JO.optString("LectEmail");
                                String lectDept = JO.optString("LectDept");
                                String lectOffice = JO.optString("LectOffice");
                                String lectNo = JO.optString("LectNo");
                                DatabaseTask.lecturerlist.add(new Lecturer(lectID, lectName, lectEmail, lectDept, lectOffice, lectNo));
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        if(progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
