package vinna.vinnernews.model;

import java.util.List;

public interface Repository {
    List<Submission> range(int from, int count);
    Submission get(Long id);
    Submission post(Submission s);
}
