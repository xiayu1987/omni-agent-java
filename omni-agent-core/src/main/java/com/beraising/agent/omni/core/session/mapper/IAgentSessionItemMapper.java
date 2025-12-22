package com.beraising.agent.omni.core.session.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beraising.agent.omni.core.session.entity.AgentSessionItemEntity;

/**
 * Agent会话项数据访问接口
 * 提供对t_agent_session_item表的基本CRUD操作以及根据会话ID查询和删除的扩展功能
 */
@Mapper
public interface IAgentSessionItemMapper extends BaseMapper<AgentSessionItemEntity> {

    /**
     * 根据会话ID查询所有相关的会话项记录
     *
     * @param sessionId 会话ID，用于筛选特定会话的所有项
     * @return 返回匹配会话ID的所有AgentSessionItemEntity对象列表
     */
    @Select("SELECT * FROM t_agent_session_item WHERE session_id = #{sessionId}")
    List<AgentSessionItemEntity> findBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据会话ID删除所有相关的会话项记录
     *
     * @param sessionId 会话ID，用于删除特定会话的所有项
     * @return 返回删除操作影响的记录数
     */
    @Delete("DELETE FROM t_agent_session_item WHERE session_id = #{sessionId}")
    int deleteBySessionId(@Param("sessionId") String sessionId);
}
