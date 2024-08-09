package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description 课程计划service接口实现类
 * @author Mr.M
 * @date 2022/9/9 11:14
 * @version 1.0
 */
@Service
  public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;
    @Override
    public List<TeachplanDto> findTeachplanTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {
        Long teachplanDtoId = teachplanDto.getId();
        if(teachplanDtoId == null){
            Teachplan teachplan = new Teachplan();
            //取出同父同级别的课程计划数量
            int count = getTeachplanCount(teachplanDto.getCourseId(), teachplanDto.getParentid());
            teachplan.setOrderby(count+1);
            BeanUtils.copyProperties(teachplanDto,teachplan);
            teachplanMapper.insert(teachplan);
        }
        else{
            Teachplan teachplan = teachplanMapper.selectById(teachplanDtoId);
            BeanUtils.copyProperties(teachplanDto,teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    /**
     * @description 获取最新的排序号
     * @param courseId  课程id
     * @param parentId  父课程计划id
     * @return int 最新排序号
     * @author Mr.M
     * @date 2022/9/9 13:43
     */
    private int getTeachplanCount(long courseId,long parentId){
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,courseId);
        queryWrapper.eq(Teachplan::getParentid,parentId);
        //TODO 讲count改为查最大max的orderby，这样性能更好
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }
}