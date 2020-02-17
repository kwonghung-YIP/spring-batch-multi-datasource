package org.hung.config.batch;

import java.util.List;

import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.OnWriteError;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyListener<T,S> {
	
	private JdbcTemplate cardDb;
	
	public MyListener(JdbcTemplate jdbcTemplate) {
		cardDb = jdbcTemplate;
	}
	
	/*@AfterWrite
	void afterWrite(List<S> items) {
		log.info("afterWrite");
	}*/
	
	@AfterChunk
	void afterChunk(ChunkContext context) {
		int count = cardDb.queryForObject("select count(1) from CARD_TXN where post = 'Y'", Integer.class);
		log.info("afterChunk {}",count);
		
	}
	
	@OnWriteError
	void writeError(Exception e, List<S> items) {
		int count = cardDb.queryForObject("select count(1) from CARD_TXN where post = 'Y'", Integer.class);
		log.error("writeError {}",count);
	}	
	
	/*@AfterChunkError
	void afterChunkError(ChunkContext context) {
		log.info("afterChunkError");
	}*/
}
