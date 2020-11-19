package com.example.controller;

import com.example.entity.Employee;
import com.example.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping(value = "/")
    public String home(Model model){
        model.addAttribute("employee", new Employee());
        List<Employee> employeeList = employeeService.findAll();
        model.addAttribute("employeeList", employeeList);

        return "view/users";
    }

    @PostMapping(value = "/fileupload")
    public String uploadFile(@ModelAttribute Employee employee, RedirectAttributes redirectAttributes){

        boolean isFlag = employeeService.saveDataFromUploadFile(employee.getFile());
        if(isFlag){
            redirectAttributes.addFlashAttribute("successmessage", "File upload successfully done");
        }else {
            redirectAttributes.addFlashAttribute("error", "File upload failled");
        }
        return "redirect:/";
    }
}
