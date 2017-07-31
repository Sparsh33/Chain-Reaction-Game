package com.example.sparshgupta.chainreaction1;

import android.content.Context;

/**
 * Created by sparshgupta on 14/07/17.
 */

public class CustomButton extends android.support.v7.widget.AppCompatButton {

    int value, player, max, row, column;

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public CustomButton(Context context) {
        super(context);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public void incrementValue(){
        if(value == -1)
            value = 0;
        value++;
    }

    public void decrementValue(){
        value--;
    }
}
