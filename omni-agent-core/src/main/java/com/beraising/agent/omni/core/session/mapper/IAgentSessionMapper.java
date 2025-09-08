package com.beraising.agent.omni.core.session.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beraising.agent.omni.core.session.entity.AgentSessionEntity;

@Mapper
public interface IAgentSessionMapper extends BaseMapper<AgentSessionEntity> {

    @Select("SELECT * FROM t_agent_session WHERE s_session_id = #{sessionId}")
    AgentSessionEntity findById(@Param("sessionId") String sessionId);

    @Delete("DELETE FROM t_agent_session WHERE s_session_id = #{sessionId}")
    int deleteById(@Param("sessionId") String sessionId);
}