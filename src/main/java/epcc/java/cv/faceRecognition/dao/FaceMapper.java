package epcc.java.cv.faceRecognition.dao;

import com.github.jelmerk.knn.hnsw.HnswIndex;
import epcc.java.cv.faceRecognition.entity.FaceItem;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;


public interface FaceMapper {
    Collection<Integer> getIdByVector(float[] vector, int idCount);

    Collection<Integer> getIdByVectors(Collection<float[]> vectors, int idCount);

    Optional<FaceItem> selectItemByIndexId(int id);

    Collection<FaceItem> selectAll();

    void insertOne(FaceItem faceItem);

    void insertList(Collection<FaceItem> faceItems) throws InterruptedException;

    boolean removeByIndexId(FaceItem faceItem);

    void setHnswIndex(HnswIndex<Integer, float[], FaceItem, Float> hnswIndex);

    //下面的方法应当删除，因为保存和加载的工作由FaceLibManager来做了，在测试中用到了这些代码，暂时保留
    void saveAsClass(String path) throws IOException;

    void loadFromClass(String path) throws IOException;
}
