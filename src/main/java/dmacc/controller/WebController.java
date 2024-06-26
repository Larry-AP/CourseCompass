package dmacc.controller;
/**
 * @author Mandy Wiedmier
 * @author Jonah Hummel
 * @author Larry Paucar
 * CIS175 - Spring 2024
 * Final Project - CourseCompass
 * Due Date: Apr 30, 2024
 */

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import dmacc.Repository.CourseRepository;
import dmacc.Repository.StudentRepository;
import dmacc.beans.Course;
import dmacc.beans.GPAMethods;
import dmacc.beans.Student;

@Controller
public class WebController {

@Autowired
StudentRepository repo;

@Autowired
CourseRepository courseRepo;


/**
 *  STUDENT CONTROLLER METHODS
 */

//View all Students
@GetMapping("/viewAll")
	public String viewAllStudents(Model model) {
		if(repo.findAll().isEmpty()) { 
			return addNewStudent(model);
	}
		model.addAttribute("students", repo.findAll());
		return "studentList";
	}

//Add new Students
@GetMapping("/inputStudent")
    public String addNewStudent(Model model) {
        Student s = new Student();
        model.addAttribute("newStudent", s);
        return "inputStudent";
	}
	
@PostMapping("/inputStudent")
    public String addNewStudent(@ModelAttribute Student s, Model model) {
         repo.save(s);
         return viewAllStudents(model);
	}
	
//Edit Students
@GetMapping("/edit/{id}")
	public String showUpdateStudent(@PathVariable("id") long id, Model model) {
		Student s = repo.findById(id).orElse(null);
		model.addAttribute("newStudent", s);
		return "inputStudent"; }
	
@PostMapping("/update/{id}")
	public String reviseStudent(Student s, Model model) {
		repo.save(s);
		return viewAllStudents(model); 
	
	}
	
//Delete Students
@GetMapping("/delete/{id}")
	public String deleteUser(@PathVariable("id") long id, Model model) {
		Student s = repo.findById(id).orElse(null); repo.delete(s);
		return viewAllStudents(model);
	}
		



/**
 *  COURSE CONTROLLER METHODS
 */

// Add new Course
@GetMapping("/inputCourse/{id}")
public String addNewCourse(@PathVariable long id, Model model) {
    Student student = repo.findById(id).orElse(null);
    model.addAttribute("student", student); // Always add the student object to the model
    if (student != null) {
        Course newCourse = new Course();
        newCourse.setStudent(student);
        model.addAttribute("newCourse", newCourse);
        return "inputCourse";
    } else {
        return "error";
    }
}

@PostMapping("/inputCourse/{id}")
public String addNewCourse(@PathVariable long id, @ModelAttribute Course newCourse, Model model) {
    Student student = repo.findById(id).orElse(null);
    if (student != null) {
        newCourse.setStudent(student);
        courseRepo.save(newCourse);
        student.getListOfCourses().add(newCourse);
        repo.save(student);
        return viewAllStudents(model);
    } else {
        return "error";
    }
}


//Delete Course
@GetMapping("/deleteCourse/{studentId}/{courseId}")
public String deleteCourse(@PathVariable long studentId, @PathVariable long courseId, Model model) {
 Optional<Student> optionalStudent = repo.findById(studentId);
 if (optionalStudent.isPresent()) {
     Student student = optionalStudent.get();
     Optional<Course> optionalCourse = courseRepo.findById(courseId);
     if (optionalCourse.isPresent()) {
         Course courseToDelete = optionalCourse.get();
         student.getListOfCourses().remove(courseToDelete);
         repo.save(student);
         courseRepo.delete(courseToDelete);
     } else {
         return "error";
     }
 } else {
     return "error";
 }
 return "redirect:/viewCourses/" + studentId;
}

// View Courses Per Student
@GetMapping("/viewCourses/{studentId}")
public String viewCourses(@PathVariable("studentId") Long Id, Model model){
	Student student = repo.findById(Id).orElse(null);
    if (student != null) {
    	model.addAttribute("student", student);
    	GPAMethods gpaClass = new GPAMethods();
    	double finalGPA = gpaClass.getGPA(student.getListOfCourses());
    	model.addAttribute("GPAView", finalGPA);
    	return "courseList";
    } else {
    	return "error";
    }
    
}

@PostMapping("/addCourse/{studentId}")
public String addCourse(@ModelAttribute Course newCourse, @PathVariable long studentId) {
    Student student = repo.findById(studentId).orElse(null);
    if (student != null) {
        newCourse.setStudent(student);
        student.getListOfCourses().add(newCourse);
        repo.save(student);
        return "redirect:/viewCourses/" + studentId;
    } else {
        return "error";
    }
}
}
