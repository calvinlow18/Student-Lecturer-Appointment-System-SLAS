package se.lowkhaiwynn.slas;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by Calvin Low on 5/26/2016.
 */
public class LecturerInfoAdapter extends BaseExpandableListAdapter {

    private Context context;
    private TreeMap<String, List<String>> lect;
    private List<String> lect_info;

    public LecturerInfoAdapter(Context context, TreeMap<String, List<String>> lect, List<String> lect_info) {
        this.context = context;
        this.lect = lect;
        this.lect_info = lect_info;
    }

    @Override
    public int getGroupCount() {
        return lect_info.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return lect.get(lect_info.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return lect_info.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return lect.get(lect_info.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String title = (String) getGroup(groupPosition);
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.parentlayout, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.parent);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(title);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String title = (String) getChild(groupPosition, childPosition);
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.childlayout, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.child);
        textView.setText(title);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
