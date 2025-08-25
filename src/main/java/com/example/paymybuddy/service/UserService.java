package com.example.paymybuddy.service;

import com.example.paymybuddy.model.User;
import com.example.paymybuddy.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
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

	@Transactional
	public User createUser(User user) {
		if (user.getEmail() == null || user.getEmail().isBlank())
			throw new IllegalArgumentException("Email requis");
		if (user.getPassword() == null || user.getPassword().isBlank())
			throw new IllegalArgumentException("Mot de passe requis");

		user.setEmail(user.getEmail().trim().toLowerCase(Locale.ROOT));

		if (user.getUsername() == null || user.getUsername().isBlank()) {
			user.setUsername(generateUniqueUsernameFromEmail(user.getEmail()));
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		if (user.getBalance() == null)
			user.setBalance(BigDecimal.ZERO);
		if (user.getBankAccount() == null)
			user.setBankAccount(0);
		if (user.getRole() == null || user.getRole().isBlank())
			user.setRole("USER");
		return repo.save(user);
	}

	private String generateUniqueUsernameFromEmail(String email) {
		String local = email.substring(0, email.indexOf('@'))
				.replaceAll("[^a-zA-Z0-9_.-]", "")
				.toLowerCase();
		if (local.isBlank())
			local = "user";
		String candidate = local;
		int i = 1;
		while (repo.existsByUsername(candidate)) {
			candidate = local + i++;
		}
		return candidate;
	}

	public boolean existsByEmail(String email) {
		return repo.existsByEmail(email.trim().toLowerCase(Locale.ROOT));
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

	@Transactional
	public void completeOnboarding(String emailOfPrincipal, User form) {
		User user = getRequiredByEmail(emailOfPrincipal);

		// Ne mets à jour QUE les champs autorisés lors de l’onboarding
		if (form.getUsername() != null && !form.getUsername().isBlank()) {
			user.setUsername(form.getUsername());
		}
		if (form.getEmail() != null && !form.getEmail().isBlank()) {
			user.setEmail(normalizeEmail(form.getEmail()));
		}

		if (form.getBankAccount() != null) {
			user.setBankAccount(form.getBankAccount());
		}

		user.setOnboardingCompleted(true);
		repo.save(user);
	}

	private static String normalizeEmail(String s) {
		return s == null ? null : s.trim().toLowerCase(Locale.ROOT);
	}

	public User saveUser(User user) {
		return repo.save(user);
	}

	// supprimer un utilisateur
	public void deleteUser(Integer id) {
		repo.deleteById(id);
	}

	public Optional<User> findByUsername(String username) {
		return repo.findByUsername(username);
	}

	public Optional<User> findByEmail(String email) {
		return repo.findByEmail(email);
	}

	public User getCurrentUser(Principal principal) {
		return repo.findByEmail(principal.getName())
				.orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
	}

	public User getRequiredByUsername(String username) {
		return findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + username));
	}

	public User getRequiredByEmail(String email) {
		return findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("email introuvable: " + email));
	}

	public List<User> findAll() {
		return repo.findAll();
	}

	// alimenter le compte
	/**
	 * 
	 * @param principal utilisateur connecté
	 * @param amount    montant à ajouter
	 * @return user mis à jour avec nouveau solde
	 *
	 */
	public User creditAccount(Principal principal, BigDecimal amount) {
		if (amount == null || amount.signum() <= 0) {
			throw new IllegalArgumentException("Montant invalide");
		}
		User me = getCurrentUser(principal);
		me.setBalance(me.getBalance().add(amount));
		return repo.save(me);
	}

	// retirer de argent du compte
	/**
	 * 
	 * @param principal user connecté
	 * @param amount    solde du compte positif à retirer
	 * @return nouveau solde renvoyé et persisté dans la BDD
	 */
	public User withdrawMoney(Principal principal, BigDecimal amount) {
		if (amount == null || amount.signum() <= 0) {
			throw new IllegalArgumentException("Montant invalide");
		}
		User me = getCurrentUser(principal);
		if (me.getBalance().compareTo(amount) < 0) {
			throw new RuntimeException("Solde insufissant");
		}
		me.setBalance(me.getBalance().subtract(amount));
		return repo.save(me);
	}

	// mappage updateProfile avec updateUser
	@Transactional
	public User updateProfile(String email, User form) {
		User me = getRequiredByEmail(email);

		User connectedUser = new User();
		connectedUser.setUsername(form.getUsername());
		connectedUser.setEmail(email);
		connectedUser.setPassword(form.getPassword());

		return updateUser(me.getId(), connectedUser);
	}

}
