package ravi_sharma.makemynotes.Model;

public class notesfile {

    private String id;
    private String title;
    private String data;
    private String time;

    public notesfile() {

    }

    public notesfile(String id, String title, String data, String time)
    {
        this.id=id;
        this.title=title;
        this.data=data;
        this.time = time;
    }

    public String getId()
    {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getData() {
        return data;
    }

    public String getTime() {
        return time;
    }
}
