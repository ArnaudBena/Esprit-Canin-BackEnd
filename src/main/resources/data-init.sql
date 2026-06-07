-- Races
INSERT INTO race (nom) VALUES
    ('Labrador'),
    ('Berger Malinois'),
    ('Caniche'),
    ('Cocker');

-- Roles
INSERT INTO role (nom, description) VALUES
    ('Adherent', 'Utilisateur inscrit au club, peut inscrire ses chiens aux séances'),
    ('Coach', 'Éducateur canin habilité à animer les séances'),
    ('Admin', 'Administrateur du club, gère les utilisateurs et le catalogue de séances et de type de séances');

-- Compétences
INSERT INTO competence (nom, description) VALUES
    ('Obéissance',
    'Discipline regroupant les commandes de base du chien.
    - Débutant : Assis, Couché sur commande au calme, à courte distance.
    - Intermédiaire : ajoute Rappel fiable, Marche en laisse sans tirer, Pas bouger court.
    - Confirmé : maîtrise de l''ensemble avec distractions et à distance, ordres à la voix et au geste.'),
    ('Agilité',
    'Discipline sportive de franchissement et de maniabilité.
    - Débutant : Saut obstacle bas, Tunnel droit, Table (arrêt 5s).
    - Intermédiaire : Slalom 6 piquets, Passerelle, Balançoire assistée, Pneu.
    - Confirmé : Parcours complet enchaîné, slalom 12 piquets, gestion du rythme et des refus.'),
    ('Pistage',
    'Travail olfactif et recherche.
    - Débutant : Recherche d''objet familier à courte distance, piste fraîche simple.
    - Intermédiaire : Piste vieillie 15-30 min avec changements de direction, objets intermédiaires.
    - Confirmé : Piste vieillie 1h+, terrain varié, distracteurs olfactifs, identification d''articles.'),
    ('Sociabilisation',
    'Comportement en environnement varié.
    - Débutant : Calme en présence d''autres chiens tenus en laisse, tolère les inconnus à distance.
    - Intermédiaire : Interaction contrôlée avec chiens congénères, urbain animé, transports courts.
    - Confirmé : Comportement neutre en toutes situations, foule, enfants, autres espèces.');

-- Utilisateurs
-- NB: password en clair pour l'instant, le hash arrivera avec Spring Security
INSERT INTO utilisateur (nom, prenom, email, password, date_inscription, telephone, id_role) VALUES
    ('Dupont', 'Jean', 'jean.dupont@mail.fr', '$2a$10$PFY2p1BU.cwA21ntAeooiOe/jzj/b5rDy7/Gj1ffCoALXDNxrjG/O', '2025-01-15', '0612345678', 1),
    ('Martin', 'Sophie', 'sophie.martin@mail.fr', '$2a$10$BXz2NIDGJoZMeXSsF4tKKeZ7rhLILGp1v7un7Ia.ZAHHBlDx9hl3K', '2025-02-03', '0687654321', 1),
    ('Bernard', 'Lucas', 'lucas.bernard@mail.fr', '$2a$10$uEhqX3Qp7wr9f1EzGxLsre/FVE3N.ExM.NfbJe71r1qzMBvddoHJi', '2024-11-20', NULL, 2),
    ('Benacquista', 'Arnaud', 'arnaud.b@mail.fr', '$2a$10$A0PmjID9zgnVfju6cYjwIuzWuUlXuXCQTWCNEsfSB.I75Zk4gHAmK', '2024-09-10', '0654321987', 3),
    ('Petit', 'Camille', 'camille.petit@mail.fr', '$2a$10$FO4A91.0qKGGubBp7wEuUewShz4LsK9oN.DUligI5hKCGezdi7DHi', '2024-10-15', '0698765432', 2),
    ('Garcia', 'Thomas', 'thomas.garcia@mail.fr', '$2a$10$ro2GKvinxjtPhZAD99AtuezvYhOVUWq6oQ5s.iLCHq9z7j7JhQS/e', '2025-03-01', NULL, 2);

-- Chiens
INSERT INTO chien (nom, poids, taille, sexe, date_naissance, numero_puce, id_utilisateur, id_race) VALUES
    ('Rex', 32.5, 60.0, 'MALE', '2020-05-12', '250269802345678', 1, 1),
    ('Luna', 28.0, 55.0, 'FEMELLE', '2021-08-30', '250269802345679', 1, 2),
    ('Bella', 6.5, 30.0, 'FEMELLE', '2022-03-15', '250269802345680', 2, 3);

-- Types de séance (âges en mois)
INSERT INTO type_seance (libelle, description, age_minimum_mois, age_maximum_mois, duree_minimale, duree_maximale, participants_minimum, participants_maximum) VALUES
    ('Obéissance débutant',
    'Séance d''initiation aux commandes de base (assis, couché, pas bouger) en environnement calme. Idéale pour les jeunes chiens et les chiens jamais éduqués.',
    0, 24, 45, 60, 2, 6),
    ('Agilité intermédiaire',
    'Parcours d''obstacles comprenant slalom 6 piquets, passerelle, pneu et balançoire assistée. Le chien doit déjà maîtriser les bases de l''agilité.',
    12, 120, 60, 90, 3, 8),
    ('Pistage débutant',
    'Introduction au travail olfactif : recherche d''objets familiers sur piste courte et fraîche. Développe la concentration et l''indépendance du chien.',
    12, 120, 60, 120, 2, 5),
    ('Sociabilisation chiots',
    'Rencontres encadrées entre jeunes chiens pour apprendre les codes canins et développer un comportement équilibré en groupe.',
    0, 12, 30, 45, 4, 10),
    ('École du chiot',
    'École du chiot pour tout les chiens entre 0 et 5 mois.',
    0, 5, 30, 60, 2, 6),
    ('Éducation chiot',
    'Cours d''éducation pour les chiots plus âgés de 6 à 12 mois.',
    6, 12, 30, 240, 2, 6),
    ('Éducation jeune chien',
    'Cours d''éducation pour les jeunes chiens de 1 à 2 ans.',
    12, 24, 30, 120, 2, 6),
    ('Éducation chien adulte',
    'Cours d''éducation pour les chiens adultes (2 ans et plus).',
    24, 360, 30, 240, 2, 6);

-- Séances
INSERT INTO seance (date, heure_debut, duree_minutes, statut, id_type_seance, id_coach) VALUES
    ('2027-05-10', '10:00:00', 60, 'ACTIVE', 1, 3),
    ('2026-05-15', '14:00:00', 90, 'ACTIVE', 2, 3),
    ('2027-05-15', '14:00:00', 90, 'ACTIVE', 2, 3),
    ('2027-05-20', '09:30:00', 60, 'ACTIVE', 3, 3);

-- Inscriptions (chien → séance)
-- Rex (chien 1) et Luna (chien 2) s'inscrivent à la séance 1 (Obéissance)
-- Bella (chien 3) s'inscrit à la séance 2 (Agilité)
INSERT INTO inscription (id_chien, id_seance, date_inscription, commentaire_coach, statut_presence, acquisition_validee) VALUES
    (1, 1, '2026-04-20', NULL, 'INSCRIT', false),
    (2, 1, '2026-04-21', NULL, 'INSCRIT', false),
    (3, 2, '2026-04-22', 'Bella progresse bien sur le slalom', 'INSCRIT', false);

-- Compétences NÉCESSAIRES (prérequis d'entrée) par type de séance — table "necessite"
-- Ex : pour entrer en "Agilité intermédiaire", il faut déjà Agilité niveau Débutant.
INSERT INTO type_seance_competence (id_type_seance, id_competence, niveau_minimum_requis) VALUES
    (2, 2, 'DEBUTANT');

-- Compétences CONFÉRÉES par type de séance — table "conferer"
-- Ce que le chien gagne en validant la séance.
INSERT INTO type_seance_competence_conferee (id_type_seance, id_competence, niveau_confere) VALUES
    (1, 1, 'DEBUTANT'),        -- Obéissance débutant      -> Obéissance, niveau Débutant
    (2, 2, 'INTERMEDIAIRE'),   -- Agilité intermédiaire     -> Agilité, niveau Intermédiaire
    (3, 3, 'DEBUTANT'),        -- Pistage débutant          -> Pistage, niveau Débutant
    (4, 4, 'DEBUTANT');        -- Sociabilisation chiots    -> Sociabilisation, niveau Débutant