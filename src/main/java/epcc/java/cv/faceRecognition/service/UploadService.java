package epcc.java.cv.faceRecognition.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public interface UploadService {
    void UploadFigures();
    List<float[]> getVectorByFigure(File figure) throws JsonProcessingException;
}
