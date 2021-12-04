package epcc.java.cv.faceRecognition.service.Impl;

import epcc.java.cv.faceRecognition.dao.FaceLibManager;
import epcc.java.cv.faceRecognition.dao.FaceMapper;
import epcc.java.cv.faceRecognition.dao.FigureMapper;
import epcc.java.cv.faceRecognition.dao.Impl.FaceMapperImpl;
import epcc.java.cv.faceRecognition.entity.FaceLibConfigEntity;
import epcc.java.cv.faceRecognition.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class QueryServiceImpl implements QueryService {
    private final FigureMapper figureMapper;
    private final FaceLibConfigEntity faceLibConfigEntity;
    private final FaceMapper faceMapper;
    private final FaceLibManager faceLibManager;

    private Collection<float[]> vectors;
    private Collection<String> groups;
    private Collection<Date> dates;

    @Autowired
    public QueryServiceImpl(FaceMapper faceMapper, FaceLibManager faceLibManager,
                            FaceLibConfigEntity faceLibConfigEntity, FigureMapper figureMapper) {
        this.faceMapper = faceMapper;
        this.faceLibManager = faceLibManager;
        this.faceLibConfigEntity = faceLibConfigEntity;
        this.figureMapper = figureMapper;
    }

    /**
     * 储存经常要使用的变量到成员属性中
     */
    private void parseParameters(Collection<float[]> vectors, Collection<String> groups, Collection<String> dates) throws ParseException {
        this.groups = groups;
        this.vectors = vectors;
        Collection<Date> tempDates = new LinkedList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (String date : dates) {
            tempDates.add(sdf.parse(date));
        }
        this.dates = tempDates;
    }

    /**
     * 根据参数生成对应的人脸库路径
     */
    private Collection<String> generateIndexPaths() {
        ArrayList<String> paths = new ArrayList<>();
        for (String group : groups) {
            for (Date date : dates) {
                paths.add(String.format("%s/%s/%s/facelib", faceLibConfigEntity.getRootPath(), group, date));
            }
        }
        return paths;
    }

    /**
     * 根据指定path挂载（引用）FacelibMapper
     */
    private void generateMapper(String path) throws IOException {
        faceMapper.setHnswIndex(faceLibManager.getIndexByPath(path));
    }

    /**
     * 根据生成的lib查询指定数量的id
     */
    private Set<Integer> getIds(int idCount) {
        return new HashSet<>(faceMapper.getIdByVectors(vectors, idCount));
    }

    /**
     * 根据id在数据库中查询图片的url
     * */
    private Collection<String> getUrlsByIds(Collection<Integer> ids) {
        return figureMapper.getUrlsByIds(ids);
    }

    @Override
    public Collection<String> getUrls(Collection<float[]> vectors, Collection<String> groups,
                                      Collection<String> dates, int idCount) throws Exception {
        parseParameters(vectors, groups, dates);
        Collection<String> paths = generateIndexPaths();
        HashSet<String> urls = new HashSet<>();
        for (String path : paths) {
            generateMapper(path);
            Set<Integer> ids = getIds(idCount);
            urls.addAll(getUrlsByIds(ids));
        }
        return urls;
    }
}
