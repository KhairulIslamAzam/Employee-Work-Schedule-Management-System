package com.example.service;

import com.example.entity.Employee;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {
    List<Employee> findAll();
    boolean saveDataFromUploadFile(MultipartFile file);
}
