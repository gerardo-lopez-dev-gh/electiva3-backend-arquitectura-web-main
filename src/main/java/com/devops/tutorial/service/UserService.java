package com.devops.tutorial.service;
/*/
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devops.tutorial.exception.ResourceNotFoundException;
import com.devops.tutorial.model.User;
import com.devops.tutorial.repository.UserRepository;

@Transactional
@Service
public class UserService implements IUserService{

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	
	@Autowired
	private UserRepository userRepository;
	
	
	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public User getUserById(Long userId) {
		User user = new User();
		try {
			user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found on :: " + userId));
		} catch (ResourceNotFoundException e) {
			LOGGER.error("Error al obtener el usuario",e);
		}
		    return user;
	}

	@Override
	public User saveUser(User user) {
		return userRepository.save(user);
	}

	@Override
	public User updateUser(Long userId, User userDet) {
		User updatedUser = new User();
		try {
			User user = new User();
			user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found on :: " + userId));
			user.setEmail(userDet.getEmail());
			user.setLastName(userDet.getLastName());
			user.setFirstName(userDet.getFirstName());
			user.setUpdatedAt(new Date());
			updatedUser = userRepository.save(user);
		} catch (ResourceNotFoundException e) {
			LOGGER.error("Error al actualizar el usuario ", e);
		}
		return updatedUser;
	}

	@Override
	public Map<String, Boolean> deleteUser(Long userId) {
		Map<String, Boolean> response = new HashMap<String, Boolean>();
		try {
			User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found on :: " + userId));
			userRepository.delete(user);
			response.put("deleted", Boolean.TRUE);
		} catch (ResourceNotFoundException e) {
			response.put("deleted", Boolean.FALSE);
			LOGGER.error("Error al eliminar el usuario ", e);
		}
		return response;
	}

}
*/