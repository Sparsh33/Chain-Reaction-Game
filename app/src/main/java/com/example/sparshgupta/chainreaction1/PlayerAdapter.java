package com.example.sparshgupta.chainreaction1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.util.ArrayList;

/**
 * Created by sparshgupta on 21/07/17.
 */

public class PlayerAdapter extends ArrayAdapter<PlayerSettingsClass> {

    private Context mContext;
    private ArrayList<PlayerSettingsClass> arrayList;
    private ViewHolder holder;

    PlayerAdapter(Context context, ArrayList<PlayerSettingsClass> arrayList) {
        super(context, 0, arrayList);
        mContext = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.player_settings_adapter, null);
            holder = new ViewHolder();
            holder.textView = (TextView)convertView.findViewById(R.id.playerTextView);
            holder.spinner = (Spinner)convertView.findViewById(R.id.colorSpinner);
            ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(mContext, R.array.colorArray, android.R.layout.simple_spinner_item);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinner.setAdapter(spinnerAdapter);
            convertView.setTag(holder);
        }
        final PlayerSettingsClass tempClass = new Select().from(PlayerSettingsClass.class).where("remote_id = ?", position).executeSingle();
        holder = (ViewHolder) convertView.getTag();
        holder.textView.setText(tempClass.player);
        holder.spinner.setSelection(tempClass.colorPos);
        tempClass.save();
        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                tempClass.colorPos = pos;
                tempClass.save();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return convertView;
    }

    @Override
    public PlayerSettingsClass getItem(int position) {
        // TODO Auto-generated method stub
        return arrayList.get(position);
    }

    class ViewHolder{
        TextView textView;
        Spinner spinner;
    }
}
