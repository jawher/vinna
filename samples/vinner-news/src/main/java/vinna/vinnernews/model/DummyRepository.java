package vinna.vinnernews.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class DummyRepository implements Repository {
    private Map<Long, Submission> submissionsById = new ConcurrentHashMap<>();
    private List<Submission> submissionsList = new CopyOnWriteArrayList<>();
    private AtomicLong idgen = new AtomicLong();

    public DummyRepository() {
        for (int i = 0; i < 50; i++) {
            post(new Submission("kmeisthax explains why it's so hard to patch or add content for disc-based Wii games." + i, "http://www.youtube.com/" + i, "Ohai", "me"));
        }
    }

    @Override
    public List<Submission> range(int from, int count) {
        if (from >= submissionsList.size()) {
            return Collections.emptyList();
        }
        if (from + count >= submissionsList.size()) {
            count = submissionsList.size() - from;
        }
        return submissionsList.subList(from, from + count - 1);
    }

    @Override
    public Submission get(Long id) {
        return submissionsById.get(id);
    }

    @Override
    public Submission post(Submission s) {
        Long id = idgen.incrementAndGet();
        s.setId(id);
        submissionsList.add(s);
        submissionsById.put(id, s);
        return s;
    }
}
