package epcc.java.cv.faceRecognition.dao;

import com.github.jelmerk.knn.hnsw.HnswIndex;
import epcc.java.cv.faceRecognition.entity.FaceItem;

import java.io.IOException;

/**
 * 这是一个管理人脸库的接口，方法 getIndexByPath 会根据路径返回一个人脸库的引用对象。
 * 这个类的目的在于维护和管理加载在内存中的人脸库，采用LRU算法，当内存中维护的人脸库数量达到配置文件中设置的最大值时，若需要访问新的人脸库，则会根据
 * LRU算法替换最长时间没有访问的人脸库，将它写回存储。
 * */
public interface FaceLibManager {
    HnswIndex<Integer, float[], FaceItem, Float> getIndexByPath(String path) throws IOException;
}
