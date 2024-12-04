package love.madohomu.madomagiarchive_fe_java.models;

import java.util.Date;
import java.util.List;

public class FileItem {
    public int Id;
    public String Type;
    public Boolean R18;
    public String Title;
    public String Description;
    public String Source;
    public int Width;
    public int Height;
    public double Duration;
    public String File;
    public long Size;
    public Date DateCreated;
    public Date DateModified;
    public List<Tag> Tags;
}
