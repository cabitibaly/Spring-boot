package help.bac.avis;

import help.bac.avis.entite.Role;
import help.bac.avis.entite.Utilisateur;
import help.bac.avis.enums.TypeDeRole;
import help.bac.avis.repository.UtilisateurRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@AllArgsConstructor
@EnableScheduling
@SpringBootApplication
public class AvisUtilisateursApplication implements CommandLineRunner { // Pour exécuter du code au démarrage de l'application

	UtilisateurRepository utilisateurRepository;
	BCryptPasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(AvisUtilisateursApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Utilisateur admin = Utilisateur.builder()
				.actif(true)
				.nom("Admin")
				.mdp(passwordEncoder.encode("admin"))
				.email("admin@konoha.jp")
				.role(
						Role.builder().libelle(TypeDeRole.ADMINISTRATEUR).build()
				)
				.build();

		admin =this.utilisateurRepository.findByEmail("admin@konoha.jp").orElse(admin);
		this.utilisateurRepository.save(admin);
		Utilisateur manager = Utilisateur.builder()
				.actif(true)
				.nom("manager")
				.mdp(passwordEncoder.encode("manager"))
				.email("manager@konoha.jp")
				.role(
						Role.builder().libelle(TypeDeRole.MANAGER).build()
				)
				.build();

		manager = this.utilisateurRepository.findByEmail("manager@konoha.jp").orElse(manager);
		this.utilisateurRepository.save(manager);
	}
}
