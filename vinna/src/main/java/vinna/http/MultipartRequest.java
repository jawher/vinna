package vinna.http;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

public interface MultipartRequest extends Request {

    public Collection<String> getPartsNames();

    public UploadedFile getPart(String name);

    public Map<String, UploadedFile> getParts();
}
