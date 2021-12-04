package epcc.java.cv.faceRecognition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.jelmerk.knn.DistanceFunctions;
import com.github.jelmerk.knn.hnsw.HnswIndex;
import epcc.java.cv.faceRecognition.dao.FaceLibManager;
import epcc.java.cv.faceRecognition.dao.Impl.FaceMapperImpl;
import epcc.java.cv.faceRecognition.entity.FaceItem;
import epcc.java.cv.faceRecognition.entity.FaceLibConfigEntity;
import epcc.java.cv.faceRecognition.service.Impl.UploadServiceImpl;
import epcc.java.cv.faceRecognition.service.QueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;


@SpringBootTest
class SpringBootTestApplicationTests {

    @Autowired
    private FaceLibConfigEntity faceLibConfigEntity;
    @Autowired
    private FaceLibManager faceLibManager;
    @Autowired
    private QueryService queryService;
    @Autowired
    private FaceMapperImpl faceMapper;
    @Autowired
    private UploadServiceImpl uploadService;

    @Test
    void contextLoads() {
        System.out.println(faceLibConfigEntity);
    }

    @Test
    void faceLibManagerTest() throws IOException {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        System.out.println(path);
        HnswIndex<Integer, float[], FaceItem, Float> hnswIndex = faceLibManager.getIndexByPath(
                faceLibConfigEntity.getRootPath());
        System.out.println(hnswIndex);
    }

    @Test
    void faceMapperCreateTest() throws IOException {
        faceMapper.setHnswIndex(HnswIndex
                .newBuilder(5, DistanceFunctions.FLOAT_COSINE_DISTANCE,
                        100)
                .withRemoveEnabled()
                .build());
        faceMapper.insertOne(new FaceItem(1, new float[] {0.9F, 0.8F, 0.9F, 0.4F, 0.2F}, 0));
        faceMapper.insertOne(new FaceItem(2, new float[] {0.9F, 0.8F, 0.8F, 0.4F, 0.2F}, 1));
        faceMapper.insertOne(new FaceItem(3, new float[] {0.7F, 0.6F, 0.3F, 0.4F, 0.2F}, 2));
        faceMapper.insertOne(new FaceItem(4, new float[] {0.3F, 0.6F, 0.9F, 0.5F, 0.2F}, 2));
        faceMapper.insertOne(new FaceItem(5, new float[] {0.7F, 0.2F, 0.9F, 0.4F, 0.1F}, 2));
        faceMapper.insertOne(new FaceItem(6, new float[] {0.5F, 0.7F, 0.9F, 0.4F, 0.2F}, 1));
        faceMapper.insertOne(new FaceItem(6, new float[] {0.5F, 0.6F, 0.9F, 0.5F, 0.3F}, 3));
        faceMapper.saveAsClass(faceLibConfigEntity.getRootPath());
    }

    @Test
    void faceMapperQueryTest() throws IOException {
        faceMapper.loadFromClass(faceLibConfigEntity.getRootPath());
        Collection<FaceItem> faceItems = faceMapper.selectAll();
        for (FaceItem faceItem : faceItems) {
            System.out.println(faceItem);
        }
        ArrayList<float[]> testVector = new ArrayList<>();
        testVector.add(new float[]{0.5F, 0.6F, 0.9F, 0.5F, 0.3F});
        testVector.add(new float[]{0.44F, 0.66F, 0.87F, 0.52F, 0.31F});
        System.out.println(faceMapper.getIdByVector(new float[]{0.6F, 0.6F, 0.9F, 0.5F, 0.3F}, 2));
        System.out.println(faceMapper.getIdByVectors(testVector, 2));
    }

    @Test
    void queryVectorTest() {
        String url;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
    }

    @Test
    void vectorGetTest() throws JsonProcessingException {
        List<float[]> faceRecognizeResult = uploadService.getVectorByFigure(new File("G:\\Programme\\IDEA\\FaceRecognition\\test2.jpg"));
        System.out.println(faceRecognizeResult);
    }
}
