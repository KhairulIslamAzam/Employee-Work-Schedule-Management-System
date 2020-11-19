package com.example.service;

import com.example.entity.Employee;
import com.example.entity.Schedule;
import com.example.repository.EmployeeRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    List<Employee> employeeList;
    List<String> schedule;
    Map<Double, Integer> workHour;
    Map<Double, Integer> totalDaysInWeek;
    LocalDate localDate = null;
    DateTimeFormatter formatter = null;

    public EmployeeServiceImpl(){

        employeeList = new ArrayList<>();
        schedule = new ArrayList<>();
        workHour = new HashMap<>();
        totalDaysInWeek = new HashMap<>();

    }


    @Override
    public List<Employee> findAll() {
        return (List<Employee>) employeeRepository.findAll();
    }

    @Override
    public boolean saveDataFromUploadFile(MultipartFile file) {
        boolean isFlag = false;
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (extension.equalsIgnoreCase("csv") || extension.equalsIgnoreCase("xlsx") || extension.equalsIgnoreCase("xls")) {
            isFlag = readDataFromCsv(file);
        }
        return isFlag;
    }

    private boolean readDataFromCsv(MultipartFile file) {

        try {

            String line = "";
            BufferedReader bufferedReader = new BufferedReader
                    (new FileReader(convertMultipartToFile(file)));

            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(",");
                Schedule workSchedule = new Schedule();
                workSchedule.setDate(data[1]);
                workSchedule.setStartTime(data[3]);
                workSchedule.setEndTime(data[4]);

                Employee employee = new Employee();
                employee.setEmployeeId(Double.parseDouble(data[0]));
                employee.setName(data[2]);
                employee.setSchedule(workSchedule);
                employeeList.add(employee);
            }
            List<String> shift = workSchedule(employeeList);

            bufferedReader = new BufferedReader
                    (new FileReader(convertMultipartToFile(file)));

            bufferedReader.readLine();

            int i = 0;
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(",");
                Schedule workSchedule = new Schedule();
                workSchedule.setDate(data[1]);
                workSchedule.setStartTime(data[3]);
                workSchedule.setEndTime(data[4]);
                workSchedule.setShiftName(shift.get(i));

                Employee employee = new Employee();
                employee.setEmployeeId(Integer.parseInt(data[0]));
                employee.setName(data[2]);
                employee.setSchedule(workSchedule);
                employeeRepository.save(employee);
                i++;
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private File convertMultipartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private List<String> workSchedule(List<Employee> cEmployeeList) {

        for (int i = 0; i < cEmployeeList.size(); i++) {

            int count = 1;
            int st = Integer.parseInt(String.valueOf(cEmployeeList.get(i).getSchedule().getStartTime().charAt(0)));
            double newId = cEmployeeList.get(i).getEmployeeId();
            String newDate = cEmployeeList.get(i).getSchedule().getDate();
            String shift = null;
            if (i == 0) {

                workHour.put(newId, count);
                totalDaysInWeek.put(newId, count);

            } else {

                for (int j = i - 1; j < i; j++) {

                    String oldDate = cEmployeeList.get(j).getSchedule().getDate();
                    double oldId = cEmployeeList.get(j).getEmployeeId();

                    if ((newDate.equalsIgnoreCase(oldDate)) && (newId == oldId)) {
                        int tempTime = workHour.get(newId);
                        int tempDays = calculateDayInWeek(totalDaysInWeek, newId);
                        int totalTime = tempTime + count;
                        workHour.put(newId, totalTime);
                        totalDaysInWeek.put(newId, tempDays);

                    } else if ((newDate.equalsIgnoreCase(oldDate)) && (newId != oldId)) {
                        int tempDays = calculateDayInWeek(totalDaysInWeek, newId);
                        totalDaysInWeek.put(newId, tempDays);
                        workHour.put(newId, count);

                    } else if (!newDate.equalsIgnoreCase(oldDate)) {
                        int tempDays = calculateDayInWeek(totalDaysInWeek, newId);
                        totalDaysInWeek.put(newId, tempDays);
                        workHour.put(newId, count);

                    }
                }
            }


            formatter = DateTimeFormatter.ofPattern("yy/MM/dd");
            localDate = LocalDate.parse(newDate, formatter);
            String dayname = localDate.getDayOfWeek().toString().toLowerCase();
            int ckTime = workHour.get(newId);
            int dkDay = totalDaysInWeek.get(newId);

            if (ckTime < 3 && dkDay < 7) {
                shift = shiftCalculation(dayname, st);
            } else {
                shift = "Invalid Shift";
            }
            if (ckTime == 3) {
                workHour.put(newId, 0);
            }
            if (dkDay == 7) {
                totalDaysInWeek.put(newId, 0);
            }
            schedule.add(shift);
        }
        return schedule;
    }

    private int calculateDayInWeek(Map<Double, Integer> totalDaysInWeek, double newId) {
        int count = 1;
        int totalTime = 0;
        boolean flag = totalDaysInWeek.containsKey(newId);

        if (!flag) {
            totalTime = count;
        } else {
            int day = totalDaysInWeek.get(newId);
            totalTime = day + count;
        }

        return totalTime;
    }


    private String shiftCalculation(String localDate, int st) {
        String shiftName = null;

        if (st >= 9) {
            shiftName = localDate + "-" + "Night";

        } else if (st >= 7) {
            shiftName = localDate + "-" + "Morning";

        } else if (st >= 2) {
            shiftName = localDate + "-" + "Evening";
        }
        return shiftName;
    }
}
