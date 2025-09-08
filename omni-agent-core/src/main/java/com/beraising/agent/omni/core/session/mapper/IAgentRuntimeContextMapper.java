package com.beraising.agent.omni.core.session.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beraising.agent.omni.core.session.entity.AgentRuntimeContextEntity;

@Mapper
public interface IAgentRuntimeContextMapper extends BaseMapper<AgentRuntimeContextEntity> {

    @Select("SELECT * FROM t_agent_runtime_context WHERE s_session_id = #{sessionId}")
    List<AgentRuntimeContextEntity> findBySessionId(@Param("sessionId") String sessionId);

    @Delete("DELETE FROM t_agent_runtime_context WHERE s_session_id = #{sessionId}")
    int deleteBySessionId(@Param("sessionId") String sessionId);
}
