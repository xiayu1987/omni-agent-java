package com.beraising.agent.omni.core.session.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beraising.agent.omni.core.session.entity.AgentSessionItemEntity;

@Mapper
public interface IAgentSessionItemMapper extends BaseMapper<AgentSessionItemEntity> {

    @Select("SELECT * FROM t_agent_session_item WHERE s_session_id = #{sessionId}")
    List<AgentSessionItemEntity> findBySessionId(@Param("sessionId") String sessionId);

    @Delete("DELETE FROM t_agent_session_item WHERE s_session_id = #{sessionId}")
    int deleteBySessionId(@Param("sessionId") String sessionId);
}
