package com.tikkunolam.momentsintime;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Note extends RealmObject {
    /**
     * this class represents a Note that may belong to a Moment.
     * necessary because Realm doesn't allow the Moment to hold a list of Strings
     * just a RealmList<Note>... so here we are
     */

    private String note;

    public Note() {

    }

    public String getNote() {

        return note;

    }

    public void setNote(String note) {

        this.note = note;

    }

}
