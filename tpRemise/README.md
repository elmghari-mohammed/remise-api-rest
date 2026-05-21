# TP Remise Facturation

Application Spring Boot de gestion de remises et transactions de facturation. Projet pédagogique illustrant l'évolution d'une architecture JDBC pure vers JPA/Hibernate et Spring Data JPA, avec coexistence des deux mondes.

---

## Sommaire

1. [Contexte pédagogique](#1-contexte-pédagogique)
2. [Architecture en couches](#2-architecture-en-couches)
3. [Nouveaux fichiers DAO](#3-nouveaux-fichiers-dao)
4. [Annotations utilisées](#4-annotations-utilisées)
5. [Chaîne d'appels](#5-chaîne-dappels)
6. [Basculer entre les deux implémentations DAO](#6-basculer-entre-les-deux-implémentations-dao)
7. [Endpoints REST de RemiseController](#7-endpoints-rest-de-remisecontroller)
8. [Initialisation de la base de données](#8-initialisation-de-la-base-de-données)
9. [Héritage historique : Transaction en JDBC](#9-héritage-historique--transaction-en-jdbc)
10. [Stack technique](#10-stack-technique)

---

## 1. Contexte pédagogique

Ce projet illustre une **migration progressive** de l'accès aux données dans une application Spring Boot :

| Étape | Technologie | Exemple |
|-------|-------------|---------|
| 1. Héritage | **JDBC pur** (`JdbcTemplate`) | `TransactionRepository`, `TransactionService` |
| 2. Nouveau développement | **JPA / Hibernate** (`EntityManager`) | `RemiseHibernateDao` |
| 3. Abstraction | **Spring Data JPA** | `RemiseSpringDataDao`, `RemiseRepository` |

L'ancien module `Transaction` reste en JDBC pour montrer la coexistence. Le nouveau module `Remise` bénéficie de JPA et propose **deux implémentations DAO interchangeables**.

---

## 2. Architecture en couches

```
┌─────────────────────────────────────────────────────────┐
│                   RemiseController                       │  ← Couche HTTP
│               (@RestController)                          │
├─────────────────────────────────────────────────────────┤
│                   RemiseService                          │  ← Couche métier
│               (@Service, @Transactional)                 │
├─────────────────────────────────────────────────────────┤
│                   RemiseDao (interface)                  │  ← Couche d'accès
│                    ↙              ↘                      │
│   RemiseSpringDataDao    RemiseHibernateDao              │     aux données
│   (@Primary, !hibernate)  (@Profile("hibernate"))        │
├─────────────────────────────────────────────────────────┤
│    RemiseRepository (Spring Data)  │  EntityManager      │
│    (JpaRepository<Remise, Long>)   │  (Hibernate)        │
├─────────────────────────────────────────────────────────┤
│                    Base H2 (mémoire)                     │  ← Base de données
└─────────────────────────────────────────────────────────┘
```

Chaque couche ne connaît que la couche inférieure via son interface :
- **Controller** → `RemiseService` (pas le DAO directement)
- **Service** → `RemiseDao` (interface, pas les implémentations)
- **DAO** → `RemiseRepository` (Spring Data) ou `EntityManager` (Hibernate)

---

## 3. Nouveaux fichiers DAO

Package : `org.example.tpremise.dao`

| Fichier | Rôle |
|---|---|
| `RemiseDao.java` | Interface définissant le contrat DAO : `findByMontant()`, `findById()`, `save()`, `update()`, `deleteById()` |
| `RemiseSpringDataDao.java` | Implémentation par défaut. Délègue toutes les opérations à `RemiseRepository` (Spring Data JPA). Annotée `@Primary` et activée pour tout profil **sauf** `hibernate`. |
| `RemiseHibernateDao.java` | Implémentation alternative via `EntityManager` (Hibernate natif). Activée uniquement avec le profil `hibernate`. Utilise `@PersistenceContext` pour injecter l'`EntityManager`. |

### RemiseDao (interface)

```java
public interface RemiseDao {
    Optional<Remise> findByMontant(Double montant);
    Optional<Remise> findById(Long id);
    Remise save(Remise remise);
    Remise update(Remise remise);
    void deleteById(Long id);
}
```

### RemiseSpringDataDao (Spring Data — par défaut)

```java
@Repository
@Primary
@Profile("!hibernate")
public class RemiseSpringDataDao implements RemiseDao {
    private final RemiseRepository remiseRepository;

    public RemiseSpringDataDao(RemiseRepository remiseRepository) {
        this.remiseRepository = remiseRepository;
    }

    @Override
    public Remise update(Remise remise) {
        if (!remiseRepository.existsById(remise.getId())) {
            throw new RemiseException("Remise non trouvée avec l'id: " + remise.getId());
        }
        return remiseRepository.save(remise);
    }
    // ... autres méthodes déléguées
}
```

### RemiseHibernateDao (Hibernate — profil "hibernate")

```java
@Repository
@Transactional
@Profile("hibernate")
public class RemiseHibernateDao implements RemiseDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Remise> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Remise.class, id));
    }

    @Override
    public Remise save(Remise remise) {
        entityManager.persist(remise);
        return remise;
    }
    // ... autres méthodes avec EntityManager
}
```

---

## 4. Annotations utilisées

### JPA (Jakarta Persistence)

| Annotation | Emplacement | Signification |
|---|---|---|
| `@Entity` | `Remise.java`, `Transaction.java` | Marque la classe comme entité JPA mappée à une table |
| `@Table(name = "REMISE")` | `Remise.java` | Spécifie le nom de la table en base de données |
| `@Id` | `Remise.id`, `Transaction.id` | Déclare la clé primaire |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | `Remise.id` | La clé est générée automatiquement par la base (AUTO_INCREMENT) |
| `@Column(name = "...", nullable = false)` | Champs de `Remise` | Mappe un champ Java à une colonne SQL avec contrainte |

### Spring

| Annotation | Emplacement | Signification |
|---|---|---|
| `@Repository` | DAO classes | Bean Spring + traduction des exceptions persistantes |
| `@Service` | `RemiseService` | Bean Spring de la couche métier |
| `@RestController` | `RemiseController` | Bean Spring + méthodes mappées aux requêtes HTTP |
| `@RequestMapping("/api/remises")` | `RemiseController` | Préfixe d'URL pour tous les endpoints du controller |
| `@Transactional` | `RemiseService`, `RemiseHibernateDao` | Délimite une transaction : tout le bloc s'exécute dans une seule session Hibernate |
| `@PersistenceContext` | `RemiseHibernateDao.entityManager` | Injecte l'`EntityManager` géré par Spring (thread-safe) |
| `@Primary` | `RemiseSpringDataDao` | Indique le bean à injecter par défaut quand plusieurs implémentations existent |
| `@Profile("hibernate")` | `RemiseHibernateDao` | Bean activé uniquement quand le profil `hibernate` est actif |
| `@Profile("!hibernate")` | `RemiseSpringDataDao` | Bean activé quand le profil `hibernate` est **inactif** |
| `@ResponseStatus(HttpStatus.BAD_REQUEST)` | `RemiseException` | Définit le code HTTP (400) renvoyé quand cette exception est lancée |

---

## 5. Chaîne d'appels

### Parcours d'une requête `GET /api/remises/montant/100`

```
1. HTTP GET /api/remises/montant/100
         ↓
2. RemiseController.getByMontant(100.0)
         ↓ appel
3. RemiseService.findByMontant(100.0)
         ↓ appel
4. RemiseDao.findByMontant(100.0)
         ↓ dispatch Spring (selon le profil actif)
   ┌──────┴──────┐
   │  Profil     │  Profil
   │  "default"  │  "hibernate"
   │     ↓       │     ↓
   │ SpringData  │  Hibernate
   │  DAO        │   DAO
   │     ↓       │     ↓
   │ JPA Query   │  JPQL Query
   │  (native)   │  (ORDER BY)
   └──────┬──────┘
         ↓
5. SELECT * FROM remise WHERE 100 BETWEEN montant_min AND montant_max
   ORDER BY montant_min DESC LIMIT 1
         ↓
6. Résultat → Remise{id=1, montantMin=0.0, montantMax=999.99, taux=0.0}
         ↓ (remonte les couches)
7. HTTP 200 + JSON
```

### Principe d'injection

Spring injecte l'implémentation de `RemiseDao` dans `RemiseService` :

- **Par défaut** → `RemiseSpringDataDao` (grâce à `@Primary`)
- **Avec le profil `hibernate`** → `RemiseHibernateDao` (grâce à `@Profile`)

Le `RemiseService` ne connaît que l'interface `RemiseDao` : **zero couplage** aux implémentations concrètes.

---

## 6. Basculer entre les deux implémentations DAO

### Mode par défaut : Spring Data JPA

```yaml
# application.yaml (aucun profil actif)
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    defer-datasource-initialization: true
```

→ `RemiseSpringDataDao` est utilisé. Toutes les opérations passent par `RemiseRepository` (`JpaRepository`).

### Mode Hibernate natif

```yaml
# application.yaml
spring:
  profiles:
    active: hibernate
```

→ `RemiseSpringDataDao` est désactivé (`@Profile("!hibernate")`), `RemiseHibernateDao` prend le relais.
→ Les opérations utilisent `EntityManager` directement (requêtes JPQL, `persist`, `merge`, `find`, `remove`).

### Vérification

Peu importe le mode, les endpoints REST et le service sont **strictement identiques**. Seul l'accès aux données change. On peut basculer sans toucher au code métier.

---

## 7. Endpoints REST de RemiseController

Base URL : `http://localhost:8090/api/remises`

| Méthode | URL | Code succès | Corps requête | Corps réponse | Comportement |
|---|---|---|---|---|---|
| `POST` | `/api/remises` | `201 Created` | `{"montantMin": 100, "montantMax": 500, "taux": 0.1}` | Remise créée | Crée une remise. Renvoie `400` si `id` est présent. |
| `GET` | `/api/remises/montant/{montant}` | `200 OK` | — | Remise trouvée | Cherche la remise dont la plage inclut le montant. Renvoie `404` si aucune. |
| `PUT` | `/api/remises/{id}` | `200 OK` | `{"montantMin": 100, "montantMax": 500, "taux": 0.15}` | Remise mise à jour | Met à jour la remise. Renvoie `400` si l'ID n'existe pas. |
| `DELETE` | `/api/remises/{id}` | `204 No Content` | — | — | Supprime la remise. Renvoie `400` si l'ID n'existe pas. |

### Exemple avec curl

```bash
# Créer une remise
curl -X POST http://localhost:8090/api/remises \
  -H "Content-Type: application/json" \
  -d '{"montantMin": 100, "montantMax": 500, "taux": 0.1}'

# Trouver une remise par montant
curl http://localhost:8090/api/remises/montant/250

# Mettre à jour une remise
curl -X PUT http://localhost:8090/api/remises/1 \
  -H "Content-Type: application/json" \
  -d '{"montantMin": 100, "montantMax": 1000, "taux": 0.12}'

# Supprimer une remise
curl -X DELETE http://localhost:8090/api/remises/1
```

### Codes d'erreur

| Code | Cas |
|---|---|
| `400 Bad Request` | `id` non nul à la création, ID inexistant en modification/suppression, montant négatif |
| `404 Not Found` | Aucune remise trouvée pour le montant donné |

---

## 8. Initialisation de la base de données

H2 est configurée en mode mémoire (`jdbc:h2:mem:testdb`).

Les tables sont créées par Hibernate à partir des entités annotées `@Entity` (`Remise`, `Transaction`).

Les données de démonstration sont insérées via `data.sql` :

```sql
INSERT INTO REMISE (montant_min, montant_max, taux) VALUES (0, 999.99, 0.00);
INSERT INTO REMISE (montant_min, montant_max, taux) VALUES (1000, 4999.99, 0.02);
INSERT INTO REMISE (montant_min, montant_max, taux) VALUES (5000, 9999.99, 0.05);
INSERT INTO REMISE (montant_min, montant_max, taux) VALUES (10000, 49999.99, 0.08);
INSERT INTO REMISE (montant_min, montant_max, taux) VALUES (50000, 99999999, 0.10);
```

Le paramètre `defer-datasource-initialization: true` garantit que `data.sql` s'exécute **après** la création des tables par Hibernate.

Console H2 accessible à : [http://localhost:8090/h2-console](http://localhost:8090/h2-console)  
(JDBC URL : `jdbc:h2:mem:testdb`, user : `sa`, password : *vide*)

---

## 9. Héritage historique : Transaction en JDBC

Le module `Transaction` reste en JDBC pur (`JdbcTemplate`) pour préserver l'existant et illustrer la coexistence de deux technologies :

```
TransactionRepository (JDBC)          RemiseRepository (Spring Data JPA)
       │                                       │
       │  JdbcTemplate.query()                 │  JpaRepository.save()
       │  JdbcTemplate.update()                │  @Query(nativeQuery = true)
       │                                       │
       └── TransactionService                  └── RemiseService
                │                                       │
                └── TransactionController               └── RemiseController
```

Le `TransactionMapper` mappe manuellement les `ResultSet` JDBC vers l'entité `Transaction`, tandis que la gestion des `Remise` bénéficie du mapping automatique de JPA/Hibernate.

---

## 10. Stack technique

| Technologie | Version |
|---|---|
| Java | 17+ |
| Spring Boot | 4.0.5 |
| Spring Data JPA | 4.0.x |
| Hibernate ORM | 7.2.x |
| H2 Database | 2.4.x |
| Lombok | Dernière |
| Maven | 3.9+ |

### Dépendances clés (pom.xml)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

### Démarrage

```bash
# Avec Spring Data JPA (par défaut)
./mvnw spring-boot:run

# Avec Hibernate natif
./mvnw spring-boot:run -Dspring-boot.run.profiles=hibernate
```

L'application démarre sur le port **8090**.
