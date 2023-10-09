package com.terryliu.springbootmultipledatasources.handlers;

import com.pgvector.PGvector;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Nyquist Data Tech Team
 * @version 1.0.0
 * @date 2023/10/9
 * @description PgVector 实体处理器
 */
public class VectorHandler implements TypeHandler<PGvector> {
    @Override
    public void setParameter(PreparedStatement ps, int i, PGvector parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter);
    }

    @Override
    public PGvector getResult(ResultSet rs, String columnName) throws SQLException {
        Object value = rs.getObject(columnName);
        return getVector(value);
    }

    @Override
    public PGvector getResult(ResultSet rs, int columnIndex) throws SQLException {
        Object value = rs.getObject(columnIndex);
        return getVector(value);
    }

    @Override
    public PGvector getResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object value = cs.getObject(columnIndex);
        return getVector(value);
    }

    private PGvector getVector(Object obj) {
        if (null == obj) {
            return null;
        }
        return (PGvector) obj;
    }
}
