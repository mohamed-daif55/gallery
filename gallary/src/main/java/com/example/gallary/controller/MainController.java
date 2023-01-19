package com.example.gallary.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gallary.entity.Pictures;
import com.example.gallary.entity.Users;
import com.example.gallary.repository.PictureRepository;
import com.example.gallary.repository.UserRepository;

@Controller
@ControllerAdvice
public class MainController {
	
	private Boolean logedInUser;
	private Users userID;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PictureRepository pictureRepository;

	@GetMapping({"","/index"})
	public ModelAndView getIndex() {
		ModelAndView modelAndView = new ModelAndView("index");
		modelAndView.addObject("condition", getLogedInUser());
		List<Pictures> pictures = pictureRepository.findAllAcceptedPic();
		return modelAndView.addObject("images",pictures);
	}
	
	@GetMapping("/allUploadedImages")
	public ModelAndView getAllUploadedImages() {
		ModelAndView modelAndView;
		if(userID != null && userID.getEmail().equals("admin") && userID.getPassword().equals("admin123")) {
			modelAndView = new ModelAndView("allUploadedImages");
			modelAndView.addObject("condition", getLogedInUser());
			List<Pictures> pictures = pictureRepository.findAll();
			return modelAndView.addObject("images",pictures);
		}else {
			Pictures pictures = new Pictures();
			modelAndView = new ModelAndView("index");
			modelAndView.addObject("condition", getLogedInUser());
			return modelAndView.addObject("images",pictures);
		}
		
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

	@GetMapping("/login")
	public ModelAndView loginPage() {
		ModelAndView modelAndView = new ModelAndView("login");
		Users user = new Users();
		return modelAndView.addObject("user", user);
	}
	
	@GetMapping("/loginAdmin")
	public ModelAndView loginAdminPage() {
		ModelAndView modelAndView = new ModelAndView("loginAdmin");
		Users user = new Users();
		return modelAndView.addObject("user", user);
	}
	
	@GetMapping("/logOut")
	public ModelAndView logOut() {
		ModelAndView modelAndView = new ModelAndView("login");
		Users user = new Users();
		setLogedInUser(Boolean.FALSE);
		setUserID(null);
		return modelAndView.addObject("user", user);
	}
	
	@GetMapping("/logOutAdmin")
	public ModelAndView logOutAdmin() {
		ModelAndView modelAndView = new ModelAndView("loginAdmin");
		Users user = new Users();
		setLogedInUser(Boolean.FALSE);
		setUserID(null);
		return modelAndView.addObject("user", user);
	}
	
	@GetMapping("loginCredentials")
	public String loginCredentials(@ModelAttribute Users user, RedirectAttributes redirectAttributes) {
		Users userEntity = userRepository.findByEmail(user.getEmail());
		if(userEntity != null && userEntity.getEmail() != null && userEntity.getPassword() != null) {
			setLogedInUser(Boolean.TRUE);
			setUserID(userEntity);
			ModelAndView modelAndView = new ModelAndView("index");
			modelAndView.addObject("condition", getLogedInUser());
			return "redirect:/index";
		}else {
			redirectAttributes.addFlashAttribute("error", "wrong username or password");
			return "redirect:/login";
		}
	}
	
	@GetMapping("loginAdminCredentials")
	public String loginAdminCredentials(@ModelAttribute Users user, RedirectAttributes redirectAttributes) {
		Users userEntity = userRepository.findByEmail(user.getEmail());
		if(userEntity != null && userEntity.getEmail() != null && userEntity.getPassword() != null) {
			setLogedInUser(Boolean.TRUE);
			setUserID(userEntity);
			ModelAndView modelAndView = new ModelAndView("index");
			modelAndView.addObject("condition", getLogedInUser());
			return "redirect:/allUploadedImages";
		}else {
			redirectAttributes.addFlashAttribute("error", "wrong username or password");
			return "redirect:/loginAdmin";
		}
	}
	
	@PostMapping("/acceptOrReject/{image}")
	public String acceptOrReject(@PathVariable Pictures picture, RedirectAttributes redirectAttributes) {
		if(picture != null) {
			pictureRepository.updatePicture(picture.getMarked(), picture.getId());
			return "redirect:/loginAdmin";
		}else {
			return "redirect:/loginAdmin";
		}
	}
	
	@GetMapping("/addNewImage")
	public ModelAndView uploadNewImage() {
		ModelAndView modelAndView;
		if(!getLogedInUser()) {
			modelAndView = new ModelAndView("login");
			Users user = new Users();
			return modelAndView.addObject("user", user);
		}else {
			modelAndView = new ModelAndView("upload-image");
		}
		Pictures picture = new Pictures();
		return modelAndView.addObject("img", picture);
		
	}

	@PostMapping("/upload")
	public String saveImage(@ModelAttribute Pictures picture, @RequestParam("image") MultipartFile multipartFile)
			throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			picture.setPath(fileName);
			picture.setUserId(userID);
			pictureRepository.save(picture);
			String upload = "/home/daif/git/gallery/gallary/src/main/resources/static/images";
			FileUploadController.saveFile(upload, fileName, multipartFile);
		}
		return "redirect:/index";
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public String handleFileUploadError(RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("error", "you couldn't upload file bigger than 2 MB");
		return "redirect:/";
	}
	
	public Boolean getLogedInUser() {
		if(logedInUser == null) {
			logedInUser = Boolean.FALSE;
		}
		return logedInUser;
	}

	public void setLogedInUser(Boolean logedInUser) {
		this.logedInUser = logedInUser;
	}
	
	public Users getUserID() {
		if( userID == null ) {
			userID = new Users();
		}
		return userID;
	}

	public void setUserID(Users userID) {
		this.userID = userID;
	}

}
