package com.abhi.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;


@Data
public class Employee {

	private String id;
	private String empName;
	private String empId;
}
