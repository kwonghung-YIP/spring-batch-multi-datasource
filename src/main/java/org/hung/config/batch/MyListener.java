package org.hung.config.batch;

import org.springframework.batch.core.annotation.AfterChunkError;
import org.springframework.batch.core.scope.context.ChunkContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyListener {

	@AfterChunkError
	void onChunkError(ChunkContext context) {
		log.info("afterChunkError");
	}
}
