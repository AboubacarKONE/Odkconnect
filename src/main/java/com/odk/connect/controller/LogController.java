package com.odk.connect.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.odk.connect.exception.ExceptionHandling;
import com.odk.connect.model.Log;
import com.odk.connect.service.logService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = { "/", "/odkConnect/log" })
@RequiredArgsConstructor
public class LogController extends ExceptionHandling {
	private final logService logService;
	@PostMapping("/saveLog")
	ResponseEntity<Log> saveLog(@RequestBody Log log) {
		return ResponseEntity.ok(logService.saveLog(log));
	}
//	@PutMapping("/updateLog/{id}")
//	ResponseEntity<Log> updateLog(@PathVariable("id") Long id,@RequestBody Log log) {
//		return ResponseEntity.ok(logService.updateLog(id, log));
//	}
	@GetMapping("/listLogs")
	ResponseEntity<List<Log>> findAllLogs(){
		return ResponseEntity.ok(logService.findAllLogs());
	}
	

}
