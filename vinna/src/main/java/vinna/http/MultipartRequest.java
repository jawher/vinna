package vinna.http;

import java.io.InputStream;
import java.util.Collection;

public interface MultipartRequest extends Request {

    public Collection<String> getPartsName();

    public UploadedFile getParts(String name);
}
