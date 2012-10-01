package vinna.vinnernews.util;

import vinna.vinnernews.model.Submission;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Normalizer;

public class Seo {
    public static String submissionLocation(Submission submission) {
        return "/submission/" + submission.getId() + "/" + submission.getSeoTitle();
    }

    public static String seo(String string) {


        int maxlen = 80;
            /*String enc = title.trim().toLowerCase()
                    .replaceAll("!|#|\\$|&|'|\"|\\(|\\)|\\*|\\+|,|\\/|\\:|;|=|\\?|@|\\[|\\]|%|~|\\.", "")
                    .replaceAll("\\s+", "_");*/

        String enc = Normalizer.normalize(string.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\p{Alnum}]+", "_");
        try {
            enc = URLEncoder.encode(enc, "utf-8");
            if (enc.length() > maxlen) {

                if (enc.contains("_")) {
                    for (int i = maxlen - 1; i > 0; i--) {
                        if (enc.charAt(i) == '_') {
                            enc = enc.substring(0, i);
                            break;
                        }
                    }
                } else {
                    enc = enc.substring(0, maxlen);
                }
            }
            return enc;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }
}
