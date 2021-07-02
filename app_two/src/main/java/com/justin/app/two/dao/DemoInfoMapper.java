package com.justin.app.two.dao;

import com.justin.app.two.entity.DemoInfo;
import com.justin.app.two.entity.DemoInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DemoInfoMapper {
    long countByExample(DemoInfoExample example);

    int deleteByExample(DemoInfoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(DemoInfo record);

    int insertSelective(DemoInfo record);

    List<DemoInfo> selectByExample(DemoInfoExample example);

    DemoInfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") DemoInfo record, @Param("example") DemoInfoExample example);

    int updateByExample(@Param("record") DemoInfo record, @Param("example") DemoInfoExample example);

    int updateByPrimaryKeySelective(DemoInfo record);

    int updateByPrimaryKey(DemoInfo record);
}