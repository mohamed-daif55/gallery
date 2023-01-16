package com.example.gallary.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gallary.entity.Pictures;
import com.example.gallary.entity.Users;
import com.example.gallary.repository.PictureRepository;
import com.example.gallary.repository.UserRepository;

@Controller
@ControllerAdvice
public class MainController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PictureRepository pictureRepository;

	@GetMapping("")
	public String getIndex() {
		return "index";
	}

	@GetMapping("/addNewUser")
	public ModelAndView addNewUser() {
		ModelAndView modelAndView = new ModelAndView("sign-up");
		Users user = new Users();
		return modelAndView.addObject("user", user);
	}

	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute Users user) {
		userRepository.save(user);
		return "redirect:/index";
	}

	@GetMapping("/addNewImage")
	public ModelAndView uploadNewImage() {
		ModelAndView modelAndView = new ModelAndView("upload-image");
		Pictures picture = new Pictures();
		return modelAndView.addObject("img", picture);
	}

	@PostMapping("/upload")
	public String saveImage(@ModelAttribute Pictures picture, @RequestParam("image") MultipartFile multipartFile) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			picture.setPath(fileName);
			pictureRepository.save(picture);
			String upload = "/home/daif/images";
			FileUploadController.saveFile(upload, fileName, multipartFile);
		}
		return "redirect:/index";
	}
	
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public String handleFileUploadError(RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("error", "you couldn't upload file bigger than 2 MB");
		return "redirect:/";
	}

}
