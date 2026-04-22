-- Races
INSERT INTO race (nom) VALUES
    ('Labrador'),
    ('Berger Malinois'),
    ('Caniche');

-- Roles
INSERT INTO role (nom, description) VALUES
    ('ADHERENT', 'Utilisateur inscrit au club, peut inscrire ses chiens aux séances'),
    ('COACH', 'Éducateur canin habilité à animer les séances'),
    ('ADMIN', 'Administrateur du club, gère les utilisateurs et le catalogue');

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
    ('Dupont', 'Jean', 'jean.dupont@mail.fr', 'motdepasse123', '2025-01-15', '0612345678', 1),
    ('Martin', 'Sophie', 'sophie.martin@mail.fr', 'motdepasse123', '2025-02-03', '0687654321', 1),
    ('Bernard', 'Lucas', 'lucas.bernard@mail.fr', 'motdepasse123', '2024-11-20', NULL, 2),
    ('Lefevre', 'Claire', 'claire.lefevre@mail.fr', 'motdepasse123', '2024-09-10', '0654321987', 3);

-- Chiens
INSERT INTO chien (nom, poids, taille, sexe, date_naissance, numero_puce, id_utilisateur, id_race) VALUES
    ('Rex', 32.5, 60.0, 'MALE', '2020-05-12', '250269802345678', 1, 1),
    ('Luna', 28.0, 55.0, 'FEMELLE', '2021-08-30', '250269802345679', 1, 2),
    ('Bella', 6.5, 30.0, 'FEMELLE', '2022-03-15', '250269802345680', 2, 3);

-- Types de séance
INSERT INTO type_seance (libelle, description, age_minimum, age_maximum, duree_minimale, duree_maximale, participants_minimum, participants_maximum) VALUES
    ('Obéissance débutant',
    'Séance d''initiation aux commandes de base (assis, couché, pas bouger) en environnement calme. Idéale pour les jeunes chiens et les chiens jamais éduqués.',
    0, 2, 45, 60, 2, 6),

    ('Agilité intermédiaire',
    'Parcours d''obstacles comprenant slalom 6 piquets, passerelle, pneu et balançoire assistée. Le chien doit déjà maîtriser les bases de l''agilité.',
    1, 10, 60, 90, 3, 8),

    ('Pistage débutant',
    'Introduction au travail olfactif : recherche d''objets familiers sur piste courte et fraîche. Développe la concentration et l''indépendance du chien.',
    1, 10, 60, 120, 2, 5),

    ('Sociabilisation chiots',
    'Rencontres encadrées entre jeunes chiens pour apprendre les codes canins et développer un comportement équilibré en groupe.',
    0, 1, 30, 45, 4, 10);