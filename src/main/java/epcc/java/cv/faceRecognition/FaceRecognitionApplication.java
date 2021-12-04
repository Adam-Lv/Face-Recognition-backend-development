package epcc.java.cv.faceRecognition;

import com.github.jelmerk.knn.hnsw.HnswIndex;
import epcc.java.cv.faceRecognition.dao.FaceLibManager;
import epcc.java.cv.faceRecognition.entity.FaceItem;
import epcc.java.cv.faceRecognition.entity.FaceLibConfigEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class FaceRecognitionApplication {

    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext run = SpringApplication.run(FaceRecognitionApplication.class, args);
        FaceLibManager faceLibManager = run.getBean(FaceLibManager.class);
        FaceLibConfigEntity faceLibConfigEntity = run.getBean(FaceLibConfigEntity.class);
        HnswIndex<Integer, float[], FaceItem, Float> hnswIndex = faceLibManager.getIndexByPath(
                faceLibConfigEntity.getRootPath());
        System.out.println(hnswIndex);
    }

}
