package se.lowkhaiwynn.slas;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Calvin Low on 4/28/2016.
 */
public class AppointmentGridAdapter extends BaseAdapter {
    private Context context;
    private int[] array;

    public AppointmentGridAdapter(Context context, int[] array) {
        this.context = context;
        this.array = array;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        gridView = inflater.inflate(R.layout.time, null);

        TextView textView = (TextView) gridView.findViewById(R.id.grid_item_image);

        gridView.setClickable(true);

        switch (array[position]) {
            case 0:
                gridView.setBackgroundColor(Color.WHITE);
                gridView.setClickable(false);
                break;
            case 1:
                gridView.setBackgroundColor(gridView.getResources().getColor(R.color.lightYellow));
                break;
            case 2:
                gridView.setBackgroundColor(gridView.getResources().getColor(R.color.mediumSeaGreen));
                break;
            case 3:
                gridView.setBackgroundColor(gridView.getResources().getColor(R.color.tomato));
                break;
        }

        Date date = Calendar.getInstance().getTime();
        Date position_date = new Date();
        int day = 0;

        switch (position / 10) {
            case 0:
                position_date.setHours(8);
                break;
            case 1:
                position_date.setHours(9);
                break;
            case 2:
                position_date.setHours(10);
                break;
            case 3:
                position_date.setHours(11);
                break;
            case 4:
                position_date.setHours(12);
                break;
            case 5:
                position_date.setHours(13);
                break;
            case 6:
                position_date.setHours(14);
                break;
            case 7:
                position_date.setHours(15);
                break;
            case 8:
                position_date.setHours(16);
                break;
            case 9:
                position_date.setHours(17);
                break;
        }

        switch (position % 10) {
            case 0:
                day = 1;
                position_date.setMinutes(0);
                break;
            case 1:
                day = 2;
                position_date.setMinutes(0);
                break;
            case 2:
                day = 3;
                position_date.setMinutes(0);
                break;
            case 3:
                day = 4;
                position_date.setMinutes(0);
                break;
            case 4:
                day = 5;
                position_date.setMinutes(0);
                break;
            case 5:
                day = 1;
                position_date.setMinutes(30);
                break;
            case 6:
                day = 2;
                position_date.setMinutes(30);
                break;
            case 7:
                day = 3;
                position_date.setMinutes(30);
                break;
            case 8:
                day = 4;
                position_date.setMinutes(30);
                break;
            case 9:
                day = 5;
                position_date.setMinutes(30);
                break;
        }

        if (date.getDay() > day && date.getDay() != 6) {
            gridView.setBackgroundColor(Color.GRAY);
            gridView.setClickable(true);
        } else if (date.getDay() == day) {
            if (date.getHours() > position_date.getHours()) {
                gridView.setBackgroundColor(Color.GRAY);
                gridView.setClickable(true);
            } else if (date.getHours() == position_date.getHours()) {
                if (date.getMinutes() >= position_date.getMinutes()) {
                    gridView.setBackgroundColor(Color.GRAY);
                    gridView.setClickable(true);
                }
            }

        }

        if (position >= 0 && position <= 4)
            textView.setText("8");
        else if (position >= 10 && position <= 14)
            textView.setText("9");
        else if (position >= 20 && position <= 24)
            textView.setText("10");
        else if (position >= 30 && position <= 34)
            textView.setText("11");
        else if (position >= 40 && position <= 44)
            textView.setText("12");
        else if (position >= 50 && position <= 54)
            textView.setText("1");
        else if (position >= 60 && position <= 64)
            textView.setText("2");
        else if (position >= 70 && position <= 74)
            textView.setText("3");
        else if (position >= 80 && position <= 84)
            textView.setText("4");
        else if (position >= 90 && position <= 94)
            textView.setText("5");

        textView.setTextSize(10);
        return gridView;
    }

    @Override
    public int getCount() {
        return array.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
