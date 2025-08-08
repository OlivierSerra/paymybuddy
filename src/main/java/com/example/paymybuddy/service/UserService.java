package com.example.paymybuddy.service;

import com.example.paymybuddy.repository.UserRepository;
import com.example.paymybuddy.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {
	@Autowired
	// création d'une variable de type userRepository afin d'ajouter des
	// comportements au système
	private UserRepository repo;

	// création d'une variable encodé de modèle BCryptPasswordEncoder qui encaissera
	// le mot de passe qui sera encodé
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	// permet le transfert d'argent en créant une transaction ( nécessité de la
	// retravailler)
	public boolean transferMoney(int fromUserId, int toUserId, double amount) {
		Optional<User> fromUserOpt = repo.findById(fromUserId);
		Optional<User> toUserOpt = repo.findById(toUserId);

		// si on trouve celui qui verse et celui qui reçoit
		if (fromUserOpt.isPresent() && toUserOpt.isPresent()) {
			// utilisateur qui verse
			User from = fromUserOpt.get();
			// utilisateur qui reçoit
			User to = toUserOpt.get();
			// on regarde si le from a assez d'argent
			if (from.getBalance() >= amount) {
				from.setBalance(from.getBalance() - amount);
				to.setBalance(to.getBalance() + amount);
				// on enregistre l'utilisateur from et to
				repo.save(from);
				repo.save(to);
				return true;
			}

		}

		return false;
	}

	// avoir la liste de tous les utilisateurs
	public List<User> getAllUsers() {
		return repo.findAll();
	}

	// avoir un seul utilisateur
	public Optional<User> getUserById(Integer id) {
		return repo.findById(id);
	}

	// créer un utilisateur
	public User createUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return repo.save(user);
	}

	// supprimer un utilisateur
	public void deleteUser(Integer id) {
		repo.deleteById(id);
	}

	// mise a jour d'un utilisateur
	public User updateUser(Integer id, User updatedUser) {
		return repo.findById(id)
				.map(user -> {
					user.setEmail(updatedUser.getEmail());
					user.setUsername(updatedUser.getUsername());
					user.setPassword(updatedUser.getPassword());
					user.setBalance(updatedUser.getBalance());
					return repo.save(user);
				})
				.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
	}

	// sauvegarder un utilisateur
	public User saveUser(User user) {
		return repo.save(user);
	}

}
