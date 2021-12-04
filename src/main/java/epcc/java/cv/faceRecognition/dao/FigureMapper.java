package epcc.java.cv.faceRecognition.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import epcc.java.cv.faceRecognition.entity.FigureTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
@Mapper
public interface FigureMapper extends BaseMapper<FigureTable> {
    @Select("select url from figuretable where id in #{ids} order by date_name")
    Collection<String> getUrlsByIds(Collection<Integer> ids);
}
