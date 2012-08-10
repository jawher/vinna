package vinna.helpers;

import org.mockito.ArgumentMatcher;

import java.util.Arrays;
import java.util.Collection;

public class VinnaMatchers {
    private static class CollectionArgumentMatcher<T> extends ArgumentMatcher<Collection<T>> {
        private final Collection<T> coll;

        public CollectionArgumentMatcher(Collection<T> coll) {
            this.coll = coll;
        }

        @Override
        public boolean matches(Object argument) {
            Collection<T> acol = (Collection<T>) argument;
            return coll.size()==acol.size() && acol.containsAll(coll);
        }
    }

    public static <T> ArgumentMatcher<Collection<T>> eqColl(final Collection<T> coll) {
        return new CollectionArgumentMatcher<>(coll);
    }

    public static <T> ArgumentMatcher<Collection<T>> eqColl(final T ... elements) {
        return new CollectionArgumentMatcher<>(Arrays.asList(elements));
    }
}
