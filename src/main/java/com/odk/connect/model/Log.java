package com.odk.connect.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Log {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String action;
	private String tableName;
	@ManyToOne
	private User createdBy;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
//	@ManyToOne
//	private User ModifiedBy;
//	@Temporal(TemporalType.TIMESTAMP)
//	protected Date ModifiedDate;
}
