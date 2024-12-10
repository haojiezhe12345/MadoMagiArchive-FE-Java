package love.madohomu.madomagiarchive_fe_java.models;

import java.util.Date;
import java.util.List;

public class FileItem {
    public int id;
    public String type;
    public Boolean r18;
    public String title;
    public String description;
    public String source;
    public int width;
    public int height;
    public double duration;
    public String file;
    public long size;
    public Date dateCreated;
    public Date dateModified;
    public List<Tag> tags;
}
