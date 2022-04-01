package com.odk.connect.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.odk.connect.model.Log;
import com.odk.connect.repository.LogRepository;
import com.odk.connect.service.logService;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class logServiceImpl implements logService{
	private final LogRepository logRepository;
	@Override
	public Log saveLog(Log log) {
		log.setCreatedDate(new Date());
		return logRepository.save(log);
	}

//	@Override
//	public Log updateLog(Long id, Log log) {
//		Log logGet = logRepository.findById(id).get();
//		Log logSave = new Log();
//		logSave.setCreatedBy(logGet.getCreatedBy());
//		logSave.setCreatedDate(logGet.getCreatedDate());
//		logSave.setAction(log.getAction());
//		logSave.setModifiedBy(logGet.getModifiedBy());
//		logSave.setModifiedDate(logGet.getModifiedDate());
//		return logRepository.save(logSave);
//	}

	@Override
	public List<Log> findAllLogs() {
		return logRepository.findAllByOrderByCreatedDateDesc();
	}

}
