package com.example.sparshgupta.chainreaction1;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by sparshgupta on 28/07/17.
 */
@Table(name="PlayerSettingsTable")
public class PlayerSettingsClass extends Model{

    @Column(name="player")
    String player;
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    long remoteId;
    @Column(name="colorPos")
    int colorPos;

    public PlayerSettingsClass(){
        super();
    }

    public PlayerSettingsClass(String player, int colorPos, long removeId){
        super();
        this.player = player;
        this.colorPos = colorPos;
        this.remoteId = removeId;
    }

}
