package com.teamsync.mapper;
import com.teamsync.entity.Document;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DocumentMapper {
    Document findById(@Param("id") Long id);
    List<Document> findByProjectId(@Param("projectId") Long projectId);
    List<Document> findAll();
    int insert(Document document);
    int deleteById(@Param("id") Long id);
    int incrementDownloadCount(@Param("id") Long id);
}
