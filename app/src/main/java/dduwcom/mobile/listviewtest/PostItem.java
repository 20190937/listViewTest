package dduwcom.mobile.listviewtest;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PostItem implements Serializable {
    private int id;
    private String title;
    private String text;

    public String getText() {
        return text;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String s){
        title = s;
    }

    public void setText(String s){
        text = s;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return title + "\t\t(내용 : " + text + ")";
    }
}
