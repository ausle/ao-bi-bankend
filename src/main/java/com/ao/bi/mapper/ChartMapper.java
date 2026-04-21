package com.ao.bi.mapper;

import com.ao.bi.model.entity.Chart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface ChartMapper extends BaseMapper<Chart> {
    int deleteByPrimaryKey(Long id);

    int updateByPrimaryKey(Chart record);
}