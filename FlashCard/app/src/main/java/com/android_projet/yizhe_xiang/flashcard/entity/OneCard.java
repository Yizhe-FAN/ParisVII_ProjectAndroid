package com.android_projet.yizhe_xiang.flashcard.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MagicienDeAnaïs on 2016/11/29 0029.
 */

public class OneCard implements Parcelable {
    private int id;    //_id
    private String question;   //question
    private String imgpath;     //le path d'image !
    private String audiopath;   //le path d'audio !
    private String answer;    //anwser
    private int level;      // 0 normal  ||  1 facile  ||  2 difficile  ||  3 trivial
    private int box;        // boîte
    private int flagAlreadyCorrect;   // pas dans le BD, mais on note l'état dans l'activité
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Parcelable.Creator<OneCard> CREATOR =
            new Parcelable.Creator<OneCard>() {
                @Override
                public OneCard[] newArray(int size) {
                    return new OneCard[size];
                }

                @Override
                public OneCard createFromParcel(Parcel source) {
                    return new OneCard(source.readInt(), source.readString(),source.readString(),source.readString(),source.readString(),source.readInt(),source.readInt(),source.readInt());
                }
            };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(question);
        dest.writeString(imgpath);
        dest.writeString(audiopath);
        dest.writeString(answer);
        dest.writeInt(level);
        dest.writeInt(box);
        dest.writeInt(flagAlreadyCorrect);
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public int getLevel() {
        return level;
    }

    public int getBox() {
        return box;
    }

    public int getFlagAlreadyCorrect() {
        return flagAlreadyCorrect;
    }

    public void setFlagAlreadyCorrect(int flagAlreadyCorrect) {
        this.flagAlreadyCorrect = flagAlreadyCorrect;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getImgpath() {
        return imgpath;
    }

    public String getAudiopath() {
        return audiopath;
    }

    public OneCard(int id, String question, String imgpath, String audiopath, String answer, int level, int box, int flagAlreadyCorrect) {
        this.id = id;
        this.question = question;
        this.imgpath = imgpath;
        this.audiopath = audiopath;
        this.answer = answer;
        this.level = level;
        this.box = box;
        this.flagAlreadyCorrect = flagAlreadyCorrect;
    }

    @Override
    public String toString() {
        return "OneCard{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", imgpath='" + imgpath + '\'' +
                ", audiopath='" + audiopath + '\'' +
                ", answer='" + answer + '\'' +
                ", level=" + level +
                ", box=" + box +
                ", flagAlreadyCorrect=" + flagAlreadyCorrect +
                '}';
    }
}
