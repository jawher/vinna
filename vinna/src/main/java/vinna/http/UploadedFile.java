package vinna.http;

import org.apache.commons.fileupload.FileItem;
import vinna.exception.VuntimeException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class UploadedFile {
    private final FileItem fileItem;

    public UploadedFile(FileItem fileItem) {
        this.fileItem = fileItem;
    }

    public String getFileName() {
        return fileItem.getName();
    }

    public String getFieldName() {
        return fileItem.getFieldName();
    }

    public InputStream getStream() {
        try {
            return fileItem.getInputStream();
        } catch (IOException e) {
            throw new VuntimeException("unexpected exception while reading the multipart data", e);
        }
    }

    public String getContentType() {
        return fileItem.getContentType();
    }

    public void saveTo(File file) {
        try {
            fileItem.write(file);
        } catch (Exception e) {
            throw new VuntimeException(e);
        }
    }
}
