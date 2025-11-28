# SimpleCashSI

**SimpleCashSI** est une application back-office pour la banque **SimpleCash**, développée avec **Spring Boot, JPA et REST**.  
Elle permet aux conseillers clientèle de gérer les clients, les comptes bancaires et les services à forte valeur ajoutée comme les virements internes, l’audit et les simulations de crédits.

---

##  Objectifs du projet

- Gérer les clients (création, modification, consultation, suppression)
- Gérer les comptes bancaires (compte courant, compte épargne)
- Réaliser des opérations financières (crédit, débit, virements internes)
- Générer des rapports et audits sur les comptes
- Maintenir un code clair et moderne avec architecture en couches

---

##  User Stories

Chaque issue dans ce projet correspond à une **user story**, représentant un besoin fonctionnel concret :

- **En tant que conseiller**, je veux créer, modifier, consulter et supprimer un client pour gérer mes relations clients.
- **En tant que conseiller**, je veux créditer ou débiter un compte pour gérer les opérations financières.
- **En tant que conseiller**, je veux faire des virements internes entre comptes de la banque de façon sécurisée.
- **En tant qu’auditeur**, je veux générer des rapports de comptes créditeurs et débiteurs pour analyser les risques.

Chaque issue créée dans ce dépôt correspond à une de ces user stories et permet de suivre le développement des fonctionnalités.


##  Structure du projet

- **Backend** : Spring Boot, JPA/Hibernate
- **REST API** : CRUD clients, comptes, opérations bancaires
- **AOP Logging** : suivi des virements internes
- **Tests unitaires** : validation des services critiques
- **Documentation** : Swagger/OpenAPI (optionnelle)

---

## Diagram de classe
![UML Diagram](diagram_de_classe.png)

---

