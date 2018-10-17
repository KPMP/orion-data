package org.kpmp.packages;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

abstract class MetadataJsonMixin {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss 'UTC'", timezone = "GMT")
	abstract Date getCreatedAt();

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	abstract Date getExperimentDate();

}
