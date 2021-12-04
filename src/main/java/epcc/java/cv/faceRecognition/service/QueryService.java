package epcc.java.cv.faceRecognition.service;

import java.util.Collection;

public interface QueryService {
    Collection<String> getUrls(Collection<float[]> vectors, Collection<String> groups,
                               Collection<String> dates, int idCount) throws Exception;
}
