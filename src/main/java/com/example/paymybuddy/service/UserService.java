package com.example.paymybuddy.service;

import com.example.paymybuddy.model.User;
import com.example.paymybuddy.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

	private final UserRepository repo;
	private final PasswordEncoder passwordEncoder;

	public UserService(PasswordEncoder passwordEncoder, UserRepository repo) {
		this.passwordEncoder = passwordEncoder;
		this.repo = repo;
	}

	@Transactional
	public boolean transferMoney(int fromUserId, int toUserId, BigDecimal amount) {
		if (amount == null || amount.signum() <= 0) {
			throw new IllegalArgumentException("Le montant doit être > 0");
		}
		if (fromUserId == toUserId) {
			throw new IllegalArgumentException("Impossible de se virer à soi-même");
		}

		User from = repo.findById(fromUserId).orElseThrow(() -> new IllegalArgumentException("Expéditeur introuvable"));
		User to = repo.findById(toUserId).orElseThrow(() -> new IllegalArgumentException("Destinataire introuvable"));

		if (from.getBalance().compareTo(amount) < 0) {
			throw new IllegalArgumentException("Solde insuffisant");
		}

		from.setBalance(from.getBalance().subtract(amount));
		to.setBalance(to.getBalance().add(amount));

		// Avec @Transactional, les modifications seront flush/commit automatiquement.
		// On peut malgré tout sauver explicitement :
		repo.save(from);
		repo.save(to);

		return true;
	}

	public List<User> getAllUsers() {
		return repo.findAll();
	}

	public Optional<User> getUserById(Integer id) {
		return repo.findById(id);
	}

	public User createUser(User user) {
		if (user.getPassword() == null || user.getPassword().isBlank()) {
			throw new IllegalArgumentException("Mot de passe requis");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		if (user.getBalance() == null)
			user.setBalance(BigDecimal.ZERO);
		if (user.getRole() == null || user.getRole().isBlank())
			user.setRole("USER");
		return repo.save(user);
	}

	@Transactional
	public User updateUser(Integer id, User updatedUser) {
		return repo.findById(id)
				.map(user -> {
					if (updatedUser.getEmail() != null)
						user.setEmail(updatedUser.getEmail());
					if (updatedUser.getUsername() != null)
						user.setUsername(updatedUser.getUsername());
					if (updatedUser.getBalance() != null)
						user.setBalance(updatedUser.getBalance());
					if (updatedUser.getRole() != null)
						user.setRole(updatedUser.getRole());

					// Si un nouveau mot de passe (non vide) est fourni, on l’encode
					if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
						user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
					}

					return repo.save(user);
				})
				.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
	}

	// Option : supprime cette méthode si elle n’est pas utilisée,
	// ou garde-la en encodant aussi le mot de passe si nécessaire.
	public User saveUser(User user) {
		// Attention: si tu passes ici pour créer/maj un user,
		// assure-toi d'encoder le password AVANT d'appeler saveUser.
		return repo.save(user);
	}

	// supprimer un utilisateur
	public void deleteUser(Integer id) {
		repo.deleteById(id);
	}
}
