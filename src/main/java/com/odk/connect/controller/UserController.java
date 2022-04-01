package com.odk.connect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import javax.mail.MessagingException;
import com.odk.connect.exception.model.EmailExistException;
import com.odk.connect.exception.model.EmailNotFoundException;
import com.odk.connect.exception.model.MotDePasseException;
import com.odk.connect.exception.model.NotAnImageFileException;
import com.odk.connect.exception.model.UserNotFoundException;
import com.odk.connect.exception.model.UsernameExistException;
import com.odk.connect.model.Alumni;
import com.odk.connect.model.AuthenticationRequest;
import com.odk.connect.model.ChangerMotDePasseUser;
import com.odk.connect.model.HttpResponse;
import com.odk.connect.model.User;
import com.odk.connect.model.UserPrincipal;
import com.odk.connect.service.UserService;
import com.odk.connect.utility.JwtTokenProvider;
import com.odk.connect.constants.SecurityConstant;
import static com.odk.connect.constants.fileConstant.*;
import com.odk.connect.exception.ExceptionHandling;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = { "/", "/odkConnect/user" })
public class UserController extends ExceptionHandling {
	public static final String EMAIL_SENT = "Un e-mail avec un nouveau mot de passe a été envoyé à";
	public static final String USER_DELETED_SUCCESSFULLY = "Utilisateur supprimé avec succès.";
	private UserService userService;
	private AuthenticationManager authenticationManager;
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	public UserController(UserService userService, AuthenticationManager authenticationManager,
			JwtTokenProvider jwtTokenProvider) {
		this.userService = userService;
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@PostMapping("/login")
	public ResponseEntity<User> login(@RequestBody AuthenticationRequest user) {
		authenticate(user.getLogin(), user.getPassword());
		User loginUser = userService.findUserByUsername(user.getLogin());
		UserPrincipal userPrincipal = new UserPrincipal(loginUser);
		HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
		return new ResponseEntity<>(loginUser, jwtHeader, OK);
	}

	@PostMapping("/registerUser")
	public ResponseEntity<User> registerUser(@RequestBody User user)
			throws EmailExistException, UsernameExistException, UserNotFoundException, MessagingException {
		User newUser = userService.registerUser(user.getPrenom(), user.getNom(), user.getLogin(), user.getEmail(),
				user.getAdresse(), user.getTelephone());
		return new ResponseEntity<User>(newUser, OK);
	}

	@PostMapping("/registerAlumni")
	public ResponseEntity<Alumni> registerAlum(@RequestBody Alumni alum) throws EmailExistException,
			UsernameExistException, UserNotFoundException, MessagingException, EmailNotFoundException {
		Alumni newAlum = userService.registerAlum(alum.getProfession(), alum.getLogin(), alum.getEmail(),
				alum.getAdresse(), alum.getTelephone());
		return new ResponseEntity<Alumni>(newAlum, OK);
	}

	@PostMapping("/update/password")
	public User changerMotDePasse(@RequestBody ChangerMotDePasseUser mdpUser) throws MotDePasseException {
		return userService.ChangerMotDePasse(mdpUser);
	}

	@PostMapping("/saveUser")
	public ResponseEntity<User> addNewUser(@RequestParam("prenom") String prenom, @RequestParam("nom") String nom,
			@RequestParam("login") String login, @RequestParam("email") String email,
			@RequestParam("adresse") String adresse, @RequestParam("telephone") String telephone,
			@RequestParam("role") String role, @RequestParam("isActive") String isActive,
			@RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
			throws UserNotFoundException, EmailExistException, IOException, UsernameExistException,
			NotAnImageFileException, MessagingException {
		User newUser = userService.addNewUser(prenom, nom, login, email, adresse, telephone, role,
				Boolean.parseBoolean(isActive), profileImage);
		return new ResponseEntity<User>(newUser, OK);

	}

	@PostMapping("/saveAlumni")
	public ResponseEntity<Alumni> addNewAlumni(@RequestParam("prenom") String prenom, @RequestParam("nom") String nom,
			@RequestParam("login") String login, @RequestParam("email") String email,
			@RequestParam("adresse") String adresse, @RequestParam("telephone") String telephone,
			@RequestParam("profession") String profession, @RequestParam("role") String role,
			@RequestParam("isActive") String isActive,
			@RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
			throws UserNotFoundException, EmailExistException, IOException, UsernameExistException,
			NotAnImageFileException, MessagingException {
		Alumni alum = userService.addNewAlumni(prenom, nom, login, email, adresse, telephone, profession, role,
				Boolean.parseBoolean(isActive), profileImage);
		return new ResponseEntity<Alumni>(alum, OK);
	}

	@PostMapping("/updateUser")
	public ResponseEntity<User> update(@RequestParam("currentUsername") String currentUsername,
			@RequestParam("prenom") String prenom, @RequestParam("nom") String nom, @RequestParam("login") String login,
			@RequestParam("email") String email, @RequestParam("adresse") String adresse,
			@RequestParam("telephone") String telephone, @RequestParam("role") String role,
			@RequestParam("isActive") String isActive, @RequestParam("isNonLocked") String isNonLocked,
			@RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
			throws UserNotFoundException, EmailExistException, IOException, UsernameExistException,
			NotAnImageFileException {
		User updateUser = userService.updateUser(currentUsername, prenom, nom, login, email, adresse, telephone, role,
				Boolean.parseBoolean(isActive), Boolean.parseBoolean(isNonLocked), profileImage);
		return new ResponseEntity<User>(updateUser, OK);
	}

	@PostMapping("/updateAlumni")
	public ResponseEntity<Alumni> updateAlumni(@RequestParam("currentUsername") String currentUsername,
			@RequestParam("prenom") String prenom, @RequestParam("nom") String nom, @RequestParam("login") String login,
			@RequestParam("email") String email, @RequestParam("adresse") String adresse,
			@RequestParam("telephone") String telephone, @RequestParam("role") String role,
			@RequestParam("isActive") String isActive, @RequestParam("isNonLocked") String isNonLocked,
			@RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
			@RequestParam("profession") String profession) throws UserNotFoundException, EmailExistException,
			IOException, UsernameExistException, NotAnImageFileException {
		Alumni updateUser = userService.updateAlumni(currentUsername, prenom, nom, login, email, adresse, telephone, role,
				Boolean.parseBoolean(isActive), Boolean.parseBoolean(isNonLocked), profileImage,profession);
		return new ResponseEntity<Alumni>(updateUser, OK);
	}

	@GetMapping("/findUser/{username}")
	public ResponseEntity<User> getUser(@PathVariable("username") String username) {
		User user = userService.findUserByUsername(username);
		return new ResponseEntity<User>(user, OK);
	}

	@GetMapping("/findUserById/{id}")
	public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
		User user = userService.getUserById(id);
		return new ResponseEntity<User>(user, OK);
	}

	@GetMapping("/listUsers")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> users = userService.getUsers();
		return new ResponseEntity<List<User>>(users, OK);
	}

	@GetMapping("/listUsersByRole/{role}")
	public ResponseEntity<List<User>> getUsersByRole(@PathVariable("role") String role) throws UserNotFoundException {
		List<User> users = userService.getUsersByRole(role);
		return new ResponseEntity<List<User>>(users, OK);
	}

	@GetMapping("/resetpassword/{email}")
	public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email)
			throws EmailNotFoundException, MessagingException {
		userService.resetPassword(email);
		return response(OK, EMAIL_SENT + ": " + email);
	}

	@GetMapping("/subscribeUserByEmail/{email}")
	public ResponseEntity<HttpResponse> subscribeUserByEmail(@PathVariable("email") String email)
			throws EmailNotFoundException, MessagingException, MalformedURLException {
		URL url = new URL("http://localhost:4200/inscrire");
		userService.subscribeUserByEmail(url, email);
		return response(OK, EMAIL_SENT + ": " + email);
	}

	@GetMapping("/subscribeAlumByEmail/{email}")
	public ResponseEntity<HttpResponse> subscribeAlumByEmail(@PathVariable("email") String email)
			throws EmailNotFoundException, MessagingException, MalformedURLException {
		URL url = new URL("http://localhost:4200/inscrire/alumni");
		userService.subscribeUserByEmail(url, email);
		return response(OK, EMAIL_SENT + ": " + email);
	}

	@DeleteMapping("/delete/{login}")
	@PreAuthorize("hasAnyAuthority('user:delete')")
	public ResponseEntity<HttpResponse> deleteUser(@PathVariable("login") String login)
			throws IOException, UsernameExistException {
		userService.deleteUser(login);
		return response(OK, USER_DELETED_SUCCESSFULLY);
	}

	@PostMapping("/updateProfileImage")
	public ResponseEntity<User> updateProfileImage(@RequestParam("username") String username,
			@RequestParam(value = "profileImage") MultipartFile profileImage) throws UserNotFoundException,
			EmailExistException, IOException, UsernameExistException, NotAnImageFileException {
		User user = userService.updateProfileImage(username, profileImage);
		return new ResponseEntity<User>(user, OK);
	}

	@GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
	public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName)
			throws IOException {
		return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
	}

	@GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
	public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
		URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try (InputStream inputStream = url.openStream()) {
			int bytesRead;
			byte[] chunk = new byte[1024];
			while ((bytesRead = inputStream.read(chunk)) > 0) {
				byteArrayOutputStream.write(chunk, 0, bytesRead);
			}
		}
		return byteArrayOutputStream.toByteArray();
	}

	@PostMapping("/many/alumni/save")
	public List<Alumni> save(@RequestBody List<Alumni> alumni) {
		return userService.addAlumni(alumni);
	}

	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
		HttpResponse body = new HttpResponse(new Date(), httpStatus.value(), httpStatus,
				httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
		return new ResponseEntity<HttpResponse>(body, httpStatus);
	}

	private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(SecurityConstant.JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
		return headers;
	}

	private void authenticate(String login, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));

	}

}
