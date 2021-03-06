package com.jd.hbase.leman.client.row;

import com.jd.hbase.leman.exception.HBaseDataAccessException;
import com.jd.hbase.leman.exception.HBaseRowMappingException;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RowExtractor<T> {
    private final RowMapper<T> rowMapper;
    private static final Logger logger = LoggerFactory.getLogger(RowExtractor.class);

    public RowExtractor(RowMapper<T> rowMapper) {
        if (rowMapper == null) {
            throw new IllegalArgumentException("RowMapper is required");
        }
        this.rowMapper = rowMapper;
    }

    public T extract(Result rs)
            throws HBaseRowMappingException {
        if ((rs == null) || (rs.isEmpty())) {
            return null;
        }
        T result = this.rowMapper.mapRow(rs);
        return result;
    }

    public List<T> extract(ResultScanner scanner)
            throws HBaseRowMappingException {
        List<T> results = new ArrayList();
        for (Result result : scanner) {
            results.add(extract(result));
        }
        return results;
    }

    public List<T> extract(Result[] rs)
            throws HBaseRowMappingException {
        List<T> results = new ArrayList();
        for (Result result : rs) {
            try {
                results.add(extract(result));
            } catch (Exception ex) {
                this.logger.error("extract error!" + ex.getMessage(), ex);
                throw new HBaseDataAccessException("execute extract error:" + result.toString(), ex);
            }
        }
        return results;
    }
}
