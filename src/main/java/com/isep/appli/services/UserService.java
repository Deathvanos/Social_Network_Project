package com.isep.appli.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.isep.appli.dbModels.Friendship;
import com.isep.appli.dbModels.FriendshipStatus;
import com.isep.appli.models.ModifyUserInfoForm;
import com.isep.appli.repositories.FriendshipRepository;
import com.isep.appli.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.isep.appli.dbModels.User;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FriendshipRepository friendshipRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public String signup(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setEnabled(false);
		user.setIsAdmin(false);
		userRepository.save(user);

		return "signup";
	}

	public Map<String, Boolean> checkUnique(Map<String, String> userInfo) {
		Boolean existingEmail = false;
		Boolean existingUsername = false;

		if (!userInfo.get("email").isEmpty()) {
			existingEmail = userRepository.findByEmail(userInfo.get("email")) != null;
		}

		if (!userInfo.get("username").isEmpty()) {
			existingUsername = userRepository.findByUsername(userInfo.get("username")) != null;
		}

		Map<String, Boolean> uniqueMap = new HashMap<>();
		uniqueMap.put("existingEmail", existingEmail);
		uniqueMap.put("existingUsername", existingUsername);

		return uniqueMap;
	}

	public boolean confirmEmail(long userId) {
		User user = userRepository.findById(userId).orElse(null);
		if (user != null) {
			user.setEnabled(true);
			userRepository.save(user);
			return true;
		} else {
			return false;
		}
	}

	public User login(String email, String password) {
		User user = userRepository.findByEmail(email);
		if (user != null && user.getEnabled()) {
			String hashedPassword = user.getPassword();
			if (passwordEncoder.matches(password, hashedPassword) || password.equals(hashedPassword)) {
				return user;
			}
		}
		return null;
	}

	public void modifyUserInfo(User user, ModifyUserInfoForm newUserInfo) {
		if (!newUserInfo.getPassword().isEmpty()) {
			user.setPassword(passwordEncoder.encode(newUserInfo.getPassword()));
		}

		if (!newUserInfo.getUsername().isEmpty()) {
			user.setUsername(newUserInfo.getUsername());
		}

		if (!newUserInfo.getEmail().isEmpty()) {
			user.setEmail(newUserInfo.getEmail());
			user.setEnabled(false);
		}

		userRepository.save(user);
	}


	public User getUserById(long userId) {
		return userRepository.findById(userId).orElse(null);
	}


	public void addFriend(User user, User friend) {
		Friendship friendship = new Friendship();
		friendship.setUser(user);
		friendship.setFriend(friend);
		friendshipRepository.save(friendship);
	}

	public void removeFriend(User user, User friend) {
		Friendship friendship = friendshipRepository.findByUserAndFriend(user, friend);
		if (friendship != null) {
			friendshipRepository.delete(friendship);
		}
	}

	public List<User> getAllUsersExceptCurrent(User currentUser) {
		return userRepository.findAll().stream()
				.filter(user -> !user.getId().equals(currentUser.getId()))
				.collect(Collectors.toList());
	}

	public List<User> getAllUsersExceptCurrentAndFriends(User currentUser) {
		List<User> friends = this.getFriends(currentUser);
		// Obtenir la liste des utilisateurs à qui une demande d'ami a été envoyée
		List<User> pendingFriends = friendshipRepository.findByUserAndStatus(currentUser, FriendshipStatus.PENDING)
				.stream()
				.map(Friendship::getFriend)
				.toList();
		// on enleve de la liste eds users : soi meme, ses amis, les admins et les demandes amis qu'on a envoyé
		return userRepository.findAll().stream()
				.filter(user -> !user.getId().equals(currentUser.getId())
						&& !friends.contains(user)
						&& !user.getIsAdmin()
						&& !pendingFriends.contains(user))
				.collect(Collectors.toList());
	}

	public void sendFriendRequest(User sender, User recipient) {
		Friendship existingFriendship = friendshipRepository.findByUserAndFriend(sender, recipient);
		if (existingFriendship == null) {
			Friendship friendship = new Friendship();
			friendship.setUser(sender);
			friendship.setFriend(recipient);
			friendship.setStatus(FriendshipStatus.PENDING);
			friendshipRepository.save(friendship);
		}
	}

	public void acceptFriendRequest(User recipient, User sender) {
		Friendship friendship = friendshipRepository.findByUserAndFriend(sender, recipient);
		if (friendship != null && friendship.getStatus() == FriendshipStatus.PENDING) {
			friendship.setStatus(FriendshipStatus.ACCEPTED);
			friendshipRepository.save(friendship);

			// relation in both direction
			Friendship friendship2 = new Friendship();
			friendship2.setUser(recipient);
			friendship2.setFriend(sender);
			friendship2.setStatus(FriendshipStatus.ACCEPTED);
			friendshipRepository.save(friendship2);
		}
	}

	public void declineFriendRequest(User recipient, User sender) {
		Friendship friendship = friendshipRepository.findByUserAndFriend(sender, recipient);
		if (friendship != null && friendship.getStatus() == FriendshipStatus.PENDING) {
			friendship.setStatus(FriendshipStatus.REJECTED);
			friendshipRepository.delete(friendship);  // Optionally, you can delete rejected requests
		}
	}

	public List<Friendship> getPendingFriendRequests(User user) {
		return friendshipRepository.findByFriendAndStatus(user, FriendshipStatus.PENDING);
	}

	public List<User> getFriends(User user) {
		List<Friendship> friendships = friendshipRepository.findByUser(user);
		return friendships.stream()
				.filter(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
				.map(Friendship::getFriend)
				.collect(Collectors.toList());
	}

	public List<User> getAllUsersByStringName(String userName) {
		String[] userNames = userName.split(" ");
		List<User> userList = new ArrayList<>();
		if (userNames.length == 1) {
			userList = getUsersByFirstName(userNames[0]);
			userList.addAll(getUsersByLastName(userNames[0]));
		}
		else if (userNames.length == 2) {
			userList = getUsersByFirstNameAndLastName(userNames[0], userNames[1]);
		}
		else if (userNames.length > 2) {
			for (int i = 1; i < userNames.length-1; i++) {
				String firstName = String.valueOf(userNames[0]);
				for (int nameIndex = 1; nameIndex < i+1; nameIndex ++) {
					firstName = firstName + ' ' + userNames[nameIndex];
				}
				String lastName = String.valueOf(userNames[i + 1]);
				for (int nameIndex = i+2; nameIndex < userNames.length; nameIndex ++) {
					lastName = lastName + ' ' + userNames[nameIndex];
				}
				userList.addAll(getUsersByFirstNameAndLastName(firstName, lastName));
			}
		}
		return userList;
	}

	public List<User> getUsersByFirstName(String firstName) {
		return userRepository.findByFirstName(firstName);
	}

	public List<User> getUsersByLastName(String lastName) {
		return userRepository.findByLastName(lastName);
	}

	public List<User> getUsersByFirstNameAndLastName(String firstName, String lastName) {
		return userRepository.findByFirstNameAndLastName(firstName, lastName);
	}


	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
}
