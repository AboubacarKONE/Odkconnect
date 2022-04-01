package com.odk.connect.service;

import java.util.List;

import com.odk.connect.model.Log;

public interface logService {
	Log saveLog(Log log);
//	Log updateLog(Long id,Log log);
	List<Log>findAllLogs();

}
