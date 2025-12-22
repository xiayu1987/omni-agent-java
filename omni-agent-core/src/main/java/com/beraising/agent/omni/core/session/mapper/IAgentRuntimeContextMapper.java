package com.beraising.agent.omni.core.session.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beraising.agent.omni.core.session.entity.AgentRuntimeContextEntity;

/**
 * Agent运行时上下文数据访问接口
 * 提供对t_agent_runtime_context表的数据操作功能
 */
@Mapper
public interface IAgentRuntimeContextMapper extends BaseMapper<AgentRuntimeContextEntity> {

    /**
     * 根据会话ID查询Agent运行时上下文信息
     *
     * @param sessionId 会话ID
     * @return Agent运行时上下文实体列表
     */
    @Select("SELECT * FROM t_agent_runtime_context WHERE session_id = #{sessionId}")
    List<AgentRuntimeContextEntity> findBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据会话ID删除Agent运行时上下文信息
     *
     * @param sessionId 会话ID
     * @return 删除记录数
     */
    @Delete("DELETE FROM t_agent_runtime_context WHERE session_id = #{sessionId}")
    int deleteBySessionId(@Param("sessionId") String sessionId);
}

