package epcc.java.cv.faceRecognition.dao.Impl;

import com.github.jelmerk.knn.SearchResult;
import com.github.jelmerk.knn.hnsw.HnswIndex;
import epcc.java.cv.faceRecognition.dao.FaceMapper;
import epcc.java.cv.faceRecognition.entity.FaceItem;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Repository
public class FaceMapperImpl implements FaceMapper {
    @Getter
    @Setter
    private HnswIndex<Integer, float[], FaceItem, Float> hnswIndex;

    @Override
    public Collection<Integer> getIdByVector(float[] vector, int idCount) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (SearchResult<FaceItem, Float> result : hnswIndex.findNearest(vector, idCount)) {
            ids.add(result.item().getFigureId());
        }
        return ids;
    }

    @Override
    public Collection<Integer> getIdByVectors(Collection<float[]> vectors, int idCount) {
        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<SearchResult<FaceItem, Float>> results = new ArrayList<>();
        for (float[] vector : vectors) {
            results.addAll(hnswIndex.findNearest(vector, idCount));
        }
        results.sort(Comparator.comparing(SearchResult::distance));
        for (SearchResult<FaceItem, Float> result : results) {
            if (ids.size() < idCount && !ids.contains(result.item().getFigureId())) ids.add(result.item().getFigureId());
        }
        return ids;
    }

    @Override
    public Optional<FaceItem> selectItemByIndexId(int id) {
        return hnswIndex.get(id);
    }

    @Override
    public Collection<FaceItem> selectAll() {
        return hnswIndex.items();
    }

    @Override
    public void insertOne(FaceItem faceItem) {
        hnswIndex.add(faceItem);
    }

    @Override
    public void insertList(Collection<FaceItem> faceItems) throws InterruptedException {
        hnswIndex.addAll(faceItems);
    }

    @Override
    public boolean removeByIndexId(FaceItem faceItem) {
        return hnswIndex.remove(faceItem.id(), faceItem.version());
    }

    /**
     * 保存人脸库到指定位置path
     */
    @Override
    public void saveAsClass(String path) throws IOException {
        hnswIndex.save(new File(path));
    }

    /**
     * 从指定路径path加载人脸库
     */
    @Override
    public void loadFromClass(String path) throws IOException {
        hnswIndex = HnswIndex.load(new File(path));
    }
}
