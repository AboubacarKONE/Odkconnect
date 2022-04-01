package com.odk.connect.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javax.mail.MessagingException;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import static com.odk.connect.enumeration.Role.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static com.odk.connect.constants.fileConstant.*;
import static com.odk.connect.constants.UserImplConstant.*;
import com.odk.connect.enumeration.Role;
import com.odk.connect.exception.model.EmailExistException;
import com.odk.connect.exception.model.EmailNotFoundException;
import com.odk.connect.exception.model.MotDePasseException;
import com.odk.connect.exception.model.NotAnImageFileException;
import com.odk.connect.exception.model.UserNotFoundException;
import com.odk.connect.exception.model.UsernameExistException;
import com.odk.connect.model.Alumni;
import com.odk.connect.model.ChangerMotDePasseUser;
import com.odk.connect.model.LignePromotion;
import com.odk.connect.model.User;
import com.odk.connect.model.UserPrincipal;
import com.odk.connect.repository.LignePromoRepository;
import com.odk.connect.repository.UserRepository;
import com.odk.connect.service.EmailService;
import com.odk.connect.service.LogginAttemptService;
import com.odk.connect.service.UserService;
import lombok.RequiredArgsConstructor;
import static org.apache.commons.lang3.StringUtils.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.springframework.http.MediaType.*;

@Service
@Transactional
@Qualifier("userDetailsService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
	private final UserRepository userRepository;
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final LogginAttemptService loginAttemptService;
	private final EmailService emailService;
	private final LignePromoRepository lignePromoRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findUserByLogin(username);
		if (user == null) {
			LOGGER.error(NO_USER_FOUND_BY_USERNAME + username);
			throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
		} else {
			validateLoginAttempt(user);
			user.setLastLoginDateDisplay(user.getLastLoginDate());
			user.setLastLoginDate(new Date());
			userRepository.save(user);
			UserPrincipal userPrincipal = new UserPrincipal(user);
			LOGGER.info(FOUND_USER_BY_USERNAME + username);
			return userPrincipal;
		}

	}

	@Override
	public User registerUser(String prenom, String nom, String login, String email, String adresse, String telephone)
			throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
		validateNewUsernameAndEmail(EMPTY, login, email);
		User user = new User();
		user.setUserId(generateUserId());
		String password = generatePassword();
		user.setPrenom(prenom);
		user.setNom(nom);
		user.setLogin(login);
		user.setEmail(email);
		user.setAdresse(adresse);
		user.setTelephone(telephone);
		user.setJoinDate(new Date());
		user.setPassword(encodePassword(password));
		user.setActive(true);
		user.setNonLocked(true);
		user.setRole(ROLE_FORMATEUR.name());
		user.setAuthorities(ROLE_FORMATEUR.getAuthorities());
		user.setProfileImageUrl(getTempraryProfileIamgeUrl(login));
		userRepository.save(user);
		LOGGER.info("New user password: " + password);
		emailService.sendNewPasswordEmail(prenom, password, email);
		return user;
	}

	@Override
	public Alumni registerAlum(String Profession, String login, String email, String adresse, String telephone)
			throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException,
			EmailNotFoundException {
		Alumni userAlum = userRepository.findUserALumniByEmail(email);
		if (userAlum == null) {
			throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL);
		}
		String password = generatePassword();
		userAlum.setLogin(login);
		userAlum.setEmail(email);
		userAlum.setProfession(Profession);
		userAlum.setAdresse(adresse);
		userAlum.setTelephone(telephone);
		userAlum.setJoinDate(new Date());
		userAlum.setPassword(encodePassword(password));
		userAlum.setActive(true);
		userAlum.setNonLocked(true);
		userRepository.save(userAlum);
		LOGGER.info("New user password: " + password);
		emailService.sendNewPasswordEmail(userAlum.getPrenom(), password, email);
		return userAlum;
	}

	@Override
	public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername,
			String newEmail, String adresse, String telephone, String role, boolean isActive, boolean isNotLocked,
			MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException,
			IOException, NotAnImageFileException {
		User currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
		currentUser.setPrenom(newFirstName);
		currentUser.setNom(newLastName);
		currentUser.setLogin(newUsername);
		currentUser.setEmail(newEmail);
		currentUser.setActive(isActive);
		currentUser.setAdresse(adresse);
		currentUser.setTelephone(telephone);
		currentUser.setNonLocked(isNotLocked);
		currentUser.setRole(getRoleEnumName(role).name());
		currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
//		currentUser.setProfileImageUrl(getTempraryProfileIamgeUrl(newUsername));
		userRepository.save(currentUser);
		saveProfileImage(currentUser, profileImage);
		return currentUser;
	}
	@Override
	public Alumni updateAlumni(String currentUsername, String newFirstName, String newLastName, String newUsername,
			String newEmail, String adresse, String telephone, String role, boolean isActive, boolean isNotLocked,
			MultipartFile profileImage,String profession) throws UserNotFoundException, EmailExistException, UsernameExistException,
			IOException, NotAnImageFileException {
//		Alumni currentUser = userRepository.findUserALumniByEmail(newEmail);
		Alumni currentUser = validateAlumniNewUsernameAndEmail(currentUsername, newUsername, newEmail);
		currentUser.setPrenom(newFirstName);
		currentUser.setNom(newLastName);
		currentUser.setLogin(newUsername);
		currentUser.setEmail(newEmail);
		currentUser.setActive(isActive);
		currentUser.setAdresse(adresse);
		currentUser.setTelephone(telephone);
		currentUser.setNonLocked(isNotLocked);
		currentUser.setRole(getRoleEnumName(role).name());
		currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
		currentUser.setProfession(profession);
//		currentUser.setProfileImageUrl(getTempraryProfileIamgeUrl(newUsername));
		userRepository.save(currentUser);
		saveProfileImage(currentUser, profileImage);
		return currentUser;
	}

	@Override
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@Override
	public User findUserByUsername(String Username) {
		return userRepository.findUserByLogin(Username);
	}

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}

	@Override
	public List<User> getUsersByRole(String role) throws UserNotFoundException {
		List<User> users = userRepository.findByRole(role);
		if (users.isEmpty()) {
			throw new UserNotFoundException(NO_USER_FOUND_BY_ROLE + role);
		}
		return users;
	}

	@Override
	public User addNewUser(String prenom, String nom, String login, String email, String adresse, String telephone,
			String role, boolean isActive, MultipartFile profileImage)
			throws UserNotFoundException, EmailExistException, UsernameExistException, IOException,
			NotAnImageFileException, MessagingException {
		validateNewUsernameAndEmail(EMPTY, login, email);
//		validateAlumniNewUsernameAndEmail(EMPTY, login, email);
		User user = new User();
		String password = generatePassword();
		user.setUserId(generateUserId());
		user.setPrenom(prenom);
		user.setNom(nom);
		user.setJoinDate(new Date());
		user.setLogin(login);
		user.setEmail(email);
		user.setAdresse(adresse);
		user.setTelephone(telephone);
		user.setPassword(encodePassword(password));
		user.setActive(isActive);
		user.setNonLocked(true);
		user.setRole(getRoleEnumName(role).name());
		user.setAuthorities(getRoleEnumName(role).getAuthorities());
		user.setProfileImageUrl(getTempraryProfileIamgeUrl(login));
		userRepository.save(user);
		saveProfileImage(user, profileImage);
		emailService.sendNewPasswordEmail(prenom, password, email);
		LOGGER.info("New user password: " + password);
		return user;
	}

	@Override
	public Alumni addNewAlumni(String prenom, String nom, String login, String email, String adresse, String telephone,
			String profession, String role, boolean isActive,MultipartFile profileImage)
			throws UserNotFoundException, EmailExistException, UsernameExistException, IOException,
			NotAnImageFileException, MessagingException {
//		validateAlumniNewUsernameAndEmail(EMPTY, login, email);
		validateNewUsernameAndEmail(EMPTY, login, email);
		Alumni alum = new Alumni();
		String password = generatePassword();
		alum.setUserId(generateUserId());
		alum.setPrenom(prenom);
		alum.setNom(nom);
		alum.setJoinDate(new Date());
		alum.setLogin(login);
		alum.setEmail(email);
		alum.setAdresse(adresse);
		alum.setTelephone(telephone);
		alum.setPassword(encodePassword(password));
		alum.setActive(isActive);
		alum.setNonLocked(true);
		alum.setProfession(profession);
		alum.setRole(getRoleEnumName(role).name());
		alum.setAuthorities(getRoleEnumName(role).getAuthorities());
		alum.setProfileImageUrl(getTempraryProfileIamgeUrl(login));
		LOGGER.info("New user password: " + password);
		userRepository.save(alum);
		saveProfileImage(alum, profileImage);
		emailService.sendNewPasswordEmail(prenom, password, email);
		return alum;
	}

	@Override
	public User getUserById(Long id) {
		Optional<User> user = userRepository.findById(id);
		if (user.isEmpty()) {
			throw new UsernameNotFoundException(NO_USER_FOUND_BY_ID);
		}
		return user.get();
	}

	@Override
	public void deleteUser(String login) throws IOException, UsernameExistException {
		List<LignePromotion> lignePromo = lignePromoRepository.findAllByUserLogin(login);
		if (!lignePromo.isEmpty()) {
			throw new UsernameExistException("impossible de supprimer un utilisateur avec une ligne de promotion");
		}
		User user = userRepository.findUserByLogin(login);
		Path userFolder = Paths.get(USER_FOLDER + user.getLogin()).toAbsolutePath().normalize();
		FileUtils.deleteDirectory(new File(userFolder.toString()));
		userRepository.deleteById(user.getId());

	}

	@Override
	public User updateProfileImage(String username, MultipartFile profileImage) throws EmailExistException,
			UsernameExistException, UserNotFoundException, IOException, NotAnImageFileException {
		User user = validateNewUsernameAndEmail(username, null, null);
		saveProfileImage(user, profileImage);
		return user;
	}

	@Override
	public void resetPassword(String email) throws MessagingException, EmailNotFoundException {
		User user = userRepository.findUserByEmail(email);
		if (user == null) {
			throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
		}
		String password = generatePassword();
		user.setPassword(encodePassword(password));
		userRepository.save(user);
		emailService.sendNewPasswordEmail(user.getPrenom(), password, user.getEmail());
	}

	@Override
	public void subscribeUserByEmail(URL url, String email) throws MessagingException, EmailNotFoundException {
//		User user = userRepository.findUserByEmail(email);
//		if (user == null) {
//			throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
//		}
		emailService.sendNewSubscribeEmail(url, email);

	}

	@Override
	public User ChangerMotDePasse(ChangerMotDePasseUser mdp) throws MotDePasseException {
		validate(mdp);
		Optional<User> userOptionnal = userRepository.findById(mdp.getId());
		if (!userOptionnal.isPresent()) {
			LOGGER.warn("Aucun utilisateur n'a ete trouve avec l'ID " + mdp.getId());
			throw new MotDePasseException(NO_USER_FOUND_BY_ID + mdp.getId());
		}
		if(userOptionnal.get().getPassword() != encodePassword(mdp.getAncienmotdepasse())) {
			throw new MotDePasseException("votre ancien mot de passe n'est pas valide");
		}
		User user = userOptionnal.get();
		user.setPassword(encodePassword(mdp.getMotDePasse()));
		return userRepository.save(user);
	}

	private void validateLoginAttempt(User user) {
		if (user.isNonLocked()) {
			if (loginAttemptService.hasExceededMaxAttempts(user.getLogin())) {
				user.setNonLocked(false);
			} else {
				user.setNonLocked(true);
			}
		} else {
			loginAttemptService.evictUserFromLoginAttemptCache(user.getLogin());
		}
	}

	private void validate(ChangerMotDePasseUser mdp) throws MotDePasseException {
		if (mdp == null) {
			LOGGER.warn("Impossible de modifier le mot de passe avec un ID NULL");
			throw new MotDePasseException(NO_INFORMATION_TO_CHANGE_PASSWORD);
		}
		if (mdp.getId() == null) {
			LOGGER.warn("Impossible de modifier le mot de passe avec un objet NULL");
			throw new MotDePasseException(IMPOSSIBLE_TO_CHANGE_PASSWORD);
		}
		
		if (StringUtils.isBlank(mdp.getMotDePasse()) || StringUtils.isBlank(mdp.getConfirmeMotDePasse())) {
			LOGGER.warn("Impossible de modifier le mot de passe avec un mot de passe NULL");
			throw new MotDePasseException(PASSWORD_NUL);
		}
		if (!mdp.getMotDePasse().equals(mdp.getConfirmeMotDePasse())) {
			LOGGER.warn("Impossible de modifier le mot de passe avec deux mots de passe different");
			throw new MotDePasseException(SAME_PASSWORD);
		}
	}

	private String getTempraryProfileIamgeUrl(String username) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username)
				.toUriString();
	}

	private String encodePassword(String password) {
		return bCryptPasswordEncoder.encode(password);
	}

	private String generatePassword() {
		return RandomStringUtils.randomAlphanumeric(10);
	}

	private String generateUserId() {
		return RandomStringUtils.randomNumeric(10);
	}

	private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail)
			throws EmailExistException, UsernameExistException, UserNotFoundException {
		User userByNewUsername = findUserByUsername(newUsername);
		User userByNewEmail = findUserByEmail(newEmail);
		if (isNotBlank(currentUsername)) {
			User currentUser = findUserByUsername(currentUsername);
			if (currentUser == null) {
				throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
			}

			if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
				throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
			}

			if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
				throw new EmailExistException(EMAIL_ALREADY_EXISTS);
			}
			return currentUser;
		} else {
			if (userByNewUsername != null) {
				throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
			}
			if (userByNewEmail != null) {
				throw new EmailExistException(EMAIL_ALREADY_EXISTS);
			}
			return null;
		}

	}

	private void saveProfileImage(User user, MultipartFile profileImage) throws IOException, NotAnImageFileException {
		if (profileImage != null) {
			if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE)
					.contains(profileImage.getContentType())) {
				throw new NotAnImageFileException(profileImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
			}
			Path userFolder = Paths.get(USER_FOLDER + user.getLogin()).toAbsolutePath().normalize();
			if (!Files.exists(userFolder)) {
				Files.createDirectories(userFolder);
				LOGGER.info(DIRECTORY_CREATED + userFolder);
			}
			Files.deleteIfExists(Paths.get(userFolder + user.getLogin() + DOT + JPG_EXTENSION));
			Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getLogin() + DOT + JPG_EXTENSION),
					REPLACE_EXISTING);
			user.setProfileImageUrl(setProfileImageUrl(user.getLogin()));
			userRepository.save(user);
			LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
		}
//		if (profileImage != null) {
//			Path userFolder = Paths.get(USER_FOLDER + user.getLogin()).toAbsolutePath().normalize();
//			if (!Files.exists(userFolder)) {
//				Files.createDirectories(userFolder);
//				LOGGER.info(DIRECTORY_CREATED + userFolder);
//			}
//			Files.deleteIfExists(Paths.get(userFolder + user.getLogin() + DOT + JPG_EXTENSION));
//			Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getLogin() + DOT + JPG_EXTENSION),
//					REPLACE_EXISTING);
//			user.setProfileImageUrl(setProfileImageUrl(user.getLogin()));
//			userRepository.save(user);
//			LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
//		}

	}

	private String setProfileImageUrl(String login) {
		return ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(USER_IMAGE_PATH + login + FORWARD_SLASH + login + DOT + JPG_EXTENSION).toUriString();

	}

	private Role getRoleEnumName(String role) {
		return Role.valueOf(role.toUpperCase());
	}

	private Alumni validateAlumniNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail)
			throws EmailExistException, UsernameExistException, UserNotFoundException {
		Alumni userByNewUsername = userRepository.findUserAlumniByLogin(newUsername);
		Alumni userByNewEmail = userRepository.findUserALumniByEmail(newEmail);
		if (isNotBlank(currentUsername)) {
			Alumni currentUser = userRepository.findUserAlumniByLogin(currentUsername);
			if (currentUser == null) {
				throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
			}

			if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
				throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
			}

			if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
				throw new EmailExistException(EMAIL_ALREADY_EXISTS);
			}
			return currentUser;
		} else {
			if (userByNewUsername != null) {
				throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
			}
			if (userByNewEmail != null) {
				throw new EmailExistException(EMAIL_ALREADY_EXISTS);
			}
			return null;
		}

	}

	@Override
	public List<Alumni> addAlumni(List<Alumni> alumni) {
		List<Alumni> list = new ArrayList<>();
		for (int i = 0; i < alumni.size(); i++) {
			System.out.println(alumni.get(i));
			Alumni part = new Alumni();
			part.setNom(alumni.get(i).getNom());
			part.setPrenom(alumni.get(i).getPrenom());
			part.setEmail(alumni.get(i).getEmail());
			part.setTelephone(alumni.get(i).getTelephone());
			part.setAdresse(alumni.get(i).getAdresse());
			part.setUserId(generateUserId());
			part.setRole(ROLE_ALUM.name());
			part.setAuthorities(ROLE_ALUM.getAuthorities());
			part.setProfileImageUrl(getTempraryProfileIamgeUrl(alumni.get(i).getNom()));
			User alumByEmail = userRepository.findUserByEmail(part.getEmail());
			if (alumByEmail == null) {
				Alumni p = userRepository.saveAndFlush(part);
				list.add(p);
			}
		}
		return list;
	}

}
