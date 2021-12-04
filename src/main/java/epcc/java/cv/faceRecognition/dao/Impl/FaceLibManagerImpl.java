package epcc.java.cv.faceRecognition.dao.Impl;

import com.github.jelmerk.knn.hnsw.HnswIndex;
import epcc.java.cv.faceRecognition.dao.FaceLibManager;
import epcc.java.cv.faceRecognition.entity.FaceItem;
import epcc.java.cv.faceRecognition.entity.FaceLibConfigEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;


@Repository
public class FaceLibManagerImpl implements FaceLibManager {
    private final FaceLibConfigEntity faceLibConfigEntity;

    /**
     * 当前内存中维护的人脸库
     */
    private final Map<String, HnswIndex<Integer, float[], FaceItem, Float>> indexPath;

    /**
     * 人脸库计时器，记录人脸库对应的最近一次访问的时间戳。根据timeStamp判断大小。timeStamp越小说明最近一次被访问的
     * 时间越久远，因此会排在小根堆的堆顶，可以以O(1)复杂度从内存中把这个Index剔除。
     */
    private static class IndexTimer implements Comparator<IndexTimer> {
        private HnswIndex<Integer, float[], FaceItem, Float> hnswIndex;
        private long timeStamp;

        public IndexTimer(HnswIndex<Integer, float[], FaceItem, Float> hnswIndex, long timeStamp) {
            this.hnswIndex = hnswIndex;
            this.timeStamp = timeStamp;
        }

        @Override
        public int compare(IndexTimer o1, IndexTimer o2) {
            return (int) (o1.getTimeStamp() - o2.getTimeStamp());
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }

        public HnswIndex<Integer, float[], FaceItem, Float> getHnswIndex() {
            return hnswIndex;
        }

        public void setHnswIndex(HnswIndex<Integer, float[], FaceItem, Float> hnswIndex) {
            this.hnswIndex = hnswIndex;
        }
    }

    /**
     * 记录timeStamp的人脸库优先级队列
     */
    private final PriorityQueue<IndexTimer> indexTimers;

    /**
     * 默认构造函数，即自动装配Java bean对象的同时，初始化优先级队列和哈希表。这个bean对象用来获取配置文件
     */
    @Autowired
    public FaceLibManagerImpl(FaceLibConfigEntity faceLibConfigEntity) {
        indexTimers = new PriorityQueue<>(faceLibConfigEntity.getMaxLibCount());
        indexPath = new HashMap<>(faceLibConfigEntity.getMaxLibCount());
        this.faceLibConfigEntity = faceLibConfigEntity;
    }

    /**
     * 析构函数，当服务停止时，储存各个人脸库到对应位置
     */
    protected void finalize() throws IOException {
        for (Map.Entry<String, HnswIndex<Integer, float[], FaceItem, Float>> entry : indexPath.entrySet()) {
            entry.getValue().save(new File(entry.getKey()));
        }
    }

    @Override
    public HnswIndex<Integer, float[], FaceItem, Float> getIndexByPath(String path) throws IOException {
        HnswIndex<Integer, float[], FaceItem, Float> hnswIndex = indexPath.get(path);
        //返回当前以秒为单位的时间戳
        long timeStamp = System.currentTimeMillis() / 1000;
        //如果从Map中找到了，说明内存中有
        if (hnswIndex != null) {
            //更新时间戳
            for (IndexTimer indexTimer : indexTimers) {
                if (indexTimer.hnswIndex.equals(hnswIndex)) {
                    indexTimers.remove(indexTimer);
                    indexTimer.setTimeStamp(timeStamp);
                    indexTimers.add(indexTimer);
                    break;
                }
            }
        }
        //如果没找到，就先尝试从路径中加载，如果路径中没有这个人脸库，则请求参数错误。该异常会逐级向上抛出
        else {
            hnswIndex = HnswIndex.load(new File(path));
            if (indexTimers.size() >= faceLibConfigEntity.getMaxLibCount()) {
                IndexTimer poll = indexTimers.poll();
                String pollPath = null;
                //从Map中删除这个键值对
                assert poll != null;
                for (Map.Entry<String, HnswIndex<Integer, float[], FaceItem, Float>> entry : indexPath.entrySet()) {
                    if (poll.hnswIndex.equals(entry.getValue())) {
                        pollPath = entry.getKey();
                        indexPath.remove(pollPath, poll.hnswIndex);
                        break;
                    }
                }
                assert pollPath != null;
                poll.hnswIndex.save(new File(pollPath));
                poll.hnswIndex = null;
            }

            //新加到内存中的index要添加到Map和Heap
            indexPath.put(path, hnswIndex);
            indexTimers.add(new IndexTimer(hnswIndex, timeStamp));
        }
        return hnswIndex;
    }


}
